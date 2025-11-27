package engine;

/**
 * Represents a physics body with position, velocity, acceleration, and physical properties.
 * Used by the physics engine for simulation.
 */
public class PhysicsBody {
    
    // Kinematic properties
    private Vector2D position;
    private Vector2D velocity;
    private Vector2D acceleration;
    
    // Physical properties
    private double mass;
    private double restitution; // Bounciness (0 = no bounce, 1 = perfect bounce)
    private double friction;
    private double drag; // Air resistance
    
    // Body type
    private BodyType bodyType;
    
    // Force accumulator
    private Vector2D forceAccumulator;
    
    // Rotation (for future use)
    private double rotation;
    private double angularVelocity;
    
    // Constraints
    private boolean freezeRotation;
    
    public enum BodyType {
        DYNAMIC,    // Affected by forces and gravity
        KINEMATIC,  // Not affected by forces, can be moved programmatically
        STATIC      // Immovable
    }
    
    /**
     * Creates a new physics body at the specified position.
     * @param position Initial position
     * @param mass Body mass (kg)
     */
    public PhysicsBody(Vector2D position, double mass) {
        this.position = position;
        this.velocity = Vector2D.ZERO;
        this.acceleration = Vector2D.ZERO;
        this.mass = Math.max(0.01, mass); // Prevent division by zero
        this.restitution = 0.5;
        this.friction = 0.3;
        this.drag = 0.01;
        this.bodyType = BodyType.DYNAMIC;
        this.forceAccumulator = Vector2D.ZERO;
        this.rotation = 0;
        this.angularVelocity = 0;
        this.freezeRotation = false;
    }
    
    /**
     * Applies a force to the body.
     * Force will be applied on the next physics update.
     * @param force The force vector to apply
     */
    public void applyForce(Vector2D force) {
        if (bodyType != BodyType.DYNAMIC) return;
        forceAccumulator = forceAccumulator.add(force);
    }
    
    /**
     * Applies an impulse (instantaneous force) to the body.
     * Directly modifies velocity.
     * @param impulse The impulse vector to apply
     */
    public void applyImpulse(Vector2D impulse) {
        if (bodyType != BodyType.DYNAMIC) return;
        velocity = velocity.add(impulse.divide(mass));
    }
    
    /**
     * Integrates forces and updates velocity and position.
     * Uses semi-implicit Euler integration for stability.
     * @param deltaTime Time step in seconds
     */
    public void integrate(double deltaTime) {
        if (bodyType == BodyType.STATIC) return;
        
        if (bodyType == BodyType.DYNAMIC) {
            // Calculate acceleration from accumulated forces (F = ma, so a = F/m)
            acceleration = forceAccumulator.divide(mass);
            
            // Update velocity with acceleration
            velocity = velocity.add(acceleration.multiply(deltaTime));
            
            // Apply drag (air resistance)
            double dragFactor = Math.max(0, 1 - drag * deltaTime);
            velocity = velocity.multiply(dragFactor);
            
            // Clear force accumulator for next frame
            forceAccumulator = Vector2D.ZERO;
        }
        
        // Update position with velocity
        position = position.add(velocity.multiply(deltaTime));
        
        // Update rotation
        if (!freezeRotation) {
            rotation += angularVelocity * deltaTime;
        }
    }
    
    /**
     * Checks if this body should collide with another body.
     * @param other The other body
     * @return true if collision should be processed
     */
    public boolean shouldCollideWith(PhysicsBody other) {
        // Static bodies don't collide with each other
        if (bodyType == BodyType.STATIC && other.bodyType == BodyType.STATIC) {
            return false;
        }
        return true;
    }
    
