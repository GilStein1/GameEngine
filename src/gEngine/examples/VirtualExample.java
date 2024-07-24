package gEngine.examples;

import gEngine.SetupManager;
import gEngine.VirtualGSetup;

import java.awt.*;

public class VirtualExample extends VirtualGSetup {

//    double x = 0;
    int[] x;
    int[] y;

    public VirtualExample(int port) {
        super(port);
    }

    @Override
    public void initialize() {
        x = new int[]{(int)(Math.random()*600),(int)(Math.random()*600),(int)(Math.random()*600),(int)(Math.random()*600),(int)(Math.random()*600)};
        y = new int[]{(int)(Math.random()*600),(int)(Math.random()*600),(int)(Math.random()*600),(int)(Math.random()*600),(int)(Math.random()*600)};
    }

    @Override
    public void execute() {
        int x = xOnCanvas();
        int y = yOnCanvas();
        SetupManager.pushValueToPool(x, "x");
        SetupManager.pushValueToPool(y, "y");

        this.x[0] = x;
        this.y[0] = y;
//        fillEllipse(x,y,100,100, Color.BLUE);
//        drawText(x,y,"Hello World", Color.BLUE);
        fillPolygon(this.x,this.y,Color.RED);
//        System.out.println(new Color(0,(x/900)*255,(y/600)*255).getRGB());
//        drawLine((int)x,100,100,400, Color.BLUE);
//        x+=0.001;
    }

    @Override
    public boolean end() {
        return false;
    }
}
