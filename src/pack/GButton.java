package pack;

import java.awt.*;

public class GButton {

    private int x,y,width,height;
    private Color c;
    private Color c2;
    private GImage image;
    private GImage image2;
    private boolean mousePressed = false;
    private SetupManager setupManager;

    public GButton(int x, int y, int width, int height) {
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        c = Color.WHITE;
        c2 = new Color((int)(0.7*c.getRed()),(int)(0.7*c.getGreen()),(int)(0.7*c.getBlue()));
        setupManager.getSetup().addGButton(this);
    }
    public GButton(int x, int y, int width, int height, Color color) {
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        c = color;
        c2 = new Color((int)(0.7*c.getRed()),(int)(0.7*c.getGreen()),(int)(0.7*c.getBlue()));
        setupManager.getSetup().addGButton(this);
    }
    public GButton(int x, int y, int width, int height,GImage image) {
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = new GImage(image);
        this.image2 = new GImage(image);
        image2.fillRectangle(0,0,image2.getImage().getWidth(),image2.getImage().getHeight(),new Color(0,0,0,76));
        setupManager.getSetup().addGButton(this);
    }

    void setPressed(boolean is) {
        mousePressed = is;
    }
    public boolean isPressed() {
        return mousePressed && setupManager.getSetup().xOnCanvas() > this.x && setupManager.getSetup().yOnCanvas() > this.y && setupManager.getSetup().xOnCanvas() < this.x + this.width && setupManager.getSetup().yOnCanvas() < this.y + this.height;
    }
    public GImage getGImage() {
        return new GImage(image);
    }
    public void setGImage(GImage newImg) {
        image = new GImage(newImg);
        image2 = new GImage(newImg);
        image2.fillRectangle(0,0,image2.getImage().getWidth(),image2.getImage().getHeight(),new Color(0,0,0,76));
    }

    void draw(Graphics g, int x, int y) {

        boolean isOn = x > this.x && y > this.y && x < width + this.x && y < height + this.y;

        if(c != null) {
            g.setColor(isOn? c2 : c);
            g.fillRect(this.x,this.y,width,height);
        }
        else {
            g.drawImage((isOn? image2.getImage(): image.getImage()),this.x,this.y,width,height,null);
        }

    }

}
