package component;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class BossDog extends Dog {
    private int health;

    protected static final int WIDTH = 50;
    protected static final int HEIGHT = 50;
    public static final double SPEED = 0.5;

    private int numHits = 5;
    private Image bossDogImage;

    public BossDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setHealth(5);
        setBossDogImage(new Image(getClass().getResource("/pic/monkey_head.png").toExternalForm()));
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
//        gc.setFill(Color.BLUE);
//        gc.fillRect(x - WIDTH / 2, y - HEIGHT / 2, WIDTH * 2, HEIGHT * 2);
        gc.drawImage(getBossDogImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    public void takeDamage() {
        setHealth(getHealth() - 1);
        if (getHealth() <= 0) {
            setDead(true);
        }
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

    public int getNumHits() {
        return numHits;
    }

    public void setNumHits(int numHits) {
        this.numHits = numHits;
    }

    public Image getBossDogImage() {
        return bossDogImage;
    }

    public void setBossDogImage(Image bossDogImage) {
        this.bossDogImage = bossDogImage;
    }
}
