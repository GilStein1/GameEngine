package gEngine;

import java.awt.*;

public class GButton implements GComponent {

    private int x,y,width,height;
    private Color c;
    private Color c2;
    private Color c3;
    private GImage image;
    private GImage image2;
    private GImage image3;
    private boolean mousePressed = false;
    private SetupManager setupManager;

    public GButton(int x, int y, int width, int height) {
        SetupManager.addTick(this);
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        c = Color.WHITE;
        c3 = new Color((int)(0.7*c.getRed()),(int)(0.7*c.getGreen()),(int)(0.7*c.getBlue()));
        c2 = new Color((int)(0.9*c.getRed()),(int)(0.9*c.getGreen()),(int)(0.9*c.getBlue()));
        setupManager.getSetup().addGButton(this);
    }
    public GButton(int x, int y, int width, int height, Color color) {
        SetupManager.addTick(this);
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        c = color;
        c3 = new Color((int)(0.7*c.getRed()),(int)(0.7*c.getGreen()),(int)(0.7*c.getBlue()));
        c2 = new Color((int)(0.9*c.getRed()),(int)(0.9*c.getGreen()),(int)(0.9*c.getBlue()));
        setupManager.getSetup().addGButton(this);
    }
    public GButton(int x, int y, int width, int height,GImage image) {
        SetupManager.addTick(this);
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = new GImage(image);
        this.image2 = new GImage(image);
        this.image3 = new GImage(image);
        image3.fillRectangle(0,0,image2.getImage().getWidth(),image2.getImage().getHeight(),new Color(0,0,0,76));
        image2.fillRectangle(0,0,image2.getImage().getWidth(),image2.getImage().getHeight(),new Color(0,0,0,25));
        setupManager.getSetup().addGButton(this);
    }
    public void setX(int x) {
        this.x = x;
    }
    public void setY(int y) {
        this.y = y;
    }
    public void setWidth(int width) {
        this.width = width;
    }
    public void setHeight(int height) {
        this.height = height;
    }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void setDimensions(int width, int height) {
        this.width = width;
        this.height = height;
    }
    public void setBounds(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    void setPressed(boolean is) {
        mousePressed = is;
    }
    /**
     * @return if the button is pressed
     */
    public boolean isPressed() {
        return mousePressed && setupManager.getSetup().xOnCanvas() > this.x && setupManager.getSetup().yOnCanvas() > this.y && setupManager.getSetup().xOnCanvas() < this.x + this.width && setupManager.getSetup().yOnCanvas() < this.y + this.height;
    }
    /**
     * @return the GImage of the button
     */
    public GImage getGImage() {
        return new GImage(image);
    }
    /**
     * Sets a new GImage to the button
     */
    public void setGImage(GImage newImg) {
        image = new GImage(newImg);
        image2 = new GImage(newImg);
        image3 = new GImage(newImg);
        image3.fillRectangle(0,0,image2.getImage().getWidth(),image2.getImage().getHeight(),new Color(0,0,0,76));
        image2.fillRectangle(0,0,image2.getImage().getWidth(),image2.getImage().getHeight(),new Color(0,0,0,25));
    }
    /**
     * @return if the cursor is hovering over the button
     */
    public boolean isMouseHovering() {

        int x = SetupManager.getInstance().getSetup().xOnCanvas();
        int y = SetupManager.getInstance().getSetup().yOnCanvas();

        return x > this.x && y > this.y && x < width + this.x && y < height + this.y;
    }

    public void draw(Graphics g) {

        int x = SetupManager.getInstance().getSetup().xOnCanvas();
        int y = SetupManager.getInstance().getSetup().yOnCanvas();

        boolean isOn = x > this.x && y > this.y && x < width + this.x && y < height + this.y;

        if(c != null) {
            g.setColor(isOn? (isPressed()? c3 : c2) : c);
            g.fillRect(this.x,this.y,width,height);
        }
        else {
            g.drawImage((isOn? (isPressed()? image3.getImage() : image2.getImage()): image.getImage()),this.x,this.y,width,height,null);
        }

    }

    @Override
    public void tickUpdate() {

    }
}
