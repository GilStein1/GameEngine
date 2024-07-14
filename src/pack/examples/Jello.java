package pack.examples;

import pack.GSetup;
import pack.SetupManager;
import pack.Vec2D;
import java.awt.*;
import java.util.ArrayList;

public class Jello extends GSetup {

    ArrayList<Vec2D> points;
    ArrayList<Vec2D> accelerations;
    ArrayList<Vec2D> speeds;
    ArrayList<Vec2D> relativeToMouse;
    ArrayList<Spring> springs;
    boolean clickNoSpam;
    boolean isStillHeld;

    final double floor = -200;

    @Override
    public void initialize() {

        clickNoSpam = true;
        isStillHeld = false;

        points = new ArrayList<>();
        accelerations = new ArrayList<>();
        speeds = new ArrayList<>();
        relativeToMouse = new ArrayList<>();
        springs = new ArrayList<>();

        setFrameSize(900,600);
        loadShapesFaster(true);

        generateCircle();

    }

    private void generateCircle() {

        double angle = 0;
        double radius = 80;
        double amount = 20;

        for(int i = 0; i < amount; i++) {
            points.add(new Vec2D(radius*Math.cos(angle), radius*Math.sin(angle)));
            accelerations.add(new Vec2D(0,0));
            speeds.add(new Vec2D(0,0));
            relativeToMouse.add(new Vec2D(0,0));
            angle += (2*Math.PI)/amount;
        }

        for(int i = 0; i < points.size(); i++) {
            for(int j = i; j < points.size(); j++) {

                double distance = Math.sqrt((points.get(i).x - points.get(j).x)*(points.get(i).x - points.get(j).x) + (points.get(i).y - points.get(j).y)*(points.get(i).y - points.get(j).y));

                springs.add(new Spring(points.get(i), points.get(j), distance));

            }
        }

    }

    private void drawDots() {

        int radius = 1;

        for(Vec2D vec2D : points) {
            fillEllipse((int)(vec2D.x + getFrameWidth()/2) - radius/2, (int)(getFrameHeight()/2 - vec2D.y) - radius/2, radius, radius, Color.BLUE);
        }

        for(Spring s : springs) {
            drawLine((int)(s.getP1().x + getFrameWidth()/2), (int)(getFrameHeight()/2 - s.getP1().y), (int)(s.getP2().x + getFrameWidth()/2), (int)(getFrameHeight()/2 - s.getP2().y), new Color(50,50,255));
        }
    }

    private void fillShape() {
        Vec2D[] arr = new Vec2D[points.size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = new Vec2D((int)(points.get(i).x + getFrameWidth()/2),(int)(getFrameHeight()/2 - points.get(i).y));
        }
        fillPolygon(Color.BLUE, arr);
    }

