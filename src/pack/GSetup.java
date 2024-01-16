package pack;

import pack.Arrays.Queue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class GSetup implements GSetups{
    private boolean firstMethods;
    public int frameWidth = 900;
    public int frameHeight = 600;
    public double lastTime = System.currentTimeMillis();
    public double currentTime;
    public boolean resize = true;
    private boolean mouseOnFrame = true;
    private boolean isPainting;
    public String title = "";
    private JFrame frame;
    private SetupManager manager;
    private JPanel panel;
    private java.util.List<GPanel> panels = new java.util.ArrayList<>();
    private java.util.List<GFrameButton> buttons = new java.util.ArrayList<>();
    private java.util.List<GFrameTextField> frameTextFields = new java.util.ArrayList<>();
    private java.util.List<GButton> gButtons = new java.util.ArrayList<>();
    private java.util.List<GTextField> gTextFields = new java.util.ArrayList<>();
    private java.util.List<GTextView> textViews = new java.util.ArrayList<>();
    private ArrayList<GComponent> componentsToPaintLast = new ArrayList<>();
    public Thread executed;
    public BufferedImage img;
    public Graphics2D graphics;
    private BufferedImage frameIcon;
    private Point mousePos = MouseInfo.getPointerInfo().getLocation();
    public Color defaultBackground = new Color(238,238,238);
    public Font font;
    private boolean updateFont = false;
    private int count;
    private double timeCountForFps = 0;
    private double countTime = 0;
    private double fps = 0;
    private int lastKeyPressed = -1;
    private char lastCharPressed = '~';
    private KeyEventSupplier keyTyped;
    private KeyEventSupplier keyPressed;
    private KeyEventSupplier keyReleased;
    private Queue<KeyAndActionPair> keyEventsPairs;
    private double[] fpsArr;
    private boolean leftMouseClicked = false;
    private boolean rightMouseClicked = false;
    private int xScreen = 0;
    private int yScreen = 0;
    private BufferedImage imgToDrawOn;
    private Graphics graphicsToDrawFrom;
    private boolean loadShapedFaster = false;
    private Smoothness smoothness;

    public GSetup() {
        smoothness = Smoothness.NORMAL;
        firstMethods = true;

        fpsArr = new double[100];
        Arrays.fill(fpsArr,0.0);

        keyEventsPairs = new Queue<>();

        manager = SetupManager.getInstance();
        manager.setSetup(this);

        frame = new JFrame();
        count = 0;
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(KeyEvent e) {

                if(e.getID() == KeyEvent.KEY_TYPED) {
                    if(keyTyped != null) {
//                    keyTyped.action(e);
                        keyEventsPairs.insert(new KeyAndActionPair(keyTyped,e));

                    }
                    for(GTextField t : gTextFields) {
                        t.typed(e,lastKeyPressed == KeyEvent.VK_BACK_SPACE);
                    }
                }
                if(e.getID() == KeyEvent.KEY_PRESSED) {
                    lastKeyPressed = e.getKeyCode();
                    if(keyPressed != null) {
//                    keyPressed.action(e);
                        keyEventsPairs.insert(new KeyAndActionPair(keyPressed,e));
                    }
                    lastCharPressed = e.getKeyChar();
                }
                if(e.getID() == KeyEvent.KEY_RELEASED) {
                    if(e.getKeyCode() == lastKeyPressed) {
                        lastKeyPressed = -1;
                    }
                    if(keyReleased != null) {
//                    keyReleased.action(e);
                        keyEventsPairs.insert(new KeyAndActionPair(keyReleased,e));
                    }
                    if(e.getKeyChar() == lastCharPressed) {
                        lastCharPressed = '~';
                    }
                }
                return false;
            }
        });

