package engine;

import java.awt.event.*;
import java.util.*;

/**
 * Centralized input management system for keyboard and mouse.
 * Provides state tracking and action mapping.
 */
public class InputManager implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {
    
    private static volatile InputManager instance;
    
    // Key state tracking
    private Set<Integer> keysPressed;
    private Set<Integer> keysJustPressed;
    private Set<Integer> keysJustReleased;
    private Set<Integer> keysPressedLastFrame;
    
    // Mouse state tracking
    private Set<Integer> mouseButtonsPressed;
    private Set<Integer> mouseButtonsJustPressed;
    private Set<Integer> mouseButtonsJustReleased;
    private Set<Integer> mouseButtonsPressedLastFrame;
    
    private Vector2D mousePosition;
    private Vector2D mouseDelta;
    private Vector2D lastMousePosition;
    private int mouseWheelRotation;
    
    // Action mapping
    private Map<String, List<Integer>> actionKeyBindings;
    
    // Input listeners
    private List<InputListener> listeners;
    
    private InputManager() {
        keysPressed = new HashSet<>();
        keysJustPressed = new HashSet<>();
        keysJustReleased = new HashSet<>();
        keysPressedLastFrame = new HashSet<>();
        
        mouseButtonsPressed = new HashSet<>();
        mouseButtonsJustPressed = new HashSet<>();
        mouseButtonsJustReleased = new HashSet<>();
        mouseButtonsPressedLastFrame = new HashSet<>();
        
        mousePosition = Vector2D.ZERO;
        mouseDelta = Vector2D.ZERO;
        lastMousePosition = Vector2D.ZERO;
        mouseWheelRotation = 0;
        
        actionKeyBindings = new HashMap<>();
        listeners = new ArrayList<>();
        
        // Default action bindings
        bindAction("MoveLeft", KeyEvent.VK_A, KeyEvent.VK_LEFT);
        bindAction("MoveRight", KeyEvent.VK_D, KeyEvent.VK_RIGHT);
        bindAction("MoveUp", KeyEvent.VK_W, KeyEvent.VK_UP);
        bindAction("MoveDown", KeyEvent.VK_S, KeyEvent.VK_DOWN);
        bindAction("Jump", KeyEvent.VK_SPACE);
        bindAction("Fire", KeyEvent.VK_CONTROL);
    }
    
