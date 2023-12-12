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
                SetupManager.getInstance().getSetup().mouseClickedFromOutSide();
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
    /**
     * Sets the background color of the button
     */
    public void setBackground(Color background) {
        button.setBackground(background);
    }
    /**
     * Sets the text to be written on the button
     */
    public void setText(String text) {
        button.setText(text);
    }
    /**
     * Sets the font of the text on the button
     */
    public void setFont(Font font) {
        button.setFont(font);
    }
    public JButton getJButton() {
        return button;
    }
    /**
     * Sets the button to not be pressed
     */
    public void reset() {
        isPressed = false;
    }
    public String getText() {
        return button.getText();
    }
    /**
     * @return if the button is pressed
     */
    public boolean isPressed() {
        return isPressed;
    }
}
