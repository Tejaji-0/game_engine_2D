package demo;

import engine.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

/**
 * Physics demo game showcasing the 2D game engine features.
 * Includes dynamic boxes, circles, platforms, and interactive controls.
 */
public class PhysicsDemo {
    
    private GameWindow window;
    private GamePanel gamePanel;
    private GameLoop gameLoop;
    
    public PhysicsDemo() {
        // Create window and panel
        window = new GameWindow();
        gamePanel = window.getGamePanel();
        
        // Configure debug display
        gamePanel.setShowFps(true);
        gamePanel.setShowDebug(true);
        gamePanel.setShowColliders(true);
        
        // Setup scene
        setupScene();
        
        // Create game loop
        gameLoop = new GameLoop(gamePanel);
    }
    
    /**
     * Sets up the demo scene with various physics objects.
     */
    private void setupScene() {
        // Configure physics world
        PhysicsWorld physics = PhysicsWorld.getInstance();
        physics.setGravity(new Vector2D(0, 980)); // Earth-like gravity
        physics.setWorldBounds(0, 0, 800, 600);
        
        // Create ground platform
        createStaticPlatform(400, 550, 800, 50, Color.GRAY);
        
        // Create side walls
        createStaticPlatform(25, 300, 50, 600, Color.DARK_GRAY);
        createStaticPlatform(775, 300, 50, 600, Color.DARK_GRAY);
        
        // Create platforms at different heights
        createStaticPlatform(200, 450, 150, 20, Color.LIGHT_GRAY);
        createStaticPlatform(500, 350, 150, 20, Color.LIGHT_GRAY);
        createStaticPlatform(300, 250, 150, 20, Color.LIGHT_GRAY);
        
        // Create dynamic boxes
        createDynamicBox(150, 100, 40, 40, Color.RED, 1.0);
        createDynamicBox(250, 150, 50, 50, Color.BLUE, 2.0);
        createDynamicBox(350, 80, 30, 30, Color.GREEN, 0.5);
        
        // Create dynamic circles
        createDynamicCircle(450, 100, 25, Color.YELLOW, 1.0);
        createDynamicCircle(550, 150, 30, Color.MAGENTA, 1.5);
        createDynamicCircle(650, 100, 20, Color.CYAN, 0.8);
        
        // Create player-controlled object
        createPlayer(400, 200);
        
        // Create object spawner
        createSpawner();
    }
    
    /**
     * Creates a static platform.
     */
    private GameObject createStaticPlatform(double x, double y, double width, double height, Color color) {
        GameObject platform = new GameObject("Platform", new Vector2D(x, y));
        
        // Add visual
        SpriteRenderer renderer = new SpriteRenderer(color, (int) width, (int) height);
        platform.addComponent(renderer);
        
        // Add physics
        PhysicsBody body = platform.createPhysicsBody(1000); // Large mass for stability
        body.setBodyType(PhysicsBody.BodyType.STATIC);
        
        // Add collider
        BoxCollider collider = new BoxCollider(platform, width, height);
        platform.setCollider(collider);
        
        gamePanel.addGameObject(platform);
        return platform;
    }
    
    /**
     * Creates a dynamic box with physics.
     */
    private GameObject createDynamicBox(double x, double y, double width, double height, 
                                       Color color, double mass) {
        GameObject box = new GameObject("Box", new Vector2D(x, y));
        box.setTag("Dynamic");
        
        // Add visual
        SpriteRenderer renderer = new SpriteRenderer(color, (int) width, (int) height);
        box.addComponent(renderer);
        
        // Add physics
        PhysicsBody body = box.createPhysicsBody(mass);
        body.setBodyType(PhysicsBody.BodyType.DYNAMIC);
        body.setRestitution(0.3); // Some bounce
        body.setFriction(0.5);
        
        // Add collider
        BoxCollider collider = new BoxCollider(box, width, height);
        box.setCollider(collider);
        
        gamePanel.addGameObject(box);
        return box;
    }
    
    /**
     * Creates a dynamic circle with physics.
     */
    private GameObject createDynamicCircle(double x, double y, double radius, 
                                          Color color, double mass) {
        GameObject circle = new GameObject("Circle", new Vector2D(x, y));
        circle.setTag("Dynamic");
        
        // Add visual
        SpriteRenderer renderer = new SpriteRenderer(color, (int) (radius * 2), (int) (radius * 2));
        circle.addComponent(renderer);
        
        // Add physics
        PhysicsBody body = circle.createPhysicsBody(mass);
        body.setBodyType(PhysicsBody.BodyType.DYNAMIC);
        body.setRestitution(0.7); // Very bouncy
        body.setFriction(0.3);
        
        // Add collider
        CircleCollider collider = new CircleCollider(circle, radius);
        circle.setCollider(collider);
        
        gamePanel.addGameObject(circle);
        return circle;
    }
    
    /**
     * Creates a player-controlled object.
     */
    private GameObject createPlayer(double x, double y) {
        GameObject player = new GameObject("Player", new Vector2D(x, y));
        player.setTag("Player");
        
        // Add visual
        SpriteRenderer renderer = new SpriteRenderer(Color.WHITE, 40, 40);
        player.addComponent(renderer);
        
        // Add physics
        PhysicsBody body = player.createPhysicsBody(1.5);
        body.setBodyType(PhysicsBody.BodyType.DYNAMIC);
        body.setRestitution(0.2);
        body.setFriction(0.7);
        body.setDrag(0.5);
        
        // Add collider
        BoxCollider collider = new BoxCollider(player, 40, 40);
        player.setCollider(collider);
        
        // Add player controller
        PlayerController controller = new PlayerController();
        player.addComponent(controller);
        
        gamePanel.addGameObject(player);
        return player;
    }
    
