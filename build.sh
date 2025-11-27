#!/bin/bash
# Build script for 2D Game Engine

echo "==================================="
echo "Building 2D Game Engine..."
echo "==================================="

# Create bin directory if it doesn't exist
mkdir -p bin

# Compile engine and demo
echo "Compiling source files..."
javac -d bin -sourcepath src src/engine/*.java src/demo/*.java

if [ $? -eq 0 ]; then
    echo ""
    echo "==================================="
    echo "Build successful!"
    echo "==================================="
    echo ""
    echo "To run the demo:"
    echo "  java -cp bin demo.PhysicsDemo"
    echo ""
else
    echo ""
    echo "==================================="
    echo "Build failed!"
    echo "==================================="
    exit 1
fi
