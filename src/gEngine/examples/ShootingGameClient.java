package gEngine.examples;

import gEngine.Vec2D;
import gEngine.VirtualGImage;
import gEngine.VirtualGSetup;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

public class ShootingGameClient extends VirtualGSetup {

    VirtualGImage hand;
    VirtualGImage draw;
    Vec2D pos;
    Vec2D velocity;
    int count = 0;
    final int width = 50;
    final int height = 50;
    final int movingStartingSpeed = 50;
    int floor;
    boolean onFloor;
    boolean movingLeft;
    boolean movingRight;

    public ShootingGameClient(int port) {
        super(port);
    }

    @Override
    public void initialize() {
        movingLeft = false;
        movingRight = false;
        onFloor = false;
        floor = 400;
        pos = new Vec2D(400, 200);
        velocity = new Vec2D(0,0);
//        VirtualGImage tmp = new VirtualGImage(16,16, this);
        hand = new VirtualGImage("shootingGame\\Hand.png", this);
        hand.fillEllipse(0,0,100,100,Color.BLACK);
        draw = new VirtualGImage(900,600,this);
    }

    private void physics() {
        double time = deltaTime()*5;

        if(isKeyPressed(KeyEvent.VK_SPACE) && !onFloor && velocity.y > 0) {
            velocity.y = 40;
        }
        else {
            velocity.y += 105*time;
        }

        onFloor &= !(onFloor && velocity.y < 0);

        if(onFloor && velocity.x != 0) {
            velocity.x += (velocity.x < 0 ? (lastKey() == KeyEvent.VK_A? 0 : 30*time) : (lastKey() == KeyEvent.VK_D? 0 : -30*time));
            if(Math.abs(velocity.x) < 30 && lastKey() != KeyEvent.VK_A && lastKey() != KeyEvent.VK_D) {
                velocity.x = 0;
            }
        }

//        pos.x += (velocity.x + (movingLeft? -movingStartingSpeed : (movingRight? movingStartingSpeed : 0)))*time;
        pos.x += (velocity.x)*time;
        pos.y += (velocity.y)*time;

        if(pos.y + height/2.0 >= floor) {
            pos.y = floor - height/2.0;
            if(velocity.y > 0) {
                velocity.y = 0;
                onFloor = true;
            }
        }
    }

    private void controls() {
        double time = deltaTime()*5;
        if((isKeyPressed(KeyEvent.VK_SPACE) || isKeyPressed(KeyEvent.VK_W)) && onFloor) {
            velocity.y = -200;
        }
        if(isKeyPressed(KeyEvent.VK_D)) {
            movingRight = true;
            velocity.x += 20*time;
            velocity.x = Math.min(velocity.x, 100);
        }
        if(isKeyPressed(KeyEvent.VK_A)) {
            movingLeft = true;
            velocity.x -= 20*time;
            velocity.x = Math.max(velocity.x, -100);
        }
        if(lastKey() == -1) {
            movingRight = false;
            movingLeft = false;
        }
//        switch (lastKey()) {
//            case KeyEvent.VK_D -> {
//                movingRight = true;
//                velocity.x += 20*time;
//                velocity.x = Math.min(velocity.x, 100);
//            }
//            case KeyEvent.VK_A -> {
//                movingLeft = true;
//                velocity.x -= 20*time;
//                velocity.x = Math.max(velocity.x, -100);
//            }
//            default -> {
//                movingRight = false;
//                movingLeft = false;
//            }
//        }
    }

    @Override
    public void execute() {
        if(lastKey() == KeyEvent.VK_ESCAPE) {
            fillRectangle(0,0,100,100,Color.RED);
        }
//        xOnCanvas();
        controls();
        physics();
//        fillRectangle(xOnCanvas(), (int)yOnCanvas(), 50, 50, Color.BLUE);
//        fillRectangle((int)pos.x, (int)pos.y, 50, 50, Color.BLUE);
        draw.fillRectangle(0,0,900,600,Color.WHITE);
        if(onFloor) {
            draw.fillRectangle(0,0,50,50,Color.RED);
        }
        draw.drawImage((int)pos.x-width/2, (int)pos.y-height/2, width, height, hand);
        drawImage(0,0,getFrameWidth(),getFrameHeight(),draw);
        drawLine(0,floor,900, floor, Color.BLACK);
    }

    @Override
    public boolean end() {
        return lastKey() == KeyEvent.VK_ESCAPE;
//        return true;
    }
}
