package pack;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GFrameButton {

    private JButton button;
    int x,y,width,height;
    private boolean isPressed = false;

    public GFrameButton(int x, int y, int width, int height) {
        button = new JButton();
        button.setFocusable(false);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        button.setBounds(x,y,width,height);
        button.setVisible(true);
        button.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public void setBackground(Color background) {
        button.setBackground(background);
    }
    public void setText(String text) {
        button.setText(text);
    }
    public void setFont(Font font) {
        button.setFont(font);
    }
    public JButton getJButton() {
        return button;
    }
    public void reset() {
        isPressed = false;
    }
    public String getText() {
        return button.getText();
    }
    public boolean isPressed() {
        return isPressed;
    }
}
