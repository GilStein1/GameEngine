package pack.examples;

import pack.GSetup;
import pack.SetupManager;

import java.awt.*;

public class Test extends GSetup {
    String value;
    @Override
    public void initialize() {
        setSmoothness(Smoothness.VERY_SMOOTH);
        value = (String) SetupManager.pullFromPool("val");
        setFont(new Font("Ariel",Font.BOLD,40));
    }

    @Override
    public void execute() {
        drawText(300,300,value, Color.BLACK);
    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