    private void physics() {

        double time = deltaTime()*10;

//        time = Math.min(0.09, time);

        for(int i = 0; i < points.size(); i++) {
            accelerations.get(i).y = -9.8;
            accelerations.get(i).x = 0;
            if(rightClick()) {
                int x = (xOnCanvas() - getFrameWidth()/2);
                int y = (getFrameHeight()/2 - yOnCanvas());
//                int x = xOnCanvas();
//                int y = yOnCanvas();
                Vec2D p = points.get(i);
                double pow = (Math.sqrt((x - p.x)*(x - p.x) + (y - p.y)*(y - p.y)))*-0.04;
                double angle = Math.atan2(y - p.y, x - p.x);
//                System.out.println(y);
//                System.out.println(Math.toDegrees(angle));
                accelerations.get(i).x += pow*Math.cos(angle);
                accelerations.get(i).y += pow*Math.sin(angle);
            }
        }

        for(Spring s : springs) {
            double distance = Math.sqrt((s.getP1().x - s.getP2().x)*(s.getP1().x - s.getP2().x) + (s.getP1().y - s.getP2().y)*(s.getP1().y - s.getP2().y));
            distance -= s.getL0();

            double angle = Math.atan2(s.getP1().y - s.getP2().y ,s.getP1().x - s.getP2().x);

            accelerations.get(points.indexOf(s.getP2())).x += Math.cos(angle)*s.getK()*distance;
            accelerations.get(points.indexOf(s.getP2())).y += Math.sin(angle)*s.getK()*distance;

            accelerations.get(points.indexOf(s.getP1())).x -= Math.cos(angle)*s.getK()*distance;
            accelerations.get(points.indexOf(s.getP1())).y -= Math.sin(angle)*s.getK()*distance;
        }

        for(int i = 0; i < points.size(); i++) {
            speeds.get(i).x += accelerations.get(i).x*time;
            speeds.get(i).y += accelerations.get(i).y*time;

            if(points.get(i).y < floor && speeds.get(i).y < 0) {
                points.get(i).y = floor;
                speeds.get(i).y *= -0.7;
            }

            if(isStillHeld) {
                accelerations.get(i).x = 0;
                accelerations.get(i).y = 0;
                speeds.get(i).x = 0;
                speeds.get(i).y = 0;
            }

            points.get(i).x += speeds.get(i).x*time;
            points.get(i).y += speeds.get(i).y*time;

            speeds.get(i).x += ((speeds.get(i).x > 0)? -1 : 1)*0.05*time;
            speeds.get(i).x *= ((Math.abs(speeds.get(i).x) < 0.002)? 0 : 1);

            speeds.get(i).y += ((speeds.get(i).y > 0)? -1 : 1)*0.05*time;
            speeds.get(i).y *= ((Math.abs(speeds.get(i).y) < 0.002)? 0 : 1);

        }
    }

    @Override
    public void execute() {

//        for(int i = -1000; i < 1000; i+=20) {
//            drawLine(0, (int)(getFrameHeight()/2 - i), getFrameWidth(), (int)(getFrameHeight()/2 - i), new Color(0,0,0,100));
//        }
//        for(int i = -1000; i < 1000; i+=20) {
//            drawLine(i+getFrameWidth()/2, 0, i+getFrameWidth()/2, getFrameHeight(), new Color(0,0,0,100));
//        }

        if(leftClick() && clickNoSpam) {
            clickNoSpam = false;
            isStillHeld = true;
            for(int i = 0; i < points.size(); i++) {
                relativeToMouse.get(i).x = points.get(i).x - (xOnCanvas() + getFrameWidth()/2);
                relativeToMouse.get(i).y = points.get(i).y - (getFrameHeight()/2 - yOnCanvas());
            }
        }
        if(!leftClick() && !clickNoSpam) {
            clickNoSpam = true;
            isStillHeld = false;
        }
        if(isStillHeld) {
            for(int i = 0; i < points.size(); i++) {
                points.get(i).x = (xOnCanvas() + getFrameWidth()/2) + relativeToMouse.get(i).x;
                points.get(i).y = (getFrameHeight()/2 - yOnCanvas()) + relativeToMouse.get(i).y;
            }
        }

        drawLine(0, (int)(getFrameHeight()/2 - floor), getFrameWidth(), (int)(getFrameHeight()/2 - floor), Color.BLACK);

        fillShape();
//        drawDots();

        physics();

    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }

    private static class Spring {

        Vec2D[] points;
        private double l0;
        private final double k = 3;

        public Spring(Vec2D p1, Vec2D p2, double l0) {
            this.l0 = l0;
            this.points = new Vec2D[]{p1,p2};
        }

        public double getK() {
            return k;
        }

        public Vec2D getP1() {
            return points[0];
        }

        public Vec2D getP2() {
            return points[1];
        }

        public double getL0() {
            return l0;
        }

        public void setL0(double l0) {
            this.l0 = l0;
        }
    }
    public static void main(String[] args) {
        SetupManager.startGame(Jello.class);
    }

}
