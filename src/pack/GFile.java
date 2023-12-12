package pack;

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
            accidentallyCreatedNewFile = true;
            try {
                System.out.println(file.createNewFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
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
    public void stopWriting() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        isEditing = false;
    }
    public boolean accidentallyCreatedNewFile() {
        return accidentallyCreatedNewFile;
    }
    public File getFile() {
        return file;
    }

}
