package engine;

/**
 * Basic game loop - handles updating and rendering.
 * TODO: Improve timing and FPS limiting
 */
public class GameLoop implements Runnable {
    
    private boolean running = false;
    private Thread gameThread;
    private GamePanel gamePanel;
    
    // Target FPS (will improve this later)
    private final int TARGET_FPS = 60;
    
    public GameLoop(GamePanel panel) {
        this.gamePanel = panel;
    }
    
    public void start() {
        if (running) return;
        
        running = true;
        gameThread = new Thread(this);
        gameThread.start();
        System.out.println("Game loop started");
    }
    
    public void stop() {
        running = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void run() {
        while (running) {
            update();
            render();
            
            // Simple sleep for now - not accurate timing yet
            try {
                Thread.sleep(1000 / TARGET_FPS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void update() {
        // TODO: Update game logic
    }
    
    private void render() {
        if (gamePanel != null) {
            gamePanel.render();
        }
    }
}
