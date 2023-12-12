package pack;

public interface GSetups {

    /**
     * The first method to run at the beginning of the program.
     * The initialize method is used to initialize values and parameters
     */
    public void initialize();
    /**
     * The method to be executed repeatedly as long as the program is running (runs on a separate thread).
     * At the end of each cycle, the frame is being drawn.
     * At the start of each cycle, the deltaTime and currentFPS values are updated.
     */
    public void execute();
    /**
     * The method to run at the end of the program, is called when the frame's WindowListener detects the window is being closed.
     */
    public void lastFunction();
    /**
     * The method to decide when the program should end
     * @return the value that decides if the program should stop running.
     */
    public boolean end();

    double[] getFpsArr();
    double currentFPS();
    void addGButton(GButton button);
    void addGTextField(GTextField textField);
    int xOnCanvas();
    int yOnCanvas();
    int xOnScreen();
    int yOnScreen();
    double deltaTime();
    void mouseClickedFromOutSide();

}
