package engine;

/**
 * Main entry point for the 2D game engine.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("2D Game Engine - Starting...");
        
        GameWindow window = new GameWindow();
        window.show();
        
        System.out.println("Window created successfully!");
        
        GameLoop gameLoop = new GameLoop(window.getGamePanel());
        gameLoop.start();
    }
}
