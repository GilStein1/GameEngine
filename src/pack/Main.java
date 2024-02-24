package pack;

import pack.examples.*;

import javax.swing.*;
import java.awt.*;

public class Main extends GSetup{

    public static void main(String[] args) {
        SetupManager.Debug.runFullScreen(false);
        SetupManager.startGame(BallGameWithSprings.class);
//        SetupManager.Debug.fpsGraph();
    }

    GImage bird;

    @Override
    public void initialize() {
        bird = new GImage("Bird.png");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        loadShapesFaster(true);
        SetupManager.Debug.fpsGraph();
    }

    @Override
    public void execute() {
        drawImage(xOnCanvas()-50,yOnCanvas()-50,100,100,bird);
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}