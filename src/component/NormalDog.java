package component;

import app.Main;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.Random;

// NormalDog class is a subclass of Dog and represents a normal dog character in a game
public class NormalDog extends Dog {
    protected static final int WIDTH = 40; // width of the normal dog
    protected static final int HEIGHT = 40; // height of the normal dog
    public static final double SPEED = 1; // speed of the normal dog
    private boolean isDead = false; // boolean to check if the normal dog is dead
    private Image normalDogImage; // image of the normal dog
    private static final String[] imagePaths = {"/pic/normalDog01.png", "/pic/normalDog02.png", "/pic/normalDog03.png"}; // array of image paths for the normal dog
    private static final Random random = new Random(); // random object to generate random numbers

    // Constructor to create a NormalDog object at a given position
    public NormalDog(double x, double y) {
        super(x, y, WIDTH, HEIGHT);
        String imagePath = imagePaths[random.nextInt(imagePaths.length)];
        setNormalDogImage(new Image(getClass().getResource(imagePath).toExternalForm()));
    }

    // Method to move the normal dog
    @Override
    public void move() {
        setY(getY() + SPEED);
    }

    // Method to render the normal dog
    @Override
    public void render(GraphicsContext gc) {
        gc.drawImage(getNormalDogImage(), x - WIDTH / 2, y - HEIGHT / 2, WIDTH, HEIGHT);
    }

    // Method to play the sound when the normal dog dies (represent POLYMORPHISM)
    @Override
    public void playDeathSound() {
        Main.playEffectSound("res/sound/effect/normalDog.wav");
    }

    // Getters and setters for the NormalDog class
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
