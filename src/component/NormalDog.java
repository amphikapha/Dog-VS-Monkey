package component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class NormalDog extends Dog {
    protected static final int WIDTH = 40;
    protected static final int HEIGHT = 40;
    public static final double SPEED = 1;
    private boolean isDead = false;
    private Image normalDogImage;

    public NormalDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
//        setMonkeyImage(new Image(getClass().getResource("/pic/monkey_head.png").toExternalForm()));
        setNormalDogImage(new Image(getClass().getResource("/pic/normalDog01.png").toExternalForm()));
    }

    @Override
    public void move() {
        setY(getY() + SPEED);
    }

    @Override
    public void render(GraphicsContext gc) {
//        gc.setFill(Color.RED);
//        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
        gc.drawImage(getNormalDogImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
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

    public Image getNormalDogImage() {
        return normalDogImage;
    }

    public void setNormalDogImage(Image normalDogImage) {
        this.normalDogImage = normalDogImage;
    }
}
