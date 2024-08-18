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
    final int height = 50;
    int floor;

    public ShootingGameClient(int port) {
        super(port);
    }

    @Override
    public void initialize() {
        floor = 600;
        pos = new Vec2D(400, 400);
        velocity = new Vec2D(0,0);
//        VirtualGImage tmp = new VirtualGImage(16,16, this);
        hand = new VirtualGImage("shootingGame\\Hand.png", this);
        hand.fillEllipse(0,0,100,100,Color.BLACK);
        draw = new VirtualGImage(900,600,this);
        setFullScreen(true);
    }

    private void physics() {
        double time = deltaTime()*3;

        if(pos.y + height/2.0 > floor) {
            pos.y = floor - height/2.0;
            velocity.y = -velocity.y;
        }

        velocity.y += 105*time;
        pos.x += velocity.x*time;
        pos.y += velocity.y*time;
    }

    @Override
    public void execute() {
        if(lastKey() == KeyEvent.VK_ESCAPE) {
            fillRectangle(0,0,100,100,Color.RED);
        }
//        xOnCanvas();
        physics();
//        fillRectangle(xOnCanvas(), (int)yOnCanvas(), 50, 50, Color.BLUE);
//        fillRectangle((int)pos.x, (int)pos.y, 50, 50, Color.BLUE);
        draw.fillRectangle(0,0,900,600,Color.WHITE);
        draw.drawImage((int)pos.x, (int)pos.y, 50, 50, hand);
        drawImage(0,0,getFrameWidth(),getFrameHeight(),draw);
    }

    @Override
    public boolean end() {
        return lastKey() == KeyEvent.VK_ESCAPE;
//        return true;
    }
}
