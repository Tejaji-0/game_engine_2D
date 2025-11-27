@echo off
REM Build script for 2D Game Engine

echo ===================================
echo Building 2D Game Engine...
echo ===================================

REM Create bin directory if it doesn't exist
if not exist "bin" mkdir bin

REM Compile engine and demo
echo Compiling source files...
javac -d bin -sourcepath src src/engine/*.java src/demo/*.java

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo Build successful!
    echo ===================================
    echo.
    echo To run the demo:
    echo   java -cp bin demo.PhysicsDemo
    echo.
) else (
    echo.
    echo ===================================
    echo Build failed!
    echo ===================================
    exit /b 1
)
