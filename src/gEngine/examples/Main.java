package gEngine.examples;

import gEngine.GSetup;
import gEngine.SetupManager;

import java.awt.*;

public class Main extends GSetup {
    public static void main(String[] args) {
        SetupManager.startGame(Jello.class);
    }
    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        fillEllipse(xOnCanvas(),yOnCanvas(),100,100, Color.BLUE);
    }

    @Override
    public boolean end() {
        return false;
    }
}
