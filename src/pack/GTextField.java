package pack;

import java.awt.*;
import java.awt.event.KeyEvent;

public class GTextField implements Component{

    private int x,y,width,height;
    private Color background;
    private boolean mousePressed = false;
    private boolean isActive = false;
    private SetupManager setupManager;
    private GImage img;
    private String text = "";
    private double timeCountForAnimation = 0;

    public GTextField(int x, int y, int width, int height) {
        SetupManager.addTick(this);
        img = new GImage(width,height);
        img.setFont(new Font("ariel",Font.PLAIN,20));
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    public GTextField(int x, int y, int width, int height, Color background) {
        SetupManager.addTick(this);
        this.background = background;
        img = new GImage(width,height);
        img.setFont(new Font("ariel",Font.PLAIN,20));
        setupManager = SetupManager.getInstance();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        img.fillRectangle(0,0,img.getImage().getWidth(),img.getImage().getHeight(),background);
    }

    private boolean isPressed() {
        return  setupManager.getSetup().xOnCanvas() > this.x && setupManager.getSetup().yOnCanvas() > this.y && setupManager.getSetup().xOnCanvas() < this.x + this.width && setupManager.getSetup().yOnCanvas() < this.y + this.height;
    }

    void typed(KeyEvent e,boolean del) {

        if(isActive) {
            if(!del) {

                text = text + e.getKeyChar();
//                System.out.println("yy");
            }
            else {
                String s = "";
                for(int i = 0; i < text.length() - 1; i++) {
                    s = s + text.charAt(i);
                }
                text = s;
            }
        }

//        img = new GImage(width,height);
        if(background == null) {
            img.drawText(10,20,text + (((int)timeCountForAnimation)%2 == 0 ? "|" : ""),Color.BLACK);
        }
        else {
            img.fillRectangle(0,0,img.getImage().getWidth(),img.getImage().getHeight(),background);
            img.drawText(0,20,text + (((int)timeCountForAnimation)%2 == 0 ? "|" : ""),Color.BLACK);
        }

    }

    void setPressed(boolean is) {

        if(is && !mousePressed && isPressed()) {
            isActive = true;
        }
        else if(is && !mousePressed && !isPressed()) {
            isActive = false;
        }

        mousePressed = is;
    }
    void draw(Graphics g) {
        g.drawImage(img.getImage(),x,y,width,height,null);
    }
    public String getText() {
        return text;
    }

    @Override
    public void tickUpdate() {
        timeCountForAnimation += 2*SetupManager.getInstance().getSetup().deltaTime();
        if(timeCountForAnimation > 4) {
            timeCountForAnimation = 0;
        }
        if(background == null) {
            img.drawText(10,20,text + (((int)timeCountForAnimation)%2 == 0 && isActive ? "|" : ""),Color.BLACK);
        }
        else {
            img.fillRectangle(0,0,img.getImage().getWidth(),img.getImage().getHeight(),background);
            img.drawText(0,20,text + (((int)timeCountForAnimation)%2 == 0 && isActive ? "|" : ""),Color.BLACK);
        }

    }
}
