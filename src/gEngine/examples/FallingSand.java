package gEngine.examples;

import gEngine.GFile;
import gEngine.GImage;
import gEngine.GSetup;
import gEngine.SetupManager;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;

public class FallingSand extends GSetup {
    GImage sand;
    boolean[][] update;
    int radius1;
    Point lastPress;
    boolean penRaised;
    Point screenPos;
    GFile export;
    @Override
    public void initialize() {
        screenPos = new Point(0,0);
        penRaised = true;
        try {
            setResizable((boolean)SetupManager.pullFromPool("canResize"));
        } catch (Exception e) {
            setResizable(false);
        }
        export = loadFileInGSetupResources("sand.png");
        if(!export.accidentallyCreatedNewFile()) {
            sand = loadFromPath(export.getFile().getPath());
//            System.out.println(export.getFile().getPath());
        }
        else {
            sand = new GImage(302,302);
            sand.fillRectangle(0,0,sand.getWidth() + 4,sand.getHeight() + 4,Color.WHITE);
        }
        radius1 = 6;
        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                radius1+=e.getWheelRotation();
                radius1 = Math.max(radius1, 0);
            }
        });
        setFrameSize(600,600);
        drawShapesFaster(true);
        update = new boolean[302][302];
        for(int i = 0; i < update.length; i++) {
            Arrays.fill(update[i],false);
        }
//        SetupManager.Debug.fpsGraph();
        lastPress = new Point(-10,0);
    }

    @Override
    public void execute() {
//        sand.getGraphics().setStroke(new BasicStroke(radius1));
        drawImage(0,0,getFrameWidth(),getFrameHeight(),sand);
        if(leftClick()) {
            leftClicked();
//            lastPress.x = (int) ((xOnCanvas()/(double)getFrameWidth())*sand.getWidth());
//            lastPress.y = (int) ((yOnCanvas()/(double)getFrameHeight())*sand.getHeight());
        }
        else if(lastKey() == KeyEvent.VK_BACK_SPACE) {
            delete((int) ((xOnCanvas()/(double)getFrameWidth())*sand.getWidth()), (int) ((yOnCanvas()/(double)getFrameHeight())*sand.getHeight()),12);
        }
        if(!leftClick()) {
            penRaised = true;
        }
        updatePhysics();
        screenPos.setLocation(getCanvasX(),getCanvasY());
    }
    public void delete(int x, int y, int radius) {
        sand.fillEllipse(x-radius,y-radius,radius*2,radius*2,Color.WHITE);
    }
    public void leftClicked() {
        draw((int) ((xOnCanvas()/(double)getFrameWidth())*sand.getWidth()), (int) ((yOnCanvas()/(double)getFrameHeight())*sand.getHeight()),radius1,new Color(240 + (int)(Math.random()*(255-240)),220 + (int)(Math.random()*(235-220)),160 + (int)(Math.random()*(160-180))));
    }
    public void draw(int x, int y, int radius, Color color) {
//        if(penRaised) {
////            sand.fillEllipse(x-radius,y-radius,radius*2,radius*2,color);
//            penRaised = false;
//        }
//        else {
//            sand.drawLine(x,y,lastPress.x,lastPress.y,color);
//        }
        sand.fillEllipse(x-radius,y-radius,radius*2,radius*2,color);

    }
    public void moveParticle(int x, int y) {
        update[x][y] = false;
        Color c = sand.getColor(x,y);
        if(!c.equals(Color.WHITE)) {
            if(y < sand.getHeight()-1 && sand.getColor(x,y+1).equals(Color.WHITE)) {
                sand.fillRectangle(x,y,1,1,Color.WHITE);
                sand.fillRectangle(x,y+1,1,1,c);
                update[x][y+1] = true;
            }
            else if(sand.getColor(x,y-1).equals(Color.WHITE)) {
                if(sand.getColor(x+1,y).equals(Color.WHITE) && sand.getColor(x+1,y+1).equals(Color.WHITE) && sand.getColor(x+1,y+2).equals(Color.WHITE)) {
                    sand.fillRectangle(x,y,1,1,Color.WHITE);
                    sand.fillRectangle(x+1,y+1,1,1,c);
                    update[x+1][y+1] = true;
                }
                else if(sand.getColor(x-1,y).equals(Color.WHITE) && sand.getColor(x-1,y+1).equals(Color.WHITE) && sand.getColor(x-1,y+2).equals(Color.WHITE)) {
                    sand.fillRectangle(x,y,1,1,Color.WHITE);
                    sand.fillRectangle(x-1,y+1,1,1,c);
                    update[x-1][y+1] = true;
                }
            }
            else if(getCanvasX() < screenPos.getX()) {
                if(sand.getColor(x+1,y).equals(Color.WHITE) && (x+1<sand.getWidth() && sand.getColor(x+2,y).equals(Color.WHITE))) {
                    sand.fillRectangle(x,y,1,1,Color.WHITE);
                    sand.fillRectangle(x+2,y,1,1,c);
                    update[x+2][y] = true;
                }
            }
            else if(getCanvasX() > screenPos.getX()) {
                if(sand.getColor(x-1,y).equals(Color.WHITE) && (x > 1 && sand.getColor(x-2,y).equals(Color.WHITE))) {
                    sand.fillRectangle(x,y,1,1,Color.WHITE);
                    sand.fillRectangle(x-2,y,1,1,c);
                    update[x-2][y] = true;
                }
            }
        }
    }
    public void updatePhysics() {
        for(int i = 1; i < 300-1; i++) {
            for(int j = 300-2; j > 0; j--) {
                if(update[i][j] ||
                        ((sand.getColor(i,j-1).equals(Color.WHITE) && !sand.getColor(i,j).equals(Color.WHITE)) || (!sand.getColor(i,j).equals(Color.WHITE) && sand.getColor(i,j+1).equals(Color.WHITE))) ||
                        getCanvasX() > screenPos.getX() ||
                        getCanvasX() < screenPos.getX()) {
                    moveParticle(i,j);
                }
            }
        }
    }
    @Override
    public void lastFunction() {
        sand.exportAsPNG(export);
    }
    @Override
    public boolean end() {
        return false;
    }
}
