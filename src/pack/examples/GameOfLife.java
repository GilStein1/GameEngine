package pack.examples;

import pack.GImage;
import pack.GSetup;
import pack.SetupManager;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class GameOfLife extends GSetup {

    boolean pressed = true;
    boolean pressed2 = true;
    boolean pressed3 = true;
    boolean pressed4 = true;
    boolean pressed5 = true;
    boolean color = true;
    boolean[][] arr1;
    boolean[][] arr2;
    double count = 0;
    double cycles = 0.1;
    boolean stop = true;
    GImage icon;
    int lastX = -1;
    int lastY = -1;

    @Override
    public void initialize() {
        SetupManager.Debug.fpsGraph();
        setFrameSize(900,900);
        setResizable(false);
        setTitle("Game Of Life");
        arr1 = new boolean[100][100];
        arr2 = new boolean[100][100];

        for(int i = 0; i < arr1.length; i++) {
            Arrays.fill(arr1[i], false);
        }
        icon = new GImage(900,900);
//        SetupManager.Debug.fpsGraph();
    }

    @Override
    public void execute() {

        if(leftClick()) {
            int x = xOnCanvas();
            int y = yOnCanvas();

            if(x >= 0 && y >= 0 && x/9 < 100 && y/9 < 100) {
                arr1[(int)(x/9)][(int)(y/9)] = true;
            }
        }
        if(rightClick()) {
            int x = (xOnCanvas())/9;
            int y = (yOnCanvas())/9;

            if(x >= 0 && x < 100 && y > 0 && y < 100) {
                arr1[x][y] = false;
                if(x > 0) {
                    arr1[x-1][y] = false;
                }
                if(y > 0) {
                    arr1[x][y-1] = false;
                }
                if(x > 0 && y > 0) {
                    arr1[x-1][y-1] = false;
                }
            }
        }

        if(lastKey() == KeyEvent.VK_ENTER && pressed) {
            pressed = false;
            randomize();
        }
        if(lastKey() == KeyEvent.VK_SPACE && pressed3) {
            pressed3 = false;
            stop = !stop;
        }
        if(lastKey() == KeyEvent.VK_UP && pressed2) {
            pressed2 = false;
            cycles /= 2.0;
        }
        if(lastKey() == KeyEvent.VK_DOWN && pressed2) {
            pressed2 = false;
            cycles *= 2.0;
        }
        if(lastKey() == KeyEvent.VK_BACK_SPACE && pressed4) {
            pressed4 = false;
            for(int i = 0; i < arr1.length; i++) {
                Arrays.fill(arr1[i],false);
            }
        }
        if(lastKey() == KeyEvent.VK_E && pressed5) {
            pressed5 = false;
            color = !color;
        }
        if(lastKey() == -1) {
            pressed = true;
            pressed2 = true;
            pressed3 = true;
            pressed4 = true;
            pressed5 = true;
        }

        count += deltaTime();

        for(int i = 0; i < arr1.length; i++) {
            for(int j = 0; j < arr1[i].length; j++) {
                fillRectangle(i*9,j*9,9,9,(!color? !arr1[i][j] : arr1[i][j])? Color.WHITE : Color.BLACK);
                drawLine(0,j*9,900,j*9,!color? Color.GRAY : Color.BLACK);
                icon.getGraphics().setColor((!color? !arr1[i][j] : arr1[i][j])? Color.WHITE : Color.BLACK);
                icon.getGraphics().fillRect((i - 50)*90,(j - 50)*90,90,90);
                icon.getGraphics().setColor(!color? Color.GRAY : Color.BLACK);
                icon.getGraphics().drawLine(0,j*90,900,j*90);
            }
            drawLine(i*9,0,i*9,900,!color? Color.GRAY : Color.BLACK);
            icon.getGraphics().setColor(Color.BLACK);
            icon.getGraphics().drawLine(i*90,0,i*90,900);
        }

        if(count > cycles && stop) {
            count = 0.0;
            update();
            setFrameIcon(icon);
        }

    }

    @Override
    public void lastFunction() {

    }

    public void randomize() {
        for(int i = 0; i < arr1.length; i++) {
            for(int j = 0; j < arr1[i].length; j++) {
                arr1[i][j] = (Math.random() < 0.5);
            }
        }
    }

    public void update() {

        int c;

        for(int i = 0; i < arr1.length; i++) {
            for(int j = 0; j < arr1[i].length; j++) {

                c = 0;

                if(i > 0 && i < 100 - 1 && j > 0 && j < 100 - 1) {
                    c += (arr1[i-1][j]? 1 : 0);
                    c += (arr1[i+1][j]? 1 : 0);
                    c += (arr1[i][j-1]? 1 : 0);
                    c += (arr1[i][j+1]? 1 : 0);
                    c += (arr1[i-1][j-1]? 1 : 0);
                    c += (arr1[i+1][j-1]? 1 : 0);
                    c += (arr1[i+1][j+1]? 1 : 0);
                    c += (arr1[i-1][j+1]? 1 : 0);

                    if(!arr1[i][j] && c == 3) {
                        arr2[i][j] = true;
                    }
                    else {
                        arr2[i][j] = arr1[i][j];
                    }
                    if(arr1[i][j] && (c < 2 || c > 3)) {
                        arr2[i][j] = false;
                    }
                }
            }
        }

        for(int i = 0; i < arr1.length; i++) {
            for(int j = 0; j < arr1[i].length; j++) {
                arr1[i][j] = arr2[i][j];
            }
        }
    }
    @Override
    public boolean end() {
        return false;
    }
}
