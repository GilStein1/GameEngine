package pack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class GImage {

    private BufferedImage img;
    private Graphics graphics;
    private double angle = 0;

    public GImage(String path) {

        path = "/" + path;

        try {
            img = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public GImage(int width, int height) {
        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
    }
    public double getAngle() {
        return angle;
    }
    public void setAngle(double angle) {
        this.angle = angle;
    }
    public GImage(BufferedImage image) {
        img = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
        graphics.drawImage(image,0,0, img.getWidth(), img.getHeight(),null);
    }
    public GImage(GImage image) {
        img = new BufferedImage(image.getImage().getWidth(),image.getImage().getHeight(),BufferedImage.TYPE_INT_ARGB);
        graphics = img.createGraphics();
        graphics.drawImage(image.getImage(),0,0, img.getWidth(), img.getHeight(),null);
    }
    public Graphics getGraphics() {
        return graphics;
    }
    public void drawEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawOval(x,y,width,height);
        }
    }
    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillOval(x,y,width,height);
        }
    }
    public void drawRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawRect(x,y,width,height);
        }
    }
    public void fillRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillRect(x,y,width,height);
        }
    }
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawLine(x1,y1,x2,y2);
        }
    }
    public void drawText(int x, int y,String text, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawString(text,x,y);

        }
    }
    public void drawImage(int x, int y, int width, int height, GImage image) {

        graphics.drawImage(image.getImage(),x,y,width,height,null);

    }
    public void drawImage(int x, int y, int width, int height, String path) {

        path = "/" + path;

        try {
            graphics.drawImage(ImageIO.read(getClass().getResourceAsStream(path)),x,y,width,height,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setFont(Font font) {
        graphics.setFont(font);
    }

    public BufferedImage getImage() {
        return img;
    }
    public int getRed(int x, int y) {
        return (img.getRGB(x, y) >> 16) & 0xFF;
    }
    public int getGreen(int x, int y) {
        return (img.getRGB(x, y) >> 8) & 0xFF;
    }
    public int getBlue(int x, int y) {
        return (img.getRGB(x, y)) & 0xFF;
    }
    public Color getColor(int x, int y) {
        return new Color((img.getRGB(x, y) >> 16) & 0xFF,(img.getRGB(x, y) >> 8) & 0xFF,(img.getRGB(x, y)) & 0xFF);
    }
    public int getWidth() {
        return img.getWidth();
    }
    public int getHeight() {
        return img.getHeight();
    }
    public void exportAsPNG(GFile file) {
        try {
            ImageIO.write(img,"PNG",file.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
