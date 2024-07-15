package pack.examples;

import pack.GImage;
import pack.GSetup;
import pack.Vec2D;
import java.awt.*;

public class AveragePolygonColor extends GSetup {

    Vec2D p1;
    Vec2D p2;
    Vec2D p3;
    int max;
    double[][] arr;
    GImage img;
    @Override
    public void initialize() {

        p1 = new Vec2D(100.1,90.1);
        p2 = new Vec2D(350.1,100.1);
        p3 = new Vec2D(120.1,390.1);

        arr = new double[450*2][300*2];
        max = 0;
        img = new GImage("House.png");

    }

    @Override
    public void execute() {

        p1.x = Math.random()*450*2;
        p1.y = Math.random()*300*2;

        p2.x = Math.random()*450*2;
        p2.y = Math.random()*300*2;

        p3.x = Math.random()*450*2;
        p3.y = Math.random()*300*2;

        for(int i = 0; i < 450*2; i++) {
            for(int j = 0; j < 300*2; j++) {
                if(!isInPolygon(i,j,p1,p2,p3)) {
                    arr[i][j]++;
                    max = (int) Math.max(max,arr[i][j]);
                }
            }
        }
        for(int i = 0; i < 450*2; i++) {
            for(int j = 0; j < 300*2; j++) {

                fillRectangle(i*1,j*1,1,1,new Color((int)(arr[i][j]*(img.getRed((i)*img.getWidth()/(450*2),(j)*img.getHeight()/(300*2)))/max),(int)(arr[i][j]*(img.getGreen((i)*img.getWidth()/(450*2),(j)*img.getHeight()/(300*2)))/max),(int)(arr[i][j]*(img.getBlue((i)*img.getWidth()/(450*2),(j)*img.getHeight()/(300*2)))/max)));
            }
        }
    }

    public boolean isInPolygon(int x, int y, Vec2D point1, Vec2D point2, Vec2D point3) {

        double m1 = (point1.y - point2.y)/(point1.x - point2.x);
        double m2 = (point2.y - point3.y)/(point2.x - point3.x);
        double m3 = (point3.y - point1.y)/(point3.x - point1.x);

        double b1 = point1.y - m1* point1.x;
        double b2 = point2.y - m2* point2.x;
        double b3 = point3.y - m3* point3.x;

        int count = 0;

        if((y-b1)/m1 > x && (y-b1)/m1 < Math.max(point1.x,point2.x) && y > Math.min(point1.y,point2.y) && y < Math.max(point1.y,point2.y)) {
            count++;
        }
        if((y-b2)/m2 > x && (y-b2)/m2 < Math.max(point2.x,point3.x) && y > Math.min(point2.y,point3.y) && y < Math.max(point2.y,point3.y)) {
            count++;
        }
        if((y-b3)/m3 > x && (y-b3)/m3 < Math.max(point1.x,point3.x) && y > Math.min(point1.y,point3.y) && y < Math.max(point1.y,point3.y)) {
            count++;
        }
        return count%2 == 0;
    }

    @Override
    public boolean end() {
        return false;
    }
}