//        frame.addKeyListener(new KeyListener() {
//            @Override
//            public void keyTyped(KeyEvent e) {
////                if(keyTyped != null) {
//////                    keyTyped.action(e);
////                    keyEventsPairs.insert(new KeyAndActionPair(keyTyped,e));
////
////                }
////                for(GTextField t : gTextFields) {
////                    t.typed(e,lastKeyPressed == KeyEvent.VK_BACK_SPACE);
////                }
//            }
//
//            @Override
//            public void keyPressed(KeyEvent e) {
////                System.out.println(e.getKeyCode());
////                lastKeyPressed = e.getKeyCode();
////                if(keyPressed != null) {
//////                    keyPressed.action(e);
////                    keyEventsPairs.insert(new KeyAndActionPair(keyPressed,e));
////                }
////                lastCharPressed = e.getKeyChar();
//            }
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//
////                if(e.getKeyCode() == lastKeyPressed) {
////                    lastKeyPressed = -1;
////                }
////                if(keyReleased != null) {
//////                    keyReleased.action(e);
////                    keyEventsPairs.insert(new KeyAndActionPair(keyReleased,e));
////                }
////                if(e.getKeyChar() == lastCharPressed) {
////                    lastCharPressed = '~';
////                }
//            }
//        });

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                lastFunction();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                lastFunction();
            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });

        isPainting = false;

        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(img,0,0,frame.getWidth(),frame.getHeight(),null);
                if(!loadShapedFaster) {

                    timeCountForFps += deltaTime();

//                if(deltaTime() < 0) {
//                    System.out.println("no");
//                }

                    if(!isPainting) {

                        img = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);

                        graphics = img.createGraphics();
                        updateSmoothness(smoothness,graphics);

                        if(font != null) {
                            graphics.setFont(font);
                        }
                        lastTime = currentTime;
                        currentTime = System.currentTimeMillis();

                        xScreen = (int)MouseInfo.getPointerInfo().getLocation().getX() - 8;
                        yScreen = (int)MouseInfo.getPointerInfo().getLocation().getY() - 31;

                        while (!keyEventsPairs.isEmpty()){
                            KeyAndActionPair pair = keyEventsPairs.remove();
                            pair.getAction().action(pair.getEvent());
                        }
                        execute();
                        thingsToDrawLast();
                    }

//                    SwingUtilities.invokeLater(() -> {
//                        panel.repaint();
//                    });
                    graphics.dispose();


                    if(!isPainting) {
                        isPainting = true;
                        if(img != null) {
                            g.drawImage(imgToDrawOn,0,0,frame.getWidth(),frame.getHeight(),null);
                        }
                        SetupManager.callTick();

                        isPainting = false;
                    }
