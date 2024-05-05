package component;

import javafx.geometry.Bounds;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

/*
GameObject class is an abstract class that represents a general object in a game.
It implements two interfaces: Movable and Renderable, which define methods
for moving the object and rendering it on the screen.
 */
public abstract class GameObject implements Movable, Renderable {
    protected double x; // x-coordinate of the object
    protected double y; // y-coordinate of the object
    protected double width; // width of the object
    protected double height; // height of the object

    // Constructor for GameObject class
    public GameObject(double x, double y, double width, double height) {
        setX(x);
        setY(y);
        setWidth(width);
        setHeight(height);
    }

    // Abstract method that updates the object's state
    public abstract void move();

    // Abstract method that renders the object on the screen
    public abstract void render(GraphicsContext gc);

    // Abstract method that checks if the object is dead
    public abstract boolean isDead();

    //represent the game object's position and size used for collision detection
    public Bounds getBounds() {
        return new Rectangle(x - getWidth() / 2, y - getHeight() / 2, getWidth(), getHeight()).getBoundsInLocal();
    }

    // Getters and setters for the object's x, y, width, and height
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
