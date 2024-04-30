package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.image.Image;

import java.util.List;

public class Monkey extends GameObject {
    private static final int WIDTH = 40;
    private static final int HEIGHT = 40;
    private static final double SPEED = 3.5;
    private boolean hasMovedLeft;
    private boolean hasMovedRight;
    private boolean hasMovedForward;
    private boolean hasMovedBackward;
    private int health = 20;
    private boolean isDead = false;

    private Image monkeyImage;

    public Monkey(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setMonkeyImage(new Image(getClass().getResource("/pic/monkey_head.png").toExternalForm()));
    }

    @Override
    public void move() {
        if (isHasMovedLeft() && getX() - SPEED > 0) {
            setX(getX() - SPEED);
        }

        if (isHasMovedRight() && getX() + getWidth() + SPEED < Main.WIDTH) {
            setX(getX() + SPEED);
        }

        if (isHasMovedForward() && getY() - SPEED > 0) {
            setY(getY() - SPEED);
        }

        if (isHasMovedBackward() && getY() + getHeight() + SPEED < Main.HEIGHT) {
            setY(getY() + SPEED);
        }
    }

    @Override
    public void render(GraphicsContext gc) {
//        gc.setFill(Color.BLUE);
//        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);

        gc.drawImage(getMonkeyImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    public void shoot(List<GameObject> newObjects) {
        Bullet bullet = new Bullet(getX(), getY() - getHeight() / 2 - Bullet.HEIGHT);
        newObjects.add(bullet);
    }

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
