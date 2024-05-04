package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class BossDog extends Dog {
    private int health;

    protected static final double WIDTH = 50;
    protected static final double HEIGHT = 50;
    public static final double SPEED = 0.5;

    private Image bossDogImage;

    public BossDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setHealth(3);
        setBossDogImage(new Image(getClass().getResource("/pic/monkey_head_green.png").toExternalForm()));
    }

    @Override
    public void move() {
//        if (getY() < 40) {
//            setY(getY() + SPEED);
//        }
        setY(getY() + SPEED);
    }

    @Override
    public void render(GraphicsContext gc) {
        double ratio = (double) getHealth() / 2; // Assuming the initial health is 3
        double currentWidth = WIDTH * ratio;
        double currentHeight = HEIGHT * ratio;
        gc.drawImage(getBossDogImage(), x - currentWidth / 2, y - currentHeight / 2, currentWidth, currentHeight);    }

    public void takeDamage() {
        setHealth(getHealth() - 1);
        if (getHealth() <= 0) {
            setDead(true);
        }
    }

    @Override
    public void playDeathSound() {
        Main.playEffectSound("res/sound/effect/bossDog.wav");
    }

    public boolean isDead() {
        return getHealth() <= 0;
    }

    @Override
    public double getWidth() {
        return WIDTH;
    }

    @Override
    public double getHeight() {
        return HEIGHT;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public Image getBossDogImage() {
        return bossDogImage;
    }

    public void setBossDogImage(Image bossDogImage) {
        this.bossDogImage = bossDogImage;
    }
}
