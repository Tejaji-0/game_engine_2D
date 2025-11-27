package engine;

/**
 * Utility class for common mathematical operations.
 */
public class MathUtils {
    
    public static final double PI = Math.PI;
    public static final double TWO_PI = Math.PI * 2;
    public static final double HALF_PI = Math.PI / 2;
    public static final double DEG_TO_RAD = Math.PI / 180.0;
    public static final double RAD_TO_DEG = 180.0 / Math.PI;
    
    /**
     * Clamps a value between min and max.
     */
    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Clamps a value between min and max.
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
    
    /**
     * Linearly interpolates between a and b by t.
     */
    public static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }
    
    /**
     * Converts degrees to radians.
     */
    public static double toRadians(double degrees) {
        return degrees * DEG_TO_RAD;
    }
    
    /**
     * Converts radians to degrees.
     */
    public static double toDegrees(double radians) {
        return radians * RAD_TO_DEG;
    }
    
    /**
     * Checks if two values are approximately equal.
     */
    public static boolean approximately(double a, double b, double epsilon) {
        return Math.abs(a - b) < epsilon;
    }
    
    /**
     * Checks if two values are approximately equal (default epsilon).
     */
    public static boolean approximately(double a, double b) {
        return approximately(a, b, 1e-6);
    }
    
    /**
     * Moves value towards target by maxDelta.
     */
    public static double moveTowards(double current, double target, double maxDelta) {
        if (Math.abs(target - current) <= maxDelta) {
            return target;
        }
        return current + Math.signum(target - current) * maxDelta;
    }
    
    /**
     * Returns the sign of a number (-1, 0, or 1).
     */
    public static int sign(double value) {
        return value < 0 ? -1 : (value > 0 ? 1 : 0);
    }
    
    /**
     * Wraps an angle to the range [0, 2π).
     */
    public static double wrapAngle(double angle) {
        angle = angle % TWO_PI;
        if (angle < 0) {
            angle += TWO_PI;
        }
        return angle;
    }
    
    /**
     * Returns the shortest angle between two angles in radians.
     */
    public static double deltaAngle(double current, double target) {
        double delta = wrapAngle(target - current);
        if (delta > PI) {
            delta -= TWO_PI;
        }
        return delta;
    }
    
    /**
     * Smooth damp - smoothly interpolates towards a target.
     */
    public static double smoothDamp(double current, double target, double currentVelocity, 
                                   double smoothTime, double maxSpeed, double deltaTime) {
        smoothTime = Math.max(0.0001, smoothTime);
        double omega = 2.0 / smoothTime;
        double x = omega * deltaTime;
        double exp = 1.0 / (1.0 + x + 0.48 * x * x + 0.235 * x * x * x);
        
        double change = current - target;
        double originalTo = target;
        
        double maxChange = maxSpeed * smoothTime;
        change = clamp(change, -maxChange, maxChange);
        target = current - change;
        
        double temp = (currentVelocity + omega * change) * deltaTime;
        double newValue = target + (change + temp) * exp;
        
        if ((originalTo - current > 0.0) == (newValue > originalTo)) {
            newValue = originalTo;
        }
        
        return newValue;
    }
    
    /**
     * Returns a random value between min (inclusive) and max (exclusive).
     */
    public static double random(double min, double max) {
        return min + Math.random() * (max - min);
    }
    
    /**
     * Returns a random integer between min (inclusive) and max (exclusive).
     */
    public static int randomInt(int min, int max) {
        return min + (int) (Math.random() * (max - min));
    }
}
