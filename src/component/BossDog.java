package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// BossDog class is a subclass of Dog and represents a boss dog character in a game
public class BossDog extends Dog {
    private int health; // Health of the boss dog
    protected static final double WIDTH = 50; // Width of the boss dog
    protected static final double HEIGHT = 50; // Height of the boss dog
    public static final double SPEED = 1; // Speed of the boss dog
    private Image bossDogImage; // Image of the boss dog

    // Constructor to create a BossDog object at a given position
    public BossDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setHealth(3); // Boss dog has to be hit 3 times to die
        setBossDogImage(new Image(getClass().getResource("/pic/monkey_head_green.png").toExternalForm()));
    }

    // Method to move the boss dog
    @Override
    public void move() {
        setY(getY() + SPEED);
    }

    // Method to render the boss dog with the health ratio
    @Override
    public void render(GraphicsContext gc) {
        double ratio = (double) getHealth() / 2; // Calculate the ratio of health to adjust the size of the boss dog
        double currentWidth = WIDTH * ratio;
        double currentHeight = HEIGHT * ratio;
        gc.drawImage(getBossDogImage(), x - currentWidth / 2, y - currentHeight / 2, currentWidth, currentHeight);
    }

    // Method to take damage when the boss dog is hit
    public void takeDamage() {
        setHealth(getHealth() - 1);
        if (getHealth() <= 0) {
            setDead(true);
        }
    }

    // Method to play the sound when the boss dog dies and got shot (represent POLYMORPHISM)
    @Override
    public void playDeathSound() {
        Main.playEffectSound("res/sound/effect/bossDog.wav");
    }

    // Getters and setters for the BossDog class
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
