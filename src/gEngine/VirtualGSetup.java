package gEngine;

import gEngine.Arrays.Queue;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public abstract class VirtualGSetup{
    private DatagramSocket udpSocket;
    private Socket tcpSocket;
    private boolean madeAConnection = false;
    private InetAddress clientAddress;
    private int clientPort;
    private int xOnScreen;
    private int yOnScreen;
    private int frameWidth;
    private int frameHeight;
    private int canvasX;
    private int canvasY;
    private boolean leftClick;
    private boolean rightClick;
    private int lastKey;
    private boolean xOnScreenValueUpdate;
    private boolean yOnScreenValueUpdate;
    private boolean leftClickValueUpdate;
    private boolean rightClickValueUpdate;
    private boolean screenWidthValueUpdate;
    private boolean screenHeightValueUpdate;
    private boolean lastKeyValueUpdate;
    private final ArrayList<Integer> keysPressedUpdate = new ArrayList<>();
    private final ArrayList<Integer> keysPressed = new ArrayList<>();
    private int port;
    private Queue<String> methods;
    private Queue<String> requests;
    private Queue<byte[]> byteMessageBuffer;
    private Thread tcpHandler;
    private String methodStr;
    private long lastTime;
    private double deltaTime;
    private boolean hasInitialized;
    private boolean shutDown;

    public abstract void initialize();
    public abstract void execute();
    public abstract boolean end();
    public VirtualGSetup(int port) {
        this.port = port;
        init();
    }

    private void init() {
        shutDown = false;
        hasInitialized = false;
        lastTime = System.nanoTime();
        xOnScreen = 0;
        yOnScreen = 0;
        frameWidth = 900;
        frameHeight = 600;
        canvasX = 0;
        canvasY = 0;
        leftClick = false;
        rightClick = false;
        lastKey = -1;
        resetAllTcpCalledValues();
        methods = new Queue<>();
        methodStr = "";
        requests = new Queue<>();
        byteMessageBuffer = new Queue<>();
        makeConnection();
        initialize();
        try {
            DatagramPacket sendMethod = new DatagramPacket(methodStr.getBytes(), methodStr.length(), clientAddress, clientPort);
            udpSocket.send(sendMethod);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        methodStr = "";
        tcpHandler = new Thread(() -> {
            OutputStream out;
            BufferedReader in;
            try {
                tcpSocket = new Socket(clientAddress, port + 1);
                out = tcpSocket.getOutputStream();
                in = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            boolean isWaiting = false;
            while (true) {
                if(!requests.isEmpty()) {
                    String message = requests.head();
//                    System.out.println(requests.remove());
                    try {
                        if(isWaiting){
                            out.write(byteMessageBuffer.remove());
                            isWaiting = false;
                        }
                        else {
                            out.write((message + "\n").getBytes());
                            isWaiting = message.contains("ge ~s ~wfb");
                            requests.remove();
                        }
                        handelTcp(in.readLine());
                    }
                    catch (IOException ignored) {

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
                case "ikp|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    boolean isPressed = Boolean.parseBoolean(vars[0].substring(2));
                    int key = Integer.parseInt(vars[1].substring(3));
                    if(isPressed) {
                        if(!keysPressed.contains(key)) {
                            keysPressed.add(key);
                        }
                    }
                    else {
                        if(keysPressed.contains(key)) {
                            keysPressed.remove((Object)key);
                        }
                    }
                }
                case "fw|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    frameWidth = Integer.parseInt(vars[0].substring(2));
                }
                case "fh|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    frameHeight = Integer.parseInt(vars[0].substring(2));
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
                case "lk|" -> {
                    String temp = message.substring(message.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    lastKey = Integer.parseInt(vars[0].substring(2));
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
    }

    public void insertToTcpRequestByteBuffer(byte[] arr) {
        byteMessageBuffer.insert(arr);
    }
    public void insertTcpRequests(String message) {
        requests.insert(message);
    }

    public void resetAllTcpCalledValues() {
        xOnScreenValueUpdate = true;
        yOnScreenValueUpdate = true;
        leftClickValueUpdate = true;
        rightClickValueUpdate = true;
        screenWidthValueUpdate = true;
        screenHeightValueUpdate = true;
        lastKeyValueUpdate = true;
        keysPressedUpdate.clear();
    }

    private void tick() {
        if(madeAConnection) {
            methods.insert("ge ~s ~exStart");
            shutDown |= end();
            if(shutDown) {
                if(!methodStr.isEmpty()) {
                    methodStr += "\\\\";
                }
                methodStr += "ge ~s ~end";
            }
            execute();
            methods.insert("ge ~s ~exEnd");
            resetAllTcpCalledValues();
            String method = methodStr.substring(0);
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
        return Math.min(deltaTime, 0.01);
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

    public boolean isKeyPressed(int key) {
        if(!keysPressedUpdate.contains(key)) {
            requests.insert("ge ~s ~ikp|~id:" + key);
            keysPressedUpdate.add(key);
        }
        return keysPressed.contains(key);
    }

    public int getFrameWidth() {
        if(screenWidthValueUpdate) {
            requests.insert("ge ~s ~fw");
            screenWidthValueUpdate = false;
        }
        return frameWidth;
    }

    public int getFrameHeight() {
        if(screenHeightValueUpdate) {
            requests.insert("ge ~s ~fh");
            screenHeightValueUpdate = false;
        }
        return frameHeight;
    }

    public void setFullScreen(boolean fullScreen) {
        insertTcpRequests("ge ~s ~" + (fullScreen? "fullSc" : "smallSc"));
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

    public int lastKey() {
        if(lastKeyValueUpdate) {
            requests.insert("ge ~s ~lk");
            lastKeyValueUpdate = false;
        }
        return lastKey;
    }

    void addToMethodStr(String message) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += message;
    }

    public void drawImage(int x, int y, int width, int height, VirtualGImage image) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~drImg|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~id:" + image.getId();
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

    public void drawPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        drawPolygon(x,y,color);
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

    public void fillPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        fillPolygon(x,y,color);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~drLine|~x1:" + x1 + "~y1:" + y1 + "~x2:" + x2 + "~y2:" + y2 + "~c:" + color.getRGB();
//        methods.insert("ge ~s ~drLine|~x1:" + x1 + "~y1:" + y1 + "~x2:" + x2 + "~y2:" + y2 + "~c:" + color.getRGB());
    }
    public void drawText(int x, int y, String text, Color color) {
        if(!methodStr.isEmpty()) {
            methodStr += "\\\\";
        }
        methodStr += "ge ~s ~drTxt|~x:" + x + "~y:" + y + "~t:" + text + "~c:" + color.getRGB();
    }
}
