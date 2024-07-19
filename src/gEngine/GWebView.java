package gEngine;

import com.sun.net.httpserver.HttpServer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GWebView {

    private static final int defaultPort = 80;
    private static boolean isCopyingScreen = true;
    private static GWebView instance;
    private GImage img;
    private GImage imgToDrawOn;
    private GImage icon;
    private String title;
    private String htmlContent;

    private GWebView(int port) {
        icon = new GImage("G-icon.png");
//        icon.drawImage(0,0,16,16,new GImage("G-icon.png"));
        img = new GImage(100,100);
        imgToDrawOn = new GImage(100,100);
        initializeTitle();
        htmlContent = readHtmlFile();
        HttpServer server = makeHttpServer(port);
        createHttpHandler(server);
        createIconHandler(server);
        createFrameHandler(server);
    }

    private void initializeTitle() {
        String[] className = SetupManager.getInstance().getSetupClass().getName().split("\\.");
        title = className[className.length-1] + " GWebView";
    }

    private HttpServer makeHttpServer(int port) {
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException("failed to create server");
        }
        return server;
    }

    private String readHtmlFile() {
        String value;
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream("WebView.html");
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            value = stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return value.replace("TITLE", title);
    }

    private void createHttpHandler(HttpServer server) {
        server.createContext("/", exchange -> {
            String response = htmlContent;
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
    }

    private void createFrameHandler(HttpServer server) {
        server.createContext("/image", exchange -> {
            GImage img = this.imgToDrawOn;
            GImage imgToSend = new GImage(img.getWidth(), img.getHeight());
            imgToSend.drawImage(0,0, img.getWidth(), img.getHeight(), img);
            BufferedImage bufferedImage = imgToSend.getImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] imageBytes = baos.toByteArray();
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, imageBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(imageBytes);
            os.close();
        });
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
    }

    private void createIconHandler(HttpServer server) {
        server.createContext("/favicon.ico", exchange -> {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(icon.getImage(), "png", baos);
            byte[] imageBytes = baos.toByteArray();
            exchange.getResponseHeaders().set("Content-Type", "image/png");
            exchange.sendResponseHeaders(200, imageBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(imageBytes);
            os.close();
        });
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
        htmlContent = readHtmlFile();
    }

    public void setIcon(GImage icon) {
        this.icon = icon;
    }

    static boolean isCreated() {
        return instance != null;
    }

    static boolean isCopyingScreen() {
        return isCopyingScreen;
    }

    static void setImage(GImage image) {
        getInstance().img = image;
    }
    static void updateFrame() {
        getInstance().imgToDrawOn = new GImage(getInstance().img);
    }

    static GWebView getInstance(int port) {
        if(instance == null) {
            instance = new GWebView(port);
        }
        isCopyingScreen &= true;
        return instance;
    }

    static GWebView getInstance() {
        if(instance == null) {
            instance = new GWebView(defaultPort);
        }
        isCopyingScreen &= true;
        return instance;
    }

    static GWebView getInstance(GImage image, int port) {
        if(instance == null) {
            instance = new GWebView(port);
        }
        isCopyingScreen &= false;
        setImage(image);
        return instance;
    }

    static GWebView getInstance(GImage image) {
        if(instance == null) {
            instance = new GWebView(defaultPort);
        }
        isCopyingScreen &= false;
        setImage(image);
        return instance;
    }

}
