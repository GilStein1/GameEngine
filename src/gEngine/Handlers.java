package gEngine;

import java.awt.*;
import java.awt.event.*;

public class Handlers {

    static class GKeyEventHandled implements KeyEventDispatcher {

        private final GSetup setup;

        public GKeyEventHandled() {
            this.setup = SetupManager.getInstance().getSetup();
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent e) {

            KeyEventSupplier keyTyped = setup.getKeyTyped();

            if (e.getID() == KeyEvent.KEY_TYPED) {
                if (keyTyped != null) {
                    setup.insertKeyAndActionPair(new KeyAndActionPair(keyTyped, e));

                }
                for (GTextField t : setup.getGTextFields()) {
                    t.typed(e, setup.lastKey() == KeyEvent.VK_BACK_SPACE);
                }
            }
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                setup.setLastKeyPressed(e.getKeyCode());
                if (setup.getKeyPressed() != null) {
                    setup.insertKeyAndActionPair(new KeyAndActionPair(setup.getKeyPressed(), e));
                }
                setup.setLastCharPressed(e.getKeyChar());
            }
            if (e.getID() == KeyEvent.KEY_RELEASED) {
                if (e.getKeyCode() == setup.lastKey()) {
                    setup.setLastKeyPressed(-1);
                }
                if (setup.getKeyReleased() != null) {
                    setup.insertKeyAndActionPair(new KeyAndActionPair(setup.getKeyReleased(), e));
                }
                if (e.getKeyChar() == setup.lastKeyChar()) {
                    setup.setLastCharPressed('~');
                }
            }
            return false;
        }

    }

    static class GMouseListener implements MouseListener {

        private GSetup setup;

        public GMouseListener() {
            setup = SetupManager.getInstance().getSetup();
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            for (GFrameTextField textField : setup.getGFrameTextFields()) {
                textField.isPressed();
                setup.removeFromPanel(textField.getJTextField());
                setup.addToPanel(textField.getJTextField());
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == 1) {
                setup.setLeftMouseClicked(true);
                for (GButton b : setup.getGButtons()) {
                    b.setPressed(true);
                }
                for (GTextField t : setup.getGTextFields()) {
                    t.setPressed(true);
                }
            }
            if (e.getButton() == 3) {
                setup.setRightMouseClicked(true);
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == 1) {
                setup.setLeftMouseClicked(false);
                for (GButton b : setup.getGButtons()) {
                    b.setPressed(false);
                }
                for (GTextField t : setup.getGTextFields()) {
                    t.setPressed(false);
                }
            }
            if (e.getButton() == 3) {
                setup.setRightMouseClicked(false);
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            setup.setMouseOnFrame(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            setup.setMouseOnFrame(false);
        }
    }

    static class GWindowHandler implements WindowListener {

        private final GSetup setup;

        public GWindowHandler() {
            setup = SetupManager.getInstance().getSetup();
        }

        @Override
        public void windowOpened(WindowEvent e) {

        }

        @Override
        public void windowClosing(WindowEvent e) {
            setup.lastFunction();
        }

        @Override
        public void windowClosed(WindowEvent e) {
            setup.lastFunction();
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
    }

}
