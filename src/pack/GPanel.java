package pack;

import javax.swing.*;
import java.awt.*;

public class GPanel {

    private JFrame frame;
    private GImage image1;
    private JPanel panel;
    boolean isAdded = false;
    private SetupManager setupManager;
    private Color defaultBackground = new Color(238,238,238);

    public GPanel(int x, int y, int width, int height, String title, GImage image) {

        setupManager = SetupManager.getInstance();
        this.image1 = image;
        frame = new JFrame(title);
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(image1.getImage(),0,0,width,height,null);
                image1.fillRectangle(0,0,image1.getImage().getWidth(),image1.getImage().getHeight(),defaultBackground);
            }
        };

        frame.add(panel);

        frame.setSize(width,height);
        frame.setLocation(x,y);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }
    public void setVisible(boolean visible) {
        frame.setVisible(visible && isAdded);
    }
    public void setGImage(GImage image) {
        this.image1 = image;
    }
    public GImage getImage() {
        return image1;
    }
    public int xOnScreen() {
        return setupManager.getSetup().xOnScreen();
    }
    public int yOnScreen() {
        return setupManager.getSetup().yOnScreen();
    }
    public int xOnCanvas() {
        return setupManager.getSetup().xOnScreen() - frame.getX();
    }
    public int yOnCanvas() {
        return setupManager.getSetup().yOnScreen() - frame.getY();
    }
    void refresh() {
        panel.repaint();
    }

}
