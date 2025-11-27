package engine;

import java.awt.Graphics2D;
import java.awt.Color;

/**
 * Box-shaped collider (AABB - Axis-Aligned Bounding Box).
 */
public class BoxCollider extends Collider {
    
    private double width;
    private double height;
    private Vector2D offset; // Offset from GameObject position
    
    public BoxCollider(GameObject gameObject, double width, double height) {
        super(gameObject);
        this.width = width;
        this.height = height;
        this.offset = Vector2D.ZERO;
    }
    
    public BoxCollider(GameObject gameObject, double width, double height, Vector2D offset) {
        super(gameObject);
        this.width = width;
        this.height = height;
        this.offset = offset;
    }
    
    @Override
    public Vector2D getCenter() {
        return gameObject.getPosition().add(offset);
    }
    
    public double getWidth() { return width; }
    public void setWidth(double width) { this.width = width; }
    
    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }
    
    public Vector2D getOffset() { return offset; }
    public void setOffset(Vector2D offset) { this.offset = offset; }
    
    public double getLeft() { return getCenter().x - width / 2; }
    public double getRight() { return getCenter().x + width / 2; }
    public double getTop() { return getCenter().y - height / 2; }
    public double getBottom() { return getCenter().y + height / 2; }
    
    @Override
    public CollisionInfo checkCollision(Collider other) {
        if (other instanceof BoxCollider) {
            return checkBoxCollision((BoxCollider) other);
        } else if (other instanceof CircleCollider) {
            return checkBoxCircleCollision((CircleCollider) other);
        }
        return null;
    }
    
    /**
     * Checks collision between two boxes using AABB collision detection.
     */
    private CollisionInfo checkBoxCollision(BoxCollider other) {
        Vector2D centerA = getCenter();
        Vector2D centerB = other.getCenter();
        
        // Calculate half extents
        double halfWidthA = width / 2;
        double halfHeightA = height / 2;
        double halfWidthB = other.width / 2;
        double halfHeightB = other.height / 2;
        
        // Calculate distance between centers
        Vector2D diff = centerB.subtract(centerA);
        
        // Calculate overlap on each axis
        double overlapX = (halfWidthA + halfWidthB) - Math.abs(diff.x);
        double overlapY = (halfHeightA + halfHeightB) - Math.abs(diff.y);
        
        // Check if there's no collision
        if (overlapX <= 0 || overlapY <= 0) {
            return null;
        }
        
        // Find the axis of least penetration
        Vector2D normal;
        double penetration;
        Vector2D contactPoint;
        
        if (overlapX < overlapY) {
            // Collision on X axis
            penetration = overlapX;
            normal = new Vector2D(diff.x > 0 ? 1 : -1, 0);
            contactPoint = new Vector2D(
                diff.x > 0 ? getRight() : getLeft(),
                centerA.y
            );
        } else {
            // Collision on Y axis
            penetration = overlapY;
            normal = new Vector2D(0, diff.y > 0 ? 1 : -1);
            contactPoint = new Vector2D(
                centerA.x,
                diff.y > 0 ? getBottom() : getTop()
            );
        }
        
        return new CollisionInfo(this, other, normal, penetration, contactPoint);
    }
    
    /**
     * Checks collision between box and circle.
     */
    private CollisionInfo checkBoxCircleCollision(CircleCollider circle) {
        Vector2D boxCenter = getCenter();
        Vector2D circleCenter = circle.getCenter();
        
        // Find closest point on box to circle center
        double closestX = Math.max(getLeft(), Math.min(circleCenter.x, getRight()));
        double closestY = Math.max(getTop(), Math.min(circleCenter.y, getBottom()));
        Vector2D closestPoint = new Vector2D(closestX, closestY);
        
        // Calculate distance from closest point to circle center
        Vector2D diff = circleCenter.subtract(closestPoint);
        double distance = diff.magnitude();
        
        // Check if there's a collision
        if (distance > circle.getRadius()) {
            return null;
        }
        
        // Calculate collision normal and penetration
        Vector2D normal;
        if (distance < 1e-6) {
            // Circle center is inside box, push out along closest axis
            Vector2D toCenter = circleCenter.subtract(boxCenter);
            double distToLeft = Math.abs(circleCenter.x - getLeft());
            double distToRight = Math.abs(getRight() - circleCenter.x);
            double distToTop = Math.abs(circleCenter.y - getTop());
            double distToBottom = Math.abs(getBottom() - circleCenter.y);
            
            double minDist = Math.min(Math.min(distToLeft, distToRight), Math.min(distToTop, distToBottom));
            
            if (minDist == distToLeft) normal = Vector2D.LEFT;
            else if (minDist == distToRight) normal = Vector2D.RIGHT;
            else if (minDist == distToTop) normal = Vector2D.UP;
            else normal = Vector2D.DOWN;
        } else {
            normal = diff.normalize();
        }
        
        double penetration = circle.getRadius() - distance;
        
        return new CollisionInfo(this, circle, normal, penetration, closestPoint);
    }
    
    @Override
    public void debugDraw(Graphics2D g2d) {
        g2d.setColor(isTrigger ? new Color(0, 255, 0, 100) : new Color(255, 0, 0, 100));
        int x = (int) (getLeft());
        int y = (int) (getTop());
        int w = (int) width;
        int h = (int) height;
        g2d.drawRect(x, y, w, h);
    }
}
