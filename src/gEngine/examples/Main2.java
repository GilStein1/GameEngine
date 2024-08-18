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
        SetupManager.addToVirtualQueue(ShootingGameClient.class, 8080);
//        SetupManager.addToVirtualQueue(VirtualExample.class, 8080);
//        SetupManager.pushValueToPool(0,"x");
//        SetupManager.pushValueToPool(0,"y");
    }

    @Override
    public void execute() {


//        drawText((int)SetupManager.pullFromPool("x"),(int)SetupManager.pullFromPool("y"),"mjashgdwjad", Color.BLUE);

//        try {
//            drawText((int)SetupManager.pullFromPool("x"),(int)SetupManager.pullFromPool("y"),"mjashgdwjad", Color.BLUE);
//        }
//        catch (IllegalArgumentException ignored) {}

    }

    @Override
    public boolean end() {
        return false;
    }
}
