package component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

// Bullet class is a subclass of GameObject and represents a bullet in a game
public class Bullet extends GameObject {
    public static final int WIDTH = 4; // Width of the bullet
    public static final int HEIGHT = 20; // Height of the bullet
    private static final double SPEED = 7; // Speed of the bullet
    private boolean isDead = false; // Flag to indicate if the bullet is dead

    // Constructor to create a bullet at a given position
    public Bullet(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    // Move the bullet up
    @Override
    public void move() {
        setY(getY() - SPEED);
    }

    // Render the bullet
    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(getX() - WIDTH / 2, getY() - HEIGHT / 2, WIDTH, HEIGHT);
    }

    // Getters and setters for the Bullet class
    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

}
