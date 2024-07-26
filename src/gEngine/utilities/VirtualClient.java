package gEngine.utilities;

import gEngine.GImage;
import gEngine.GSetup;
import gEngine.SetupManager;
import gEngine.VirtualGImage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.HashMap;

public class VirtualClient extends GSetup {
    private InetAddress address;
    private DatagramSocket udpSocket;
    private ServerSocket serverSocket;
    private GImage drawingImg;
    private GImage lastFrame;
    private Thread frameUpdate;
    private Thread tcpServer;
    private String lastMessage;
    private HashMap<Integer, GImage> images;
    private boolean initialized;
    @Override
    public void initialize() {
        initialized = false;
        setTitle("virtual client");
        images = new HashMap<>();
        drawingImg = new GImage(900,600);
        lastFrame = new GImage(900,600);
        drawShapesFaster(true);
        frameUpdate = new Thread(() -> {
            try {
                byte[] receiveData = new byte[65507];
                DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
                udpSocket.receive(receivedPacket);
                lastMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
            }
            catch (IOException ignored) {

            }
            while (true) {
                if(initialized) {
                    try {
                        byte[] receiveData = new byte[65507];
                        DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
                        udpSocket.receive(receivedPacket);
                        lastMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                    }
                    catch (IOException ignored) {

                    }
                }
                else {
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        try {
            address = InetAddress.getByName((String)SetupManager.pullFromPool("ipOfHost"));
            String initMessage = "ge ~c ~init";
            DatagramPacket initPacket = new DatagramPacket(initMessage.getBytes(), initMessage.length(), address, (int)SetupManager.pullFromPool("portOfHost"));
            udpSocket = new DatagramSocket();
            udpSocket.send(initPacket);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        frameUpdate.start();

        tcpServer = new Thread(() -> {
            DataOutputStream out = null;
            BufferedReader in = null;
            Socket clientSocket = null;

            try {
                serverSocket = new ServerSocket((int)SetupManager.pullFromPool("portOfHost") + 1);
                clientSocket = serverSocket.accept();
//                System.out.println("connected");
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            while (true) {
                try {
                    String message = in.readLine();
                    handelTCPRequest(out, message);
                    Thread.sleep(5);
                }
                catch (IOException ignored) {
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        tcpServer.start();
    }

    private void handelTCPRequest(DataOutputStream outputStream, String message) {
//        System.out.println("gotHere");
        if(message.startsWith("ge ~s")) {
            message = message.substring(6);
            String[] parts = message.split("~");
            try {
                switch (parts[1]) {
                    case "xoc" -> {
                        String response = "ge ~c ~xoc|~x:" + xOnCanvas();
                        outputStream.write((response + "\n").getBytes());
                    }
                    case "yoc" -> {
                        String response = "ge ~c ~yoc|~y:" + yOnCanvas();
                        outputStream.write((response + "\n").getBytes());
                    }
                    case "lc" -> {
                        String response = "ge ~c ~lc|~a:" + leftClick();
                        outputStream.write((response + "\n").getBytes());
                    }
                    case "rc" -> {
                        String response = "ge ~c ~rc|~a:" + rightClick();
                        outputStream.write((response + "\n").getBytes());
                    }
                    case "lk" -> {
                        String response = "ge ~c ~lk|~k:" + lastKey();
                        outputStream.write((response + "\n").getBytes());
                    }
                }
            }
            catch (IOException ignored) {}
        }
    }

    private void updateImgByMessage(String message) {
        if(message.startsWith("ge ~s")) {
            message = message.substring(6);
            String[] parts = message.split("~");
            switch (parts[1]) {
                case "mkImg|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
//                        System.out.println("id of the image is: " + Integer.valueOf(vars[vars.length-1].substring(3)));
                        images.put(Integer.valueOf(vars[vars.length-1].substring(3)), new GImage(
                                Integer.parseInt(vars[0].substring(2)),
                                Integer.parseInt(vars[1].substring(2))
                        ));
                    }
                }
                case "mkImgData|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        String[] bytesStr = vars[0].substring(5).split(",");
                        byte[] bytes = new byte[bytesStr.length];
                        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                        BufferedImage bufferedImage;
                        try {
                            bufferedImage = ImageIO.read(bais);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        images.put(Integer.valueOf(vars[vars.length-1].substring(3)), new GImage(bufferedImage));
                    }
                }
                case "edImg " -> {
                    if(images != null) {
                        VirtualGImage.handleDrawingCommands(images, message);
                    }
                }
                case "drImg|" -> {
//                    System.out.println("trying to draw the image");
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        if(images.get(Integer.parseInt(vars[4].substring(3))) != null) {
                            drawingImg.drawImage(
                                    Integer.parseInt(vars[0].substring(2)),
                                    Integer.parseInt(vars[1].substring(2)),
                                    Integer.parseInt(vars[2].substring(2)),
                                    Integer.parseInt(vars[3].substring(2)),
                                    images.get(Integer.parseInt(vars[4].substring(3)))
                            );
                        }
                    }
                }
                case "drEll|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        drawingImg.drawEllipse(
                                Integer.parseInt(vars[0].substring(2)),
                                Integer.parseInt(vars[1].substring(2)),
                                Integer.parseInt(vars[2].substring(2)),
                                Integer.parseInt(vars[3].substring(2)),
                                new Color(Integer.parseInt(vars[4].substring(2)))
                        );
                    }
                }
                case "flEll|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        drawingImg.fillEllipse(
                                Integer.parseInt(vars[0].substring(2)),
                                Integer.parseInt(vars[1].substring(2)),
                                Integer.parseInt(vars[2].substring(2)),
                                Integer.parseInt(vars[3].substring(2)),
                                new Color(Integer.parseInt(vars[4].substring(2)))
                        );
                    }
                }
                case "drRec|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        drawingImg.drawRectangle(
                                Integer.parseInt(vars[0].substring(2)),
                                Integer.parseInt(vars[1].substring(2)),
                                Integer.parseInt(vars[2].substring(2)),
                                Integer.parseInt(vars[3].substring(2)),
                                new Color(Integer.parseInt(vars[4].substring(2)))
                        );
                    }
                }
                case "flRec|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        drawingImg.fillRectangle(
                                Integer.parseInt(vars[0].substring(2)),
                                Integer.parseInt(vars[1].substring(2)),
                                Integer.parseInt(vars[2].substring(2)),
                                Integer.parseInt(vars[3].substring(2)),
                                new Color(Integer.parseInt(vars[4].substring(2)))
                        );
                    }
                }
                case "drPol|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        String[] xStr = vars[0].substring(2).split(",");
                        String[] yStr = vars[1].substring(2).split(",");
                        int[] x = new int[xStr.length];
                        int[] y = new int[yStr.length];
                        for(int i = 0; i < x.length; i++) {
                            x[i] = Integer.valueOf(xStr[i]);
                            y[i] = Integer.valueOf(yStr[i]);
                        }
                        drawingImg.drawPolygon(x,y,new Color(Integer.parseInt(vars[2].substring(2))));
                    }
                }
                case "flPol|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        String[] xStr = vars[0].substring(2).split(",");
                        String[] yStr = vars[1].substring(2).split(",");
                        int[] x = new int[xStr.length];
                        int[] y = new int[yStr.length];
                        for(int i = 0; i < x.length; i++) {
                            x[i] = Integer.valueOf(xStr[i]);
                            y[i] = Integer.valueOf(yStr[i]);
                        }
                        drawingImg.fillPolygon(x,y,new Color(Integer.parseInt(vars[2].substring(2))));
                    }
                }
                case "drTxt|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        drawingImg.drawText(
                                Integer.parseInt(vars[0].substring(2)),
                                Integer.parseInt(vars[1].substring(2)),
                                vars[2].substring(2),
                                new Color(Integer.parseInt(vars[3].substring(2)))
                        );
                    }
                }
                case "drLine|" -> {
                    if(drawingImg != null) {
                        String temp = message.substring(message.indexOf("|")+2);
                        String[] vars = temp.split("~");
                        drawingImg.drawLine(
                                Integer.parseInt(vars[0].substring(3)),
                                Integer.parseInt(vars[1].substring(3)),
                                Integer.parseInt(vars[2].substring(3)),
                                Integer.parseInt(vars[3].substring(3)),
                                new Color(Integer.parseInt(vars[4].substring(2)))
                        );
                    }
                }
            }
        }
    }

    @Override
    public void execute() {
        initialized = true;
        boolean is = true;
        if(lastMessage != null) {
//            is = updateImgByMessage(lastMessage);
            String[] methods = lastMessage.split("\\\\");
//            System.out.println(lastMessage);
//            System.out.println(methods.length);
            if(getFrameWidth() != drawingImg.getWidth() || getFrameHeight() != drawingImg.getHeight()) {
                drawingImg = new GImage(getFrameWidth(), getFrameHeight());
            }
            drawingImg.fillRectangle(0,0,getFrameWidth(), getFrameHeight(), defaultBackground);
//            System.out.println(lastMessage);
            for(int i = 0; i < methods.length; i++) {
                updateImgByMessage(methods[i]);
            }
        }
        if(drawingImg != null && is) {
            lastFrame = new GImage(drawingImg);
//            lastFrame.fillRectangle(0,0,900,600, defaultBackground);
//            lastFrame.drawImage(0,0,getFrameWidth(),getFrameHeight(),drawingImg);
        }
        drawImage(0,0,getFrameWidth(),getFrameHeight(),lastFrame);
    }

    @Override
    public boolean end() {
        return false;
    }
}
