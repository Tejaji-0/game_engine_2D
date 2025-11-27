package engine;

import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;

/**
 * Component for rendering sprites (images) on GameObjects.
 */
public class SpriteRenderer extends Component {
    
    private Image sprite;
    private Color color;
    private int width;
    private int height;
    private boolean flipX;
    private boolean flipY;
    private double opacity;
    
    /**
     * Creates a new SpriteRenderer with a color (for simple shapes).
     * @param color The color to render
     * @param width Width in pixels
     * @param height Height in pixels
     */
    public SpriteRenderer(Color color, int width, int height) {
        super();
        this.color = color;
        this.width = width;
        this.height = height;
        this.sprite = null;
        this.flipX = false;
        this.flipY = false;
        this.opacity = 1.0;
    }
    
    /**
     * Creates a new SpriteRenderer with an image.
     * @param sprite The sprite image
     */
    public SpriteRenderer(Image sprite) {
        super();
        this.sprite = sprite;
        this.width = sprite.getWidth(null);
        this.height = sprite.getHeight(null);
        this.color = Color.WHITE;
        this.flipX = false;
        this.flipY = false;
        this.opacity = 1.0;
    }
    
    /**
     * Creates a new SpriteRenderer with an image and custom dimensions.
     * @param sprite The sprite image
     * @param width Width in pixels
     * @param height Height in pixels
     */
    public SpriteRenderer(Image sprite, int width, int height) {
        super();
        this.sprite = sprite;
        this.width = width;
        this.height = height;
        this.color = Color.WHITE;
        this.flipX = false;
        this.flipY = false;
        this.opacity = 1.0;
    }
    
    @Override
    public void render(Graphics2D g2d) {
        if (!enabled || gameObject == null) return;
        
        Vector2D pos = gameObject.getPosition();
        double rotation = gameObject.getRotation();
        Vector2D scale = gameObject.getScale();
        
        // Save original transform
        AffineTransform oldTransform = g2d.getTransform();
        
        // Apply transformations
        AffineTransform transform = new AffineTransform();
        transform.translate(pos.x, pos.y);
        transform.rotate(rotation);
        transform.scale(
            scale.x * (flipX ? -1 : 1),
            scale.y * (flipY ? -1 : 1)
        );
        g2d.setTransform(transform);
        
        // Set opacity
        if (opacity < 1.0) {
            g2d.setComposite(java.awt.AlphaComposite.getInstance(
                java.awt.AlphaComposite.SRC_OVER, (float) opacity));
        }
        
        // Render sprite or colored rectangle
        int x = -width / 2;
        int y = -height / 2;
        
        if (sprite != null) {
            g2d.drawImage(sprite, x, y, width, height, null);
        } else if (color != null) {
            g2d.setColor(color);
            g2d.fillRect(x, y, width, height);
        }
        
        // Restore original transform and composite
        g2d.setTransform(oldTransform);
        g2d.setComposite(java.awt.AlphaComposite.getInstance(
            java.awt.AlphaComposite.SRC_OVER, 1.0f));
    }
    
    // Getters and setters
    
    public Image getSprite() { return sprite; }
    public void setSprite(Image sprite) { 
        this.sprite = sprite;
        if (sprite != null) {
            this.width = sprite.getWidth(null);
            this.height = sprite.getHeight(null);
        }
    }
    
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    
    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }
    
    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }
    
    public boolean isFlipX() { return flipX; }
    public void setFlipX(boolean flipX) { this.flipX = flipX; }
    
    public boolean isFlipY() { return flipY; }
    public void setFlipY(boolean flipY) { this.flipY = flipY; }
    
    public double getOpacity() { return opacity; }
    public void setOpacity(double opacity) { 
        this.opacity = Math.max(0, Math.min(1, opacity)); 
    }
}
