package gEngine.utilities;

import gEngine.GImage;
import gEngine.GSetup;
import gEngine.SetupManager;

import java.awt.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;

public class VirtualClient extends GSetup {
    private InetAddress address;
    private DatagramSocket udpSocket;
    private ServerSocket serverSocket;
    private GImage drawingImg;
    private GImage lastFrame;
    private Thread frameUpdate;
    private Thread tcpServer;
    private String lastMessage;
    @Override
    public void initialize() {
        setTitle("virtual client");
        drawingImg = new GImage(900,600);
        lastFrame = new GImage(900,600);
        drawShapesFaster(true);
        frameUpdate = new Thread(() -> {
            while (true) {
                try {
                    byte[] receiveData = new byte[1024];
                    DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
                    udpSocket.receive(receivedPacket);
                    lastMessage = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
//                    updateImgByMessage(new String(receivedPacket.getData(), 0, receivedPacket.getLength()));
                }
                catch (IOException ignored) {

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
                serverSocket = new ServerSocket((int)SetupManager.pullFromPool("portOfHost"));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                try {
                    clientSocket = serverSocket.accept();
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    out = new DataOutputStream(clientSocket.getOutputStream());
                    String message = in.readLine();
                    clientSocket.close();
                    in.close();
                    out.close();
                }
                catch (IOException ignored) {

                }
            }
        });
        tcpServer.start();
    }

    private boolean updateImgByMessage(String message) {
        if(message.startsWith("ge ~s")) {
            message = message.substring(6);
//            System.out.println(message);
            String[] parts = message.split("~");
//            System.out.println(parts[1]);
            switch (parts[1]) {
                case "exStart" -> {
                    drawingImg = new GImage(getFrameWidth(),getFrameHeight());
                    System.out.println("yes");
                    return false;
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
//                        System.out.println(temp);
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
                default -> {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void execute() {
        boolean is = true;
        if(lastMessage != null) {
            is = updateImgByMessage(lastMessage);
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