    public static InputManager getInstance() {
        if (instance == null) {
            synchronized (InputManager.class) {
                if (instance == null) {
                    instance = new InputManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Updates input state. Should be called once per frame at the end of the update cycle.
     */
    public void update() {
        // Update key state
        keysJustPressed.clear();
        keysJustReleased.clear();
        
        for (Integer key : keysPressed) {
            if (!keysPressedLastFrame.contains(key)) {
                keysJustPressed.add(key);
            }
        }
        
        for (Integer key : keysPressedLastFrame) {
            if (!keysPressed.contains(key)) {
                keysJustReleased.add(key);
            }
        }
        
        keysPressedLastFrame.clear();
        keysPressedLastFrame.addAll(keysPressed);
        
        // Update mouse button state
        mouseButtonsJustPressed.clear();
        mouseButtonsJustReleased.clear();
        
        for (Integer button : mouseButtonsPressed) {
            if (!mouseButtonsPressedLastFrame.contains(button)) {
                mouseButtonsJustPressed.add(button);
            }
        }
        
        for (Integer button : mouseButtonsPressedLastFrame) {
            if (!mouseButtonsPressed.contains(button)) {
                mouseButtonsJustReleased.add(button);
            }
        }
        
        mouseButtonsPressedLastFrame.clear();
        mouseButtonsPressedLastFrame.addAll(mouseButtonsPressed);
        
        // Update mouse delta
        mouseDelta = mousePosition.subtract(lastMousePosition);
        lastMousePosition = mousePosition;
        
        // Reset mouse wheel
        mouseWheelRotation = 0;
    }
    
    // Keyboard input methods
    
    public boolean isKeyPressed(int keyCode) {
        return keysPressed.contains(keyCode);
    }
    
    public boolean isKeyJustPressed(int keyCode) {
        return keysJustPressed.contains(keyCode);
    }
    
    public boolean isKeyJustReleased(int keyCode) {
        return keysJustReleased.contains(keyCode);
    }
    
    // Mouse input methods
    
    public boolean isMouseButtonPressed(int button) {
        return mouseButtonsPressed.contains(button);
    }
    
    public boolean isMouseButtonJustPressed(int button) {
        return mouseButtonsJustPressed.contains(button);
    }
    
    public boolean isMouseButtonJustReleased(int button) {
        return mouseButtonsJustReleased.contains(button);
    }
    
    public Vector2D getMousePosition() {
        return mousePosition;
    }
    
    public Vector2D getMouseDelta() {
        return mouseDelta;
    }
    
    public int getMouseWheelRotation() {
        return mouseWheelRotation;
    }
    
    // Action mapping methods
    
    /**
     * Binds an action to one or more keys.
     */
    public void bindAction(String action, int... keyCodes) {
        List<Integer> keys = actionKeyBindings.getOrDefault(action, new ArrayList<>());
        for (int keyCode : keyCodes) {
            if (!keys.contains(keyCode)) {
                keys.add(keyCode);
            }
        }
        actionKeyBindings.put(action, keys);
    }
    
    /**
     * Unbinds all keys from an action.
     */
    public void unbindAction(String action) {
        actionKeyBindings.remove(action);
    }
    
    /**
     * Checks if any key bound to the action is pressed.
     */
    public boolean isActionPressed(String action) {
        List<Integer> keys = actionKeyBindings.get(action);
        if (keys == null) return false;
        
        for (Integer key : keys) {
            if (isKeyPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if any key bound to the action was just pressed.
     */
    public boolean isActionJustPressed(String action) {
        List<Integer> keys = actionKeyBindings.get(action);
        if (keys == null) return false;
        
        for (Integer key : keys) {
            if (isKeyJustPressed(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Checks if any key bound to the action was just released.
     */
    public boolean isActionJustReleased(String action) {
        List<Integer> keys = actionKeyBindings.get(action);
        if (keys == null) return false;
        
        for (Integer key : keys) {
            if (isKeyJustReleased(key)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the horizontal axis value (-1 for left, 1 for right, 0 for none).
     */
    public double getHorizontalAxis() {
        double value = 0;
        if (isActionPressed("MoveLeft")) value -= 1;
        if (isActionPressed("MoveRight")) value += 1;
        return value;
    }
    
    /**
     * Gets the vertical axis value (-1 for up, 1 for down, 0 for none).
     */
    public double getVerticalAxis() {
        double value = 0;
        if (isActionPressed("MoveUp")) value -= 1;
        if (isActionPressed("MoveDown")) value += 1;
        return value;
    }
    
    // Listener methods
    
    public void addInputListener(InputListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeInputListener(InputListener listener) {
        listeners.remove(listener);
    }
    
    // KeyListener implementation
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.add(keyCode);
        
        for (InputListener listener : listeners) {
            listener.onKeyPressed(keyCode);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        keysPressed.remove(keyCode);
        
        for (InputListener listener : listeners) {
            listener.onKeyReleased(keyCode);
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
        for (InputListener listener : listeners) {
            listener.onKeyTyped(e.getKeyChar());
        }
    }
    
    // MouseListener implementation
    
    @Override
    public void mousePressed(MouseEvent e) {
        mouseButtonsPressed.add(e.getButton());
        
        for (InputListener listener : listeners) {
            listener.onMousePressed(e.getButton(), new Vector2D(e.getX(), e.getY()));
        }
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        mouseButtonsPressed.remove(e.getButton());
        
        for (InputListener listener : listeners) {
            listener.onMouseReleased(e.getButton(), new Vector2D(e.getX(), e.getY()));
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        for (InputListener listener : listeners) {
            listener.onMouseClicked(e.getButton(), new Vector2D(e.getX(), e.getY()));
        }
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
    }
    
    // MouseMotionListener implementation
    
    @Override
    public void mouseMoved(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        mousePosition = new Vector2D(e.getX(), e.getY());
    }
    
    // MouseWheelListener implementation
    
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        mouseWheelRotation = e.getWheelRotation();
        
        for (InputListener listener : listeners) {
            listener.onMouseWheel(mouseWheelRotation);
        }
    }
    
    /**
     * Interface for listening to input events.
     */
    public interface InputListener {
        default void onKeyPressed(int keyCode) {}
        default void onKeyReleased(int keyCode) {}
        default void onKeyTyped(char keyChar) {}
        default void onMousePressed(int button, Vector2D position) {}
        default void onMouseReleased(int button, Vector2D position) {}
        default void onMouseClicked(int button, Vector2D position) {}
        default void onMouseWheel(int rotation) {}
    }
}
