package pack;

import java.awt.*;

public class Setup extends GSetup{

    GTextField textField;
    GButton b;
    GPanel p;

    @Override
    public void initialize() {
        textField = new GTextField(100,0,200,30,Color.YELLOW);
        addGTextField(textField);
        b = new GButton(200,00,100,100,Color.GREEN);
        addGButton(b);
        p = new GPanel(90,500,500,500,"Hello",new GImage(500,500));
        addGPanel(p);
        p.setVisible(true);
    }

    @Override
    public void execute() {
        drawButton(b);
        drawTextField(textField);
        if(b.isPressed()) {
            drawText(0,300, textField.getText(), Color.BLACK);
        }
        p.getImage().fillEllipse(p.xOnCanvas(),p.yOnCanvas(),30,30,Color.RED);

    }

    @Override
    public void lastFunction() {

    }

    @Override
    public boolean end() {
        return false;
    }
}
