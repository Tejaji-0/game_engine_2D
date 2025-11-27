package engine;

import java.awt.Graphics2D;

/**
 * Base class for all components that can be attached to GameObjects.
 * Components add behavior and functionality to GameObjects.
 */
public abstract class Component {
    
    protected GameObject gameObject;
    protected boolean enabled;
    
    public Component() {
        this.enabled = true;
    }
    
    /**
     * Called once when the component is initialized.
     * Override to perform setup logic.
     */
    public void initialize() {
        // Override in subclasses
    }
    
    /**
     * Called every frame to update component logic.
     * @param deltaTime Time since last update in seconds
     */
    public void update(double deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Called every frame to render the component.
     * @param g2d Graphics context
     */
    public void render(Graphics2D g2d) {
        // Override in subclasses
    }
    
    /**
     * Called when the GameObject collides with another GameObject.
     * @param other The other GameObject
     * @param collision Collision information
     */
    public void onCollision(GameObject other, Collider.CollisionInfo collision) {
        // Override in subclasses
    }
    
    /**
     * Called when the component is destroyed.
     * Override to perform cleanup logic.
     */
    public void destroy() {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public GameObject getGameObject() { return gameObject; }
    
    void setGameObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
