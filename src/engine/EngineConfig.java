package engine;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Thread-safe singleton configuration manager.
 * Loads configuration from properties file.
 */
public class EngineConfig {
    
    private static volatile EngineConfig instance;
    private static final Logger logger = Logger.getLogger(EngineConfig.class.getName());
    
    private Properties properties;
    private static final String DEFAULT_CONFIG_FILE = "engine.properties";
    
    private EngineConfig() {
        properties = new Properties();
        loadDefaults();
        loadFromFile(DEFAULT_CONFIG_FILE);
    }
    
    public static EngineConfig getInstance() {
        if (instance == null) {
            synchronized (EngineConfig.class) {
                if (instance == null) {
                    instance = new EngineConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Loads default configuration values.
     */
    private void loadDefaults() {
        properties.setProperty("window.title", "2D Game Engine");
        properties.setProperty("window.width", "800");
        properties.setProperty("window.height", "600");
        properties.setProperty("window.resizable", "false");
        
        properties.setProperty("physics.gravity", "980");
        properties.setProperty("physics.fixedTimestep", "0.016666");
        
        properties.setProperty("debug.showFps", "true");
        properties.setProperty("debug.showColliders", "false");
        
        properties.setProperty("assets.path", "assets/");
    }
    
    /**
     * Loads configuration from a properties file.
     */
    public void loadFromFile(String filename) {
        try {
            InputStream input = new FileInputStream(filename);
            properties.load(input);
            input.close();
            logger.info("Loaded configuration from: " + filename);
        } catch (Exception e) {
            logger.warning("Could not load config file: " + filename + " (using defaults)");
        }
    }
    
    /**
     * Gets a string property.
     */
    public String getString(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
    
    /**
     * Gets an integer property.
     */
    public int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a double property.
     */
    public double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(properties.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Gets a boolean property.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(key, String.valueOf(defaultValue)));
    }
    
    /**
     * Sets a property.
     */
    public void set(String key, String value) {
        properties.setProperty(key, value);
    }
}
