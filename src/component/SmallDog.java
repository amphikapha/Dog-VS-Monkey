package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class SmallDog extends Dog {
    public static final int WIDTH = 30;
    public static final int HEIGHT = 30;
    private static final double SPEED = 1;
    private boolean isDead = false;
    private Image smallDogImage;

    public SmallDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setSmallDogImage(new Image(getClass().getResource("/pic/smallDog.png").toExternalForm()));
    }

    @Override
    public void move() {
        setY(getY() + SPEED);
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(getSmallDogImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    @Override
    public void playDeathSound() {
        Main.playEffectSound("res/sound/effect/smallDog.wav");
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

    public Image getSmallDogImage() {
        return smallDogImage;
    }

    public void setSmallDogImage(Image smallDogImage) {
        this.smallDogImage = smallDogImage;
    }
}
