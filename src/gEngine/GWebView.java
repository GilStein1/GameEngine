package gEngine;

import com.sun.net.httpserver.HttpServer;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class GWebView {

    private static GWebView instance;
    private static final int defaultPort = 8080;
    private GImage img;
    private GImage imgToDrawOn;
    private static boolean isCopyingScreen = true;
    private String title;

    private GWebView(int port) {

        img = new GImage(100,100);
        imgToDrawOn = new GImage(100,100);
        String[] className = SetupManager.getInstance().getSetupClass().getName().split("\\.");

        title = className[className.length-1] + " GWebView";

        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            throw new RuntimeException("failed to create server");
        }
        server.createContext("/", exchange -> {
            int width = SetupManager.getInstance().getSetup().getFrameWidth();
            int height = SetupManager.getInstance().getSetup().getFrameHeight();
            System.out.println(width + "," + height);
            String response = "<html>" +
                    "<head><title>" + title + "</title></head>" +
                    "<style>" +
                    "html, body { height: 100%; margin: 0; }" +
                    "#container { display: flex; justify-content: center; align-items: center; height: 100%; }" +
                    "#dynamicImage { max-width: 100%; max-height: 100%; }" +
                    "</style>" +
                    "<body>" +
                    "<div id='container'>" +
                    "<img id='dynamicImage' src='/image' alt='Dynamic Image'/>" +
                    "</div>" +
                    "<script>" +
                    "function updateImage() {" +
                    "    var img = document.getElementById('dynamicImage');" +
                    "    img.src = '/image?' + new Date().getTime();" +
                    "}" +
                    "setInterval(updateImage, 50);" +
                    "</script>" +
                    "</body>" +
                    "</html>";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });

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

    public void setTitle(String newTitle) {
        this.title = newTitle;
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
