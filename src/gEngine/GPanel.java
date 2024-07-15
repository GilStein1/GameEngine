package gEngine;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;

public class GPanel implements GComponent {
    private JFrame frame;
    private GImage image1;
    private JPanel panel;
    private boolean pressedLeft = false;
    private boolean pressedRight = false;
    private boolean visible = false;
    boolean isAdded = false;
    boolean selected = false;
    private SetupManager setupManager;
    private Color defaultBackground = new Color(238,238,238);
    private boolean resizable = true;
    private Graphics graphics;
    private BufferedImage img;
    private boolean isClosed = false;
    private int x,y,width,height;
    private String title;

    public GPanel(int x, int y, int width, int height, String title, GImage image) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.title = title;

        SetupManager.addTick(this);

        setupManager = SetupManager.getInstance();
        this.image1 = image;
        frame = new JFrame(title);
        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                g.drawImage(image1.getImage(),0,0,frame.getWidth(),frame.getHeight(),null);
                if(!((GSetup)SetupManager.getInstance().getSetup()).loadShapesFaster) {
                    image1.fillRectangle(0,0,image1.getImage().getWidth(),image1.getImage().getHeight(),defaultBackground);
                }
                if(((GSetup)SetupManager.getInstance().getSetup()).loadShapesFaster) {
                    g.drawImage(img,0,0,frame.getWidth(),frame.getHeight(),null);
                }
                if(graphics != null) {
                    graphics.dispose();
                }
            }
        };

        frame.add(panel);

        frame.setSize(width,height);
        frame.setLocation(x,y);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {
                visible = false;
                isClosed = true;
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {
                selected = true;
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                selected = false;
            }
        });

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                switch (e.getButton()) {
                    case 1 -> pressedLeft = true;
                    case 3 -> pressedRight = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                switch (e.getButton()) {
                    case 1 -> pressedLeft = false;
                    case 3 -> pressedRight = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
    }
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
        frame.setResizable(resizable);
    }
    public boolean isResizable() {
        return resizable;
    }
    public void setVisible(boolean isVisible) {

        if(isClosed) {
            frame = new JFrame(title);
            panel = new JPanel() {
                @Override
                public void paintComponent(Graphics g) {
                    g.drawImage(image1.getImage(),0,0,frame.getWidth(),frame.getHeight(),null);
                    if(!((GSetup)SetupManager.getInstance().getSetup()).loadShapesFaster) {
                        image1.fillRectangle(0,0,image1.getImage().getWidth(),image1.getImage().getHeight(),defaultBackground);
                    }
                    if(((GSetup)SetupManager.getInstance().getSetup()).loadShapesFaster) {
                        g.drawImage(img,0,0,frame.getWidth(),frame.getHeight(),null);
                    }
                    if(graphics != null) {
                        graphics.dispose();
                    }
                }
            };

            frame.add(panel);

            frame.setSize(width,height);
            frame.setLocation(x,y);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            frame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {

                }

                @Override
                public void windowClosed(WindowEvent e) {
                    visible = false;
                    isClosed = true;
                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {
                    selected = true;
                }

                @Override
                public void windowDeactivated(WindowEvent e) {
                    selected = false;
                }
            });

            frame.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {
                    switch (e.getButton()) {
                        case 1 -> pressedLeft = true;
                        case 3 -> pressedRight = true;
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    switch (e.getButton()) {
                        case 1 -> pressedLeft = false;
                        case 3 -> pressedRight = false;
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });
        }

        frame.setVisible(isVisible && isAdded);
        this.visible = isVisible && isAdded;
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
    public boolean isVisible() {
        return visible;
    }
    void refresh() {

        if(((GSetup)SetupManager.getInstance().getSetup()).loadShapesFaster) {
            if(img == null || !(img.getWidth() == frame.getWidth() && img.getHeight() == frame.getHeight())) {
                img = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);
            }
            graphics = img.createGraphics();
            graphics.drawImage(image1.getImage(),0,0,frame.getWidth(),frame.getHeight(),null);
            image1.fillRectangle(0,0,image1.getImage().getWidth(),image1.getImage().getHeight(),defaultBackground);
        }

        panel.repaint();
    }
    public boolean leftClick() {
        return pressedLeft;
    }
    public boolean rightClick() {
        return pressedRight;
    }
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void tickUpdate() {
        refresh();
    }

    @Override
    public void draw(Graphics g) {

    }
}
