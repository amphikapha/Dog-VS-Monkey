package component;

import javafx.scene.canvas.GraphicsContext;

public abstract class Dog extends GameObject {
    protected static int width;
    protected static int height;
    public static double speed = 2;
    private boolean isDead = false;

    public Dog(double x, double y, int width, int height) {
        super(x, y, width, height);
    }

    @Override
    public void move() {

    }

    @Override
    public void render(GraphicsContext gc) {
    }

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
