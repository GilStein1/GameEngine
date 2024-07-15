package gEngine.examples;

import gEngine.GFile;
import gEngine.GImage;
import gEngine.GSetup;

import java.awt.*;

public class Painter extends GSetup {
    GImage img;
    GFile exportedImage;
    GImage imported;
    @Override
    public void initialize() {
        exportedImage = loadFileInGSetupResources("FinalImage.png");
        img = new GImage(900,600);
        img.fillRectangle(0,0,900,600,Color.WHITE);
        setResizable(false);
        imported = loadFromPath(GFileChooser("Yes"));
        img.drawImage(0,0,900,600,imported);
    }

    @Override
    public void execute() {
        if(leftClick()) {
            img.fillEllipse(xOnCanvas()-10,yOnCanvas()-10,20,20, Color.BLACK);
        }
        drawImage(0,0,900,600,img);
    }

    @Override
    public void lastFunction() {
        img.exportAsPNG(exportedImage);
    }

    @Override
    public boolean end() {
        return false;
    }
}
