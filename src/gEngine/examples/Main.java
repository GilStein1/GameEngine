package gEngine.examples;

import gEngine.GFile;
import gEngine.GImage;
import gEngine.GSetup;
import gEngine.SetupManager;
import gEngine.utilities.ExecutableBuilder;

import java.awt.*;

public class Main extends GSetup {
    public static void main(String[] args) {
//        SetupManager.pushValueToPool(args[0],"file");
//        SetupManager.startGame(Main.class);
        SetupManager.startGame(ExecutableBuilder.class);
    }

    private GFile f;
    private GImage img;
    @Override
    public void initialize() {
        img = loadFromPath((String) SetupManager.pullFromPool("file"));
    }

    @Override
    public void execute() {
//        fillEllipse(xOnCanvas(),yOnCanvas(),100,100, Color.BLUE);
        drawImage(0,0,900,600,img);
    }

    @Override
    public boolean end() {
        return false;
    }
}
