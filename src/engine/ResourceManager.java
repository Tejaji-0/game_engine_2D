package engine;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Manages loading and caching of game resources (images, sounds, etc.).
 * Thread-safe singleton implementation.
 */
public class ResourceManager {
    
    private static volatile ResourceManager instance;
    private static final Logger logger = Logger.getLogger(ResourceManager.class.getName());
    
    private Map<String, Image> imageCache;
    private String assetPath;
    
    private ResourceManager() {
        imageCache = new HashMap<>();
        assetPath = "assets/";
    }
    
    public static ResourceManager getInstance() {
        if (instance == null) {
            synchronized (ResourceManager.class) {
                if (instance == null) {
                    instance = new ResourceManager();
                }
            }
        }
        return instance;
    }
    
    /**
     * Sets the base path for assets.
     * @param path The base asset path
     */
    public void setAssetPath(String path) {
        this.assetPath = path.endsWith("/") ? path : path + "/";
    }
    
    /**
     * Loads an image from file or cache.
     * @param filename The filename relative to asset path
     * @return The loaded image, or null if loading failed
     */
    public Image loadImage(String filename) {
        // Check cache first
        if (imageCache.containsKey(filename)) {
            return imageCache.get(filename);
        }
        
        Image image = null;
        
        try {
            // Try loading from file system
            File file = new File(assetPath + filename);
            if (file.exists()) {
                image = ImageIO.read(file);
                logger.info("Loaded image from file: " + filename);
            } else {
                // Try loading from classpath
                InputStream stream = getClass().getClassLoader().getResourceAsStream(assetPath + filename);
                if (stream != null) {
                    image = ImageIO.read(stream);
                    stream.close();
                    logger.info("Loaded image from classpath: " + filename);
                } else {
                    logger.warning("Image not found: " + filename);
                }
            }
            
            // Cache the image
            if (image != null) {
                imageCache.put(filename, image);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to load image: " + filename, e);
        }
        
        return image;
    }
    
    /**
     * Unloads an image from the cache.
     * @param filename The filename to unload
     */
    public void unloadImage(String filename) {
        Image image = imageCache.remove(filename);
        if (image != null) {
            image.flush();
            logger.info("Unloaded image: " + filename);
        }
    }
    
    /**
     * Clears all cached resources.
     */
    public void clearCache() {
        for (Image image : imageCache.values()) {
            image.flush();
        }
        imageCache.clear();
        logger.info("Cleared resource cache");
    }
    
    /**
     * Gets cache statistics.
     * @return A map with cache statistics
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("imageCount", imageCache.size());
        stats.put("cachedImages", imageCache.keySet());
        return stats;
    }
    
    /**
     * Checks if an image is cached.
     * @param filename The filename to check
     * @return true if the image is in cache
     */
    public boolean isCached(String filename) {
        return imageCache.containsKey(filename);
    }
}
