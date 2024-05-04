package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;

public abstract class Dog extends GameObject {
    protected static double width;
    protected static double height;
    public static double speed = 2;
    private boolean isDead = false;

    public Dog(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    @Override
    public void move() {

    }

    @Override
    public void render(GraphicsContext gc) {
    }

    public void playDeathSound() {
        // default sound for a dog
        Main.playEffectSound("res/sound/effect/dog.wav");
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
