package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;

// Dog class is an abstract subclass of GameObject and represents a dog character in a game
public abstract class Dog extends GameObject {
    protected static double width; // width of the dog
    protected static double height; // height of the dog
    public static double speed = 2; // speed of the dog
    private boolean isDead = false; // boolean to check if the dog is dead

    // Constructor to create a dog object
    public Dog(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    // Method to update the dog position
    @Override
    public void move() {
    }

    // Method to render the dog
    @Override
    public void render(GraphicsContext gc) {
    }

    // Method to play the sound when the dog dies (represent POLYMORPHISM)
    public void playDeathSound() {
        // default sound for a dog
        Main.playEffectSound("res/sound/effect/dog.wav");
    }

    // Getters and setters for the Dog class
    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    public static void setWidth(int width) {
        Dog.width = width;
    }

    public static void setHeight(int height) {
        Dog.height = height;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public static double getSpeed() {
        return speed;
    }

    public static void setSpeed(double speed) {
        Dog.speed = speed;
    }
}