    /**
     * Resolves a collision with another body using impulse-based resolution.
     * @param other The other body
     * @param normal The collision normal (pointing from this to other)
     * @param penetration The penetration depth
     */
    public void resolveCollision(PhysicsBody other, Vector2D normal, double penetration) {
        if (!shouldCollideWith(other)) return;
        
        // Calculate relative velocity
        Vector2D relativeVelocity = other.velocity.subtract(velocity);
        double velAlongNormal = relativeVelocity.dot(normal);
        
        // Don't resolve if velocities are separating
        if (velAlongNormal > 0) return;
        
        // Calculate restitution (use minimum of both bodies)
        double e = Math.min(restitution, other.restitution);
        
        // Calculate impulse scalar
        double j = -(1 + e) * velAlongNormal;
        j /= (1 / mass + 1 / other.mass);
        
        // Apply impulse
        Vector2D impulse = normal.multiply(j);
        
        if (bodyType == BodyType.DYNAMIC) {
            velocity = velocity.subtract(impulse.divide(mass));
        }
        
        if (other.bodyType == BodyType.DYNAMIC) {
            other.velocity = other.velocity.add(impulse.divide(other.mass));
        }
        
        // Position correction to prevent sinking
        final double percent = 0.4; // Penetration percentage to correct
        final double slop = 0.01; // Penetration allowance
        double correctionAmount = Math.max(penetration - slop, 0) / (1 / mass + 1 / other.mass) * percent;
        Vector2D correction = normal.multiply(correctionAmount);
        
        if (bodyType == BodyType.DYNAMIC) {
            position = position.subtract(correction.divide(mass));
        }
        
        if (other.bodyType == BodyType.DYNAMIC) {
            other.position = other.position.add(correction.divide(other.mass));
        }
        
        // Apply friction
        applyFrictionImpulse(other, normal, relativeVelocity);
    }
    
    /**
     * Applies friction impulse during collision.
     */
    private void applyFrictionImpulse(PhysicsBody other, Vector2D normal, Vector2D relativeVelocity) {
        // Calculate tangent vector (perpendicular to normal)
        Vector2D tangent = relativeVelocity.subtract(normal.multiply(relativeVelocity.dot(normal)));
        if (tangent.magnitudeSquared() < 1e-10) return;
        tangent = tangent.normalize();
        
        // Calculate friction impulse
        double jt = -relativeVelocity.dot(tangent);
        jt /= (1 / mass + 1 / other.mass);
        
        // Coulomb's law: friction <= normal force
        double mu = (friction + other.friction) * 0.5;
        double velAlongNormal = relativeVelocity.dot(normal);
        double maxFriction = Math.abs(mu * velAlongNormal * (1 / mass + 1 / other.mass));
        
        // Clamp friction
        jt = Math.max(-maxFriction, Math.min(maxFriction, jt));
        
        Vector2D frictionImpulse = tangent.multiply(jt);
        
        if (bodyType == BodyType.DYNAMIC) {
            velocity = velocity.subtract(frictionImpulse.divide(mass));
        }
        
        if (other.bodyType == BodyType.DYNAMIC) {
            other.velocity = other.velocity.add(frictionImpulse.divide(other.mass));
        }
    }
    
    // Getters and setters
    
    public Vector2D getPosition() { return position; }
    public void setPosition(Vector2D position) { this.position = position; }
    
    public Vector2D getVelocity() { return velocity; }
    public void setVelocity(Vector2D velocity) { this.velocity = velocity; }
    
    public Vector2D getAcceleration() { return acceleration; }
    
    public double getMass() { return mass; }
    public void setMass(double mass) { this.mass = Math.max(0.01, mass); }
    
    public double getRestitution() { return restitution; }
    public void setRestitution(double restitution) { 
        this.restitution = Math.max(0, Math.min(1, restitution)); 
    }
    
    public double getFriction() { return friction; }
    public void setFriction(double friction) { 
        this.friction = Math.max(0, Math.min(1, friction)); 
    }
    
    public double getDrag() { return drag; }
    public void setDrag(double drag) { this.drag = Math.max(0, drag); }
    
    public BodyType getBodyType() { return bodyType; }
    public void setBodyType(BodyType bodyType) { this.bodyType = bodyType; }
    
    public double getRotation() { return rotation; }
    public void setRotation(double rotation) { this.rotation = rotation; }
    
    public double getAngularVelocity() { return angularVelocity; }
    public void setAngularVelocity(double angularVelocity) { this.angularVelocity = angularVelocity; }
    
    public boolean isFreezeRotation() { return freezeRotation; }
    public void setFreezeRotation(boolean freezeRotation) { this.freezeRotation = freezeRotation; }
}
