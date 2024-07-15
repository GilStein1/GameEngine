package gEngine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
/**
 * An object that represents a matrix of pixels, can be drawn on the screen or another GImage object. Is also used to set an icon
 * for the screen and is able to be exported as a png file.
 */
public class GImage {

    private BufferedImage img;
    private Graphics2D graphics;
    private GSetup.Smoothness smoothness = GSetup.Smoothness.NORMAL;
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
    public void setSmoothness(GSetup.Smoothness smoothness) {
        this.smoothness = smoothness;
        updateSmoothness(smoothness,(Graphics2D) graphics);
    }
    void updateSmoothness(GSetup.Smoothness amount, Graphics2D graphics) {
        switch (amount) {
            case VERY_SMOOTH -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            case NORMAL -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_DEFAULT);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
            }
            case NOT_SMOOTH -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }
        }
    }
    public void setToEraser(boolean eraser) {
        graphics.setColor(Color.BLACK);
        graphics.setComposite(eraser? AlphaComposite.Clear : AlphaComposite.SrcOver);
    }
    /**
     * @return the current angle (in degrees) of the GImage
     */
    public double getAngle() {
        return angle;
    }
    /**
     * Sets the angle of the GImage
     * @param angle the angle (in degrees)
     */
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
    public Graphics2D getGraphics() {
        return graphics;
    }
    /**
     * Draws an ellipse on the GImage
     * @param     x the x of the ellipse
     * @param     y the y of the ellipse
     * @param     width the width of the ellipse
     * @param     height the height of the ellipse
     * @param     color the color of the ellipse
     */
    public void drawEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawOval(x,y,width,height);
        }
    }
    /**
     * Fills an ellipse on the GImage
     * @param     x the x of the ellipse
     * @param     y the y of the ellipse
     * @param     width the width of the ellipse
     * @param     height the height of the ellipse
     * @param     color the color of the ellipse
     */
    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillOval(x,y,width,height);
        }
    }
    /**
     *Draws a rectangle on the GImage
     * @param     x the x of the rectangle
     * @param     y the y of the rectangle
     * @param     width the width of the rectangle
     * @param     height the height of the rectangle
     * @param     color the color of the rectangle
     */
    public void drawRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawRect(x,y,width,height);
        }
    }
    /**
     *Fills a rectangle on the GImage
     * @param     x the x of the rectangle
     * @param     y the y of the rectangle
     * @param     width the width of the rectangle
     * @param     height the height of the rectangle
     * @param     color the color of the rectangle
     */
    public void fillRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillRect(x,y,width,height);
        }
    }
    /**
     *Draws a polygon by a set of given points
     * @param points the array of points of the polygon
     * @param color the color of the polygon
     */
    public void drawPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.drawPolygon(x,y, points.length);
    }
    /**
     *Draws a polygon by a set of given points
     * @param x an array of x coordinates
     * @param y an array of y coordinates
     * @param color the color of the polygon
     */
    public void drawPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.drawPolygon(x,y,x.length);
    }
    /**
     *Fills a polygon by a set of given points
     * @param points the array of points of the polygon
     * @param color the color of the polygon
     */
    public void fillPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.fillPolygon(x,y, points.length);
    }
    /**
     *Fills a polygon by a set of given points
     * @param x an array of x coordinates
     * @param y an array of y coordinates
     * @param color the color of the polygon
     */
    public void fillPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.fillPolygon(x,y,x.length);
    }
    /**
     * Draws a line through 2 points
     * @param     x1 the x of the first point
     * @param     y1 the y of the first point
     * @param     x2 the x of the second point
     * @param     y2 the y of the second point
     * @param     color the color of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawLine(x1,y1,x2,y2);
        }
    }
    /**
     *Draws text on the GImage (font can be edited using the setFont() method)
     * @param     x the x of the text
     * @param     y the y of the text
     * @param     text the text to be drawn
     * @param     color the color of the text
     */
    public void drawText(int x, int y,String text, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawString(text,x,y);

        }
    }
    /**
     *Draws an image on the GImage by given coordinates and dimensions and a GImage
     * @param     x the x of the image
     * @param     y the y of the image
     * @param     width the width of the image
     * @param     height the height of the image
     * @param     image the image to be drawn
     */
    public void drawImage(int x, int y, int width, int height, GImage image) {

        graphics.drawImage(image.getImage(),x,y,width,height,null);

    }
    /**
     *Draws an image on the GImage by given coordinates and dimensions and a path to the image in the resources directory
     * @param     x the x of the image
     * @param     y the y of the image
     * @param     width the width of the image
     * @param     height the height of the image
     * @param     path the path of the image
     */
    public void drawImage(int x, int y, int width, int height, String path) {

        path = "/" + path;

        try {
            graphics.drawImage(ImageIO.read(getClass().getResourceAsStream(path)),x,y,width,height,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Sets a new font
     * @param     font the new font
     */
    public void setFont(Font font) {
        graphics.setFont(font);
    }

    public BufferedImage getImage() {
        return img;
    }
    /**
     * @param x the x of the pixel
     * @param y the y of the pixel
     * @return the red value of the pixel
     */
    public int getRed(int x, int y) {
        return (img.getRGB(x, y) >> 16) & 0xFF;
    }
    /**
     * @param x the x of the pixel
     * @param y the y of the pixel
     * @return the green value of the pixel
     */
    public int getGreen(int x, int y) {
        return (img.getRGB(x, y) >> 8) & 0xFF;
    }
    /**
     * @param x the x of the pixel
     * @param y the y of the pixel
     * @return the blue value of the pixel
     */
    public int getBlue(int x, int y) {
        return (img.getRGB(x, y)) & 0xFF;
    }
    /**
     * @param x the x of the pixel
     * @param y the y of the pixel
     * @return the color of the pixel
     */
    public Color getColor(int x, int y) {
        return new Color((img.getRGB(x, y) >> 16) & 0xFF,(img.getRGB(x, y) >> 8) & 0xFF,(img.getRGB(x, y)) & 0xFF);
    }
    /**
     * @return the width of the GImage
     */
    public int getWidth() {
        return img.getWidth();
    }
    /**
     * @return the height of the GImage
     */
    public int getHeight() {
        return img.getHeight();
    }
    /**
     * Exports the GImage as a png
     * @param  file the GFile object of the new exported image
     */
    public void exportAsPNG(GFile file) {
        try {
            ImageIO.write(img,"PNG",file.getFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
