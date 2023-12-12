package pack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GFrameTextField implements GComponent {

    private JTextField textField;


    public GFrameTextField(int x, int y, int width, int height) {
        SetupManager.addTick(this);
        textField = new JTextField();
        textField.setBounds(x,y,width,height);
        textField.setVisible(true);
//        textField.setEnabled(false);
        textField.setFocusable(false);
        textField.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SetupManager.getInstance().getSetup().mouseClickedFromOutSide();
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                textField.setFocusable(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public JTextField getJTextField() {
        return textField;
    }
    void isPressed() {
        textField.setFocusable(false);
    }
    public String getText() {
        return textField.getText();
    }
    public void setFont(Font font) {
        textField.setFont(font);
    }
    @Override
    public void tickUpdate() {

    }
}