//                System.out.println(timeCountForFps);
                    if(timeCountForFps > 0.25) {

                        timeCountForFps = 0;
                        for(int i = 0; i < fpsArr.length - 1; i++) {
                            fpsArr[i] = fpsArr[i + 1];
                        }
                        fpsArr[fpsArr.length - 1] = currentFPS();
                    }
                    manager.reDraw();

                }
                if((true ||img.getWidth() != frame.getWidth() || img.getHeight() != frame.getHeight()) && loadShapedFaster) {
                    img = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);
                    imgToDrawOn = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);
                }
            }
        };

        panel.setLayout(null);

        initialize();

        frame.setTitle(title);
        panel.setBackground(defaultBackground);
        frame.setSize(frameWidth,frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(resize);
        frame.add(panel);

        frame.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for(GFrameTextField textField : frameTextFields) {
                    textField.isPressed();
                    panel.remove(textField.getJTextField());
                    panel.add(textField.getJTextField());
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if(e.getButton() == 1) {
                    leftMouseClicked = true;
                    for(GButton b : gButtons) {
                        b.setPressed(true);
                    }
                    for(GTextField t : gTextFields) {
                        t.setPressed(true);
                    }
                }
                if(e.getButton() == 3) {
                    rightMouseClicked = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if(e.getButton() == 1) {
                    leftMouseClicked = false;
                    for(GButton b : gButtons) {
                        b.setPressed(false);
                    }
                    for(GTextField t : gTextFields) {
                        t.setPressed(false);
                    }
                }
                if(e.getButton() == 3) {
                    rightMouseClicked = false;
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                mouseOnFrame = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                mouseOnFrame = false;
            }
        });

        if(frameIcon != null) {
            frame.setIconImage(frameIcon);
        }

        executed = new Thread(() -> {

            while(!end()) {

                if(firstMethods) {
                    firstMethods = false;
                    for(Method method : getClass().getDeclaredMethods()) {
                        if(method.isAnnotationPresent(DoAtStart.class)) {
                            try {
                                method.invoke(this);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                if(count == 100) {

                    fps = (double)count/countTime;

//                    System.out.println(fps);

                    count = 0;
                    countTime = 0.0;

                }
                if(loadShapedFaster) {
//                    updateSmoothness(smoothness,graphics);
                    updateFrame();
                }
                panel.repaint();
                count++;
                countTime += (double)deltaTime();

            }
            frame.dispose();

        });

        executed.start();


    }
    public void loadShapedFaster(boolean drawFaster) {
        this.loadShapedFaster = drawFaster;
    }
    private void updateFrame() {
        timeCountForFps += deltaTime();

        if(!isPainting) {

            if(img == null) {
                img = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);
                imgToDrawOn = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);
            }

            graphicsToDrawFrom = img.createGraphics();
            graphics = imgToDrawOn.createGraphics();

            if(font != null) {
                graphics.setFont(font);
            }
            lastTime = currentTime;
            currentTime = System.currentTimeMillis();

            xScreen = (int)MouseInfo.getPointerInfo().getLocation().getX() - 8;
            yScreen = (int)MouseInfo.getPointerInfo().getLocation().getY() - 31;

            while (!keyEventsPairs.isEmpty()){
                KeyAndActionPair pair = keyEventsPairs.remove();
                pair.getAction().action(pair.getEvent());
            }
            updateSmoothness(smoothness,graphics);
            execute();
            thingsToDrawLast();
        }

        SwingUtilities.invokeLater(() -> {
            panel.repaint();
        });
        graphics.dispose();


        if(!isPainting) {
            isPainting = true;
            if(img != null) {
                graphicsToDrawFrom.drawImage(imgToDrawOn,0,0,frame.getWidth(),frame.getHeight(),null);
            }
            SetupManager.callTick();

            isPainting = false;
        }
        graphicsToDrawFrom.dispose();
//                System.out.println(timeCountForFps);
        if(timeCountForFps > 0.25) {

            timeCountForFps = 0;
            for(int i = 0; i < fpsArr.length - 1; i++) {
                fpsArr[i] = fpsArr[i + 1];
            }
            fpsArr[fpsArr.length - 1] = currentFPS();
        }
        manager.reDraw();
    }
    public void moveCanvas(int x, int y) {
        frame.setLocation(x,y);
    }
    public void setCanvasX(int x){
        frame.setLocation(x,frame.getY());
    }
    public void setCanvasY(int y){
        frame.setLocation(frame.getX(),y);
    }
    public int getCanvasX() {
        return frame.getX();
    }
    public int getCanvasY() {
        return frame.getY();
    }
    private void thingsToDrawLast() {
        for(GComponent component : componentsToPaintLast) {
            component.draw(graphics);
        }
        for(GTextView textView : textViews) {
            textView.draw(this);
        }
    }
    public enum Smoothness {
        VERY_SMOOTH,
        NORMAL,
        NOT_SMOOTH
    }
    public void setSmoothness(Smoothness smoothness) {
        this.smoothness = smoothness;
    }
    void updateSmoothness(Smoothness amount,Graphics2D graphics) {
        switch (amount) {
            case VERY_SMOOTH -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            case NORMAL -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_DEFAULT);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
            }
            case NOT_SMOOTH -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }
        }
    }
    /**
     * Draws an ellipse on the screen
     * @param     x the x of the ellipse
     * @param     y the y of the ellipse
     * @param     width the width of the ellipse
     * @param     height the height of the ellipse
     * @param     color the color of the ellipse
     */
    public void drawEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawOval(x,y,width,height);
        }
    }
    /**
     * Fills an ellipse on the screen
     * @param     x the x of the ellipse
     * @param     y the y of the ellipse
     * @param     width the width of the ellipse
     * @param     height the height of the ellipse
     * @param     color the color of the ellipse
     */
    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillOval(x,y,width,height);
        }
    }
    /**
     *Draws a rectangle on the screen
     * @param     x the x of the rectangle
     * @param     y the y of the rectangle
     * @param     width the width of the rectangle
     * @param     height the height of the rectangle
     * @param     color the color of the rectangle
     */
    public void drawRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawRect(x,y,width,height);
        }
    }
    /**
     *Fills a rectangle on the screen
     * @param     x the x of the rectangle
     * @param     y the y of the rectangle
     * @param     width the width of the rectangle
     * @param     height the height of the rectangle
     * @param     color the color of the rectangle
     */
    public void fillRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillRect(x,y,width,height);
        }
    }
    /**
     *Draws a polygon by a set of given points
     * @param points the array of points of the polygon
     * @param color the color of the polygon
     */
    public void drawPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.drawPolygon(x,y, points.length);
    }
    /**
     *Draws a polygon by a set of given points
     * @param x an array of x coordinates
     * @param y an array of y coordinates
     * @param color the color of the polygon
     */
    public void drawPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.drawPolygon(x,y,x.length);
    }
    /**
     *Fills a polygon by a set of given points
     * @param points the array of points of the polygon
     * @param color the color of the polygon
     */
    public void fillPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.fillPolygon(x,y, points.length);
    }
    /**
     *Fills a polygon by a set of given points
     * @param x an array of x coordinates
     * @param y an array of y coordinates
     * @param color the color of the polygon
     */
    public void fillPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.fillPolygon(x,y,x.length);
    }
    /**
     * Draws a line through 2 points
     * @param     x1 the x of the first point
     * @param     y1 the y of the first point
     * @param     x2 the x of the second point
     * @param     y2 the y of the second point
     * @param     color the color of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawLine(x1,y1,x2,y2);
        }
    }
    /**
     *Draws text on the screen (font can be edited using the setFont() method)
     * @param     x the x of the text
     * @param     y the y of the text
     * @param     text the text to be drawn
     * @param     color the color of the text
     */
    public void drawText(int x, int y,String text, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawString(text,x,y);

        }
    }
    /**
     *Draws an image on the screen by given coordinates and dimensions and a GImage
     * @param     x the x of the image
     * @param     y the y of the image
     * @param     width the width of the image
     * @param     height the height of the image
     * @param     image the image to be drawn
     */
    public void drawImage(int x, int y, int width, int height, GImage image) {

        if(image.getAngle() != 0) {
            graphics.translate(x + width/2,y + height/2);

            graphics.rotate(Math.toRadians(image.getAngle()));

            graphics.drawImage(image.getImage(),-(width/2),-(height/2),width,height,null);

            graphics.rotate(-Math.toRadians(image.getAngle()));
            graphics.translate(-x - width/2,-y - height/2);
        }
        else {
            graphics.drawImage(image.getImage(),x,y,width,height,null);
        }
    }
    /**
     *Draws an image on the screen by given coordinates and dimensions and a path to the image in the resources directory
     * @param     x the x of the image
     * @param     y the y of the image
     * @param     width the width of the image
     * @param     height the height of the image
     * @param     path the path of the image
     */
    public void drawImage(int x, int y, int width, int height, String path) {

        path = "/" + path;

        try {
            graphics.drawImage(ImageIO.read(getClass().getResourceAsStream(path)),x,y,width,height,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     *Draws a GButton on the screen
     * @param     button the GButton to be drawn
     */
    public void drawButton(GButton button) {
        button.draw(graphics);
    }
    /**
     *Draws a GTextField on the screen
     * @param     textField the GTextField to be drawn
     */
    public void drawTextField(GTextField textField) {
        textField.draw(graphics);
    }
    public void drawGProgressBar(GProgressBar progressBar) {
        progressBar.draw((GSetup) this);
    }
    /**
     *Adds a GFrameButton to the setup
     * @param     button the button
     */
    public void addFrameButton(GFrameButton button) {
        panel.add(button.getJButton());
        buttons.add(button);
    }
    /**
     *Adds a GFrameTextField to the setup
     * @param textField the GFrameTextField
     */
    public void addFrameTextField(GFrameTextField textField) {
        panel.add(textField.getJTextField());
        frameTextFields.add(textField);
    }
    /**
     *Adds a GButton to the setup
     * @param     button the button
     */
    public void addGButton(GButton button) {
        gButtons.add(button);
        if(button.getClass().isAnnotationPresent(PaintLast.class)) {
//            System.out.println("yes");
            componentsToPaintLast.add(button);
        }
    }
    /**
     *Adds a GTextField to the setup
     * @param     textField the GTextField
     */
    public void addGTextField(GTextField textField) {
        gTextFields.add(textField);
    }
    public void addGTextView(GTextView textView) {
        panel.add(textView.getTextArea());
        textViews.add(textView);
    }
    public void mouseClickedFromOutSide() {
        if(!mouseOnFrame) {
            for(GTextField textField : gTextFields) {
                textField.turnOff();
            }
        }
    }
    /**
     *Adds a GPanel to the setup
     * @param     panel the panel
     */
    public void addGPanel(GPanel panel) {
        panels.add(panel);
        panel.isAdded = true;
    }
    public void setKeyPressedAction(KeyEventSupplier event) {
        keyPressed = event;
    }
    public void setKeyTypedAction(KeyEventSupplier event) {
        keyTyped = event;
    }
    public void setKeyReleasedAction(KeyEventSupplier event) {
        keyReleased = event;
    }

    /**
     * Sets the icon of the frame to an image given from a path
     * @param     path the path to the image
     */
    public void setFrameIcon(String path){

        path = "/" + path;

        try {
            frameIcon = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(frame != null) {
            frame.setIconImage(frameIcon);
        }
    }
    /**
     * Sets the icon of the frame to a given GImage
     * @param     image the image
     */
    public void setFrameIcon(GImage image) {
        frameIcon = image.getImage();
        if(frame != null) {
            frame.setIconImage(frameIcon);
        }
    }
    /**
     * Sets a new font
     * @param     font the new font
     */
    public void setFont(Font font) {
        this.font = font;
        updateFont = true;
    }
    /**
     * Sets new dimensions for the screen
     * @param width the width of the screen
     * @param height the height of the screen
     */
    public void setFrameSize(int width, int height) {
        frameWidth = width;
        frameHeight = height;
    }
    /**
     * No explanation needed
     */
    public void setResizable(boolean resize) {
        this.resize = resize;
    }
    /**
     * Sets the title of the screen
     * @param     title the new title
     */
    public void setTitle(String title) {
        this.title = title;
        if(frame != null) {
            frame.setTitle(title);
        }
    }
    /**
     * Sets the default background color of the screen
     * @param     color the color
     */
    public void setBackground(Color color) {
        panel.setBackground(color);
        defaultBackground = color;
    }
    /**
     * @return the time passed since the last frame in seconds
     */
    public double deltaTime() {
        return ((currentTime - lastTime)/1000 < 1000000000 && (currentTime - lastTime)/1000 > 0)? (currentTime - lastTime)/1000 : 0;
//        return (currentTime - lastTime)/1000;
    }
    /**
     * Shows the regular JColorChooser dialog
     */
    public Color GColorPicker() {
        JColorChooser j = new JColorChooser();
        return JColorChooser.showDialog(j,"Color Picker",Color.WHITE);
    }
    /**
     * Creates a new GFile object based on a path to a file.
     * If the given file does not exist, it will create a new file in that name
     * @param     path the path to the file
     */
    public GFile loadFile(String... path) {

        String p = "";

        String name = path[path.length-1];

        for(int i = 0; i < path.length - 1; i++) {
            p += path[i] + "/";
        }

        File f = new File(p.substring(0,p.length()-1));

        if(!f.exists()) {
            f.mkdir();
        }
        f = new File(p + name);

//        System.out.println(f.getPath());

        return new GFile(f);
    }
//    public GFile loadFile(String path, String name) {
//        File f = new File(path);
//
//        if(!f.exists()) {
//            f.mkdir();
//        }
//        f = new File(path + "/" + name);
//
//        return new GFile(f);
//    }
    /**
     * Creates a new GFile object in the GSetup resources directory.
     * If the given file name does not already exist in the GSetup resources directory, it will create a new file in that name
     * @param     name the name of the file
     */
//    public GFile loadFileInGSetupResources(String name) {
//
//        File f = new File(getPath() + "/GSetup");
//
//        if(!f.exists()) {
//            f.mkdir();
//        }
//        String s = getClass().getName();
//        s = s.split("\\.")[s.split("\\.").length - 1];
//        f = new File(getPath() + "/GSetup/" + s);
//        if(!f.exists()) {
//            f.mkdir();
//        }
//        f = new File(getPath() + "\\GSetup\\" + s + "\\" + name);
//
////        System.out.println(f.exists());
//
////        System.out.println(getPath() + "\\GSetup\\" + s + "\\" + name);
//        return new GFile(f);
//    }
    public GFile loadFileInGSetupResources(String... name) {

        String folder = "";
        for(int i = 0; i < name.length-1; i++) {
            folder += name[i] + "/";
        }

        File f = new File(getPath() + "/" + folder + "/GSetup");

        if(!f.exists()) {
            f.mkdir();
        }
        String s = getClass().getName();
        s = s.split("\\.")[s.split("\\.").length - 1];
        f = new File(getPath() + "/GSetup/" + s + "/" + folder);
        if(!f.exists()) {
            f.mkdirs();
        }
        f = new File(getPath() + "\\" + "\\GSetup\\" + s + "\\" + folder + "\\" +  name[name.length-1]);

        return new GFile(f);
    }
    public GFile loadFileInGSetupResources(String folder ,String name) {

        File f = new File(getPath() + "/" + folder + "/GSetup");

        if(!f.exists()) {
            f.mkdir();
        }
        String s = getClass().getName();
        s = s.split("\\.")[s.split("\\.").length - 1];
        f = new File(getPath() + "/GSetup/" + s + "/" + folder);
        if(!f.exists()) {
            f.mkdirs();
        }
        f = new File(getPath() + "\\" + "\\GSetup\\" + s + "\\" + folder + "\\" +  name);

        return new GFile(f);
    }
    /**
     * Creates a new GImage object from an image file.
     * @param     path the path to the image file
     */
    public GImage loadFromPath(String path) {
        try {
            return new GImage(ImageIO.read(new File(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * @return  the path to the project (or jar file if compiled)
     */
    public String getPath() {
        return System.getProperty("user.dir");
    }
    /**
     * Shows the regular JFileChooser dialog
     */
    public String GFileChooser(String approveButton) {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(frame,approveButton);
        return fc.getSelectedFile().getAbsolutePath();
    }
    /**
     * Shows the regular JFileChooser dialog
     */
    public String GFolderChooser(String approveButton) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showDialog(frame,approveButton);
        return fc.getSelectedFile().getAbsolutePath();
    }
    /**
     * @return  the BufferedImage object of the GImage of the canvas
     */
    public BufferedImage getBufferedImg() {
        return img;
    }
    /**
     * @return  the GImage object of the canvas
     */
    public GImage getGImage() {
        return new GImage(img);
    }
    /**
     * @return  the current width of the screen
     */
    public int getFrameWidth() {
        return frame.getWidth();
    }
    /**
     * @return  the current height of the screen
     */
    public int getFrameHeight() {
        return frame.getHeight();
    }
    /**
     * @return  the x of the cursor on the screen
     */
    public int xOnScreen() {
        return xScreen;
    }
    /**
     * @return  the y of the cursor on the screen
     */
    public int yOnScreen() {
        return yScreen;
    }
    /**
     * @return  the x of the cursor on the canvas
     */
    public int xOnCanvas() {
        return xScreen - frame.getX();
    }
    /**
     * @return  the y of the cursor on the canvas
     */
    public int yOnCanvas() {
        return yScreen - frame.getY();
    }
    public double[] getFpsArr() {
        return fpsArr;
    }
    /**
     * @return  the last key that the user pressed
     */
    public int lastKey() {
        return lastKeyPressed;
    }
    /**
     * @return  the char value of the last key that the user pressed
     */
    public char lastKeyChar() {
        return lastCharPressed;
    }
    /**
     * @return  if left click
     */
    public boolean leftClick() {
        return leftMouseClicked;
    }
    /**
     * @return  if right click
     */
    public boolean rightClick() {
        return rightMouseClicked;
    }
    /**
     * @return  the current fps
     */
    public double currentFPS() {
        return fps;
    }
}