package engine;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Base class for all colliders.
 * Colliders define the shape for collision detection.
 */
public abstract class Collider {
    
    protected GameObject gameObject;
    protected boolean isTrigger; // If true, detects collisions but doesn't resolve them
    protected String tag;
    
    public Collider(GameObject gameObject) {
        this.gameObject = gameObject;
        this.isTrigger = false;
        this.tag = "";
    }
    
    /**
     * Checks if this collider intersects with another collider.
     * @param other The other collider
     * @return Collision information, or null if no collision
     */
    public abstract CollisionInfo checkCollision(Collider other);
    
    /**
     * Draws debug visualization of the collider.
     * @param g2d Graphics context
     */
    public abstract void debugDraw(Graphics2D g2d);
    
    /**
     * Gets the center position of the collider.
     * @return Center position
     */
    public abstract Vector2D getCenter();
    
    public boolean isTrigger() { return isTrigger; }
    public void setTrigger(boolean trigger) { isTrigger = trigger; }
    
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    
    public GameObject getGameObject() { return gameObject; }
    
    /**
     * Collision information returned when two colliders intersect.
     */
    public static class CollisionInfo {
        public final Collider colliderA;
        public final Collider colliderB;
        public final Vector2D normal; // Normal pointing from A to B
        public final double penetration; // Penetration depth
        public final Vector2D contactPoint;
        
        public CollisionInfo(Collider a, Collider b, Vector2D normal, double penetration, Vector2D contactPoint) {
            this.colliderA = a;
            this.colliderB = b;
            this.normal = normal;
            this.penetration = penetration;
            this.contactPoint = contactPoint;
        }
    }
}