    /**
     * Creates an object spawner component.
     */
    private void createSpawner() {
        GameObject spawner = new GameObject("Spawner", Vector2D.ZERO);
        spawner.addComponent(new ObjectSpawner(gamePanel));
        gamePanel.addGameObject(spawner);
    }
    
    public void start() {
        window.display();
        gameLoop.start();
    }
    
    public static void main(String[] args) {
        System.out.println("=== 2D Physics Engine Demo ===");
        System.out.println("Controls:");
        System.out.println("  WASD / Arrow Keys - Move player");
        System.out.println("  SPACE - Jump");
        System.out.println("  1 - Spawn Box");
        System.out.println("  2 - Spawn Circle");
        System.out.println("  R - Reset Scene");
        System.out.println("  C - Toggle Collider Debug");
        System.out.println("  D - Toggle Debug Info");
        System.out.println("==============================");
        
        PhysicsDemo demo = new PhysicsDemo();
        demo.start();
    }
}

/**
 * Component for controlling the player character.
 */
class PlayerController extends Component {
    
    private static final double MOVE_SPEED = 300;
    private static final double JUMP_FORCE = -500;
    private static final double MAX_SPEED = 400;
    
    private boolean isGrounded = false;
    
    @Override
    public void update(double deltaTime) {
        PhysicsBody body = gameObject.getPhysicsBody();
        if (body == null) return;
        
        InputManager input = InputManager.getInstance();
        
        // Horizontal movement
        double horizontal = input.getHorizontalAxis();
        if (horizontal != 0) {
            Vector2D force = new Vector2D(horizontal * MOVE_SPEED, 0);
            body.applyForce(force);
            
            // Limit horizontal speed
            Vector2D vel = body.getVelocity();
            if (Math.abs(vel.x) > MAX_SPEED) {
                body.setVelocity(new Vector2D(
                    Math.signum(vel.x) * MAX_SPEED,
                    vel.y
                ));
            }
        }
        
        // Jump
        if (input.isActionJustPressed("Jump") && isGrounded) {
            body.applyImpulse(new Vector2D(0, JUMP_FORCE));
            isGrounded = false;
        }
        
        // Check if grounded (simple check - can be improved)
        if (body.getVelocity().y >= 0) {
            isGrounded = true;
        }
    }
    
    @Override
    public void onCollision(GameObject other, Collider.CollisionInfo collision) {
        // Determine if collision is with ground
        if (collision.normal.y < -0.5) { // Normal pointing up
            isGrounded = true;
        }
    }
}

/**
 * Component for spawning objects on key press.
 */
class ObjectSpawner extends Component {
    
    private GamePanel gamePanel;
    private java.util.Random random = new java.util.Random();
    
    public ObjectSpawner(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }
    
    @Override
    public void update(double deltaTime) {
        InputManager input = InputManager.getInstance();
        
        // Spawn box on '1'
        if (input.isKeyJustPressed(KeyEvent.VK_1)) {
            spawnBox();
        }
        
        // Spawn circle on '2'
        if (input.isKeyJustPressed(KeyEvent.VK_2)) {
            spawnCircle();
        }
        
        // Reset scene on 'R'
        if (input.isKeyJustPressed(KeyEvent.VK_R)) {
            resetScene();
        }
        
        // Toggle collider debug on 'C'
        if (input.isKeyJustPressed(KeyEvent.VK_C)) {
            gamePanel.setShowColliders(!gamePanel.isShowColliders());
        }
        
        // Toggle debug info on 'D'
        if (input.isKeyJustPressed(KeyEvent.VK_D)) {
            gamePanel.setShowDebug(!gamePanel.isShowDebug());
        }
    }
    
    private void spawnBox() {
        double x = 100 + random.nextDouble() * 600;
        double y = 50;
        double size = 20 + random.nextDouble() * 40;
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        double mass = 0.5 + random.nextDouble() * 2.0;
        
        GameObject box = new GameObject("SpawnedBox", new Vector2D(x, y));
        box.setTag("Dynamic");
        
        SpriteRenderer renderer = new SpriteRenderer(color, (int) size, (int) size);
        box.addComponent(renderer);
        
        PhysicsBody body = box.createPhysicsBody(mass);
        body.setBodyType(PhysicsBody.BodyType.DYNAMIC);
        body.setRestitution(0.3 + random.nextDouble() * 0.4);
        body.setFriction(0.3 + random.nextDouble() * 0.4);
        
        BoxCollider collider = new BoxCollider(box, size, size);
        box.setCollider(collider);
        
        gamePanel.addGameObject(box);
    }
    
    private void spawnCircle() {
        double x = 100 + random.nextDouble() * 600;
        double y = 50;
        double radius = 15 + random.nextDouble() * 30;
        Color color = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        double mass = 0.5 + random.nextDouble() * 2.0;
        
        GameObject circle = new GameObject("SpawnedCircle", new Vector2D(x, y));
        circle.setTag("Dynamic");
        
        SpriteRenderer renderer = new SpriteRenderer(color, (int) (radius * 2), (int) (radius * 2));
        circle.addComponent(renderer);
        
        PhysicsBody body = circle.createPhysicsBody(mass);
        body.setBodyType(PhysicsBody.BodyType.DYNAMIC);
        body.setRestitution(0.5 + random.nextDouble() * 0.4);
        body.setFriction(0.2 + random.nextDouble() * 0.3);
        
        CircleCollider collider = new CircleCollider(circle, radius);
        circle.setCollider(collider);
        
        gamePanel.addGameObject(circle);
    }
    
    private void resetScene() {
        // Remove all dynamic objects
        for (GameObject obj : gamePanel.getAllGameObjects()) {
            if ("Dynamic".equals(obj.getTag())) {
                gamePanel.removeGameObject(obj);
            }
        }
    }
}
