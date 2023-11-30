package pack.examples;

import pack.GImage;
import pack.GSetup;

import java.awt.*;

public class SierpinskiTriangle extends GSetup {
    @Override
    public void initialize() {
        setFrameSize(900,900);
        setTitle("Sierpiński Triangle");
        setResizable(true);
    }

    public void triangle(int[] c1, int[] c2, int[] c3) {


        if(Math.sqrt(Math.pow(c1[0] - c2[0],2) + Math.pow(c1[1] - c2[1],2)) > 2) {

            int[] c4 = {(c1[0] + c2[0])/2,(c1[1] + c2[1])/2};
            int[] c5 = {(c3[0] + c2[0])/2,(c3[1] + c2[1])/2};
            int[] c6 = {(c3[0] + c1[0])/2,(c3[1] + c1[1])/2};

            triangle(c1,c4,c6);
            triangle(c4,c2,c5);
            triangle(c6,c5,c3);

            drawLine(c1[0],c1[1],c2[0],c2[1], Color.BLUE);
            drawLine(c3[0],c3[1],c2[0],c2[1], Color.BLUE);
            drawLine(c1[0],c1[1],c3[0],c3[1], Color.BLUE);

        }

    }

    @Override
    public void execute() {
        triangle(new int[]{445,100},new int[]{145,619},new int[]{745,619});

        GImage gi = new GImage(getBufferedImg().getWidth(), getBufferedImg().getHeight());
        gi.fillRectangle(0,0, getBufferedImg().getWidth(), getBufferedImg().getHeight(), Color.WHITE);
        gi.drawImage(0,0, getBufferedImg().getWidth(), getBufferedImg().getHeight(),new GImage(getBufferedImg()));

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
