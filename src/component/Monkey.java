package component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.List;

public class Monkey extends GameObject {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final double SPEED = 5;
    private boolean hasMovedLeft;
    private boolean hasMovedRight;
    private boolean hasMovedForward;
    private boolean hasMovedBackward;
    private int health = 20;
    private boolean isDead = false;

    public Monkey(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    @Override
    public void move() {
//        if (hasMovedLeft && x - SPEED > 0) {
//            x -= SPEED;
//        }
//
//        if (hasMovedRight && x + width + SPEED < SpaceShooter.WIDTH) {
//            x += SPEED;
//        }
//
//        if (hasMovedForward && y - SPEED > 0) {
//            y -= SPEED;
//        }
//
//        if (hasMovedBackward && y + height + SPEED < SpaceShooter.HEIGHT) {
//            y += SPEED;
//        }
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.BLUE);
        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
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
}
