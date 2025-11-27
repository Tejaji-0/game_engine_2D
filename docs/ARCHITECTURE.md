# Architecture Documentation

## Overview

This 2D Game Engine is designed following SOLID principles and industry-standard design patterns to ensure long-term maintainability, testability, and extensibility.

## Core Components

### 1. Engine Core

#### EngineConfig
- **Pattern**: Thread-safe Singleton
- **Purpose**: Centralized configuration management
- **Thread Safety**: Double-checked locking
- **Features**: Property file loading with defaults, type-safe getters

#### EngineLogger
- **Pattern**: Singleton with Factory Method
- **Purpose**: Structured logging across all components
- **Features**: Console and file handlers, custom formatting, log rotation

### 2. Game Loop (GameLoop.java)

#### Fixed Timestep Algorithm
```
accumulator = 0
lastTime = currentTime()

while running:
    currentTime = now()
    deltaTime = currentTime - lastTime
    lastTime = currentTime
    
    accumulator += deltaTime
    
    while accumulator >= FIXED_TIMESTEP:
        update(FIXED_TIMESTEP)
        accumulator -= FIXED_TIMESTEP
    
    alpha = accumulator / FIXED_TIMESTEP
    render(alpha)
```

**Benefits**:
- Deterministic physics/logic
- Consistent behavior across different frame rates
- Smooth visual interpolation
- Frame skip protection

### 3. GameObject System

#### GameObject Base Class
- **Pattern**: Component pattern with Template Method
- **Lifecycle**: initialize() → update() → render() → destroy()
- **Features**:
  - Hierarchical parent-child relationships
  - Transform properties (position, rotation, scale)
  - Tag and layer system for categorization
  - Component management

#### Component System
- **Pattern**: Component pattern
- **Purpose**: Modular, reusable behaviors
- **Design**: 
  - Abstract base class defining lifecycle
  - Components attached to GameObjects
  - Communication via GameObject reference

**Example Components**:
```java
// Future implementations
- SpriteRenderer: Visual representation
- Collider: Collision detection
- RigidBody: Physics simulation
- AudioSource: Sound playback
- Script: Custom game logic
```

### 4. Scene Management (GamePanel.java)

**Responsibilities**:
- GameObject collection management
- Update loop coordination
- Rendering orchestration
- Scene queries (find by tag, layer, etc.)

**Thread Safety**:
- Uses CopyOnWriteArrayList for concurrent access
- Safe modification during iteration

### 5. Input System (InputManager.java)

#### Features
1. **State Tracking**
   - Current frame state (pressed)
   - Previous frame comparison (just pressed/released)
   - Mouse position and wheel

2. **Action Mapping**
   - Named actions bound to keys
   - Multiple keys per action
   - Flexible input remapping

3. **Event System**
   - Observer pattern for listeners
   - Keyboard, mouse, and wheel events

**Update Cycle**:
```
Frame N:
  - KeyPressed event → add to pressed + justPressed
  - Poll: isKeyJustPressed() = true
  
Frame N+1:
  - update() called → clear justPressed
  - Poll: isKeyPressed() = true, isKeyJustPressed() = false
```

### 6. Resource Management (ResourceManager.java)

#### Cache Strategy
- **Pattern**: Singleton with caching
- **Thread Safety**: ConcurrentHashMap
- **Features**:
  - Lazy loading
  - Automatic caching
  - Memory management (explicit unload)
  - Statistics tracking

**Loading Priority**:
1. Check cache
2. Try file system (assets folder)
3. Try classpath (bundled resources)
4. Return null + log warning

## Threading Model

### Thread Responsibilities

1. **Main Thread (Game Loop)**
   - Game logic updates
   - Scene management
   - Resource loading

2. **Event Dispatch Thread (EDT)**
   - Window event handling
   - Swing component updates
   - Repaint requests

3. **Thread Safety Measures**
   - Volatile flags for control flow
   - ConcurrentHashMap for shared state
   - CopyOnWriteArrayList for collections
   - Synchronized blocks only where necessary

