package engine;

import java.util.logging.Logger;

/**
 * Production-ready game loop with fixed timestep for deterministic physics.
 * Implements the "Fix Your Timestep" pattern for smooth, consistent updates.
 */
public class GameLoop implements Runnable {
    
    private static final Logger logger = Logger.getLogger(GameLoop.class.getName());
    
    private boolean running = false;
    private Thread gameThread;
    private GamePanel gamePanel;
    
    // Fixed timestep for physics (60 updates per second)
    private static final double FIXED_TIMESTEP = 1.0 / 60.0;
    private static final double MAX_FRAME_TIME = 0.25; // Prevent spiral of death
    
    // FPS tracking
    private int fps = 0;
    private int frameCount = 0;
    private long lastFpsTime = 0;
    
    // Accumulator for fixed timestep
    private double accumulator = 0.0;
    
    public GameLoop(GamePanel panel) {
        this.gamePanel = panel;
    }
    
    public void start() {
        if (running) return;
        
        running = true;
        gameThread = new Thread(this, "GameLoop-Thread");
        gameThread.start();
        logger.info("Game loop started with fixed timestep: " + FIXED_TIMESTEP + "s");
    }
    
    public void stop() {
        if (!running) return;
        
        running = false;
        try {
            gameThread.join(3000); // Wait up to 3 seconds
            logger.info("Game loop stopped");
        } catch (InterruptedException e) {
            logger.warning("Game loop thread interrupted during shutdown");
            Thread.currentThread().interrupt();
        }
    }
    
    @Override
    public void run() {
        long lastTime = System.nanoTime();
        final long SECOND = 1_000_000_000L;
        
        logger.info("Entering main game loop");
        
        while (running) {
            long currentTime = System.nanoTime();
            double deltaTime = (currentTime - lastTime) / (double) SECOND;
            lastTime = currentTime;
            
            // Prevent spiral of death
            if (deltaTime > MAX_FRAME_TIME) {
                deltaTime = MAX_FRAME_TIME;
            }
            
            accumulator += deltaTime;
            
            // Fixed timestep updates
            while (accumulator >= FIXED_TIMESTEP) {
                update(FIXED_TIMESTEP);
                accumulator -= FIXED_TIMESTEP;
            }
            
            // Calculate interpolation factor for smooth rendering
            double alpha = accumulator / FIXED_TIMESTEP;
            
            // Render with interpolation
            render(alpha);
            
            // Update FPS counter
            updateFpsCounter(currentTime);
            
            // Small yield to prevent CPU spinning
            Thread.yield();
        }
        
        logger.info("Exited main game loop");
    }
    
    /**
     * Updates game logic with fixed timestep.
     * @param deltaTime Fixed time step in seconds
     */
    private void update(double deltaTime) {
        if (gamePanel != null) {
            gamePanel.update(deltaTime);
        }
        
        // Update physics world
        PhysicsWorld.getInstance().step(deltaTime);
        
        // Update input manager (should be called after all updates)
        InputManager.getInstance().update();
    }
    
    /**
     * Renders the game with interpolation factor.
     * @param alpha Interpolation factor (0-1) for smooth rendering
     */
    private void render(double alpha) {
        if (gamePanel != null) {
            gamePanel.render();
        }
    }
    
    /**
     * Updates FPS counter for debugging.
     */
    private void updateFpsCounter(long currentTime) {
        frameCount++;
        if (currentTime - lastFpsTime >= 1_000_000_000L) {
            fps = frameCount;
            frameCount = 0;
            lastFpsTime = currentTime;
        }
    }
    
    public int getFps() {
        return fps;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public static double getFixedTimestep() {
        return FIXED_TIMESTEP;
    }
}
