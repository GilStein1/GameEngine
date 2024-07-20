package gEngine;

import gEngine.examples.Main;
import gEngine.utilities.ExecutableBuilder;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

public class SetupManager {

    private static SetupManager instance;
    private static ArrayList<GComponent> tickCalls = new ArrayList<>();
    private GSetup setup;
    private JFrame fps;
    private JPanel fpsPanel;
    private boolean showFpsGraph = false;
    private double[] fpsArr;
    private Class<?> setupClass;
    private HashMap<String, Object> valuesPool;
    private SetupManager() {
        valuesPool = new HashMap<>();
        fps = new JFrame("fps graph");
        fps.setSize(412,260);
        fps.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        fps.setResizable(false);
        fpsPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);

                g.setColor(Color.BLACK);

                if(fpsArr != null && showFpsGraph) {
                    double max = 0.1;
                    double min = fpsArr[0];

                    for(int i = 0; i < fpsArr.length; i++) {
                        max = Math.max(max,fpsArr[i]);
                        min = Math.min(min,fpsArr[i]);
//                        System.out.println(fpsArr[i]);
                    }
                    for(int i = 0; i < fpsArr.length - 1; i++) {
                        g.drawLine(i*4,(int)(200 - ((fpsArr[i]-min)*200/(max - min))),(i + 1)*4,(int)(200 - ((fpsArr[i + 1] - min)*200/(max - min))));
//                        System.out.println((int)(200 - (fpsArr[i]*200/(max - min))));

                    }
                    g.drawString("Fps = " + Double.toString(fpsArr[fpsArr.length - 1]),0,220);
                }


            }
        };
        fps.add(fpsPanel);
    }
    public static void makeExecutable(Class<?> mainClass, Class<?> game) {
        String[] path = System.getProperty("user.dir").split("\\\\");
        if(!path[path.length-2].equals("Users") && mainClass.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1).endsWith(".jar")) {

            System.out.println("it is compiled");

            File f = new File(System.getenv("APPDATA") + "/GSetup");

            if (!f.exists()) {
                f.mkdir();
            }
            String s = game.getName();
            s = "SetupManager\\" + s.split("\\.")[s.split("\\.").length - 1];
            f = new File(System.getenv("APPDATA") + "/GSetup/" + s);
            if (!f.exists()) {
                f.mkdirs();
            }
            f = new File(System.getenv("APPDATA") + "\\" + "\\GSetup\\" + s + "\\values.txt");

            GFile file = new GFile(f);

            if(file.accidentallyCreatedNewFile()) {
                file.println("executedCreated");
                file.stopWriting();
                System.out.println("starting to create executable");
                SetupManager.pushValueToPool(mainClass.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1), "fileToExecutable");
                startGame(ExecutableBuilder.class);

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }


    }
    public static SetupManager getInstance() {
        if(instance == null) {
            instance = new SetupManager();
        }
        return instance;
    }
    public static boolean pushValueToPool(Object value, String name) {
        boolean ret = getInstance().valuesPool.containsKey(name);
        instance.valuesPool.put(name, value);
        return ret;
    }
    public static Object pullFromPool(String name) {
        if(getInstance().valuesPool.containsKey(name)) {
            return getInstance().valuesPool.get(name);
        }
        else {
            throw new RuntimeException("pulled value does not exist");
        }
    }
    public static void moveToSetup(Class<?> setup) {
        ((GSetup)(instance.getSetup())).stop();
        startGame(setup);
    }
    public static GSetup startGame(Class<?> c) {
        GSetup setup = null;
        getInstance().setupClass = c;
        try {
            Constructor<?> con = c.getConstructor();
            setup = (GSetup) con.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassCastException e) {
            throw new RuntimeException("cannot start a class that is not a Setup");
        }
        return setup;
    }
    Class<?> getSetupClass() {
        return setupClass;
    }
    public void setSetup(GSetup setup) {
        this.setup = setup;
        fpsArr = setup.getFpsArr();
    }
    public static void addTick(GComponent GComponent) {
        tickCalls.add(GComponent);
    }
    static void callTick() {
        for(GComponent c : tickCalls) {
            c.tickUpdate();
        }
    }
    public JFrame getFpsFrame() {
        return fps;
    }
    void reDraw() {
        fpsPanel.repaint();
    }
    void showFpsGraph(boolean is) {
        showFpsGraph = is;
    }
    GSetup getSetup() {
        return setup;
    }
    public static class Debug {
        private static boolean runFullScreen = false;
        public static void fpsGraph() {
            instance.showFpsGraph(true);
            instance.getFpsFrame().setVisible(true);
        }
        public static void runFullScreen(boolean fullScreen) {
            runFullScreen = fullScreen;
        }
        public static boolean runFullScreen() {
            return runFullScreen;
        }

    }
}
