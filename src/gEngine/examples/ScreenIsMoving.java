package gEngine.examples;

import gEngine.GSetup;
import gEngine.Vec2D;

import java.awt.*;

public class ScreenIsMoving extends GSetup {

    Color background;
    Color backGroundLines;
    double x, y;
    double pointX,pointY;
    double countForShoot;
    int score;
    @Override
    public void initialize() {
        score = 0;
        countForShoot = 0;
        x = 500;
        y = 500;
        setFrameSize(600,600);
        setResizable(false);
        moveCanvas(300,100);
        background = new Color(20,20,40);
        backGroundLines = new Color(40,40,90);
        pointY = Math.random()*700;
        pointX = Math.random()*1300;
    }

    @Override
    public void execute() {
        fillRectangle(0,0,600,600,background);
        for(int i = -80; i < 1920; i+=40)
            fillRectangle(i-getCanvasX(),-40-getCanvasY(),5,1080,backGroundLines);
        for(int j = -80; j < 1080; j+=40)
            fillRectangle(-40-getCanvasX(),j-getCanvasY(),1920,5,backGroundLines);
        fillEllipse((int) (pointX-getCanvasX() - 10), (int) (pointY - getCanvasY() - 10),20,20,Color.RED);
//        fillEllipse((int) (x-getCanvasX())-25, (int) (y-getCanvasY())-25,50,50,new Color(10, 10, 20));
        fillEllipse((int) (300-25), (int) (300-25),50,50,new Color(10, 10, 20));

        Vec2D vecToMouse = new Vec2D(xOnScreen() - x, yOnScreen() - y);
        vecToMouse.normalize();
        double x1 = 20*vecToMouse.x;
        double y1 = 20* vecToMouse.y;

        if(leftClick() && (Math.sqrt((xOnScreen() - x)*(xOnScreen() - x) + (yOnScreen() - y)*(yOnScreen() - y)) > 10)) {
            x += 100*vecToMouse.x*deltaTime();
            y += 100*vecToMouse.y*deltaTime();
        }
        fillEllipse((int) (x1 + 300 - 3.5), (int) (y1 + y - getCanvasY()-3.5),7,7,new Color(10, 200, 20));
        drawLine((int) (300), (int) (300), (int) (300+x1), (int) (300+y1), new Color(10, 200, 20));

        if(Math.sqrt((x-pointX)*(x-pointX) + (y-pointY)*(y-pointY)) < 20) {
            pointY = Math.random()*700;
            pointX = Math.random()*1300;
            score++;
        }
        moveCanvas((int) (x-300), (int) (y-300));
        setTitle("score: " + String.valueOf(score));


//        System.out.println("(" + pointX + "," + pointY + ")");

    }

    @Override
    public boolean end() {
        return false;
    }
}
