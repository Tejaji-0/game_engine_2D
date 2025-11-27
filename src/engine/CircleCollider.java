package engine;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Circle-shaped collider.
 */
public class CircleCollider extends Collider {
    
    private double radius;
    private Vector2D offset;
    
    public CircleCollider(GameObject gameObject, double radius) {
        super(gameObject);
        this.radius = radius;
        this.offset = Vector2D.ZERO;
    }
    
    public CircleCollider(GameObject gameObject, double radius, Vector2D offset) {
        super(gameObject);
        this.radius = radius;
        this.offset = offset;
    }
    
    @Override
    public Vector2D getCenter() {
        return gameObject.getPosition().add(offset);
    }
    
    public double getRadius() { return radius; }
    public void setRadius(double radius) { this.radius = radius; }
    
    public Vector2D getOffset() { return offset; }
    public void setOffset(Vector2D offset) { this.offset = offset; }
    
    @Override
    public CollisionInfo checkCollision(Collider other) {
        if (other instanceof CircleCollider) {
            return checkCircleCollision((CircleCollider) other);
        } else if (other instanceof BoxCollider) {
            // Reverse the box-circle collision
            CollisionInfo info = ((BoxCollider) other).checkCollision(this);
            if (info != null) {
                // Flip the normal since we're reversing the collision
                return new CollisionInfo(
                    this, other,
                    info.normal.multiply(-1),
                    info.penetration,
                    info.contactPoint
                );
            }
            return null;
        }
        return null;
    }
    
    /**
     * Checks collision between two circles.
     */
    private CollisionInfo checkCircleCollision(CircleCollider other) {
        Vector2D centerA = getCenter();
        Vector2D centerB = other.getCenter();
        
        Vector2D diff = centerB.subtract(centerA);
        double distance = diff.magnitude();
        double radiusSum = radius + other.radius;
        
        // Check if circles are colliding
        if (distance >= radiusSum) {
            return null;
        }
        
        // Calculate collision normal and penetration
        Vector2D normal;
        if (distance < 1e-6) {
            // Circles are at same position, choose arbitrary normal
            normal = Vector2D.RIGHT;
        } else {
            normal = diff.normalize();
        }
        
        double penetration = radiusSum - distance;
        Vector2D contactPoint = centerA.add(normal.multiply(radius));
        
        return new CollisionInfo(this, other, normal, penetration, contactPoint);
    }
    
    @Override
    public void debugDraw(Graphics2D g2d) {
        g2d.setColor(isTrigger ? new Color(0, 255, 0, 100) : new Color(255, 0, 0, 100));
        Vector2D center = getCenter();
        int x = (int) (center.x - radius);
        int y = (int) (center.y - radius);
        int diameter = (int) (radius * 2);
        g2d.drawOval(x, y, diameter, diameter);
    }
}
