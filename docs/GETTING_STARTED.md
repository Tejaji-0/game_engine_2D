# Getting Started with the 2D Game Engine

This guide will help you create your first game using the 2D Game Engine.

## Table of Contents
1. [Basic Setup](#basic-setup)
2. [Creating Your First Game](#creating-your-first-game)
3. [Adding Physics](#adding-physics)
4. [Handling Input](#handling-input)
5. [Working with Collisions](#working-with-collisions)
6. [Next Steps](#next-steps)

## Basic Setup

### Prerequisites
- Java Development Kit (JDK) 8 or higher
- A text editor or IDE (VSCode, IntelliJ IDEA, Eclipse, etc.)

### Building the Engine
```bash
# Windows
build.bat

# Linux/Mac
chmod +x build.sh
./build.sh
```

### Running the Demo
```bash
# Windows
run_demo.bat

# Linux/Mac
chmod +x run_demo.sh
./run_demo.sh
```

## Creating Your First Game

### Step 1: Create the Main Class

Create a new file `src/demo/MyFirstGame.java`:

```java
package demo;

import engine.*;
import java.awt.Color;

public class MyFirstGame {
    private GameWindow window;
    private GamePanel gamePanel;
    private GameLoop gameLoop;
    
    public MyFirstGame() {
        window = new GameWindow();
        gamePanel = window.getGamePanel();
        
        setupScene();
        
        gameLoop = new GameLoop(gamePanel);
    }
    
    private void setupScene() {
        // We'll add objects here
    }
    
    public void start() {
        window.display();
        gameLoop.start();
    }
    
    public static void main(String[] args) {
        MyFirstGame game = new MyFirstGame();
        game.start();
    }
}
```

### Step 2: Add a Simple Game Object

Add this inside the `setupScene()` method:

```java
private void setupScene() {
    // Create a simple box
    GameObject box = new GameObject("MyBox", new Vector2D(400, 300));
    
    // Add visual representation
    SpriteRenderer renderer = new SpriteRenderer(Color.BLUE, 50, 50);
    box.addComponent(renderer);
    
    // Add to scene
    gamePanel.addGameObject(box);
}
```

### Step 3: Compile and Run

```bash
javac -d bin -sourcepath src src/engine/*.java src/demo/MyFirstGame.java
java -cp bin demo.MyFirstGame
```

You should see a blue box in the center of the screen!

## Adding Physics

### Making Objects Fall

```java
private void setupScene() {
    // Create ground
    GameObject ground = new GameObject("Ground", new Vector2D(400, 550));
    ground.addComponent(new SpriteRenderer(Color.GRAY, 800, 50));
    
    PhysicsBody groundBody = ground.createPhysicsBody(1000);
    groundBody.setBodyType(PhysicsBody.BodyType.STATIC);
    
    BoxCollider groundCollider = new BoxCollider(ground, 800, 50);
    ground.setCollider(groundCollider);
    
    gamePanel.addGameObject(ground);
    
    // Create falling box
    GameObject box = new GameObject("FallingBox", new Vector2D(400, 100));
    box.addComponent(new SpriteRenderer(Color.RED, 40, 40));
    
    PhysicsBody boxBody = box.createPhysicsBody(1.0);
    boxBody.setBodyType(PhysicsBody.BodyType.DYNAMIC);
    boxBody.setRestitution(0.5); // Bounce
    
    BoxCollider boxCollider = new BoxCollider(box, 40, 40);
    box.setCollider(boxCollider);
    
    gamePanel.addGameObject(box);
}
```

The box will now fall due to gravity and bounce off the ground!

## Handling Input

### Creating a Movable Player

```java
// Inside setupScene()
GameObject player = new GameObject("Player", new Vector2D(400, 300));
player.addComponent(new SpriteRenderer(Color.GREEN, 40, 40));

PhysicsBody playerBody = player.createPhysicsBody(1.0);
playerBody.setBodyType(PhysicsBody.BodyType.DYNAMIC);
playerBody.setFriction(0.8);

BoxCollider playerCollider = new BoxCollider(player, 40, 40);
player.setCollider(playerCollider);

// Add custom movement component
player.addComponent(new PlayerMovement());

gamePanel.addGameObject(player);
```

Create the PlayerMovement component:

```java
class PlayerMovement extends Component {
    private static final double MOVE_SPEED = 300;
    
    @Override
    public void update(double deltaTime) {
        PhysicsBody body = gameObject.getPhysicsBody();
        if (body == null) return;
        
        InputManager input = InputManager.getInstance();
        
        // Get horizontal input (-1, 0, or 1)
        double horizontal = input.getHorizontalAxis();
        
        if (horizontal != 0) {
            Vector2D force = new Vector2D(horizontal * MOVE_SPEED, 0);
            body.applyForce(force);
        }
        
        // Jump
        if (input.isActionJustPressed("Jump")) {
            body.applyImpulse(new Vector2D(0, -400));
        }
    }
}
```

## Working with Collisions

### Detecting Collisions

```java
class CollisionHandler extends Component {
    @Override
    public void onCollision(GameObject other, Collider.CollisionInfo collision) {
        System.out.println("Collided with: " + other.getName());
        
        // Check what we hit
        if (other.getTag().equals("Enemy")) {
            // Handle enemy collision
            System.out.println("Hit enemy!");
        } else if (other.getTag().equals("Coin")) {
            // Collect coin
            gameObject.getGameObject().removeGameObject(other);
        }
    }
}
```

### Creating Trigger Zones

```java
GameObject trigger = new GameObject("TriggerZone", new Vector2D(300, 300));

BoxCollider triggerCollider = new BoxCollider(trigger, 100, 100);
triggerCollider.setTrigger(true); // Won't physically collide
trigger.setCollider(triggerCollider);

trigger.addComponent(new Component() {
    @Override
    public void onCollision(GameObject other, Collider.CollisionInfo collision) {
        if (other.getTag().equals("Player")) {
            System.out.println("Player entered trigger zone!");
        }
    }
});

gamePanel.addGameObject(trigger);
```

## Common Patterns

### Spawning Objects on Click

```java
class ClickSpawner extends Component {
    @Override
    public void update(double deltaTime) {
        InputManager input = InputManager.getInstance();
        
        if (input.isMouseButtonJustPressed(MouseEvent.BUTTON1)) {
            Vector2D mousePos = input.getMousePosition();
            spawnObjectAt(mousePos);
        }
    }
    
    private void spawnObjectAt(Vector2D position) {
        GameObject obj = new GameObject("Spawned", position);
        obj.addComponent(new SpriteRenderer(Color.YELLOW, 30, 30));
        
        PhysicsBody body = obj.createPhysicsBody(1.0);
        body.setBodyType(PhysicsBody.BodyType.DYNAMIC);
        
        CircleCollider collider = new CircleCollider(obj, 15);
        obj.setCollider(collider);
        
        gamePanel.addGameObject(obj);
    }
}
```

### Destroying Objects

```java
// Destroy after time
class TimedDestroy extends Component {
    private double lifetime;
    private double elapsed = 0;
    
    public TimedDestroy(double lifetime) {
        this.lifetime = lifetime;
    }
    
    @Override
    public void update(double deltaTime) {
        elapsed += deltaTime;
        if (elapsed >= lifetime) {
            gamePanel.removeGameObject(gameObject);
        }
    }
}

// Usage
obj.addComponent(new TimedDestroy(3.0)); // Destroy after 3 seconds
```

### Following Another Object

```java
class FollowTarget extends Component {
    private GameObject target;
    private double speed;
    
    public FollowTarget(GameObject target, double speed) {
        this.target = target;
        this.speed = speed;
    }
    
    @Override
    public void update(double deltaTime) {
        if (target == null || target.isDestroyed()) return;
        
        Vector2D myPos = gameObject.getPosition();
        Vector2D targetPos = target.getPosition();
        
        Vector2D direction = targetPos.subtract(myPos).normalize();
        Vector2D newPos = myPos.add(direction.multiply(speed * deltaTime));
        
        gameObject.setPosition(newPos);
    }
}
```

## Next Steps

### Learn More
- Read the [ARCHITECTURE.md](../docs/ARCHITECTURE.md) for deep dive into engine design
- Study the [PhysicsDemo.java](../src/demo/PhysicsDemo.java) for complete examples
- Explore the API documentation in README.md

### Extend the Engine
- Add sprite animations
- Implement particle systems
- Create a tilemap system
- Add audio support
- Build a level editor

### Example Projects to Try
1. **Platformer** - Use platforms, player controller, and enemies
2. **Physics Puzzle** - Stack objects, create contraptions
3. **Top-Down Shooter** - Mouse aiming, projectiles, enemies
4. **Breakout Clone** - Ball physics, brick destruction
5. **Angry Birds Style** - Trajectory physics, destructible structures

## Debugging Tips

### Enable Debug Visualization
```java
gamePanel.setShowDebug(true);      // Show object counts
gamePanel.setShowFps(true);        // Show FPS
gamePanel.setShowColliders(true);  // Show collision bounds
```

### Print GameObject Info
```java
System.out.println("Position: " + gameObject.getPosition());
System.out.println("Velocity: " + body.getVelocity());
System.out.println("Object count: " + gamePanel.getAllGameObjects().size());
```

### Common Issues
- **Objects falling through floor**: Increase mass or reduce velocity
- **Objects not colliding**: Ensure collider is added and GameObject is active
- **Input not working**: Make sure GamePanel has focus (click on window)
- **Poor performance**: Use object pooling, reduce object count

## Resources

- [Java 2D Graphics Tutorial](https://docs.oracle.com/javase/tutorial/2d/)
- [Game Programming Patterns](https://gameprogrammingpatterns.com/)
- [Fix Your Timestep](https://gafferongames.com/post/fix_your_timestep/)

Happy Game Development! 🎮
