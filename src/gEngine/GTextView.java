package gEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class GTextView implements GComponent{

    private int x,y,width,height;
    private String text;
    private JTextArea textArea;
    private boolean showFrame = false;

    public GTextView(int x, int y, int width, int height, String text) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        textArea = new JTextArea();
        textArea.setBackground(new Color(0,0,0,0));
        textArea.setBounds(x,y,width,height);
        textArea.setText(text);
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.addMouseListener(new MouseListener() {
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

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public void showFrame(boolean show) {
        showFrame = show;
    }
    void draw(GSetup setup) {
        if(showFrame) {
            setup.drawRectangle(x,y,width,height,Color.BLACK);
        }
    }
    public void setText(String text) {
        this.text = text;
        textArea.setText(text);
    }
    public JTextArea getTextArea() {
        return textArea;
    }

    @Override
    public void tickUpdate() {

    }

    @Override
    public void draw(Graphics g) {

    }
}
