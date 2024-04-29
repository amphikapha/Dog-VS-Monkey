package component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class SmallDog extends Dog {
    public static final int WIDTH = 20;
    public static final int HEIGHT = 20;

    private static final double SPEED = 2;

    private boolean isDead = false;

    public SmallDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
    }

    @Override
    public void move() {
        setY(getY() + SPEED);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
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
