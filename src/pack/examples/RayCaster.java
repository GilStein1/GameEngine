package pack.examples;

import pack.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class RayCaster extends GSetup {
    Vec2D[] points;
    Vec2D[] entities;
    GPanel panel;
    GImage img;
    GImage imageToScale;
    GImage texture;
    GImage entityTexture;
    GImage render;
    Vec2D light;
    double rot;
    int screenWidth;
    int screenHeight;

    @Override
    public void initialize() {
        entityTexture = new GImage("Bird With Shadow.png");
        texture = new GImage("BrickWall.png");
        render = new GImage(1, texture.getHeight());
        screenWidth = 600;
        screenHeight = 600;
        imageToScale = new GImage(screenWidth,screenHeight);
        light = new Vec2D(45 + screenWidth/2.0,45 + screenHeight/2.0);
        img = new GImage(screenWidth,screenHeight);
        panel = new GPanel(600,0,600,600,"RayCasting View",img);
        addGPanel(panel);
        panel.setVisible(true);
        rot = 0.01;
        setFrameSize(screenWidth,screenHeight);
        setResizable(false);
        setBackground(Color.BLACK);

        setSmoothness(Smoothness.VERY_SMOOTH);

        img.setSmoothness(Smoothness.VERY_SMOOTH);
        render.setSmoothness(Smoothness.VERY_SMOOTH);

        points = new Vec2D[200];
        entities = new Vec2D[4];

        for(int i = 0; i < entities.length; i++) {
            entities[i] = new Vec2D((int)(Math.random()*10)*60 + 30,(int)(Math.random()*10)*60 + 30);
        }



        for(int i = 0; i < points.length-0; i+=2) {
            int x = (int)(Math.random()*10);
            int y = (int)(Math.random()*10);
            if(Math.random() > 0.5) {
                points[i] = new Vec2D(x*60,y*60);
                points[i + 1] = new Vec2D(x*60 + 60,y*60 + 0.001);
            }
            else {
                points[i + 1] = new Vec2D(x*60 + 60.003,y*60 + 60.003);
                points[i] = new Vec2D(x*60 + 60,y*60 + 0.001);
            }
        }
//        for(int i = 0; i < 40; i+=8) {
//            points[i+points.length-40] = new Vec2D(i*60,0.01);
//            points[i+1+points.length-40] = new Vec2D((i+1)*60,0);
//            points[i+2+points.length-40] = new Vec2D((i+2)*60,600.01);
//            points[i+3+points.length-40] = new Vec2D((i+3)*60,600);
//
//            points[i+4+points.length-40] = new Vec2D(0.01,(i+4)*60);
//            points[i+5+points.length-40] = new Vec2D(0,(i+5)*60);
//            points[i+6+points.length-40] = new Vec2D(600.01,(i+6)*60);
//            points[i+7+points.length-40] = new Vec2D(600,(i+7)*60);
//        }
    }
    @Override
    public void execute() {

        img.fillRectangle(0,0,screenWidth,screenHeight,Color.BLACK);
        imageToScale.fillRectangle(0,0, imageToScale.getWidth(), imageToScale.getHeight()/2,new Color(0, 0, 171));
        imageToScale.fillRectangle(0,imageToScale.getWidth()/2,getFrameWidth(), imageToScale.getHeight()/2,new Color(8, 72, 0));

        if(lastKey() == KeyEvent.VK_RIGHT) {
            rot += 70*deltaTime();
        }
        else if(lastKey() == KeyEvent.VK_LEFT) {
            rot -= 70*deltaTime();
        }
        else if(lastKey() == KeyEvent.VK_W) {
            light.x += Math.cos(Math.toRadians(rot + 30))*90*deltaTime();
            light.y += Math.sin(Math.toRadians(rot + 30))*90*deltaTime();
        }
        else if(lastKey() == KeyEvent.VK_S) {
            light.x -= Math.cos(Math.toRadians(rot + 30))*90*deltaTime();
            light.y -= Math.sin(Math.toRadians(rot + 30))*90*deltaTime();
        }
        else if(lastKey() == KeyEvent.VK_D) {
            light.x -= Math.sin(Math.toRadians(rot + 30))*90*deltaTime();
            light.y += Math.cos(Math.toRadians(rot + 30))*90*deltaTime();
        }
        else if(lastKey() == KeyEvent.VK_A) {
            light.x += Math.sin(Math.toRadians(rot + 30))*90*deltaTime();
            light.y -= Math.cos(Math.toRadians(rot + 30))*90*deltaTime();
        }
        if(rot > 360) {
            rot = rot - 360;
        }
        for(int i = 0; i < points.length; i+=2) {
            img.drawLine((int) points[i].x, (int) points[i].y, (int) points[i+1].x, (int) points[i+1].y,Color.WHITE);
        }
        double rot2 = Math.toRadians(rot);

        ArrayList<Vec2D> depth = new ArrayList<>();
        ArrayList<Double> uv = new ArrayList<>();
        ArrayList<Vec2D> depthEn = new ArrayList<>();
        ArrayList<Double> uvEn = new ArrayList<>();
        ArrayList<Boolean> hasTexture = new ArrayList<>();

        double minUV = 0;
        double minUVEn = 0;

        int count = 0;

        for(double j = rot2; j < (Math.PI/3.0) + rot2; j+= 0.002) {
            Vec2D point = new Vec2D(1000*Math.cos(j) + light.x,1000*Math.sin(j) + light.y);
            point.x = 1000*Math.cos(j) + light.x;
            point.y = 1000*Math.sin(j) + light.y;
            Vec2D min = new Vec2D(10000000,10000000);
            Vec2D minEn = new Vec2D(10000000,10000000);
            for(int i = 0; i < points.length; i+=2) {
                Vec2D intersection = calcIntersection(new Vec2D(light.x,light.y),point,points[i],points[i+1]);
                if(intersection != null) {
                    if(Math.sqrt((intersection.x - light.x)*(intersection.x - light.x) + (intersection.y - light.y)*(intersection.y - light.y)) < Math.sqrt((min.x - light.x)*(min.x - light.x) + (min.y - light.y)*(min.y - light.y))) {
                        min = intersection;
                        minUV = (Math.sqrt((intersection.x - points[i].x)*(intersection.x - points[i].x) + (intersection.y - points[i].y)*(intersection.y - points[i].y))/(Math.sqrt((points[i+1].x - points[i].x)*(points[i+1].x - points[i].x) + (points[i+1].y - points[i].y)*(points[i+1].y - points[i].y))));
                    }
                }
            }
            for(int i = 0; i < entities.length; i++) {
                Vec2D intersection = calcEntityIntersection(new Vec2D(light.x,light.y),point,entities[i]);
                if(intersection != null) {
                    if(Math.sqrt((intersection.x - light.x)*(intersection.x - light.x) + (intersection.y - light.y)*(intersection.y - light.y)) < Math.sqrt((min.x - light.x)*(min.x - light.x) + (min.y - light.y)*(min.y - light.y))) {
                        Vec2D dir = new Vec2D(light.x - entities[i].x, light.y - entities[i].y);
                        dir.normalize();
                        Vec2D dir2 = new Vec2D(dir.y,-dir.x);
                        dir2.normalize();

                        Vec2D p3 = new Vec2D(entities[i].x + 15*dir2.x,entities[i].y + 15*dir2.y);

                        Vec2D p4 = new Vec2D(entities[i].x - 15*dir2.x,entities[i].y - 15*dir2.y);

                        minEn = intersection;
                        minUVEn = (Math.sqrt((intersection.x - p3.x)*(intersection.x - p3.x) + (intersection.y - p3.y)*(intersection.y - p3.y))/(Math.sqrt((p4.x - p3.x)*(p4.x - p3.x) + (p4.y - p3.y)*(p4.y - p3.y))));
                    }
                }
            }
            hasTexture.add((Math.sqrt((minEn.x - light.x)*(minEn.x - light.x) + (minEn.y - light.y)*(minEn.y - light.y)) > Math.sqrt((min.x - light.x)*(min.x - light.x) + (min.y - light.y)*(min.y - light.y))));
            if(min.x == 10000000) {
                img.drawLine((int) light.x, (int) light.y, (int) point.x, (int) point.y,Color.LIGHT_GRAY);
                depth.add(null);
                uv.add(null);
            }
            else {
                img.drawLine((int) light.x, (int) light.y, (int) min.x, (int) min.y,Color.LIGHT_GRAY);
//                depth[count] = min;
                depth.add(min);
                uv.add(minUV);
            }
            if(minEn.x == 10000000) {
                depthEn.add(null);
                uvEn.add(null);
            }
            else {
                depthEn.add(minEn);
                uvEn.add(minUVEn);
            }
            count++;
        }

        for(int i = 0; i < depth.size(); i++) {
            if(depth.get(i) != null) {
                double d = Math.sqrt((light.x - depth.get(i).x)*(light.x - depth.get(i).x) + (light.y - depth.get(i).y)*(light.y - depth.get(i).y));
                d = 60/d;
                double height = d*700;

//                System.out.println((int) (uv.get(i)*texture.getWidth()));
//                System.out.println(uv);

//                render = new GImage(1, texture.getHeight());

                render.drawImage((int) (uv.get(i)*texture.getWidth()) - texture.getWidth(),0,texture.getWidth(), texture.getHeight(),texture);

                imageToScale.drawImage((int) (((double)i/depth.size())*screenWidth), screenHeight/2 - (int) (height/2),screenHeight/depth.size() + 1, (int) height,render);

//                imageToScale.fillRectangle((int) (((double)i/depth.size())*screenWidth), screenHeight/2 - (int) (height/2),screenHeight/depth.size() + 1, (int) height, (d > 1)? Color.WHITE : new Color((int) (d*255), (int) (d*255), (int) (d*255)));
            }
            if(!hasTexture.get(i) && depthEn.get(i) != null) {

                double d = Math.sqrt((light.x - depthEn.get(i).x)*(light.x - depthEn.get(i).x) + (light.y - depthEn.get(i).y)*(light.y - depthEn.get(i).y));
                d = 60/d;
                double height = d*700;

                render = new GImage(1,entityTexture.getHeight());

                render.drawImage((int) (uvEn.get(i)*entityTexture.getWidth()) - entityTexture.getWidth(),render.getHeight()*2/3,entityTexture.getWidth()/2, render.getHeight()/3,entityTexture);

                imageToScale.drawImage((int) (((double)i/depth.size())*screenWidth), screenHeight/2 - (int) (height/2),screenHeight/depth.size() + 1, (int) height,render);
            }
        }
        drawImage(0,0,getFrameWidth(),getFrameHeight(),imageToScale);
//        drawImage(0,0,getFrameWidth(),getFrameHeight(),render);
    }
    Vec2D calcIntersection(Vec2D p1, Vec2D p2, Vec2D p3, Vec2D p4) {

        double m = (p1.y - p2.y)/(p1.x - p2.x);
        double m1 = (p3.y - p4.y)/(p3.x - p4.x);

        double b = p1.y - m*p1.x;
        double b1 = p3.y - m1*p3.x;

        double x = (b1-b)/(m-m1);
        double y = m*x + b;

        if(((x < Math.max(p3.x,p4.x) && x > Math.min(p3.x,p4.x)) && (y < Math.max(p3.y,p4.y) && y > Math.min(p3.y,p4.y)))) {
            if(((x < Math.max(p1.x,p2.x) && x > Math.min(p1.x,p2.x)) && (y < Math.max(p1.y,p2.y) && y > Math.min(p1.y,p2.y)))) {
                return new Vec2D(x,y);
            }
        }
        return null;
    }
    Vec2D calcEntityIntersection(Vec2D p1, Vec2D p2, Vec2D entity) {

        Vec2D dir = new Vec2D(p1.x - entity.x, p1.y - entity.y);
        dir.normalize();
        Vec2D dir2 = new Vec2D(dir.y,-dir.x);
        dir2.normalize();

        Vec2D p3 = new Vec2D(entity.x + 15*dir2.x,entity.y + 15*dir2.y);

        Vec2D p4 = new Vec2D(entity.x - 15*dir2.x,entity.y - 15*dir2.y);

        double m = (p1.y - p2.y)/(p1.x - p2.x);
        double m1 = (p3.y - p4.y)/(p3.x - p4.x);

        double b = p1.y - m*p1.x;
        double b1 = p3.y - m1*p3.x;

        double x = (b1-b)/(m-m1);
        double y = m*x + b;

        if(((x < Math.max(p3.x,p4.x) && x > Math.min(p3.x,p4.x)) && (y < Math.max(p3.y,p4.y) && y > Math.min(p3.y,p4.y)))) {
            if(((x < Math.max(p1.x,p2.x) && x > Math.min(p1.x,p2.x)) && (y < Math.max(p1.y,p2.y) && y > Math.min(p1.y,p2.y)))) {
                return new Vec2D(x,y);
            }
        }

        return null;
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
