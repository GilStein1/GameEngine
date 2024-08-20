package gEngine;

import gEngine.Arrays.Queue;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class GSetup {
    private boolean firstMethods;
    public int frameWidth = 900;
    public int frameHeight = 600;
    public double lastTime = System.currentTimeMillis();
    public double currentTime;
    public boolean resize = true;
    private boolean mouseOnFrame = true;
    private boolean isPainting;
    public String title = "";
    private final JFrame frame;
    private final SetupManager manager;
    private final JPanel panel;
    private final java.util.List<GPanel> panels = new java.util.ArrayList<>();
    private final java.util.List<GFrameButton> buttons = new java.util.ArrayList<>();
    private final java.util.List<GFrameTextField> frameTextFields = new java.util.ArrayList<>();
    private final java.util.List<GButton> gButtons = new java.util.ArrayList<>();
    private final java.util.List<GTextField> gTextFields = new java.util.ArrayList<>();
    private final java.util.List<GTextView> textViews = new java.util.ArrayList<>();
    private final ArrayList<GComponent> componentsToPaintLast = new ArrayList<>();
    public Thread executed;
    public BufferedImage img;
    public Graphics2D graphics;
    private BufferedImage frameIcon;
    public Color defaultBackground = new Color(238, 238, 238);
    public Font font;
    private int count;
    private double timeCountForFps = 0;
    private double countTime = 0;
    private double fps = 0;
    private int lastKeyPressed = -1;
    private char lastCharPressed = '~';
    private KeyEventSupplier keyTyped;
    private KeyEventSupplier keyPressed;
    private KeyEventSupplier keyReleased;
    private final ArrayList<Integer> keysPressed = new ArrayList<>();
    private final Queue<KeyAndActionPair> keyEventsPairs;
    private final double[] fpsArr;
    private boolean leftMouseClicked = false;
    private boolean rightMouseClicked = false;
    private int xScreen = 0;
    private int yScreen = 0;
    private BufferedImage imgToDrawOn;
    private Graphics graphicsToDrawFrom;
    boolean loadShapesFaster = false;
    private gEngine.utilities.Smoothness smoothness;
    private boolean continueRun;
    private int extendedState;
    private boolean doFullScreen;
    private boolean hasInitialized;
    private int limitedFps;
    private final Robot robot;
    private int cursorXOffset = 0;
    private int cursorYOffset = 0;

    /**
     * The first method to run at the beginning of the program.
     * The initialize method is used to initialize values and parameters
     */
    public abstract void initialize();

    /**
     * The method to be executed repeatedly as long as the program is running (runs on a separate thread).
     * At the end of each cycle, the frame is being drawn.
     * At the start of each cycle, the deltaTime and currentFPS values are updated.
     */
    public abstract void execute();

    /**
     * The method to run at the end of the program, is called when the frame's WindowListener detects the window is being closed.
     */
    public void lastFunction() {

    }

    /**
     * The method to decide when the program should end
     *
     * @return the value that decides if the program should stop running.
     */
    public abstract boolean end();


    public GSetup() {
        try {
            robot = new Robot();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }

        limitedFps = -1;

        hasInitialized = false;

        doFullScreen = SetupManager.Debug.runFullScreen();

        if (SetupManager.getInstance().getSetupClass() != this.getClass()) {
            throw new RuntimeException("illegal use of the class");
        }

        smoothness = gEngine.utilities.Smoothness.NORMAL;
        firstMethods = true;
        continueRun = true;

        fpsArr = new double[100];
        Arrays.fill(fpsArr, 0.0);

        keyEventsPairs = new Queue<>();

        manager = SetupManager.getInstance();
        manager.setSetup(this);

        frame = new JFrame();

        count = 0;
        KeyboardFocusManager
                .getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(new Handlers.GKeyEventHandled());

        frame.addWindowListener(new Handlers.GWindowHandler());

        isPainting = false;

        panel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.drawImage(img, 0, 0, frame.getWidth(), frame.getHeight(), null);
                if (img != null && !loadShapesFaster) {
                    Graphics2D g2 = img.createGraphics();
                    g2.setColor(defaultBackground);
                    g2.fillRect(0, 0, frame.getWidth(), frame.getHeight());
                    g2.dispose();
                }
                if (!loadShapesFaster) {

                    timeCountForFps += deltaTime();

                    if (!isPainting) {

                        if (img == null || (img.getWidth() != frame.getWidth() || img.getHeight() != frame.getHeight())) {
                            img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        }

                        graphics = img.createGraphics();
                        updateSmoothness(smoothness, graphics);

                        if (font != null) {
                            graphics.setFont(font);
                        }
                        if (limitedFps == -1) {
                            lastTime = currentTime;
                            currentTime = Math.abs(System.nanoTime()) / 1000000.0;
                        }

                        xScreen = (int) MouseInfo.getPointerInfo().getLocation().getX() - 8 + cursorXOffset;
                        yScreen = (int) MouseInfo.getPointerInfo().getLocation().getY() - 31 + cursorYOffset;

                        while (!keyEventsPairs.isEmpty()) {
                            KeyAndActionPair pair = keyEventsPairs.remove();
                            pair.getAction().action(pair.getEvent());
                        }
                        execute();
                        if(GWebView.isCreated()) {
                            GWebView.updateFrame();
                        }
                        if(GWebView.isCreated() && GWebView.isCopyingScreen()) {
                            GWebView.setImage(getGImage());
                        }
                        thingsToDrawLast();
                    }
                    graphics.dispose();


                    if (!isPainting) {
                        isPainting = true;
                        if (img != null) {
                            g.drawImage(imgToDrawOn, 0, 0, frame.getWidth(), frame.getHeight(), null);
                        }
                        SetupManager.callTick();

                        isPainting = false;
                    }
//                System.out.println(timeCountForFps);
                    if (timeCountForFps > 0.25) {

                        timeCountForFps = 0;
                        for (int i = 0; i < fpsArr.length - 1; i++) {
                            fpsArr[i] = fpsArr[i + 1];
                        }
                        fpsArr[fpsArr.length - 1] = currentFPS();
                    }
                    manager.reDraw();

                }
                if ((img.getWidth() != frame.getWidth() || img.getHeight() != frame.getHeight()) && loadShapesFaster) {
                    img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                    imgToDrawOn = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                }
            }
        };

        panel.setLayout(null);

        extendedState = JFrame.NORMAL;

        initialize();

        if (doFullScreen) {
            frame.setUndecorated(true);
            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        frame.setTitle(title);
        panel.setBackground(defaultBackground);
        frame.setSize(frameWidth, frameHeight);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setResizable(resize);
        frame.add(panel);
        hasInitialized = true;

        frame.addMouseListener(new Handlers.GMouseListener());

        if (frameIcon != null) {
            frame.setIconImage(frameIcon);
        }

        executed = new Thread(() -> {

            while (!end() && continueRun) {

                if (firstMethods) {
                    firstMethods = false;
                }
                if (limitedFps != -1) {
                    if (countTime > 1.0 / limitedFps) {
                        fps = 1.0 / countTime;
                        if (fps == 1.0 / 0) {
                            fps = 0;
                        }
                        countTime = 0;
                        if (loadShapesFaster) {
                            updateFrame();
                        }
                        panel.repaint();
                    }
                    lastTime = currentTime;
                    currentTime = Math.abs(System.nanoTime()) / 1000000.0;
                } else {
                    if (count == 100) {

                        fps = (double) count / countTime;
                        if (fps == 1.0 / 0) {
                            fps = 0;
                        }
                        count = 0;
                        countTime = 0.0;

                    }
                    if (loadShapesFaster) {
                        lastTime = currentTime;
                        currentTime = Math.abs(System.nanoTime()) / 1000000.0;
                        updateFrame();
                    }
                    panel.repaint();
                    count++;
                }
                countTime += deltaTime();
            }
            frame.dispose();

        });

        executed.start();
    }

    //-------methods in use of the library-------
    //those methods are not meant to be used by the user

    KeyEventSupplier getKeyTyped() {
        return keyTyped;
    }

    KeyEventSupplier getKeyPressed() {
        return keyPressed;
    }

    KeyEventSupplier getKeyReleased() {
        return keyReleased;
    }

    void setLastKeyPressed(int lastKeyPressed) {
        this.lastKeyPressed = lastKeyPressed;
    }

    void setLastCharPressed(char lastCharPressed) {
        this.lastCharPressed = lastCharPressed;
    }

    void setMouseOnFrame(boolean mouseOnFrame) {
        this.mouseOnFrame = mouseOnFrame;
    }

    void setLeftMouseClicked(boolean leftMouseClicked) {
        this.leftMouseClicked = leftMouseClicked;
    }

    void setRightMouseClicked(boolean rightMouseClicked) {
        this.rightMouseClicked = rightMouseClicked;
    }

    void insertKeyAndActionPair(KeyAndActionPair pair) {
        keyEventsPairs.insert(pair);
    }

    void addToPanel(JComponent component) {
        panel.add(component);
    }

    void removeFromPanel(JComponent component) {
        panel.remove(component);
    }

    public ArrayList<GTextField> getGTextFields() {
        return (ArrayList<GTextField>)gTextFields;
    }

    public ArrayList<GFrameTextField> getGFrameTextFields() {
        return (ArrayList<GFrameTextField>)frameTextFields;
    }

    public ArrayList<GButton> getGButtons() {
        return (ArrayList<GButton>)gButtons;
    }

    void stop() {
        continueRun = false;
    }

    ArrayList<Integer> getKeysPressedArray() {
        return keysPressed;
    }

    void updateSmoothness(gEngine.utilities.Smoothness amount, Graphics2D graphics) {
        switch (amount) {
            case VERY_SMOOTH -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            case NORMAL -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_DEFAULT);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT);
            }
            case NOT_SMOOTH -> {
                graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }
        }
    }

    private void thingsToDrawLast() {
        for (GComponent component : componentsToPaintLast) {
            component.draw(graphics);
        }
        for (GTextView textView : textViews) {
            textView.draw(this);
        }
    }

    public void mouseClickedFromOutSide() {
        if (!mouseOnFrame) {
            for (GTextField textField : gTextFields) {
                textField.turnOff();
            }
        }
    }

    //--------------

    public GSetup drawShapesFaster(boolean drawFaster) {
        this.loadShapesFaster = drawFaster;
        return this;
    }

    private void updateFrame() {
        timeCountForFps += deltaTime();

        if (imgToDrawOn != null) {
            Graphics2D g2 = imgToDrawOn.createGraphics();
            g2.setColor(defaultBackground);
            g2.fillRect(0, 0, frame.getWidth(), frame.getHeight());
            g2.dispose();
        }

        if (!isPainting) {

            if (img == null) {
                img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
                imgToDrawOn = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_ARGB);
            }

            graphicsToDrawFrom = img.createGraphics();
            graphics = imgToDrawOn.createGraphics();

            if (font != null) {
                graphics.setFont(font);
            }

            xScreen = (int) MouseInfo.getPointerInfo().getLocation().getX() - 8 + cursorXOffset;
            yScreen = (int) MouseInfo.getPointerInfo().getLocation().getY() - 31 + cursorYOffset;

            while (!keyEventsPairs.isEmpty()) {
                KeyAndActionPair pair = keyEventsPairs.remove();
                pair.getAction().action(pair.getEvent());
            }
            updateSmoothness(smoothness, graphics);
            execute();
            if(GWebView.isCreated()) {
                GWebView.updateFrame();
            }
            if(GWebView.isCreated() && GWebView.isCopyingScreen()) {
                GWebView.setImage(getGImage());
            }
            thingsToDrawLast();
        }

        SwingUtilities.invokeLater(() -> {
            panel.repaint();
        });
        graphics.dispose();


        if (!isPainting) {
            isPainting = true;
            if (img != null) {
                graphicsToDrawFrom.drawImage(imgToDrawOn, 0, 0, frame.getWidth(), frame.getHeight(), null);
            }
            SetupManager.callTick();

            isPainting = false;
        }
        graphicsToDrawFrom.dispose();
        if (timeCountForFps > 0.25) {

            timeCountForFps = 0;
            for (int i = 0; i < fpsArr.length - 1; i++) {
                fpsArr[i] = fpsArr[i + 1];
            }
            fpsArr[fpsArr.length - 1] = currentFPS();
        }
        manager.reDraw();
    }

    public void setLimitedFps(int fps) {
        this.limitedFps = fps;
    }

    public boolean isKeyPressed(int key) {
        return keysPressed.contains(key);
    }

    public GSetup setExtendedState(int state) {
        this.extendedState = state;
        frame.setExtendedState(state);
        return this;
    }

    public void setFullScreen(boolean fullScreen) {
        doFullScreen = fullScreen;
        if(frame.isVisible()) {
            frame.setExtendedState(fullScreen? JFrame.MAXIMIZED_BOTH : JFrame.NORMAL);
        }
    }

    public void moveCanvas(int x, int y) {
        frame.setLocation(x, y);
    }

    public void setCanvasX(int x) {
        frame.setLocation(x, frame.getY());
    }

    public void setCanvasY(int y) {
        frame.setLocation(frame.getX(), y);
    }

    public int getCanvasX() {
        return frame.getX();
    }

    public int getCanvasY() {
        return frame.getY();
    }

    /**
     * Takes a screenshot of the screen
     *
     * @param x      the x of the screenshot
     * @param y      the y of the screenshot
     * @param width  the width of the screenshot
     * @param height the height of the screenshot
     * @return a GImage containing the screenshot
     */
    public GImage getScreenShot(int x, int y, int width, int height) {
        return new GImage(robot.createScreenCapture(new Rectangle(x, y, width, height)));
    }

    /**
     * @return the JFrame object of the canvas
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Sets a level of smoothness (Anti-Aliasing) of the renderer
     *
     * @param smoothness the amount of Anti-Aliasing
     */
    public void setSmoothness(gEngine.utilities.Smoothness smoothness) {
        this.smoothness = smoothness;
    }

    /**
     * Draws an ellipse on the screen
     *
     * @param x      the x of the ellipse
     * @param y      the y of the ellipse
     * @param width  the width of the ellipse
     * @param height the height of the ellipse
     * @param color  the color of the ellipse
     */
    public void drawEllipse(int x, int y, int width, int height, Color color) {
        if (img != null) {
            graphics.setColor(color);
            graphics.drawOval(x, y, width, height);
        }
    }

    /**
     * Fills an ellipse on the screen
     *
     * @param x      the x of the ellipse
     * @param y      the y of the ellipse
     * @param width  the width of the ellipse
     * @param height the height of the ellipse
     * @param color  the color of the ellipse
     */
    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if (img != null) {
            graphics.setColor(color);
            graphics.fillOval(x, y, width, height);
        }
    }

    /**
     * Draws a rectangle on the screen
     *
     * @param x      the x of the rectangle
     * @param y      the y of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the color of the rectangle
     */
    public void drawRectangle(int x, int y, int width, int height, Color color) {
        if (img != null) {
            graphics.setColor(color);
            graphics.drawRect(x, y, width, height);
        }
    }

    /**
     * Fills a rectangle on the screen
     *
     * @param x      the x of the rectangle
     * @param y      the y of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the color of the rectangle
     */
    public void fillRectangle(int x, int y, int width, int height, Color color) {
        if (img != null) {
            graphics.setColor(color);
            graphics.fillRect(x, y, width, height);
        }
    }

    /**
     * Draws a polygon by a set of given points
     *
     * @param points the array of points of the polygon
     * @param color  the color of the polygon
     */
    public void drawPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.drawPolygon(x, y, points.length);
    }

    /**
     * Draws a polygon by a set of given points
     *
     * @param x     an array of x coordinates
     * @param y     an array of y coordinates
     * @param color the color of the polygon
     */
    public void drawPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.drawPolygon(x, y, x.length);
    }

    /**
     * Fills a polygon by a set of given points
     *
     * @param points the array of points of the polygon
     * @param color  the color of the polygon
     */
    public void fillPolygon(Color color, Vec2D... points) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for (int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.fillPolygon(x, y, points.length);
    }

    /**
     * Fills a polygon by a set of given points
     *
     * @param x     an array of x coordinates
     * @param y     an array of y coordinates
     * @param color the color of the polygon
     */
    public void fillPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.fillPolygon(x, y, x.length);
    }

    /**
     * Draws a line through 2 points
     *
     * @param x1    the x of the first point
     * @param y1    the y of the first point
     * @param x2    the x of the second point
     * @param y2    the y of the second point
     * @param color the color of the line
     */
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if (img != null) {
            graphics.setColor(color);
            graphics.drawLine(x1, y1, x2, y2);
        }
    }

    /**
     * Draws text on the screen (font can be edited using the setFont() method)
     *
     * @param x     the x of the text
     * @param y     the y of the text
     * @param text  the text to be drawn
     * @param color the color of the text
     */
    public void drawText(int x, int y, String text, Color color) {
        if (img != null) {
            graphics.setColor(color);
            graphics.drawString(text, x, y);

        }
    }

    /**
     * Draws an image on the screen by given coordinates and dimensions and a GImage
     *
     * @param x      the x of the image
     * @param y      the y of the image
     * @param width  the width of the image
     * @param height the height of the image
     * @param image  the image to be drawn
     */
    public void drawImage(int x, int y, int width, int height, GImage image) {

        if (image.getAngle() != 0) {
            graphics.translate(x + width / 2, y + height / 2);

            graphics.rotate(Math.toRadians(image.getAngle()));

            graphics.drawImage(image.getImage(), -(width / 2), -(height / 2), width, height, null);

            graphics.rotate(-Math.toRadians(image.getAngle()));
            graphics.translate(-x - width / 2, -y - height / 2);
        } else {
            graphics.drawImage(image.getImage(), x, y, width, height, null);
        }
    }

    /**
     * Draws an image on the screen by given coordinates and dimensions and a path to the image in the resources directory
     *
     * @param x      the x of the image
     * @param y      the y of the image
     * @param width  the width of the image
     * @param height the height of the image
     * @param path   the path of the image
     */
    public void drawImage(int x, int y, int width, int height, String path) {

        path = "/" + path;

        try {
            graphics.drawImage(ImageIO.read(getClass().getResourceAsStream(path)), x, y, width, height, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Draws a GButton on the screen
     *
     * @param button the GButton to be drawn
     */
    public void drawButton(GButton button) {
        button.draw(graphics);
    }

    /**
     * Draws a GTextField on the screen
     *
     * @param textField the GTextField to be drawn
     */
    public void drawTextField(GTextField textField) {
        textField.draw(graphics);
    }

    /**
     * Draws a GProgressBar on the screen
     *
     * @param progressBar the GProgressBar to be drawn
     */
    public void drawGProgressBar(GProgressBar progressBar) {
        progressBar.draw(this);
    }

    /**
     * Adds a GFrameButton to the setup
     *
     * @param button the button
     */
    public void addFrameButton(GFrameButton button) {
        panel.add(button.getJButton());
        buttons.add(button);
    }

    /**
     * Adds a GFrameTextField to the setup
     *
     * @param textField the GFrameTextField
     */
    public void addFrameTextField(GFrameTextField textField) {
        panel.add(textField.getJTextField());
        frameTextFields.add(textField);
    }

    /**
     * Adds a GButton to the setup
     *
     * @param button the button
     */
    public void addGButton(GButton button) {
        gButtons.add(button);
        if (button.getClass().isAnnotationPresent(PaintLast.class)) {
//            System.out.println("yes");
            componentsToPaintLast.add(button);
        }
    }

    /**
     * Adds a GTextField to the setup
     *
     * @param textField the GTextField to be added
     */
    public void addGTextField(GTextField textField) {
        gTextFields.add(textField);
    }

    /**
     * Adds a GTextView to the setup
     *
     * @param textView the GTextView to be added
     */
    public void addGTextView(GTextView textView) {
        panel.add(textView.getTextArea());
        textViews.add(textView);
    }

    /**
     * Adds a GPanel to the setup
     *
     * @param panel the panel
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
     *
     * @param path the path to the image
     */
    public void setFrameIcon(String path) {

        path = "/" + path;

        try {
            frameIcon = ImageIO.read(getClass().getResourceAsStream(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (frame != null) {
            frame.setIconImage(frameIcon);
        }
    }

    /**
     * Sets the icon of the frame to a given GImage
     *
     * @param image the image
     */
    public void setFrameIcon(GImage image) {
        frameIcon = image.getImage();
        if (frame != null) {
            frame.setIconImage(frameIcon);
        }
    }

    /**
     * Sets an image for the cursor
     *
     * @param path the path to the image in project's resources
     */
    public void setCursorImage(String path) {
        frame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(new GImage(path).getImage(), new Point(0, 0), path));
    }

    /**
     * Sets an image for the cursor
     *
     * @param image the image for the cursor
     */
    public void setCursorImage(GImage image) {
        frame.getContentPane().setCursor(Toolkit.getDefaultToolkit().createCustomCursor(image.getImage(), new Point(0, 0), "cursor"));
    }

    /**
     * Sets an offset for the cursor
     * (note that this does not affect GFrameButtons and GFrameTextFields)
     *
     * @param x the offset in the x-axis
     * @param y the offset in the y-axis
     */
    public void setCursorOffset(int x, int y) {
        this.cursorXOffset = x;
        this.cursorYOffset = y;
    }

    /**
     * Sets a new font
     *
     * @param font the new font
     */
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Sets new dimensions for the screen
     *
     * @param width  the width of the screen
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
     *
     * @param title the new title
     */
    public void setTitle(String title) {
        this.title = title;
        if (frame != null) {
            frame.setTitle(title);
        }
    }

    /**
     * Sets the default background color of the screen
     *
     * @param color the color
     */
    public void setBackground(Color color) {
        panel.setBackground(color);
        defaultBackground = color;
    }

    /**
     * @return the time passed since the last frame in seconds
     */
    public double deltaTime() {
        return (Math.abs(currentTime - lastTime) / 1000 < 100000 && Math.abs(currentTime - lastTime) / 1000 > 0) ? Math.abs(currentTime - lastTime) / 1000 : 0;
//        return Math.abs(currentTime - lastTime)/1000;
    }

    /**
     * Shows the regular JColorChooser dialog
     */
    public Color GColorPicker() {
        JColorChooser j = new JColorChooser();
        return JColorChooser.showDialog(j, "Color Picker", Color.WHITE);
    }

    /**
     * Creates a new GFile object based on a path to a file.
     * If the given file does not exist, it will create a new file in that name
     *
     * @param path the path to the file
     */
    public GFile loadFile(String... path) {

        if (path.length != 1) {
            String p = "";

            String name = path[path.length - 1];

            for (int i = 0; i < path.length - 1; i++) {
                p += path[i] + "/";
            }

            File f = new File(p.substring(0, p.length() - 1));

            if (!f.exists()) {
                f.mkdir();
            }
            f = new File(p + name);

            return new GFile(f);
        } else {
            File f = new File(path[0]);
            if (!f.exists()) {
                f.mkdir();
            }
            return new GFile(f);
        }
    }

    /**
     * Creates a new GFile object in the GSetup resources directory.
     * If the given file name does not already exist in the GSetup resources directory, it will create a new file in that name
     *
     * @param name the name of the file
     */
    public GFile loadFileInGSetupResources(String... name) {

        String folder = "";
        for (int i = 0; i < name.length - 1; i++) {
            folder += name[i] + "/";
        }

        File f = new File(System.getenv("APPDATA") + "/" + folder + "/GSetup");

        if (!f.exists()) {
            f.mkdir();
        }
        String s = getClass().getName();
        s = s.split("\\.")[s.split("\\.").length - 1];
        f = new File(System.getenv("APPDATA") + "/GSetup/" + s + "/" + folder);
        if (!f.exists()) {
            f.mkdirs();
        }
        f = new File(System.getenv("APPDATA") + "\\" + "\\GSetup\\" + s + "\\" + folder + "\\" + name[name.length - 1]);

        return new GFile(f);
    }

    /**
     * Creates a new GImage object from an image file.
     *
     * @param path the path to the image file
     */
    public GImage loadFromPath(String path) {
        try {
            return new GImage(ImageIO.read(new File(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return the path to the project (or jar file if compiled)
     */
    public String getPath() {
        return System.getProperty("user.dir");
    }

    /**
     * Shows the regular JFileChooser dialog
     */
    public String GFileChooser(String approveButton) {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(frame, approveButton);
        return fc.getSelectedFile().getAbsolutePath();
    }

    /**
     * Shows the regular JFileChooser dialog
     */
    public String GFolderChooser(String approveButton) {
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.showDialog(frame, approveButton);
        return fc.getSelectedFile().getAbsolutePath();
    }

    public GWebView makeWebView(int port) {
        return GWebView.getInstance(port);
    }

    public GWebView makeWebView() {
        return GWebView.getInstance();
    }

    public GWebView makeWebView(GImage image, int port) {
        return GWebView.getInstance(image, port);
    }

    public GWebView makeWebView(GImage image) {
        return GWebView.getInstance(image);
    }

    /**
     * @return the BufferedImage object of the GImage of the canvas
     */
    public BufferedImage getBufferedImg() {
        return img;
    }

    /**
     * @return the GImage object of the canvas
     */
    public GImage getGImage() {
        return new GImage(img);
    }

    /**
     * @return the current width of the screen
     */
    public int getFrameWidth() {
        return frame.getWidth();
    }

    /**
     * @return the current height of the screen
     */
    public int getFrameHeight() {
        return frame.getHeight();
    }

    /**
     * @return the x of the cursor on the screen
     */
    public int xOnScreen() {
        return xScreen;
    }

    /**
     * @return the y of the cursor on the screen
     */
    public int yOnScreen() {
        return yScreen;
    }

    /**
     * @return the x of the cursor on the canvas
     */
    public int xOnCanvas() {
        return xScreen - frame.getX();
    }

    /**
     * @return the y of the cursor on the canvas
     */
    public int yOnCanvas() {
        return yScreen - frame.getY();
    }

    public double[] getFpsArr() {
        return fpsArr;
    }

    /**
     * @return the last key that the user pressed
     */
    public int lastKey() {
        return lastKeyPressed;
    }

    /**
     * @return the char value of the last key that the user pressed
     */
    public char lastKeyChar() {
        return lastCharPressed;
    }

    /**
     * @return if left click
     */
    public boolean leftClick() {
        return leftMouseClicked;
    }

    /**
     * @return if right click
     */
    public boolean rightClick() {
        return rightMouseClicked;
    }

    /**
     * @return the current fps
     */
    public double currentFPS() {
        return fps;
    }

    public void addKeyListener(KeyListener keyListener) {
        frame.addKeyListener(keyListener);
    }

    public void addMouseListener(MouseListener mouseListener) {
        frame.addMouseListener(mouseListener);
    }

    public void addWindowListener(WindowListener windowListener) {
        frame.addWindowListener(windowListener);
    }

    public void addMouseMotionListener(MouseMotionListener mouseMotionListener) {
        frame.addMouseMotionListener(mouseMotionListener);
    }

    public void addMouseWheelListener(MouseWheelListener mouseWheelListener) {
        frame.addMouseWheelListener(mouseWheelListener);
    }
}