package pack;

import java.awt.*;

public class GProgressBar implements GComponent{
    private int x,y,width,height;
    private double offset = 0;
    private int lines;
    private GImage img;
    private double progress = 0;
    private Color color;
    private Color backgroundColor;
    private double angle = 0;

    public GProgressBar(int x, int y, int width, int height) {
        img = new GImage(width,height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        lines = width/40;
        SetupManager.addTick(this);
        color = Color.RED;
        backgroundColor = new Color(255,255,255);
    }
    public GProgressBar(int x, int y, int width, int height, Color color) {
        img = new GImage(width,height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        lines = width/40;
        SetupManager.addTick(this);
        this.color = color;
        backgroundColor = new Color(255,255,255);
    }
    public GProgressBar(int x, int y, int width, int height, Color color,Color background) {
        img = new GImage(width,height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        lines = width/40;
        SetupManager.addTick(this);
        this.color = color;
        backgroundColor = background;
    }
    public void setAngle(double angle) {
        this.angle = angle;
        img.setAngle(angle);
    }
    public double getAngle() {
        return angle;
    }
    public void setProgress(double progress) {
        this.progress = progress;
        if (progress > 1) {
            this.progress = 1;
        }
        if(progress < 0) {
            this.progress = 0;
        }
    }
    public double getProgress() {
        return progress;
    }
    void draw(GSetup setup) {

        img.fillRectangle(0,0,width,height,Color.WHITE);

        img.fillRectangle(0,0, (int) (width*progress),height,color);
        img.fillRectangle((int) (width*progress),0, (int) (width),height,backgroundColor);

        for(int i = -1; i < lines + 1; i++) {
//            g.fillRect(x + (int) (offset + i*40), y,20,height);
            img.fillPolygon(new int[]{(int) (offset + i*40),(int) (offset + i*40) + 20,(int) (offset + i*40) + 0,(int) (offset + i*40) + -20},new int[]{0,0,height,height},new Color(0,0,0,50));
        }
        setup.drawImage(x,y,width,height,img);

//        g.setColor(Color.WHITE);
//        g.fillRect(x,y,width,height);
//        g.setColor(Color.GRAY);
//        for(int i = -1; i < lines - 1; i++) {
////            g.fillRect(x + (int) (offset + i*40), y,20,height);
//            g.fillPolygon(new int[]{x + (int) (offset + i*40),x + (int) (offset + i*40) + 20,x + (int) (offset + i*40) + 40,x + (int) (offset + i*40) + 20},new int[]{y,y,height + y,height + y},4);
//        }
    }
    @Override
    public void tickUpdate() {
        offset += 20*SetupManager.getInstance().getSetup().deltaTime();
        if(offset > 40) {
            offset -= 40;
        }
    }

    @Override
    public void draw(Graphics g) {

        img.fillRectangle(0,0,width,height,Color.WHITE);

        img.fillRectangle(0,0, (int) (width*progress),height,color);
        img.fillRectangle((int) (width*progress),0, (int) (width),height,backgroundColor);

        for(int i = -1; i < lines + 1; i++) {
//            g.fillRect(x + (int) (offset + i*40), y,20,height);
            img.fillPolygon(new int[]{(int) (offset + i*40),(int) (offset + i*40) + 20,(int) (offset + i*40) + 0,(int) (offset + i*40) + -20},new int[]{0,0,height,height},new Color(0,0,0,50));
        }
        g.drawImage(img.getImage(),x,y,width,height,null);

//        g.setColor(Color.WHITE);
//        g.fillRect(x,y,width,height);
//        g.setColor(color);
//        g.fillRect(0,0, (int) (width*progress),height);
//        g.setColor(backgroundColor);
//        g.fillRect((int) (width*progress),0, (int) (width),height);
//        g.setColor(Color.GRAY);
//        for(int i = -1; i < lines + 1; i++) {
////            g.fillRect(x + (int) (offset + i*40), y,20,height);
//            g.fillPolygon(new int[]{x + (int) (offset + i*40),x + (int) (offset + i*40) + 20,x + (int) (offset + i*40) + 40,x + (int) (offset + i*40) + 20},new int[]{y,y,height + y,height + y},4);
//        }
    }
}
