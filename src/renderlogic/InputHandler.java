package renderlogic;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import sceneobjects.*;

public class InputHandler implements KeyListener, MouseMotionListener {
    private Camera cam;

    public InputHandler(Camera cam) {
        this.cam = cam;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Example of moving the camera forward and backward
        if (key == KeyEvent.VK_UP) {
            cam.increaseISO();
        } else if (key == KeyEvent.VK_DOWN) {
            cam.decreaseISO();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) {
        // Implement camera rotation based on mouse movement
    }

    @Override
    public void mouseMoved(MouseEvent e) {}
}