package engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Physics simulation world that manages all physics bodies and colliders.
 * Handles gravity, collision detection, and collision resolution.
 */
public class PhysicsWorld {
    
    private static volatile PhysicsWorld instance;
    
    private Vector2D gravity;
    private List<PhysicsBody> bodies;
    private List<Collider> colliders;
    private int velocityIterations;
    private int positionIterations;
    
    // Spatial partitioning bounds (for future optimization)
    private double worldMinX, worldMinY, worldMaxX, worldMaxY;
    
    private PhysicsWorld() {
        gravity = new Vector2D(0, 980); // 980 pixels/s^2 downward (approximates Earth gravity)
        bodies = new ArrayList<>();
        colliders = new ArrayList<>();
        velocityIterations = 6;
        positionIterations = 2;
        
        // Default world bounds
        worldMinX = -10000;
        worldMinY = -10000;
        worldMaxX = 10000;
        worldMaxY = 10000;
    }
    
    /**
     * Gets the singleton instance of PhysicsWorld.
     * @return The PhysicsWorld instance
     */
    public static PhysicsWorld getInstance() {
        if (instance == null) {
            synchronized (PhysicsWorld.class) {
                if (instance == null) {
                    instance = new PhysicsWorld();
                }
            }
        }
        return instance;
    }
    
    /**
     * Registers a physics body with the world.
     * @param body The body to register
     */
    public void addBody(PhysicsBody body) {
        if (!bodies.contains(body)) {
            bodies.add(body);
        }
    }
    
    /**
     * Removes a physics body from the world.
     * @param body The body to remove
     */
    public void removeBody(PhysicsBody body) {
        bodies.remove(body);
    }
    
    /**
     * Registers a collider with the world.
     * @param collider The collider to register
     */
    public void addCollider(Collider collider) {
        if (!colliders.contains(collider)) {
            colliders.add(collider);
        }
    }
    
    /**
     * Removes a collider from the world.
     * @param collider The collider to remove
     */
    public void removeCollider(Collider collider) {
        colliders.remove(collider);
    }
    
    /**
     * Steps the physics simulation forward by deltaTime.
     * @param deltaTime Time step in seconds
     */
    public void step(double deltaTime) {
        // Apply gravity to all dynamic bodies
        for (PhysicsBody body : bodies) {
            if (body.getBodyType() == PhysicsBody.BodyType.DYNAMIC) {
                body.applyForce(gravity.multiply(body.getMass()));
            }
        }
        
        // Integrate all bodies
        for (PhysicsBody body : bodies) {
            body.integrate(deltaTime);
        }
        
        // Detect and resolve collisions
        detectAndResolveCollisions();
        
        // Enforce world bounds
        enforceWorldBounds();
    }
    
    /**
     * Detects all collisions and resolves them.
     */
    private void detectAndResolveCollisions() {
        List<Collider.CollisionInfo> collisions = new ArrayList<>();
        
        // Broad phase: Check all collider pairs (O(n²) - can be optimized with spatial partitioning)
        for (int i = 0; i < colliders.size(); i++) {
            for (int j = i + 1; j < colliders.size(); j++) {
                Collider a = colliders.get(i);
                Collider b = colliders.get(j);
                
                // Skip if either GameObject is inactive
                if (!a.getGameObject().isActive() || !b.getGameObject().isActive()) {
                    continue;
                }
                
                // Narrow phase: Check collision
                Collider.CollisionInfo collision = a.checkCollision(b);
                if (collision != null) {
                    collisions.add(collision);
                    
                    // Notify game objects about collision
                    a.getGameObject().onCollision(b.getGameObject(), collision);
                    b.getGameObject().onCollision(a.getGameObject(), collision);
                }
            }
        }
        
        // Resolve collisions (iterative for stability)
        for (int iteration = 0; iteration < velocityIterations; iteration++) {
            for (Collider.CollisionInfo collision : collisions) {
                // Skip triggers
                if (collision.colliderA.isTrigger() || collision.colliderB.isTrigger()) {
                    continue;
                }
                
                // Get physics bodies
                PhysicsBody bodyA = collision.colliderA.getGameObject().getPhysicsBody();
                PhysicsBody bodyB = collision.colliderB.getGameObject().getPhysicsBody();
                
                if (bodyA != null && bodyB != null) {
                    bodyA.resolveCollision(bodyB, collision.normal, collision.penetration);
                }
            }
        }
    }
    
