package pack;

import pack.Arrays.Queue;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public abstract class GSetup implements GSetups{

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

    public GSetup() {

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

                timeCountForFps += deltaTime();

//                if(deltaTime() < 0) {
//                    System.out.println("no");
//                }

                if(!isPainting) {

                    img = new BufferedImage(frame.getWidth(),frame.getHeight(),BufferedImage.TYPE_INT_ARGB);
                    graphics = img.createGraphics();
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
                }

                SwingUtilities.invokeLater(() -> {
                    panel.repaint();
                });
                graphics.dispose();


                if(!isPainting) {
                    isPainting = true;
                    if(img != null) {
                        g.drawImage(img,0,0,frame.getWidth(),frame.getHeight(),null);
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

                if(count == 100) {

                    fps = (double)count/countTime;

//                    System.out.println(fps);

                    count = 0;
                    countTime = 0.0;

                }

                panel.repaint();
                count++;
                countTime += (double)deltaTime();

            }
            frame.dispose();

        });

        executed.start();


    }
    /**
     * פעולה שמציירת עיגול/אליפסה ריקים על המסך - רק את המסגרת של הצורה על פי צבע נתון, מיקום נתון ומימדים נתונים
     * @param     x המיקום על הציר האופקי של הצורה על המסך
     * @param     y המיקום על הציר האנכי של הצורה על המסך
     * @param     width רוחב הצורה
     * @param     height אורך הצורה
     * @param     color צבע הצורה
     */
    public void drawEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawOval(x,y,width,height);
        }
    }
    /**
     * פעולה שמציירת עיגול/אליפסה על המסך על פי צבע נתון, מיקום נתון ומימדים נתונים
     * @param     x המיקום על הציר האופקי של הצורה על המסך
     * @param     y המיקום על הציר האנכי של הצורה על המסך
     * @param     width רוחב הצורה
     * @param     height אורך הצורה
     * @param     color צבע הצורה
     */
    public void fillEllipse(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillOval(x,y,width,height);
        }
    }
    /**
     * פעולה שמציירת ריבוע/מלבן ריקים על המסך - רק את המסגרת של הצורה על פי צבע נתון, מיקום נתון ומימדים נתונים
     * @param     x המיקום על הציר האופקי של הצורה על המסך
     * @param     y המיקום על הציר האנכי של הצורה על המסך
     * @param     width רוחב הצורה
     * @param     height אורך הצורה
     * @param     color צבע הצורה
     */
    public void drawRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawRect(x,y,width,height);
        }
    }
    /**
     * פעולה שמציירת ריבוע/מלבן על המסך על פי צבע נתון, מיקום נתון ומימדים נתונים
     * @param     x המיקום על הציר האופקי של הצורה על המסך
     * @param     y המיקום על הציר האנכי של הצורה על המסך
     * @param     width רוחב הצורה
     * @param     height אורך הצורה
     * @param     color צבע הצורה
     */
    public void fillRectangle(int x, int y, int width, int height, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.fillRect(x,y,width,height);
        }
    }
    public void drawPolygon(Vec2D[] points, Color color) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.drawPolygon(x,y, points.length);
    }
    public void drawPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.drawPolygon(x,y,x.length);
    }

    public void fillPolygon(Vec2D[] points, Color color) {
        int[] x = new int[points.length];
        int[] y = new int[points.length];
        for(int i = 0; i < points.length; i++) {
            x[i] = (int) points[i].x;
            y[i] = (int) points[i].y;
        }
        graphics.setColor(color);
        graphics.fillPolygon(x,y, points.length);
    }
    public void fillPolygon(int[] x, int[] y, Color color) {
        graphics.setColor(color);
        graphics.fillPolygon(x,y,x.length);
    }
    /**
     * פעולה המציירת קו על המסך בין שתי נקודות נתונות
     * @param     x1 המיקום על הציר האופקי של הנקודה הראשונה על המסך
     * @param     y1 המיקום על הציר האנכי של הנקודה הראשונה על המסך
     * @param     x2 המיקום על הציר האופקי של הנקודה השנייה על המסך
     * @param     y2 המיקום על הציר האנכי של הנקודה השנייה על המסך
     * @param     color צבע הקו
     */
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawLine(x1,y1,x2,y2);
        }
    }
    /**
     * פעולה המקבלת טקסט ומציירת אותו על המסך על פי מיקום נתון וצבע נתון. אפשר להגדיר את הפונט של הכתיבה עם אובייקט הפונט של ג'אווה
     * @param     x המיקום על הציר האופקי של הטקסט
     * @param     y המיקום על הציר האנכי של הטקסט
     * @param     text הטקסט אותו רוצים להדפיס על המסך
     * @param     color צבע הטקסט
     */
    public void drawText(int x, int y,String text, Color color) {
        if(img != null) {
            graphics.setColor(color);
            graphics.drawString(text,x,y);

        }
    }
    /**
     * פעולה שמציירת תמונה על המסך על פי קובץ תמונה נתון, מיקום נתון ומימדים נתונים
     * @param     x המיקום על הציר האופקי של התמונה על המסך
     * @param     y המיקום על הציר האנכי של התמונה על המסך
     * @param     width רוחב התמונה
     * @param     height אורך התמונה
     * @param     image התמונה אותה הפעולה קוראת ומציירת על המסך
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
     * פעולה שמציירת תמונה על המסך על פי קובץ תמונה נתון, מיקום נתון ומימדים נתונים
     * @param     x המיקום על הציר האופקי של התמונה על המסך
     * @param     y המיקום על הציר האנכי של התמונה על המסך
     * @param     width רוחב התמונה
     * @param     height אורך התמונה
     * @param     path שם הקובץ של התמונה אותה הפעולה קוראת ומציירת על המסך
     */
    public void drawImage(int x, int y, int width, int height, String path) {

        path = "/" + path;

        try {
            graphics.drawImage(ImageIO.read(getClass().getResourceAsStream(path)),x,y,width,height,null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void drawButton(GButton button) {
        button.draw(graphics,xOnCanvas(),yOnCanvas());
    }
    public void drawTextField(GTextField textField) {
        textField.draw(graphics);
    }
    /**
     *פעולה המאפשרת הוספת כפתור למסך
     * @param     button הכפתור אותו רוצים להוסיף
     */
    public void addFrameButton(GFrameButton button) {
        panel.add(button.getJButton());
        buttons.add(button);
    }
    public void addFrameTextField(GFrameTextField textField) {
        panel.add(textField.getJTextField());
        frameTextFields.add(textField);
    }
    public void addGButton(GButton button) {
        gButtons.add(button);
    }
    public void addGTextField(GTextField textField) {
        gTextFields.add(textField);
    }
    public void mouseClickedFromOutSide() {
        if(!mouseOnFrame) {
            for(GTextField textField : gTextFields) {
                textField.turnOff();
            }
        }
    }
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
     *פעולה המקבלת תמונה ומשנה את אייקון החלון לתמונה הנתונה
     * @param     path שם ומיקום קובץ התמונה שרוצים לשים כאייקון לחלון
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
     *פעולה המקבלת תמונה ומשנה את אייקון החלון לתמונה הנתונה
     * @param image התמונה הרצוייה
     */
    public void setFrameIcon(GImage image) {
        frameIcon = image.getImage();
        if(frame != null) {
            frame.setIconImage(frameIcon);
        }
    }
    /**
     *פעולה המשנה את פונט הכתיבה הנוכחי על המסך על פי פונט נתון
     * @param font הפונט הרצוי
     */
    public void setFont(Font font) {
        this.font = font;
        updateFont = true;
    }
    /**
     *פעולה הקובעת את רוחב וגובה חלון התוכנית
     * @param width רוחב המסך
     * @param height גובה המסך
     */
    public void setFrameSize(int width, int height) {
        frameWidth = width;
        frameHeight = height;
    }
    /**
     *פעולה הקובעת האם אפשרי שינוי גודל המסך על ידי המשתמש
     */
    public void setResizable(boolean resize) {
        this.resize = resize;
    }
    /**
     *פעולה המשנה את כותרת התוכנית על פי טקסט נתון
     * @param title כותרת החלון
     */
    public void setTitle(String title) {
        this.title = title;
        if(frame != null) {
            frame.setTitle(title);
        }
    }
    /**
     *פעולה המשנה את צבע רקע התוכנית על פי צבע רקע נתון
     * @param color צבע הרקע החדש
     */
    public void setBackground(Color color) {
        panel.setBackground(color);
        defaultBackground = color;
    }
    /**
     * פעולה המחזירה את הזמן שעבר בין כל מחזור של התוכנית, משמש לספירת זמנים בלי חששות מקצב המחזוריות הלא קבוע של התוכנה
     * @return  מחזיר את הזמן שעבר מהפריים האחרון בשניות
     */
    public double deltaTime() {
        return ((currentTime - lastTime)/1000 < 1000000000 && (currentTime - lastTime)/1000 > 0)? (currentTime - lastTime)/1000 : 0;
//        return (currentTime - lastTime)/1000;
    }
    public Color GColorPicker() {
        JColorChooser j = new JColorChooser();
        return JColorChooser.showDialog(j,"Color Picker",Color.WHITE);
    }
    public GFile loadFile(String path, String name) {
        File f = new File(path);

        if(!f.exists()) {
            f.mkdir();
        }
        f = new File(path + "/" + name);

        return new GFile(f);
    }
    public GFile loadFileInGSetupResources(String name) {

        File f = new File(getPath() + "/GSetup");

        if(!f.exists()) {
            f.mkdir();
        }
        String s = getClass().getName();
        s = s.split("\\.")[s.split("\\.").length - 1];
        f = new File(getPath() + "/GSetup/" + s);
        if(!f.exists()) {
            f.mkdir();
        }
        f = new File(getPath() + "\\GSetup\\" + s + "\\" + name);

//        System.out.println(f.exists());

//        System.out.println(getPath() + "\\GSetup\\" + s + "\\" + name);

        return new GFile(f);
    }
    public GImage loadFromPath(String path) {
        try {
            return new GImage(ImageIO.read(new File(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getPath() {
        return System.getProperty("user.dir");
    }
    public String GFileChooser(String approveButton) {
        JFileChooser fc = new JFileChooser();
        fc.showDialog(frame,approveButton);
        return fc.getSelectedFile().getAbsolutePath();
    }
    public BufferedImage getBufferedImg() {
        return img;
    }
    public GImage getGImage() {
        return new GImage(img);
    }
    public int getFrameWidth() {
        return frame.getWidth();
    }
    public int getFrameHeight() {
        return frame.getHeight();
    }
    /**
     * @return  את המקום על הציר האופקי של העכבר ביחס למסך (ולא ביחס לחלון)
     */
    public int xOnScreen() {
//        this.mousePos = MouseInfo.getPointerInfo().getLocation();
//        return (int)this.mousePos.getX();
        return xScreen;
    }
    /**
     * @return  את המקום על הציר האנכי של העכבר ביחס למסך (ולא ביחס לחלון)
     */
    public int yOnScreen() {
//        this.mousePos = MouseInfo.getPointerInfo().getLocation();
//        return (int)this.mousePos.getY();
        return yScreen;
    }
    /**
     * @return  את המיקום על הציר האופקי של העכבר ביחס לחלון
     */
    public int xOnCanvas() {
//        this.mousePos = MouseInfo.getPointerInfo().getLocation();
//        return (int)this.mousePos.getX() - frame.getX();
        return xScreen - frame.getX();
    }
    /**
     * @return  את המיקום על הציר האנכי של העכבר ביחס לחלון
     */
    public int yOnCanvas() {
//        this.mousePos = MouseInfo.getPointerInfo().getLocation();
//        return (int)this.mousePos.getY() - frame.getY();
        return yScreen - frame.getY();
    }
    public double[] getFpsArr() {
        return fpsArr;
    }
    /**
     * @return  את ערך הכפתור האחרון שנלחץ במקלדת
     */
    public int lastKey() {
        return lastKeyPressed;
    }
    /**
     * @return  את ערך האות האחרונה שנלחצה במקלדת
     */
    public char lastKeyChar() {
        return lastCharPressed;
    }
    public boolean leftClick() {
        return leftMouseClicked;
    }
    public boolean rightClick() {
        return rightMouseClicked;
    }
    /**
     * @return  את מהירות החלפת הפריימים העדכנית
     */
    public double currentFPS() {
        return fps;
    }
}
