package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// SmallDog class is a subclass of Dog and represents a small dog character in a game
public class SmallDog extends Dog {
    public static final int WIDTH = 30; // Width of the small dog
    public static final int HEIGHT = 30; // Height of the small dog
    private static final double SPEED = 1.25; // Speed of the small dog
    private boolean isDead = false; // Boolean to check if the small dog is dead
    private Image smallDogImage; // Image of the small dog

    // Constructor to create a SmallDog object at a given position
    public SmallDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        setSmallDogImage(new Image(getClass().getResource("/pic/smallDog.png").toExternalForm()));
    }

    // Method to move the small dog
    @Override
    public void move() {
        setY(getY() + SPEED);
    }

    // Method to render the small dog
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(getSmallDogImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    // Method to play the sound when the small dog dies (represent POLYMORPHISM)
    @Override
    public void playDeathSound() {
        Main.playEffectSound("res/sound/effect/smallDog.wav");
    }

    // Getters and setters for the SmallDog class
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
