package gEngine.examples;

import gEngine.GImage;
import gEngine.GSetup;
import gEngine.Vec2D;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class Jello extends GSetup {
    ArrayList<Vec2D> points;
    ArrayList<Vec2D> accelerations;
    ArrayList<Vec2D> speeds;
    ArrayList<Vec2D> relativeToMouse;
    ArrayList<Spring> springs;
    boolean clickNoSpam;
    boolean isStillHeld;
    boolean eNoSpam;
    boolean isTransparent;
    final double floor = -200;
    final double mew = 0.5;

    @Override
    public void initialize() {

        setFrameIcon(new GImage(10,10));

        setTitle("left click to make a spring, right click to make wind, E to make transparent");

        clickNoSpam = true;
        isStillHeld = false;
        eNoSpam = true;
        isTransparent = false;

        points = new ArrayList<>();
        accelerations = new ArrayList<>();
        speeds = new ArrayList<>();
        relativeToMouse = new ArrayList<>();
        springs = new ArrayList<>();

        setFrameSize(900, 600);
        drawShapesFaster(true);

        generateCircle();
    }

    private void generateCircle() {

        double angle = 0;
        double radius = 80;
        double amount = 10;

        points.add(new Vec2D(0, 0));
        accelerations.add(new Vec2D(0, 0));
        speeds.add(new Vec2D(0, 0));
        relativeToMouse.add(new Vec2D(0, 0));

        for (int i = 0; i < amount; i++) {
            points.add(new Vec2D(radius * Math.cos(angle), radius * Math.sin(angle)));
            accelerations.add(new Vec2D(0, 0));
            speeds.add(new Vec2D(0, 0));
            relativeToMouse.add(new Vec2D(0, 0));
            angle += (2 * Math.PI) / amount;
        }

        for (int i = 0; i < points.size(); i++) {
            for (int j = i; j < points.size(); j++) {

                double distance = Math.sqrt((points.get(i).x - points.get(j).x) * (points.get(i).x - points.get(j).x) + (points.get(i).y - points.get(j).y) * (points.get(i).y - points.get(j).y));

                springs.add(new Spring(points.get(i), points.get(j), distance));

            }
            double distance = Math.sqrt((points.get(i).x - points.get(0).x) * (points.get(i).x - points.get(0).x) + (points.get(i).y - points.get(0).y) * (points.get(i).y - points.get(0).y));
            springs.add(new Spring(points.get(i), points.get(0), distance));
        }

        points.add(new Vec2D(xOnCanvas() - getFrameWidth() / 2.0, yOnCanvas() + 2 * yOnCanvas() - getFrameHeight() / 2));
        accelerations.add(new Vec2D(0, 0));
        speeds.add(new Vec2D(0, 0));
        relativeToMouse.add(new Vec2D(0, 0));
    }

    private void drawDots() {

        int radius = 1;

        for (Vec2D vec2D : points) {
            fillEllipse((int) (vec2D.x + getFrameWidth() / 2) - radius / 2, (int) (getFrameHeight() / 2 - vec2D.y) - radius / 2, radius, radius, Color.BLUE);
        }

        for (Spring s : springs) {
            drawLine((int) (s.getP1().x + getFrameWidth() / 2), (int) (getFrameHeight() / 2 - s.getP1().y), (int) (s.getP2().x + getFrameWidth() / 2), (int) (getFrameHeight() / 2 - s.getP2().y), new Color(50, 50, 255));
        }
    }

    private void fillShape() {
        Vec2D[] arr = new Vec2D[points.size() - 2];
        for (int i = 1; i < arr.length + 1; i++) {
            arr[i - 1] = new Vec2D((int) (points.get(i).x + getFrameWidth() / 2), (int) (getFrameHeight() / 2 - points.get(i).y));
        }
        fillPolygon(Color.BLUE, arr);
        if (isStillHeld) {
            drawLine(xOnCanvas(), yOnCanvas(), (int) (points.get(0).x + getFrameWidth() / 2), (int) (getFrameHeight() / 2 - points.get(0).y), Color.BLACK);
        }
    }

    private void physics() {

        double time = deltaTime() * 10;

        for (int i = 0; i < points.size(); i++) {
            accelerations.get(i).y = -9.8;
            accelerations.get(i).x = 0;
            if (rightClick()) {
                int x = (xOnCanvas() - getFrameWidth() / 2);
                int y = (getFrameHeight() / 2 - yOnCanvas());
                Vec2D p = points.get(i);
                double pow = 1/(Math.sqrt((x - p.x) * (x - p.x) + (y - p.y) * (y - p.y))) * -800;
                double angle = Math.atan2(y - p.y, x - p.x);
                accelerations.get(i).x += pow * Math.cos(angle);
                accelerations.get(i).y += pow * Math.sin(angle);
            }
        }

        for (Spring s : springs) {
            double distance = Math.sqrt((s.getP1().x - s.getP2().x) * (s.getP1().x - s.getP2().x) + (s.getP1().y - s.getP2().y) * (s.getP1().y - s.getP2().y));
            distance -= s.getL0();

            double angle = Math.atan2(s.getP1().y - s.getP2().y, s.getP1().x - s.getP2().x);

            accelerations.get(points.indexOf(s.getP2())).x += Math.cos(angle) * s.getK() * distance;
            accelerations.get(points.indexOf(s.getP2())).y += Math.sin(angle) * s.getK() * distance;

            accelerations.get(points.indexOf(s.getP1())).x -= Math.cos(angle) * s.getK() * distance;
            accelerations.get(points.indexOf(s.getP1())).y -= Math.sin(angle) * s.getK() * distance;
        }

        for (int i = 0; i < points.size() - 1; i++) {

            if(Math.abs(points.get(i).y - floor) < 5 && speeds.get(i).y < 0 && accelerations.get(i).y < 0) {
                points.get(i).y = floor;
                double normal = -accelerations.get(i).y;
                accelerations.get(i).x += normal*mew*(speeds.get(i).x<0? 1 : (speeds.get(i).x>0? -1 : 0));
                accelerations.get(i).y = 0;
            }

            speeds.get(i).x += accelerations.get(i).x * time;
            speeds.get(i).y += accelerations.get(i).y * time;

            if (points.get(i).y < floor && speeds.get(i).y < 0) {
                points.get(i).y = floor;
                speeds.get(i).y *= -0.7;
            } else if (points.get(i).y > getFrameHeight() / 2.0 && speeds.get(i).y > 0) {
                points.get(i).y = getFrameHeight() / 2.0;
                speeds.get(i).y *= -0.7;
            }

            if (points.get(i).x > getFrameWidth() / 2.0 && speeds.get(i).x > 0) {
                points.get(i).x = getFrameWidth() / 2.0;
                speeds.get(i).x *= -0.7;
            } else if (points.get(i).x < -getFrameWidth() / 2.0 && speeds.get(i).x < 0) {
                points.get(i).x = -getFrameWidth() / 2.0;
                speeds.get(i).x *= -0.7;
            }

            points.get(i).x += speeds.get(i).x * time;
            points.get(i).y += speeds.get(i).y * time;

            speeds.get(i).x *= (1 - time / 20);
            speeds.get(i).x *= ((Math.abs(speeds.get(i).x) < 0.002) ? 0 : 1);

            speeds.get(i).y *= (1 - time / 20);
            speeds.get(i).y *= ((Math.abs(speeds.get(i).y) < 0.002) ? 0 : 1);

        }
    }

    private void rotatePointer() {
        double x = 0;
        double y = 0;
        int count = 0;

        for (Vec2D p : points) {
            x += p.x;
            y += p.y;
            count++;
        }
        x /= count;
        y /= count;

        x = getFrameWidth() / 2.0 + x;
        y = getFrameHeight() / 2.0 - y;

        Vec2D dir = new Vec2D(x - xOnCanvas(), y - yOnCanvas());
        dir.normalize();

        final int length = 30;

        double px = xOnCanvas() + length * dir.x;
        double py = yOnCanvas() + length * dir.y;

        Vec2D dir2 = new Vec2D(dir.y, -dir.x);

        double px2 = xOnCanvas() + length / 2.0 * dir2.x;
        double py2 = yOnCanvas() + length / 2.0 * dir2.y;

        double px3 = xOnCanvas() - length / 2.0 * dir2.x;
        double py3 = yOnCanvas() - length / 2.0 * dir2.y;

        if (rightClick()) {
            drawPolygon(new int[]{(int) px3, (int) px2, (int) px}, new int[]{(int) py3, (int) py2, (int) py}, Color.BLACK);
        }
    }

    private void makeSpringWhenLeftClicked() {

        double x = xOnCanvas() - getFrameWidth() / 2.0;
        double y = -(yOnCanvas() - getFrameHeight() / 2.0);

        boolean isMouseOnShape = Math.abs(points.get(0).x-x) < 80 && Math.abs(points.get(0).y-y) < 80;

        if (leftClick() && clickNoSpam && isMouseOnShape) {
            clickNoSpam = false;
            isStillHeld = true;
            springs.add(new Spring(points.get(0), points.get(points.size() - 1), 0));
            springs.get(springs.size() - 1).setK(3);
        }
        if (!leftClick() && !clickNoSpam) {
            clickNoSpam = true;
            isStillHeld = false;
            springs.remove(springs.get(springs.size() - 1));
        }
    }

    private void updateMouseVector() {
        points.get(points.size() - 1).x = xOnCanvas() - getFrameWidth() / 2.0;
        points.get(points.size() - 1).y = -(yOnCanvas() - getFrameHeight() / 2.0);
    }
    private void draw() {
        if(isTransparent) {
            drawDots();
        }
        else {
            fillShape();
        }

        if(lastKey() == KeyEvent.VK_E && eNoSpam) {
            eNoSpam = false;
            isTransparent = !isTransparent;
        }
        else if(lastKey() == -1 && !eNoSpam) {
            eNoSpam = true;
        }

    }

    @Override
    public void execute() {
        updateMouseVector();
        rotatePointer();
        makeSpringWhenLeftClicked();
        drawLine(0, (int) (getFrameHeight() / 2 - floor), getFrameWidth(), (int) (getFrameHeight() / 2 - floor), Color.BLACK);
        draw();
        physics();
    }

    @Override
    public boolean end() {
        return false;
    }

    private static class Spring {

        Vec2D[] points;
        private double l0;
        private double k = 1.5;

        public Spring(Vec2D p1, Vec2D p2, double l0) {
            this.l0 = l0;
            this.points = new Vec2D[]{p1, p2};
        }

        public double getK() {
            return k;
        }

        public void setK(double newK) {
            this.k = newK;
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
}
