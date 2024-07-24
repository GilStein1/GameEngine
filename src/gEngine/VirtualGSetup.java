package gEngine;

import gEngine.Arrays.Queue;

import java.awt.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class VirtualGSetup{
    private DatagramSocket udpSocket;
    boolean madeAConnection = false;
    private InetAddress clientAddress;
    private int clientPort;
    private int xOnScreen;
    private int yOnScreen;
    private int canvasX;
    private int canvasY;
    private int port;
    private Queue<String> methods;

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
        makeConnection();
        Thread t = new Thread(() -> {
            while (true) {
                tick();
            }
        });
        t.start();
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
        System.out.println("yes");
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
}
