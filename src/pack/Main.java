package pack;

import pack.examples.Test;

import java.awt.*;

public class Main extends GSetup{
    public static void main(String[] args) {
        SetupManager.startGame(Main.class);
    }
    GButton b;
    GTextField tf;
    GProgressBar pr;

    @Override
    public void initialize() {
        pr = new GProgressBar(0,100,200,50,Color.GREEN);
        pr.setAngle(45);
        b = new GButton(100,100,100,100, Color.GREEN);
        tf = new GTextField(400,400,200,40,Color.GRAY);
        addGTextField(tf);
    }

    @Override
    public void execute() {

        pr.setProgress(xOnCanvas()/200.0);
        drawTextField(tf);
        drawButton(b);
        drawGProgressBar(pr);
        if(b.isPressed()) {
            System.out.println(SetupManager.pushValueToPool(tf.getText(),"val"));
            SetupManager.moveToSetup(Test.class);
        }
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}