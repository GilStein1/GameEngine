package gEngine;

import java.awt.event.KeyEvent;

public class KeyAndActionPair {
    private KeyEventSupplier action;
    private KeyEvent event;

    public KeyAndActionPair(KeyEventSupplier action, KeyEvent event) {
        this.action = action;
        this.event = event;
    }
    public KeyEventSupplier getAction() {
        return action;
    }
    public KeyEvent getEvent() {
        return event;
    }

}
