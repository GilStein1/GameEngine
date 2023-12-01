package pack;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Setup extends GSetup{
    GImage i;
    GButton b;
    GTextField t;
    @Override
    public void initialize() {
        t = new GTextField(0,500,200,40,Color.GRAY);
        i = new GImage(400,400);
        b = new GButton(0,0,200,200,new GImage("buttonExample.png"));
        addGButton(b);
        addGTextField(t);
    }

    @Override
    public void execute() {
        drawButton(b);
        drawTextField(t);
        if(b.isMouseHovering()) {
            fillEllipse(400,400,50,50,(b.isPressed()? Color.BLUE : Color.RED));
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