    /**
     * Keeps all bodies within world bounds.
     */
    private void enforceWorldBounds() {
        for (PhysicsBody body : bodies) {
            Vector2D pos = body.getPosition();
            Vector2D vel = body.getVelocity();
            
            boolean changed = false;
            double newX = pos.x;
            double newY = pos.y;
            double newVelX = vel.x;
            double newVelY = vel.y;
            
            if (pos.x < worldMinX) {
                newX = worldMinX;
                newVelX = Math.abs(vel.x) * body.getRestitution();
                changed = true;
            } else if (pos.x > worldMaxX) {
                newX = worldMaxX;
                newVelX = -Math.abs(vel.x) * body.getRestitution();
                changed = true;
            }
            
            if (pos.y < worldMinY) {
                newY = worldMinY;
                newVelY = Math.abs(vel.y) * body.getRestitution();
                changed = true;
            } else if (pos.y > worldMaxY) {
                newY = worldMaxY;
                newVelY = -Math.abs(vel.y) * body.getRestitution();
                changed = true;
            }
            
            if (changed) {
                body.setPosition(new Vector2D(newX, newY));
                body.setVelocity(new Vector2D(newVelX, newVelY));
            }
        }
    }
    
    /**
     * Performs a raycast from start to end.
     * @param start Start position
     * @param end End position
     * @return The first collider hit, or null if none
     */
    public RaycastHit raycast(Vector2D start, Vector2D end) {
        Vector2D direction = end.subtract(start);
        double maxDistance = direction.magnitude();
        direction = direction.normalize();
        
        RaycastHit closestHit = null;
        double closestDistance = Double.MAX_VALUE;
        
        for (Collider collider : colliders) {
            if (!collider.getGameObject().isActive()) continue;
            
            // Simple ray-circle intersection for now
            if (collider instanceof CircleCollider) {
                CircleCollider circle = (CircleCollider) collider;
                Vector2D toCircle = circle.getCenter().subtract(start);
                double projection = toCircle.dot(direction);
                
                if (projection < 0 || projection > maxDistance) continue;
                
                Vector2D closestPoint = start.add(direction.multiply(projection));
                double distance = closestPoint.distanceTo(circle.getCenter());
                
                if (distance <= circle.getRadius() && projection < closestDistance) {
                    closestDistance = projection;
                    closestHit = new RaycastHit(collider, closestPoint, projection);
                }
            }
            // Box raycast can be added here
        }
        
        return closestHit;
    }
    
    /**
     * Clears all bodies and colliders from the world.
     */
    public void clear() {
        bodies.clear();
        colliders.clear();
    }
    
    // Getters and setters
    
    public Vector2D getGravity() { return gravity; }
    public void setGravity(Vector2D gravity) { this.gravity = gravity; }
    
    public void setWorldBounds(double minX, double minY, double maxX, double maxY) {
        this.worldMinX = minX;
        this.worldMinY = minY;
        this.worldMaxX = maxX;
        this.worldMaxY = maxY;
    }
    
    public List<PhysicsBody> getBodies() { return new ArrayList<>(bodies); }
    public List<Collider> getColliders() { return new ArrayList<>(colliders); }
    
    /**
     * Raycast hit information.
     */
    public static class RaycastHit {
        public final Collider collider;
        public final Vector2D point;
        public final double distance;
        
        public RaycastHit(Collider collider, Vector2D point, double distance) {
            this.collider = collider;
            this.point = point;
            this.distance = distance;
        }
    }
}
