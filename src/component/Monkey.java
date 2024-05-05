package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

//Monkey class is a subclass of GameObject and represents a monkey character in the game
public class Monkey extends GameObject {
    private static final int WIDTH = 40; // width of the monkey
    private static final int HEIGHT = 40; // height of the monkey
    private static final double SPEED = 3.5; // speed of the monkey
    private boolean hasMovedLeft; // boolean to check if the monkey has moved left
    private boolean hasMovedRight; // boolean to check if the monkey has moved right
    private boolean hasMovedForward; // boolean to check if the monkey has moved forward
    private boolean hasMovedBackward; // boolean to check if the monkey has moved backward
    private int health = 20; // health of the monkey
    private boolean isDead = false; // boolean to check if the monkey is dead
    private Image monkeyImage; // image of the monkey

    // Constructor to create a Monkey object at a given position
    public Monkey(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setMonkeyImage(new Image(getClass().getResource("/pic/monkey_head_red.png").toExternalForm()));
    }

    // Method to move the monkey
    @Override
    public void move() {
        if (hasMovedLeft && x - WIDTH / 2 > 0) {
            setX(getX() - SPEED);
        }
        if (hasMovedRight && x + WIDTH / 2 < Main.WIDTH) {
            setX(getX() + SPEED);
        }
        if (hasMovedForward && y - HEIGHT / 2 > 0) {
            setY(getY() - SPEED);
        }
        if (hasMovedBackward && y + HEIGHT / 2 < Main.HEIGHT) {
            setY(getY() + SPEED);
        }
    }

    // Method to render the monkey
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(getMonkeyImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    // Creates a new Bullet object at the monkey's position and plays a shooting sound effect
    public void shoot(List<GameObject> newObjects) {
        Bullet bullet = new Bullet(getX(), getY() - getHeight() / 2 - Bullet.HEIGHT);
        newObjects.add(bullet);
        Main.playEffectSound("res/sound/effect/shooting_sound1.wav");
    }

    // Getters and setters for the Monkey class
    public boolean isHasMovedLeft() {
        return hasMovedLeft;
    }

    public void setHasMovedLeft(boolean hasMovedLeft) {
        this.hasMovedLeft = hasMovedLeft;
    }

    public boolean isHasMovedRight() {
        return hasMovedRight;
    }

    public void setHasMovedRight(boolean hasMovedRight) {
        this.hasMovedRight = hasMovedRight;
    }

    public boolean isHasMovedForward() {
        return hasMovedForward;
    }

    public void setHasMovedForward(boolean hasMovedForward) {
        this.hasMovedForward = hasMovedForward;
    }

    public boolean isHasMovedBackward() {
        return hasMovedBackward;
    }

    public void setHasMovedBackward(boolean hasMovedBackward) {
        this.hasMovedBackward = hasMovedBackward;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

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

    public Image getMonkeyImage() {
        return monkeyImage;
    }

    public void setMonkeyImage(Image monkeyImage) {
        this.monkeyImage = monkeyImage;
    }
}
