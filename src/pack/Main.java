package pack;

import pack.examples.AngryFlappy;
import pack.examples.GameOfLife;

import java.awt.*;

public class Main extends GSetup{

    public static void main(String[] args) {
        new GameOfLife();
    }

    GTextView tv;
    GTextField tf;

    @Override
    public void initialize() {

        tv = new GTextView(100,100,200,200,"HEllo Yuval");
//        tv.showFrame(true);

        tf = new GTextField(400,400,200,40);

        addGTextField(tf);

        addGTextView(tv);

    }

    @Override
    public void execute() {
        drawTextField(tf);
        tv.setText(tf.getText());
    }

    @Override
    public void lastFunction() {
    }

    @Override
    public boolean end() {
        return false;
    }
}