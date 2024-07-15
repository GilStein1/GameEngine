package gEngine;

import java.io.*;

public class GFile {

    private File file;
    private FileReader fileReader;
    private FileWriter fileWriter;
    private String fileContent = "";
    private boolean isEditing = false;
    private boolean accidentallyCreatedNewFile = false;

    GFile(File file) {
        this.file = file;
        if(!file.exists()) {
//            accidentallyCreatedNewFile = true;
            try {
                accidentallyCreatedNewFile = file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * @return a String (if the file is a txt file), containing the String in the file.
     */
    public String read() {
        try {
            fileReader = new FileReader(file);
            int data = fileReader.read();
            fileContent = String.valueOf((char)data);
//            System.out.println((char)data + " = " + data);
            while (data != -1) {
//                System.out.println((char)data + " = " + data);
                data = fileReader.read();
                if(data != (int)'\n') {
                    fileContent += (char)data;
                }
                else {
                    fileContent += "\n";
                }
            }
            fileReader.close();
            fileContent = fileContent.substring(0,fileContent.length() - 1);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileContent;
    }
    /**
     * Prints new text into the file (if the file is a txt file).
     */
    public void print(String text) {
        if (!isEditing) {
            isEditing = true;
            try {
                fileWriter = new FileWriter(file);
                fileWriter.write(text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                fileWriter.write(text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    /**
     * Prints text in a new line in the file (if the file is a txt file).
     */
    public void println(String text) {
        if (!isEditing) {
            isEditing = true;
            try {
                fileWriter = new FileWriter(file);
                fileWriter.write(text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
            try {
                fileWriter.write(text);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            fileWriter.write("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    /**
     * Stops the writing to the file
     */
    public void stopWriting() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        isEditing = false;
    }
    /**
     * @return if a new file in that name was recently created
     */
    public boolean accidentallyCreatedNewFile() {
        return accidentallyCreatedNewFile;
    }
    public File getFile() {
        return file;
    }

}
