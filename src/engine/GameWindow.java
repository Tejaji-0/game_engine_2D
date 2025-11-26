package engine;

import javax.swing.JFrame;

/**
 * Main game window using JFrame.
 */
public class GameWindow extends JFrame {
    
    private GamePanel gamePanel;
    
    public GameWindow() {
        setTitle("2D Game Engine");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        gamePanel = new GamePanel();
        add(gamePanel);
        
        pack();
        setLocationRelativeTo(null); // center on screen
    }
    
    public void show() {
        setVisible(true);
    }
    
    public GamePanel getGamePanel() {
        return gamePanel;
    }
}
