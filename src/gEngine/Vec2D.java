package gEngine;

public class Vec2D {

    public double x;
    public double y;

    public Vec2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    public Vec2D(Vec2D ved2d) {
        this.x = ved2d.x;
        this.y = ved2d.y;
    }
    public void normalize() {
        double d = Math.sqrt(x*x + y*y);
        x /= d;
        y /= d;
    }
    public double calcAngle() {
        return Math.toDegrees(Math.atan2(y,x));
    }
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }

}
