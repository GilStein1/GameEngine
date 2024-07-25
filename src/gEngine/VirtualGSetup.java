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
    private boolean leftClick;
    private boolean rightClick;
    private boolean xOnScreenValueUpdate;
    private boolean yOnScreenValueUpdate;
    private boolean leftClickValueUpdate;
    private boolean rightClickValueUpdate;
    private int port;
    private Queue<String> methods;
    private Queue<String> requests;
    private Thread tcpHandler;
    private String methodStr;
    private long lastTime;
    private double deltaTime;

    public abstract void initialize();
    public abstract void execute();
    public abstract boolean end();
    public VirtualGSetup(int port) {
        this.port = port;
//        SetupManager.addTick(this);
        init();
    }

    private void init() {
        lastTime = System.nanoTime();
        xOnScreen = 0;
        yOnScreen = 0;
        canvasX = 0;
        canvasY = 0;
        leftClick = false;
        rightClick = false;
        resetAllTcpCalledValues();
        methods = new Queue<>();
        methodStr = "";
        requests = new Queue<>();
        makeConnection();
        tcpHandler = new Thread(() -> {
            OutputStream out;
            BufferedReader in;
            try {
                tcpSocket = new Socket(clientAddress, port + 1);
                out = tcpSocket.getOutputStream();
                in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            while (true) {
                if(!requests.isEmpty()) {
                    String message = requests.head();
//                    System.out.println(message);
                    requests.remove();
                    try {
//                        tcpSocket = new Socket(clientAddress, port + 1);
//                        OutputStream out = tcpSocket.getOutputStream();
//                        System.out.println("writing message");
                        out.write((message + "\n").getBytes());
//                        System.out.println("waiting for message");
//                        BufferedReader in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
                        handelTcp(in.readLine());
//                        System.out.println("got message");
//                        tcpSocket.close();
                    } catch (IOException ignored) {
//                        System.out.println("there is an error");
                    }
                }
            }
        });
        tcpHandler.start();
        initialize();
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
                case "lc|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    leftClick = Boolean.parseBoolean(vars[0].substring(2));
                }
                case "rc|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    rightClick = Boolean.parseBoolean(vars[0].substring(2));
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

    public void resetAllTcpCalledValues() {
        xOnScreenValueUpdate = true;
        yOnScreenValueUpdate = true;
        leftClickValueUpdate = true;
        rightClickValueUpdate = true;
    }

    public void tick() {
        if(madeAConnection) {
            methods.insert("ge ~s ~exStart");
            execute();
            methods.insert("ge ~s ~exEnd");
            resetAllTcpCalledValues();
//            System.out.println(methodStr.split("\\\\").length);
            String method = methodStr.substring(0);
//            System.out.println(method.getBytes().length);
//            System.out.println(method);
            try {
                DatagramPacket sendMethod = new DatagramPacket(method.getBytes(), method.length(), clientAddress, clientPort);
                udpSocket.send(sendMethod);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            methodStr = "";
            deltaTime = (System.nanoTime() - lastTime)/1000000000.0;
            lastTime = System.nanoTime();
        }
    }

    public double deltaTime() {
        if(deltaTime > 0.1) {
            return 0;
        }
        return deltaTime;
    }

    public int xOnCanvas() {
        if(xOnScreenValueUpdate) {
            requests.insert("ge ~s ~xoc");
            xOnScreenValueUpdate = false;
        }
        return xOnScreen;
    }
    public int yOnCanvas() {
        if(yOnScreenValueUpdate) {
            requests.insert("ge ~s ~yoc");
            yOnScreenValueUpdate = false;
        }
        return yOnScreen;
    }

    public boolean leftClick() {
        if(leftClickValueUpdate) {
            requests.insert("ge ~s ~lc");
            leftClickValueUpdate = false;
        }
        return leftClick;
    }

    public boolean rightClick() {
        if(rightClickValueUpdate) {
            requests.insert("ge ~s ~rc");
            rightClickValueUpdate = false;
        }
        return rightClick;
    }

    public void drawEllipse(int x, int y, int width, int height, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~drEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~drEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~flEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~flEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void drawRectangle(int x, int y, int width, int height, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~drRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~drRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void fillRectangle(int x, int y, int width, int height, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~flRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~flRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB());
    }

    public void drawPolygon(int[] x, int[] y, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        String xVal = "";
        for(int i = 0; i < x.length; i++) {
            xVal += x[i] + ((i < x.length-1)? "," : "");
        }
        String yVal = "";
        for(int i = 0; i < x.length; i++) {
            yVal += y[i] + ((i < y.length-1)? "," : "");
        }
        methodStr += "ge ~s ~drPol|~x:" + xVal + "~y:" + yVal + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~drPol|~x:" + xVal + "~y:" + yVal + "~c:" + color.getRGB());
    }

    public void fillPolygon(int[] x, int[] y, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        String xVal = "";
        for(int i = 0; i < x.length; i++) {
            xVal += x[i] + ((i < x.length-1)? "," : "");
        }
        String yVal = "";
        for(int i = 0; i < x.length; i++) {
            yVal += y[i] + ((i < y.length-1)? "," : "");
        }
        methodStr += "ge ~s ~flPol|~x:" + xVal + "~y:" + yVal + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~flPol|~x:" + xVal + "~y:" + yVal + "~c:" + color.getRGB());
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~drLine|~x1:" + x1 + "~y1:" + y1 + "~x2:" + x2 + "~y2:" + y2 + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~drLine|~x1:" + x1 + "~y1:" + y1 + "~x2:" + x2 + "~y2:" + y2 + "~c:" + color.getRGB());
    }
    public void drawText(int x, int y, String text, Color color) {
        methods.insert("ge ~s ~drTxt|~x:" + x + "~y:" + y + "~t:" + text + "~c:" + color.getRGB());
    }
}
