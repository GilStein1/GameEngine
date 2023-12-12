package pack;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class SetupManager {

    private static SetupManager instance;
    private static ArrayList<GComponent> tickCalls = new ArrayList<>();
    private GSetups setup;
    private JFrame fps;
    private JPanel fpsPanel;
    private boolean showFpsGraph = false;
    private double[] fpsArr;
    private SetupManager() {
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

    public static SetupManager getInstance() {
        if(instance == null) {
            instance = new SetupManager();
        }
        return instance;
    }
    public void setSetup(GSetups setup) {
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
    GSetups getSetup() {
        return setup;
    }
    public static class Debug {

        public static void fpsGraph() {

            instance.showFpsGraph(true);
            instance.getFpsFrame().setVisible(true);

        }

    }

}