### Concurrency Best Practices
- Minimize shared mutable state
- Use immutable objects where possible
- Avoid blocking operations in game loop
- Graceful shutdown with timeouts

## Error Handling Strategy

### Levels of Recovery

1. **Graceful Degradation**
   - Missing resources: Use defaults or continue without
   - Component errors: Disable component, log warning
   - Rendering errors: Skip frame, continue

2. **Controlled Shutdown**
   - Fatal errors: Log stack trace, cleanup, exit
   - Shutdown hook: Ensures cleanup on JVM termination
   - Thread interruption: Propagate properly

3. **Logging Severity**
   - SEVERE: Fatal errors requiring immediate attention
   - WARNING: Recoverable issues
   - INFO: Normal operations
   - FINE/FINER: Debug information

## Performance Considerations

### Optimization Techniques

1. **Object Pooling** (Ready for implementation)
   - Reuse GameObjects to reduce GC pressure
   - Pool frequently created/destroyed objects

2. **Caching**
   - Resource manager caches all loaded assets
   - Avoid repeated file I/O

3. **Lazy Initialization**
   - Singletons created only when needed
   - Resources loaded on-demand

4. **Efficient Collections**
   - CopyOnWriteArrayList for read-heavy workloads
   - ArrayList for components (small, local)

### Memory Management
- No circular references (parent knows children, not vice versa)
- Explicit cleanup in destroy() methods
- Cache statistics for monitoring

## Extensibility Points

### Adding New Components
```java
public class MyComponent extends Component {
    @Override
    public void initialize() {
        // Setup
    }
    
    @Override
    public void update(double deltaTime) {
        // Per-frame logic
    }
    
    @Override
    public void render(Graphics2D g2d) {
        // Drawing
    }
    
    @Override
    public void destroy() {
        // Cleanup
    }
}
```

### Adding New Managers
Follow singleton pattern with thread safety:
```java
public class MyManager {
    private static volatile MyManager instance;
    
    public static MyManager getInstance() {
        if (instance == null) {
            synchronized (MyManager.class) {
                if (instance == null) {
                    instance = new MyManager();
                }
            }
        }
        return instance;
    }
}
```

## Future Architecture Plans

### v2.x
- Physics engine integration (separation of concerns)
- Audio manager with spatial sound
- Advanced collision detection (broad phase + narrow phase)
- Particle system with pooling

### v3.x
- Entity-Component-System (ECS) architecture migration
- Multi-threaded rendering
- Scene serialization/deserialization
- Script binding layer (JavaScript/Lua)

## Testing Strategy

### Unit Tests
- Component lifecycle testing
- Input state verification
- Resource loading/caching
- Configuration parsing

### Integration Tests
- Game loop timing accuracy
- GameObject-Component interaction
- Scene management operations

### Performance Tests
- Frame timing consistency
- Memory leak detection
- Cache hit rates

## Code Quality Standards

### Javadoc Requirements
- All public classes, methods, and fields
- Include @since, @param, @return, @throws
- Usage examples for complex APIs

### Code Metrics
- Maximum method length: 50 lines
- Maximum class length: 500 lines
- Cyclomatic complexity: < 10
- Test coverage: > 80% (target)

### Design Principles Applied
- **SOLID**:
  - Single Responsibility: Each class has one job
  - Open/Closed: Extensible via components
  - Liskov Substitution: Component polymorphism
  - Interface Segregation: Focused interfaces
  - Dependency Inversion: Depend on abstractions

- **DRY**: Shared utilities in managers
- **KISS**: Simple, readable implementations
- **YAGNI**: Features added when needed

## References

- [Game Programming Patterns](https://gameprogrammingpatterns.com/)
- [Fix Your Timestep](https://gafferongames.com/post/fix_your_timestep/)
- [Java Concurrency in Practice](https://jcip.net/)
- [Effective Java](https://www.oreilly.com/library/view/effective-java/9780134686097/)
