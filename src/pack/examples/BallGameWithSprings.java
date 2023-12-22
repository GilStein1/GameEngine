package pack.examples;

import pack.GButton;
import pack.GImage;
import pack.GSetup;
import pack.Vec2D;

import javax.swing.*;
import java.awt.*;

public class BallGameWithSprings extends GSetup {

    Vec2D pos;
    Vec2D v;
    Vec2D a;
    double l0 = 0;
    double k = 1;
    boolean pressed = false;
    boolean clicked = false;
    boolean bPressed = false;
    GImage back;
    GButton b;
    Color c;

    @Override
    public void initialize() {

        setFrameSize(900,900);
        setResizable(false);
//        b = new GButton(400,400,100,100,Color.GREEN);
//        addGButton(b);
        c = Color.BLACK;

        pos = new Vec2D(450,450);
        v = new Vec2D(0,0);
        a = new Vec2D(0,0);
        back = new GImage(900,900);
        back.fillRectangle(0,0,900,900,new Color(247,247,247));

//        SetupManager.Debug.fpsGraph();
    }

    @Override
    public void execute() {

        if(!pressed && leftClick()) {
            pressed = true;
            if(xOnCanvas() > pos.x - 50 && xOnCanvas() < pos.x - 50 + 100 && yOnCanvas() > pos.y - 50 && yOnCanvas() < pos.y - 50 + 100) {
                clicked = true;
            }
        }
        else if(!leftClick()) {
            pressed = false;
            clicked = false;
        }

        double A = (clicked)? (k * (Math.sqrt((xOnCanvas() - pos.x)*(xOnCanvas() - pos.x) + (yOnCanvas() - pos.y)*(yOnCanvas() - pos.y)) - l0)) : 0;

        Vec2D normalpos = new Vec2D(xOnCanvas() - pos.x,yOnCanvas() - pos.y);
        normalpos.normalize();

        a.x = normalpos.x * A;
        a.y = normalpos.y * A + 200;


        v.x = v.x + a.x * deltaTime();
        v.y = v.y + a.y * deltaTime();

        v.x -= 0.1*v.x*deltaTime();
        v.y -= 0.1*v.y*deltaTime();

        v.x = (pos.x - 50 > 0)? v.x : -v.x;
        v.y = (pos.y - 50 > 0)? v.y : -v.y;

        v.x = (pos.x - 50 < 784)? v.x : -v.x;
        v.y = (pos.y - 50 < 764)? v.y : -v.y;

        pos.x = (pos.x - 50 < 0)? 50 : pos.x;
        pos.x = (pos.x - 50 > 784)? 784 + 50 : pos.x;

        pos.y = (pos.y - 50 < 0)? 50 : pos.y;
        pos.y = (pos.y - 50 > 764)? 764 + 50 : pos.y;

        pos.x += v.x * deltaTime();
        pos.y += v.y * deltaTime();

//        back = new GImage(getGImage());
//        back.drawImage(0,0,900,900,getGImage());
//        back.fillRectangle(0,0, 900,900,new Color(255,255,255,10));

//        if(b.isPressed() && !bPressed) {
//            bPressed = true;
//
//            JColorChooser jc = new JColorChooser();
//
//            c = JColorChooser.showDialog(jc,"Choose Color",Color.BLACK);
//
//        }
//        if(!b.isPressed()) {
//            bPressed = false;
//        }

        drawImage(0,0,900,900,back);

        setFrameIcon(back);

//        drawButton(b);

        fillEllipse((int) pos.x - 50, (int) pos.y - 50,100,100, c);

        if(clicked) {
            drawLine((int) pos.x, (int) pos.y,xOnCanvas(),yOnCanvas(),Color.BLACK);
        }

        back = new GImage(back);
        back.fillEllipse((int) pos.x - 50, (int) pos.y - 50,100,100, c);
        back.fillRectangle(0,0, 900,900,new Color(255,255,255,15));
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
