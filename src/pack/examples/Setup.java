package pack.examples;

import pack.GFrameButton;
import pack.GFrameTextField;
import pack.GSetup;
import pack.GTextField;

import java.awt.*;

public class Setup extends GSetup {

    GTextField tf;
    GFrameTextField ftf;
    GFrameButton b;
    @Override
    public void initialize() {

        tf = new GTextField(20,20,200,50, Color.LIGHT_GRAY);
        addGTextField(tf);
        ftf = new GFrameTextField(400,400,200,50);
        addFrameTextField(ftf);
        b = new GFrameButton(220,20,50,50);
        addFrameButton(b);

    }

    @Override
    public void execute() {
        drawTextField(tf);
        if(tf.isActive()) {
            fillEllipse(100,100,100,100,Color.BLUE);
        }
        setTitle(ftf.getText());
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
