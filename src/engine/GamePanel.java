package engine;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * Main rendering panel for the game.
 * Manages the scene and all game objects.
 */
public class GamePanel extends JPanel {
    
    private static final Logger logger = Logger.getLogger(GamePanel.class.getName());
    
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    // Scene management
    private List<GameObject> gameObjects;
    private List<GameObject> objectsToAdd;
    private List<GameObject> objectsToRemove;
    
    // Camera
    private Vector2D cameraPosition;
    
    // Debug options
    private boolean showDebug = false;
    private boolean showFps = true;
    private boolean showColliders = false;
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.DARK_GRAY);
        setDoubleBuffered(true);
        setFocusable(true);
        
        gameObjects = new CopyOnWriteArrayList<>();
        objectsToAdd = new ArrayList<>();
        objectsToRemove = new ArrayList<>();
        cameraPosition = Vector2D.ZERO;
        
        // Add input listeners
        InputManager inputManager = InputManager.getInstance();
        addKeyListener(inputManager);
        addMouseListener(inputManager);
        addMouseMotionListener(inputManager);
        addMouseWheelListener(inputManager);
        
        logger.info("GamePanel initialized: " + WIDTH + "x" + HEIGHT);
    }
    
    /**
     * Updates all game objects.
     * @param deltaTime Time since last update in seconds
     */
    public void update(double deltaTime) {
        // Add pending objects
        if (!objectsToAdd.isEmpty()) {
            for (GameObject obj : objectsToAdd) {
                if (!gameObjects.contains(obj)) {
                    gameObjects.add(obj);
                    obj.initialize();
                }
            }
            objectsToAdd.clear();
        }
        
        // Remove pending objects
        if (!objectsToRemove.isEmpty()) {
            for (GameObject obj : objectsToRemove) {
                gameObjects.remove(obj);
                obj.destroy();
            }
            objectsToRemove.clear();
        }
        
        // Update all game objects
        for (GameObject obj : gameObjects) {
            if (obj.isActive()) {
                obj.update(deltaTime);
            }
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Apply camera transform
        g2d.translate(-cameraPosition.x, -cameraPosition.y);
        
        // Render all game objects
        for (GameObject obj : gameObjects) {
            if (obj.isActive()) {
                obj.render(g2d);
            }
        }
        
        // Render debug information
        if (showColliders) {
            renderColliders(g2d);
        }
        
        // Reset transform for UI rendering
        g2d.translate(cameraPosition.x, cameraPosition.y);
        
        // Render UI/Debug info
        if (showFps || showDebug) {
            renderDebugInfo(g2d);
        }
    }
    
    /**
     * Renders collision debug visualization.
     */
    private void renderColliders(Graphics2D g2d) {
        for (Collider collider : PhysicsWorld.getInstance().getColliders()) {
            if (collider.getGameObject().isActive()) {
                collider.debugDraw(g2d);
            }
        }
    }
    
    /**
     * Renders debug information on screen.
     */
    private void renderDebugInfo(Graphics2D g2d) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        int y = 15;
        int lineHeight = 15;
        
        if (showFps) {
            g2d.drawString("FPS: " + GameLoop.class.getName(), 10, y);
            y += lineHeight;
        }
        
        if (showDebug) {
            g2d.drawString("GameObjects: " + gameObjects.size(), 10, y);
            y += lineHeight;
            g2d.drawString("Physics Bodies: " + PhysicsWorld.getInstance().getBodies().size(), 10, y);
            y += lineHeight;
            g2d.drawString("Colliders: " + PhysicsWorld.getInstance().getColliders().size(), 10, y);
            y += lineHeight;
            g2d.drawString("Camera: " + cameraPosition, 10, y);
        }
    }
    
    /**
     * Triggers a repaint.
     */
    public void render() {
        repaint();
    }
    
    /**
     * Adds a GameObject to the scene.
     * @param obj The GameObject to add
     */
    public void addGameObject(GameObject obj) {
        objectsToAdd.add(obj);
    }
    
    /**
     * Removes a GameObject from the scene.
     * @param obj The GameObject to remove
     */
    public void removeGameObject(GameObject obj) {
        objectsToRemove.add(obj);
    }
    
    /**
     * Finds a GameObject by name.
     * @param name The name to search for
     * @return The GameObject, or null if not found
     */
    public GameObject findGameObject(String name) {
        for (GameObject obj : gameObjects) {
            if (obj.getName().equals(name)) {
                return obj;
            }
        }
        return null;
    }
    
    /**
     * Finds all GameObjects with the specified tag.
     * @param tag The tag to search for
     * @return List of GameObjects with the tag
     */
    public List<GameObject> findGameObjectsWithTag(String tag) {
        List<GameObject> result = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj.getTag().equals(tag)) {
                result.add(obj);
            }
        }
        return result;
    }
    
    /**
     * Gets all GameObjects in the scene.
     * @return List of all GameObjects
     */
    public List<GameObject> getAllGameObjects() {
        return new ArrayList<>(gameObjects);
    }
    
    /**
     * Clears all GameObjects from the scene.
     */
    public void clearScene() {
        for (GameObject obj : gameObjects) {
            obj.destroy();
        }
        gameObjects.clear();
        objectsToAdd.clear();
        objectsToRemove.clear();
        PhysicsWorld.getInstance().clear();
        logger.info("Scene cleared");
    }
    
    // Getters and setters
    
    public Vector2D getCameraPosition() { return cameraPosition; }
    public void setCameraPosition(Vector2D position) { this.cameraPosition = position; }
    
    public boolean isShowDebug() { return showDebug; }
    public void setShowDebug(boolean showDebug) { this.showDebug = showDebug; }
    
    public boolean isShowFps() { return showFps; }
    public void setShowFps(boolean showFps) { this.showFps = showFps; }
    
    public boolean isShowColliders() { return showColliders; }
    public void setShowColliders(boolean showColliders) { this.showColliders = showColliders; }
    
    public int getWidth() { return WIDTH; }
    public int getHeight() { return HEIGHT; }
}
