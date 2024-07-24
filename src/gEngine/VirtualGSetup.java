package gEngine;

import gEngine.Arrays.Queue;

import java.awt.*;
import java.io.*;
import java.net.*;

public abstract class VirtualGSetup{
    private DatagramSocket udpSocket;
    private Socket tcpSocket;
    private boolean madeAConnection = false;
    private InetAddress clientAddress;
    private int clientPort;
    private int xOnScreen;
    private int yOnScreen;
    private int canvasX;
    private int canvasY;
    private int port;
    private Queue<String> methods;
    private Queue<String> requests;
    private Thread tcpHandler;

    public abstract void initialize();
    public abstract void execute();
    public abstract boolean end();
    public VirtualGSetup(int port) {
        this.port = port;
//        SetupManager.addTick(this);
        init();
    }

    private void init() {
        xOnScreen = 0;
        yOnScreen = 0;
        canvasX = 0;
        canvasY = 0;
        methods = new Queue<>();
        requests = new Queue<>();
        makeConnection();
        tcpHandler = new Thread(() -> {
            while (true) {
                if(!requests.isEmpty()) {
                    String message = requests.head();
                    requests.remove();
                    try {
                        tcpSocket = new Socket(clientAddress, port + 1);
//                        System.out.println("aright, connected");
                        OutputStream out = tcpSocket.getOutputStream();
                        out.write((message + "\n").getBytes());
//                        System.out.println("aright, sent");
                        BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                        handelTcp(in.readLine());
                        tcpSocket.close();
                    } catch (IOException e) {
//                        System.out.println(clientPort + 1);
//                        System.out.println("error");
//                        throw new RuntimeException(e);
                    }
                }
            }
        });
        tcpHandler.start();
        Thread t = new Thread(() -> {
            while (true) {
                tick();
            }
        });
        t.start();
    }

    private void handelTcp(String message) {
//        System.out.println("here");
        if(message.startsWith("ge ~c")) {
            message = message.substring(6);
            String[] parts = message.split("~");
            switch (parts[1]) {
                case "xoc|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    xOnScreen = Integer.parseInt(vars[0].substring(2));
                }
                case "yoc|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    yOnScreen = Integer.parseInt(vars[0].substring(2));
                }
            }
        }
    }

    private void makeConnection() {
        madeAConnection = false;
        DatagramPacket udpPacket = null;
        while (!madeAConnection) {
            try {
                udpSocket = new DatagramSocket(port);
                byte[] receiveData = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
                udpSocket.receive(receivedPacket);
                String message = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                madeAConnection = message.startsWith("ge ~c ~init");
                if(madeAConnection) {
                    udpPacket = receivedPacket;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        clientAddress = udpPacket.getAddress();
        clientPort = udpPacket.getPort();
//        System.out.println("yes");
    }

    public void tick() {
        if(madeAConnection) {
            methods.insert("ge ~s ~exStart");
            execute();
            methods.insert("ge ~s ~exEnd");
            while (!methods.isEmpty()) {
                try {
                    String method = methods.head();
                    DatagramPacket sendMethod = new DatagramPacket(method.getBytes(), method.length(), clientAddress, clientPort);
                    udpSocket.send(sendMethod);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                methods.remove();
            }
        }
    }
    public int xOnCanvas() {
        requests.insert("ge ~s ~xoc");
        return xOnScreen;
    }
    public int yOnCanvas() {
        requests.insert("ge ~s ~yoc");
        return yOnScreen;
    }

    public void drawEllipse(int x, int y, int width, int height, Color color) {
        methods.insert("ge ~s ~drEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void fillEllipse(int x, int y, int width, int height, Color color) {
        methods.insert("ge ~s ~flEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void drawRectangle(int x, int y, int width, int height, Color color) {
        methods.insert("ge ~s ~drRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void fillRectangle(int x, int y, int width, int height, Color color) {
        methods.insert("ge ~s ~flRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        methods.insert("ge ~s ~drLine|~x1:" + x1 + "~y1:" + y1 + "~x2:" + x2 + "~y2:" + y2 + "~c:" + color.getRGB());
    }
    public void drawText(int x, int y, String text, Color color) {
        methods.insert("ge ~s ~drTxt|~x:" + x + "~y:" + y + "~t:" + text + "~c:" + color.getRGB());
    }
}
