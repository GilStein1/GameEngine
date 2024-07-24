package gEngine.examples;

import gEngine.SetupManager;
import gEngine.VirtualGSetup;

import java.awt.*;

public class VirtualExample extends VirtualGSetup {

    double x = 0;

    public VirtualExample(int port) {
        super(port);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        int x = xOnCanvas();
        int y = yOnCanvas();
        SetupManager.pushValueToPool(x, "x");
        SetupManager.pushValueToPool(y, "y");
//        fillEllipse(x,y,100,100, Color.BLUE);
        drawText(x,y,"Hello World", Color.BLUE);
//        System.out.println(new Color(0,(x/900)*255,(y/600)*255).getRGB());
//        drawLine((int)x,100,100,400, Color.BLUE);
//        x+=0.001;
    }

    @Override
    public boolean end() {
        return false;
    }
}
