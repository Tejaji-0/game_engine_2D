package engine;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * Main rendering panel for the game.
 */
public class GamePanel extends JPanel {
    
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    
    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.DARK_GRAY);
        setDoubleBuffered(true);
        setFocusable(true);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Clear screen - just showing background for now
        // TODO: render actual game objects
    }
    
    public void render() {
        repaint();
    }
}
