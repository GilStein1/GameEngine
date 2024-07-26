package gEngine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

public class VirtualGImage{

    private VirtualGSetup virtualGSetup;
    private final int id;

    public VirtualGImage(int width, int height, VirtualGSetup virtualGSetup) {
        this.virtualGSetup = virtualGSetup;
        this.id = this.hashCode();
        virtualGSetup.addToMethodStr("ge ~s ~mkImg|~w:" + width + "~h:" + height + "~id:" + id);
    }

    public static void handleDrawingCommands(HashMap<Integer, GImage> images, String command) {
        command = command.substring(7);
        String[] parts = command.split("~");
        if(images.get(Integer.parseInt(parts[parts.length-1].substring(3))) != null) {
            switch (parts[1])  {
                case "drEll|" -> {
                    String temp = command.substring(command.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    images.get(Integer.valueOf(vars[vars.length-1].substring(3))).drawEllipse(
                            Integer.parseInt(vars[0].substring(2)),
                            Integer.parseInt(vars[1].substring(2)),
                            Integer.parseInt(vars[2].substring(2)),
                            Integer.parseInt(vars[3].substring(2)),
                            new Color(Integer.parseInt(vars[4].substring(2)))
                    );
                }
                case "flEll|" -> {
                    String temp = command.substring(command.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    images.get(Integer.valueOf(vars[vars.length-1].substring(3))).fillEllipse(
                            Integer.parseInt(vars[0].substring(2)),
                            Integer.parseInt(vars[1].substring(2)),
                            Integer.parseInt(vars[2].substring(2)),
                            Integer.parseInt(vars[3].substring(2)),
                            new Color(Integer.parseInt(vars[4].substring(2)))
                    );
                }
                case "drRec|" -> {
                    String temp = command.substring(command.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    images.get(Integer.valueOf(vars[vars.length-1].substring(3))).drawRectangle(
                            Integer.parseInt(vars[0].substring(2)),
                            Integer.parseInt(vars[1].substring(2)),
                            Integer.parseInt(vars[2].substring(2)),
                            Integer.parseInt(vars[3].substring(2)),
                            new Color(Integer.parseInt(vars[4].substring(2)))
                    );
                }
                case "flRec|" -> {
                    String temp = command.substring(command.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    images.get(Integer.valueOf(vars[vars.length-1].substring(3))).fillRectangle(
                            Integer.parseInt(vars[0].substring(2)),
                            Integer.parseInt(vars[1].substring(2)),
                            Integer.parseInt(vars[2].substring(2)),
                            Integer.parseInt(vars[3].substring(2)),
                            new Color(Integer.parseInt(vars[4].substring(2)))
                    );
                }
                case "drPol|" -> {
                    String temp = command.substring(command.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    String[] xStr = vars[0].substring(2).split(",");
                    String[] yStr = vars[1].substring(2).split(",");
                    int[] x = new int[xStr.length];
                    int[] y = new int[yStr.length];
                    for(int i = 0; i < x.length; i++) {
                        x[i] = Integer.valueOf(xStr[i]);
                        y[i] = Integer.valueOf(yStr[i]);
                    }
                    images.get(Integer.valueOf(vars[vars.length-1].substring(3))).drawPolygon(
                            x,
                            y,
                            new Color(Integer.parseInt(vars[4].substring(2)))
                    );
                }
                case "flPol|" -> {
                    String temp = command.substring(command.indexOf("|")+2);
                    String[] vars = temp.split("~");
                    String[] xStr = vars[0].substring(2).split(",");
                    String[] yStr = vars[1].substring(2).split(",");
                    int[] x = new int[xStr.length];
                    int[] y = new int[yStr.length];
                    for(int i = 0; i < x.length; i++) {
                        x[i] = Integer.valueOf(xStr[i]);
                        y[i] = Integer.valueOf(yStr[i]);
                    }
                    images.get(Integer.valueOf(vars[vars.length-1].substring(3))).fillPolygon(
                            x,
                            y,
                            new Color(Integer.parseInt(vars[4].substring(2)))
                    );
                }
            }
        }
    }

    public int getId () {
        return id;
    }

    public void drawEllipse(int x, int y, int width, int height, Color color) {
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~drEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB() + "~id:" + id);
    }

    public void fillEllipse(int x, int y, int width, int height, Color color) {
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~flEll|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB() + "~id:" + id);
    }

    public void drawRectangle(int x, int y, int width, int height, Color color) {
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~drRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB() + "~id:" + id);
    }
    public void fillRectangle(int x, int y, int width, int height, Color color) {
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~flRec|~x:" + x + "~y:" + y + "~w:" + width + "~h:" + height + "~c:" + color.getRGB() + "~id:" + id);
    }
    public void drawPolygon(int[] x, int[] y, Color color) {
        String xVal = "";
        for(int i = 0; i < x.length; i++) {
            xVal += x[i] + ((i < x.length-1)? "," : "");
        }
        String yVal = "";
        for(int i = 0; i < x.length; i++) {
            yVal += y[i] + ((i < y.length-1)? "," : "");
        }
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~drPol|~x:" + xVal + "~y:" + yVal + "~c:" + color.getRGB() + "~id:" + id);
    }

    public void drawPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        drawPolygon(x,y,color);
    }

    public void fillPolygon(int[] x, int[] y, Color color) {
        String xVal = "";
        for(int i = 0; i < x.length; i++) {
            xVal += x[i] + ((i < x.length-1)? "," : "");
        }
        String yVal = "";
        for(int i = 0; i < x.length; i++) {
            yVal += y[i] + ((i < y.length-1)? "," : "");
        }
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~flPol|~x:" + xVal + "~y:" + yVal + "~c:" + color.getRGB() + "~id:" + id);
    }

    public void fillPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        fillPolygon(x,y,color);
    }

    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~drLine|~x1:" + x1 + "~y1:" + y1 + "~x2:" + x2 + "~y2:" + y2 + "~c:" + color.getRGB() + "~id:" + id);
    }

    public void drawText(int x, int y, String text, Color color) {
        virtualGSetup.addToMethodStr("ge ~s ~edImg ~drTxt|~x:" + x + "~y:" + y + "~t:" + text + "~c:" + color.getRGB() + "~id:" + id);
    }
}
