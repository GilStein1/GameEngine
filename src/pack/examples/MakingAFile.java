package pack.examples;

import pack.GFile;
import pack.GSetup;

import java.awt.*;

public class MakingAFile extends GSetup {

    GFile f;
    @Override
    public void initialize() {
        f = loadFileInGSetupResources("ThisIsAFile.txt");
        f.println("Hello.");
        f.println("I Just Made A File");
        f.stopWriting();
//        System.out.println(f.read());
    }

    @Override
    public void execute() {
        drawText(400,400,"Check The File",Color.BLACK);
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
