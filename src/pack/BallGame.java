package pack;

import java.awt.*;

public class BallGame extends GSetup {

    double vx = 0;
    double vy = 0;
    double ax = 0;
    double ay = 0;
    double a = 60;
    double x = 400;
    double y = 400;

    @Override
    public void initialize() {
        setResizable(false);
        setTitle("Ball");
        setFrameIcon("circle.png");
        setFrameSize(908,607);
//        SetupManager.Debug.fpsGraph();
    }

    @Override
    public void execute() {

//        for(int i = 0; i < 10000; i++) {
//            System.out.println(i);
//        }
//
//        setTitle(Double.toString(currentFPS()));

        if(deltaTime() < 10) {
            fillEllipse((int)x - 50,(int)y - 50,100,100,Color.blue);
            ax = a*(xOnCanvas() - x)/(Math.sqrt((xOnCanvas() - x)*(xOnCanvas() - x) + (yOnCanvas() - y)*(yOnCanvas() - y)));
            ay = a*(yOnCanvas() - y)/(Math.sqrt((xOnCanvas() - x)*(xOnCanvas() - x) + (yOnCanvas() - y)*(yOnCanvas() - y)));

            vx = vx + ax*deltaTime();
            vy = vy + ay*deltaTime();

            x = x + vx*deltaTime();
            y = y + vy*deltaTime();

            if(y > 520) {
                y = 520;
                vy = -vy;
            }
            else if(y < 50) {
                y = 50;
                vy = -vy;
            }

            if(x > 845) {
                x = 845;
                vx = -vx;
            }
            else if(x < 50) {
                x = 50;
                vx = -vx;
            }
        }

        GImage gi = new GImage(getImg().getWidth(),getImg().getHeight());
        gi.fillRectangle(0,0, getImg().getWidth(), getImg().getHeight(), Color.WHITE);
        gi.drawImage(-getImg().getWidth()/2,-getImg().getHeight()/2, 2*getImg().getWidth(), 2*getImg().getHeight(),new GImage(getImg()));

        setFrameIcon(gi);

    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
