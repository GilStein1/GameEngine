package gEngine.examples;

import gEngine.GFile;
import gEngine.GImage;
import gEngine.GSetup;

import java.awt.*;
import java.awt.event.KeyEvent;

public class AngryFlappy extends GSetup {

    final double widthToScale = 900;
    final double heightToScale = 600;
    double[] walls;
    double wallsOffset;
    double backgroundOffSet;
    boolean failed;
    GImage bird;
    GImage failedMessage;
    GImage icon;
    GImage background;
    GFile keepScore;
    double birdY;
    double birdVY;
    boolean spacePressed;
    double ratioX;
    double ratioY;
    double counterForRestart;
    double countForFailAnimation;
    double speed;
    boolean start;
    int score;
    int bestScore;
    @Override
    public void initialize() {
//        setFullScreen(true);
        keepScore = loadFileInGSetupResources("Scores.txt");
        if(!keepScore.accidentallyCreatedNewFile()) {
            bestScore = Integer.parseInt(keepScore.read(),16);
//            System.out.println(keepScore.read());
//            bestScore = 0;
        }
        else {
            bestScore = 0;
        }
        start = false;
        failed = false;
        bird = new GImage("Bird.png");
        failedMessage = new GImage("fail-stamp-7.png");
        int failedX = failedMessage.getWidth();
        int failedY = failedMessage.getHeight();
        failedMessage = new GImage(failedX/5,failedY/5);
        failedMessage.drawImage(0,0,failedMessage.getWidth(),failedMessage.getHeight(),"fail-stamp-7.png");
        icon = new GImage(100,100);
        icon.fillEllipse(0,0,100,100,Color.BLACK);
        icon.fillEllipse(5,5,90,90,new Color(200,200,200));
        icon.drawImage(10,10,80,80,bird);
        birdY = 250;
        birdVY = 0;
        counterForRestart = 0;
        countForFailAnimation = 0;
        spacePressed = false;
        background = new GImage("bird background.png");
        setFrameIcon(icon);
        setTitle("Angry Flappy");
        walls = new double[5];
        for(int i = 0; i < walls.length; i++) {
            walls[i] = 100 + Math.random()*370;
//            walls[i] = Math.random()*600;
//            walls[i] = 470;
        }
        wallsOffset = 600;
        backgroundOffSet = 0;
        speed = 0.7;
        score = 0;
        setFullScreen(true);
    }
    void restart() {
        start = false;
        failed = false;
        birdY = 250;
        birdVY = 0;
        counterForRestart = 0;
        countForFailAnimation = 0;
        spacePressed = false;
        walls = new double[5];
        for(int i = 0; i < walls.length; i++) {
            walls[i] = 100 + Math.random()*370;
        }
        wallsOffset = 600;
        backgroundOffSet = 0;
        speed = 0.7;
        score = 0;
    }
    @Override
    public void execute() {

        if(failed) {
            counterForRestart += deltaTime();
            if(countForFailAnimation < 1) {
                countForFailAnimation += 2.5*deltaTime();
            }
        }

        if(failed && (leftClick() || lastKey() == KeyEvent.VK_SPACE) && !spacePressed && counterForRestart > 1) {
            counterForRestart = 0;
            restart();
        }


        if(birdY < -10 || birdY > 490) {
            failed = true;
        }

        if(wallsOffset < -100) {
            walls[0] = walls[1];
            walls[1] = walls[2];
            walls[2] = walls[3];
            walls[3] = walls[4];
            walls[4] = 100 + Math.random()*370;
            wallsOffset = 200;
            speed += 0.1;
            score++;
            bestScore = Math.max(score,bestScore);
        }
        ratioX = getFrameWidth()/widthToScale;
        ratioY = getFrameHeight()/heightToScale;

        if(!failed && start) {
            wallsOffset -= 70*deltaTime()*speed;
            backgroundOffSet -= deltaTime()*15*speed;
        }
        if(backgroundOffSet < -1000) {
            backgroundOffSet += 1000;
        }

        if(!spacePressed && (lastKey() == KeyEvent.VK_SPACE || leftClick()) && !failed && start) {
            spacePressed = true;
            birdVY = -420;
        }
        else if(!spacePressed && (lastKey() == KeyEvent.VK_SPACE || leftClick()) && !failed && !start) {
            start = true;
        }
        if(lastKey() == -1 && !leftClick()) {
            spacePressed = false;
        }

        if(start) {
            birdVY += 1500*deltaTime();
            birdY += birdVY*deltaTime();
        }

        bird.setAngle(Math.toDegrees(Math.atan(birdVY/1000)));

        drawImage((int) (backgroundOffSet*ratioX),0, (int) (1000*ratioX), (int) (600*ratioY),background);
        drawImage((int) ((backgroundOffSet + 1000)*ratioX),0, (int) (1000*ratioX), (int) (600*ratioY),background);
        drawWalls();
        setFont(new Font("Ariel",Font.BOLD, (int) (20*ratioX)));
        drawText(0, (int) (20*ratioY),"score: " + Integer.toString(score),Color.BLACK);
        drawText(0, (int) (40*ratioY),"best: " + Integer.toString(bestScore),Color.BLACK);
        drawImage((int) (100*ratioX), (int) ((int) birdY*ratioY), (int) (80*ratioX), (int) (80*ratioY),bird);
        failed = failed || checkIfIsInWalls();

        if(failed) {
            drawImage((int)((450 - countForFailAnimation*900/2)*ratioX),(int)((300 - countForFailAnimation*600/2)*ratioY), (int) (countForFailAnimation*900*ratioX), (int) (countForFailAnimation*600*ratioY),failedMessage);
        }

    }
    boolean checkIfIsInWalls() {
        for(int i = 0; i < walls.length; i++) {
            if(i*300 + wallsOffset < 180 && i*300+wallsOffset > 0) {
                if(birdY < walls[i] - 75 || birdY > walls[i] + 25) {
                    return true;
                }
            }
        }
        return false;
    }
    void drawWalls() {
        for(int i = 0; i < walls.length; i++) {
//            fillRectangle((int) (ratioX*(i*300 + wallsOffset)), (int) (ratioY*walls[i]), (int) (100*ratioX), (int) (100*ratioY),Color.RED);
            fillRectangle((int) (ratioX*(i*300 + wallsOffset)), (int) (ratioY*(walls[i] + 100)), (int) (100*ratioX), (int) (ratioY*1080),Color.GREEN);
            fillRectangle((int) (ratioX*(i*300 + wallsOffset)), (int) (ratioY*(walls[i] - 1150)), (int) (100*ratioX), (int) (ratioY*1080),Color.GREEN);
        }
    }
    @Override
    public void lastFunction() {
        keepScore.print(Integer.toHexString(bestScore));
        keepScore.stopWriting();
//        System.out.println(String.valueOf(bestScore));
    }
    @Override
    public boolean end() {
        return false;
    }
}
