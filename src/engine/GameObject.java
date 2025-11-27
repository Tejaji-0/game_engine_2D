package engine;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for all game objects.
 * Supports component-based architecture and lifecycle management.
 */
public class GameObject {
    
    private static int nextId = 0;
    
    private int id;
    private String name;
    private String tag;
    private int layer;
    private boolean active;
    
    // Transform
    private Vector2D position;
    private double rotation;
    private Vector2D scale;
    
    // Physics
    private PhysicsBody physicsBody;
    
    // Collider
    private Collider collider;
    
    // Components
    private List<Component> components;
    
    // Hierarchy
    private GameObject parent;
    private List<GameObject> children;
    
    // Lifecycle state
    private boolean initialized;
    private boolean destroyed;
    
    /**
     * Creates a new GameObject at (0, 0).
     */
    public GameObject() {
        this("GameObject", Vector2D.ZERO);
    }
    
    /**
     * Creates a new GameObject with the specified name and position.
     * @param name The name of the GameObject
     * @param position The initial position
     */
    public GameObject(String name, Vector2D position) {
        this.id = nextId++;
        this.name = name;
        this.tag = "Untagged";
        this.layer = 0;
        this.active = true;
        
        this.position = position;
        this.rotation = 0;
        this.scale = new Vector2D(1, 1);
        
        this.components = new ArrayList<>();
        this.children = new ArrayList<>();
        
        this.initialized = false;
        this.destroyed = false;
    }
    
    /**
     * Initializes the GameObject and all its components.
     * Called once before the first update.
     */
    public void initialize() {
        if (initialized) return;
        
        for (Component component : components) {
            component.initialize();
        }
        
        for (GameObject child : children) {
            child.initialize();
        }
        
        initialized = true;
    }
    
    /**
     * Updates the GameObject and all its components.
     * @param deltaTime Time since last update in seconds
     */
    public void update(double deltaTime) {
        if (!active || destroyed) return;
        
        if (!initialized) {
            initialize();
        }
        
        // Update physics
        if (physicsBody != null) {
            position = physicsBody.getPosition();
            rotation = physicsBody.getRotation();
        }
        
        // Update components
        for (Component component : components) {
            if (component.isEnabled()) {
                component.update(deltaTime);
            }
        }
        
        // Update children
        for (GameObject child : children) {
            child.update(deltaTime);
        }
    }
    
    /**
     * Renders the GameObject and all its components.
     * @param g2d Graphics context
     */
    public void render(Graphics2D g2d) {
        if (!active || destroyed) return;
        
        for (Component component : components) {
            if (component.isEnabled()) {
                component.render(g2d);
            }
        }
        
        for (GameObject child : children) {
            child.render(g2d);
        }
    }
    
    /**
     * Called when this GameObject collides with another.
     * @param other The other GameObject
     * @param collision Collision information
     */
    public void onCollision(GameObject other, Collider.CollisionInfo collision) {
        for (Component component : components) {
            if (component.isEnabled()) {
                component.onCollision(other, collision);
            }
        }
    }
    
    /**
     * Destroys the GameObject and all its components.
     */
    public void destroy() {
        if (destroyed) return;
        
        // Destroy components
        for (Component component : components) {
            component.destroy();
        }
        components.clear();
        
        // Destroy children
        for (GameObject child : children) {
            child.destroy();
        }
        children.clear();
        
        // Remove from physics world
        if (physicsBody != null) {
            PhysicsWorld.getInstance().removeBody(physicsBody);
        }
        if (collider != null) {
            PhysicsWorld.getInstance().removeCollider(collider);
        }
        
        // Remove from parent
        if (parent != null) {
            parent.removeChild(this);
        }
        
        destroyed = true;
    }
    
    /**
     * Adds a component to this GameObject.
     * @param component The component to add
     */
    public void addComponent(Component component) {
        if (!components.contains(component)) {
            components.add(component);
            component.setGameObject(this);
            
            if (initialized) {
                component.initialize();
            }
        }
    }
    
    /**
     * Removes a component from this GameObject.
     * @param component The component to remove
     */
    public void removeComponent(Component component) {
        if (components.remove(component)) {
            component.destroy();
        }
    }
    
    /**
     * Gets a component of the specified type.
     * @param componentClass The component class
     * @return The component, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component component : components) {
            if (componentClass.isInstance(component)) {
                return (T) component;
            }
        }
        return null;
    }
    
    /**
     * Gets all components of the specified type.
     * @param componentClass The component class
     * @return List of components
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> List<T> getComponents(Class<T> componentClass) {
        List<T> result = new ArrayList<>();
        for (Component component : components) {
            if (componentClass.isInstance(component)) {
                result.add((T) component);
            }
        }
        return result;
    }
    
    /**
     * Adds a child GameObject.
     * @param child The child to add
     */
    public void addChild(GameObject child) {
        if (!children.contains(child)) {
            children.add(child);
            child.parent = this;
        }
    }
    
    /**
     * Removes a child GameObject.
     * @param child The child to remove
     */
    public void removeChild(GameObject child) {
        if (children.remove(child)) {
            child.parent = null;
        }
    }
    
    /**
     * Gets the world position (accounting for parent transforms).
     * @return World position
     */
    public Vector2D getWorldPosition() {
        if (parent == null) {
            return position;
        }
        return parent.getWorldPosition().add(position);
    }
    
    /**
     * Creates a physics body for this GameObject.
     * @param mass The mass of the body
     * @return The created PhysicsBody
     */
    public PhysicsBody createPhysicsBody(double mass) {
        if (physicsBody == null) {
            physicsBody = new PhysicsBody(position, mass);
            PhysicsWorld.getInstance().addBody(physicsBody);
        }
        return physicsBody;
    }
    
    /**
     * Sets the collider for this GameObject.
     * @param collider The collider to set
     */
    public void setCollider(Collider collider) {
        // Remove old collider
        if (this.collider != null) {
            PhysicsWorld.getInstance().removeCollider(this.collider);
        }
        
        this.collider = collider;
        
        if (collider != null) {
            PhysicsWorld.getInstance().addCollider(collider);
        }
    }
    
    // Getters and setters
    
    public int getId() { return id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }
    
    public int getLayer() { return layer; }
    public void setLayer(int layer) { this.layer = layer; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D position) { 
        this.position = position;
        if (physicsBody != null) {
            physicsBody.setPosition(position);
        }
    }
    
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { 
        this.rotation = rotation;
        if (physicsBody != null) {
            physicsBody.setRotation(rotation);
        }
    }
    
    public Vector2D getScale() { return scale; }
    public void setScale(Vector2D scale) { this.scale = scale; }
    
    public PhysicsBody getPhysicsBody() { return physicsBody; }
    public Collider getCollider() { return collider; }
    
    public GameObject getParent() { return parent; }
    public List<GameObject> getChildren() { return new ArrayList<>(children); }
    
    public boolean isInitialized() { return initialized; }
    public boolean isDestroyed() { return destroyed; }
    
    @Override
    public String toString() {
        return String.format("GameObject[id=%d, name=%s, position=%s]", id, name, position);
    }
}
