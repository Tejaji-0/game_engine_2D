# 2D Java Game Engine 🎮

A production-ready 2D game engine built with Java and AWT/Swing, featuring a complete physics system, component-based architecture, and comprehensive collision detection.

## ✨ Features

### Core Engine
- ✅ **Fixed Timestep Game Loop** - Deterministic physics with smooth interpolation
- ✅ **Component-Based Architecture** - Modular, extensible GameObject system
- ✅ **Scene Management** - Easy GameObject lifecycle management
- ✅ **Input System** - Keyboard, mouse, and action mapping support
- ✅ **Resource Management** - Efficient asset loading and caching

### Physics System
- ✅ **2D Physics Simulation** - Complete rigid body dynamics
- ✅ **Collision Detection** - AABB and Circle colliders with SAT
- ✅ **Collision Resolution** - Impulse-based physics with friction
- ✅ **Multiple Body Types** - Dynamic, Kinematic, and Static bodies
- ✅ **Physics World** - Global gravity, world bounds, and force application

### Rendering
- ✅ **Sprite Rendering** - Image and shape rendering with transforms
- ✅ **Camera System** - Viewport positioning
- ✅ **Debug Visualization** - FPS counter, collider outlines, debug info

## 🚀 Quick Start

### Build & Run
```bash
# Compile all source files
javac -d bin -sourcepath src src/engine/*.java src/demo/*.java

# Run the physics demo
java -cp bin demo.PhysicsDemo
```

### Controls (Physics Demo)
- **WASD / Arrow Keys** - Move player
- **SPACE** - Jump
- **1** - Spawn random box
- **2** - Spawn random circle
- **R** - Reset scene (remove spawned objects)
- **C** - Toggle collider debug visualization
- **D** - Toggle debug info

## 📖 API Quick Reference

### Creating a Game
```java
GameWindow window = new GameWindow();
GamePanel gamePanel = window.getGamePanel();
GameLoop gameLoop = new GameLoop(gamePanel);
setupScene(gamePanel);
window.display();
gameLoop.start();
```

### Creating GameObjects
```java
GameObject player = new GameObject("Player", new Vector2D(400, 300));
SpriteRenderer renderer = new SpriteRenderer(Color.BLUE, 40, 40);
player.addComponent(renderer);

PhysicsBody body = player.createPhysicsBody(1.0);
body.setBodyType(PhysicsBody.BodyType.DYNAMIC);

BoxCollider collider = new BoxCollider(player, 40, 40);
player.setCollider(collider);

gamePanel.addGameObject(player);
```

### Applying Forces
```java
PhysicsBody body = gameObject.getPhysicsBody();
body.applyForce(new Vector2D(100, 0));      // Continuous force
body.applyImpulse(new Vector2D(0, -500));   // Instant impulse
body.setVelocity(new Vector2D(200, 0));     // Direct velocity
```

### Input Handling
```java
InputManager input = InputManager.getInstance();
if (input.isKeyPressed(KeyEvent.VK_W)) { /* ... */ }
if (input.isKeyJustPressed(KeyEvent.VK_SPACE)) { /* ... */ }

double horizontal = input.getHorizontalAxis(); // -1 to 1
Vector2D mousePos = input.getMousePosition();
```

### Custom Components
```java
public class MyComponent extends Component {
    @Override
    public void update(double deltaTime) {
        // Per-frame logic
    }
    
    @Override
    public void onCollision(GameObject other, Collider.CollisionInfo collision) {
        // Handle collision
    }
}
```

## 🏗️ Architecture

### Project Structure
```
game_engine_2D/
├── src/engine/          # Core engine components
│   ├── GameLoop.java    # Fixed timestep game loop
│   ├── GameObject.java  # Base game object
│   ├── Component.java   # Component base class
│   ├── PhysicsWorld.java # Physics simulation
│   ├── PhysicsBody.java  # Rigid body dynamics
│   ├── Collider.java    # Collision detection
│   └── ...
├── src/demo/            # Demo games
│   └── PhysicsDemo.java # Physics showcase
├── assets/              # Game assets
└── docs/                # Documentation
```

### Design Patterns
- **Singleton** - Resource, Input, Physics managers
- **Component Pattern** - Modular GameObject behaviors  
- **Observer Pattern** - Input event system
- **Fixed Timestep** - Deterministic physics simulation

## 🎯 Status

- [x] Window creation & management
- [x] Fixed timestep game loop
- [x] Rendering system with transforms
- [x] Input handling (keyboard & mouse)
- [x] Complete physics engine
- [x] Collision detection & resolution
- [x] Component-based GameObject system
- [x] Scene management
- [x] Resource loading & caching
- [x] Debug visualization
- [x] Production-ready demo game

## 📚 Documentation

See [ARCHITECTURE.md](docs/ARCHITECTURE.md) for detailed technical documentation including:
- Threading model and concurrency
- Physics simulation details
- Performance optimization strategies
- Extensibility guide

## 🤝 Contributing

This is a learning/portfolio project, but suggestions are welcome!

## 📄 License

MIT License

---

**Made with ☕ and Java**
