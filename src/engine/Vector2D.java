package engine;

/**
 * Immutable 2D vector class for physics calculations.
 * Provides standard vector operations for game physics.
 */
public class Vector2D {
    
    public final double x;
    public final double y;
    
    // Common vector constants
    public static final Vector2D ZERO = new Vector2D(0, 0);
    public static final Vector2D UP = new Vector2D(0, -1);
    public static final Vector2D DOWN = new Vector2D(0, 1);
    public static final Vector2D LEFT = new Vector2D(-1, 0);
    public static final Vector2D RIGHT = new Vector2D(1, 0);
    
    /**
     * Creates a new vector with the specified components.
     * @param x The x component
     * @param y The y component
     */
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Adds another vector to this vector.
     * @param other The vector to add
     * @return A new vector representing the sum
     */
    public Vector2D add(Vector2D other) {
        return new Vector2D(x + other.x, y + other.y);
    }
    
    /**
     * Subtracts another vector from this vector.
     * @param other The vector to subtract
     * @return A new vector representing the difference
     */
    public Vector2D subtract(Vector2D other) {
        return new Vector2D(x - other.x, y - other.y);
    }
    
    /**
     * Multiplies this vector by a scalar.
     * @param scalar The scalar value
     * @return A new scaled vector
     */
    public Vector2D multiply(double scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }
    
    /**
     * Divides this vector by a scalar.
     * @param scalar The scalar value
     * @return A new scaled vector
     */
    public Vector2D divide(double scalar) {
        if (Math.abs(scalar) < 1e-10) {
            return ZERO;
        }
        return new Vector2D(x / scalar, y / scalar);
    }
    
    /**
     * Calculates the dot product with another vector.
     * @param other The other vector
     * @return The dot product
     */
    public double dot(Vector2D other) {
        return x * other.x + y * other.y;
    }
    
    /**
     * Calculates the magnitude (length) of this vector.
     * @return The magnitude
     */
    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }
    
    /**
     * Calculates the squared magnitude of this vector.
     * Faster than magnitude() as it avoids square root.
     * @return The squared magnitude
     */
    public double magnitudeSquared() {
        return x * x + y * y;
    }
    
    /**
     * Returns a normalized version of this vector (unit vector).
     * @return A normalized vector with magnitude 1
     */
    public Vector2D normalize() {
        double mag = magnitude();
        if (mag < 1e-10) {
            return ZERO;
        }
        return divide(mag);
    }
    
    /**
     * Calculates the distance to another vector.
     * @param other The other vector
     * @return The distance
     */
    public double distanceTo(Vector2D other) {
        return subtract(other).magnitude();
    }
    
    /**
     * Calculates the angle between this vector and another in radians.
     * @param other The other vector
     * @return The angle in radians
     */
    public double angleTo(Vector2D other) {
        double dot = dot(other);
        double mags = magnitude() * other.magnitude();
        if (mags < 1e-10) {
            return 0;
        }
        return Math.acos(Math.max(-1, Math.min(1, dot / mags)));
    }
    
    /**
     * Rotates this vector by the specified angle in radians.
     * @param angle The angle in radians
     * @return A new rotated vector
     */
    public Vector2D rotate(double angle) {
        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        return new Vector2D(
            x * cos - y * sin,
            x * sin + y * cos
        );
    }
    
    /**
     * Projects this vector onto another vector.
     * @param onto The vector to project onto
     * @return The projected vector
     */
    public Vector2D project(Vector2D onto) {
        double mag = onto.magnitudeSquared();
        if (mag < 1e-10) {
            return ZERO;
        }
        double scalar = dot(onto) / mag;
        return onto.multiply(scalar);
    }
    
    /**
     * Returns a perpendicular vector (rotated 90 degrees counter-clockwise).
     * @return A perpendicular vector
     */
    public Vector2D perpendicular() {
        return new Vector2D(-y, x);
    }
    
    /**
     * Linearly interpolates between this vector and another.
     * @param target The target vector
     * @param t The interpolation factor (0-1)
     * @return The interpolated vector
     */
    public Vector2D lerp(Vector2D target, double t) {
        return new Vector2D(
            x + (target.x - x) * t,
            y + (target.y - y) * t
        );
    }
    
    @Override
    public String toString() {
        return String.format("Vector2D(%.2f, %.2f)", x, y);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector2D)) return false;
        Vector2D other = (Vector2D) obj;
        return Math.abs(x - other.x) < 1e-10 && Math.abs(y - other.y) < 1e-10;
    }
    
    @Override
    public int hashCode() {
        long xBits = Double.doubleToLongBits(x);
        long yBits = Double.doubleToLongBits(y);
        return (int) (xBits ^ (xBits >>> 32) ^ yBits ^ (yBits >>> 32));
    }
}
