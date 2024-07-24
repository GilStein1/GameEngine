package gEngine.examples;

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
        fillEllipse((int)x,100,100,100, Color.BLUE);
        x+=0.001;
    }

    @Override
    public boolean end() {
        return false;
    }
}
