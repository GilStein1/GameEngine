package gEngine.utilities;

import gEngine.GFile;
import gEngine.GSetup;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExecutableBuilder extends GSetup {
    String javaFile;
    GFile kept;

    @Override
    public void initialize() {
        setFrameSize(5,5);
        javaFile = GFileChooser("select");
        loadFileInGSetupResources("jarFiles", "non.txt");
        if(!javaFile.equals("")) {
            String sourceFile = javaFile;
            String[] fileName = javaFile.split("\\\\");
            String destinationFile = System.getenv("APPDATA") + "\\GSetup\\ExecutableBuilder\\jarFiles\\" + fileName[fileName.length - 1];

            try (FileInputStream fis = new FileInputStream(sourceFile); FileOutputStream fos = new FileOutputStream(destinationFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
            } catch (IOException ignored) {
            }
            String[] fileNames2 = javaFile.split("\\.");
            String[] batLocations = (fileNames2[fileNames2.length-2] + ".bat").split("\\\\");
            String batLocation = "";
            for(int i = 0; i < batLocations.length-1; i++) {
                batLocation += batLocations[i];
                if(i != batLocations.length-2) {
                    batLocation += "/";
                }
            }
            System.out.println(batLocation);
            System.out.println(batLocations[batLocations.length-1]);
            kept = loadFile(batLocation, batLocations[batLocations.length-1]);
            kept.println("@echo off");
            kept.println("start javaw -jar \"" + destinationFile + "\" %1");
            kept.println("exit");
            kept.stopWriting();
        }
    }

    @Override
    public void execute() {

    }

    @Override
    public boolean end() {
        return true;
    }
}
