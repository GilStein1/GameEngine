package gEngine.examples;

import gEngine.GSetup;
import gEngine.SetupManager;
import gEngine.VirtualGSetup;

import java.awt.*;

public class Main2 extends GSetup {

    public static void main(String[] args) {
        SetupManager.startGame(Main2.class);
    }

    @Override
    public void initialize() {
        Thread t = new Thread(() -> {
            VirtualExample ve = new VirtualExample(8080);
        });
        t.start();
    }

    @Override
    public void execute() {

        drawText((int)SetupManager.pullFromPool("x"),(int)SetupManager.pullFromPool("y"),"mjashgdwjad", Color.BLUE);

    }

    @Override
    public boolean end() {
        return false;
    }
}
