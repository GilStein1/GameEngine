package gEngine.examples;

import gEngine.GSetup;
import gEngine.SetupManager;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Main extends GSetup {
    public static void main(String[] args) {
        SetupManager.startVirtualClient("localhost", 8080);
//        SetupManager.startGame(Main.class);
    }

    @Override
    public void initialize() {

    }

    @Override
    public void execute() {
        if(isKeyPressed(KeyEvent.VK_SPACE)) {
            fillRectangle(0,0,400,400, Color.BLUE);
        }
        if(isKeyPressed(KeyEvent.VK_E)) {
            fillRectangle(400,0,50,50, Color.RED);
        }
    }

    @Override
    public boolean end() {
        return false;
    }
}
