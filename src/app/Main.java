package app;

import component.*;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    public static final int WIDTH = 300;
    public static final int HEIGHT = 600;
    public static int numLives = 400;

    private AnimationTimer gameLoop;

    private boolean isRunning = false;
    private Button pauseButton;
    private int score = 0;
    private boolean bossExists = false;
    private boolean reset = false;
    private final Label scoreLabel = new Label("Score: " + score);
    private final Label lifeLabel = new Label("Lives: " + numLives);
    private final List<GameObject> gameObjects = new ArrayList<>();

    private final List<GameObject> newObjects = new ArrayList<>();

    private Monkey monkey = new Monkey(WIDTH / 2, HEIGHT - 40);

    private Pane root = new Pane();

    private Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
    private boolean levelUpShown = false;
    private Scene menuScene; // Make menuScene a class member
    private Stage primaryStage;
    //    public MediaPlayer menuSound;
    private MediaPlayer backgroundMusic;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Shooter");
        primaryStage.setResizable(false);

        playBackgroundMusic("res/sound/mainsong.mp3");

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        root.getChildren().addAll(canvas, scoreLabel, lifeLabel);
        lifeLabel.setTranslateX(10);
        lifeLabel.setTranslateY(40);
        lifeLabel.setTextFill(Color.BLACK);
        lifeLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gameObjects.add(monkey);
        Pane menuPane = createMenu();
        menuScene = new Scene(menuPane, WIDTH, HEIGHT);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Dog VS Monkey");
        primaryStage.setResizable(false);
        initEventHandlers(scene);


        gameLoop = new AnimationTimer() {
            private long lastEnemySpawned = 0;

            private long lastPowerUpSpawned = 0;

            @Override
            public void handle(long now) {
                if (reset) {
                    this.start();
                    reset = false;
                }
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                if (now - lastEnemySpawned > 1_000_000_000) {
                    spawnEnemy();
                    lastEnemySpawned = now;
                }

                if (now - lastPowerUpSpawned > 10_000_000_000L) {
                    spawnSmallDog();
                    lastPowerUpSpawned = now;
                }

                if (score >= 200 && score % 200 == 0) {
                    boolean bossExists = false;
                    for (GameObject obj : gameObjects) {
                        if (obj instanceof BossDog) {
                            bossExists = true;
                            break;
                        }
                    }
                    if (!bossExists) {
                        spawnBossEnemy();
                    }
                }

                checkCollisions();

                checkEnemiesReachingBottom();
                gameObjects.addAll(newObjects);
                newObjects.clear();

                for (GameObject obj : gameObjects) {
                    obj.move();
                    obj.render(gc);
                }

                Iterator<GameObject> iterator = gameObjects.iterator();
                while (iterator.hasNext()) {
                    GameObject obj = iterator.next();
                    if (obj.isDead()) {
                        iterator.remove();
                    }
                }

                Image backgroundImage = new Image(getClass().getResource("/pic/bg_without_logo.png").toExternalForm());

                // Draw the image that covers the entire canvas
                gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

                for (GameObject obj : gameObjects) {
                    obj.move();
                    obj.render(gc);
                }

            }


        };

        pauseButton = new Button("Pause");
        pauseButton.setLayoutX(230); // Position the button at the left of the screen
        pauseButton.setLayoutY(10); // Position the button at the top of the screen

        Button backButton = new Button("Back");
        backButton.setLayoutX(160);
        backButton.setLayoutY(10); // Position the button at the bottom of the pane
        backButton.setOnAction(event -> {
                    // Stop the current background music
                    if (backgroundMusic != null) {
                        backgroundMusic.stop();
                    }
                    playBackgroundMusic("res/sound/mainsong.mp3");

                    primaryStage.setScene(menuScene);
                    scene.getRoot().requestFocus(); // Request focus for the game scene
                    numLives = 400; // Reset numLives to 400
                    score = 0; // Reset score to 0
                    lifeLabel.setText("Lives: " + numLives);
                    scoreLabel.setText("Score: " + score);

                    if (!isRunning) {
                        gameLoop.start();
                        if (backgroundMusic != null) {
                            backgroundMusic.play();
                        }
                        pauseButton.setText("Pause");
                        isRunning = true;
                    }
                }
        );

        // ********** this for exit =w=
//        quitButton.setOnAction(event -> System.exit(0));


        pauseButton.setOnAction(event -> {
            if (isRunning) {
                gameLoop.stop();
                if (backgroundMusic != null) {
                    backgroundMusic.pause();
                }
                pauseButton.setText("Resume");
                root.requestFocus();
                isRunning = false;
            } else {
                gameLoop.start();
                if (backgroundMusic != null) {
                    backgroundMusic.play();
                }
                pauseButton.setText("Pause");
                root.requestFocus();
                isRunning = true;
            }
        });

        root.getChildren().add(pauseButton);
        root.getChildren().add(backButton);

        gameLoop.start();
        root.requestFocus();
        isRunning = true;

        primaryStage.show();
    }


    private void playBackgroundMusic() {
        String musicFile = "res/sound/mainsong.mp3"; // path to the music file
        Media sound = new Media(Paths.get(musicFile).toUri().toString());
        backgroundMusic = new MediaPlayer(sound);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // loop indefinitely
        backgroundMusic.play();
    }

    private void spawnEnemy() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - 50) + 25;

        boolean bossExists = false;

        for (GameObject obj : gameObjects) {
            if (obj instanceof BossDog) {
                bossExists = true;
                break;
            }
        }

        if (!bossExists && score % 200 == 0 && score > 0) {
            BossDog boss = new BossDog(x, -50);
            gameObjects.add(boss);
        } else {
            Dog enemy = new NormalDog(x, -40);
            gameObjects.add(enemy);
        }

        if (!bossExists && score % 200 == 0 && score > 0) {
            BossDog boss = new BossDog(x, -50);
            gameObjects.add(boss);
            showTempMessage("A boss is ahead, watch out!", 75, HEIGHT / 2 - 100, 5);
            bossExists = true;
        } else {
            Dog enemy = new NormalDog(x, -40);
            gameObjects.add(enemy);
        }

    }


    private void checkCollisions() {
        List<Bullet> bullets = new ArrayList<>();
        List<NormalDog> dogs = new ArrayList<>();
        List<SmallDog> powerUps = new ArrayList<>();
        List<BossDog> bossDogs = new ArrayList<>();

        for (GameObject obj : gameObjects) {
            if (obj instanceof Bullet) {
                bullets.add((Bullet) obj);
            } else if (obj instanceof NormalDog) {
                dogs.add((NormalDog) obj);
            } else if (obj instanceof SmallDog) {
                powerUps.add((SmallDog) obj);
            } else if (obj instanceof BossDog) {
                bossDogs.add((BossDog) obj);
            }
        }

        for (Bullet bullet : bullets) {
            for (Dog enemy : dogs) {
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    bullet.setDead(true);
                    if (enemy instanceof BossDog) {
                        ((BossDog) enemy).takeDamage();
                        score += 20;
                    } else {
                        enemy.setDead(true);
                        score += 10;
                    }
                    scoreLabel.setText("Score: " + score);

                    if (score % 100 == 0) {
                        Dog.setSpeed(Dog.getSpeed() + 2);
                    }
                }
            }

            // Check collisions between bullets and power-ups
            for (SmallDog powerUp : powerUps) {
                if (bullet.getBounds().intersects(powerUp.getBounds())) {
                    bullet.setDead(true);
                    powerUp.setDead(true);
                    score += 50; // Deduct 5 points when a bullet hits a power-up
                    scoreLabel.setText("Score: " + score);
                }
            }

            for (BossDog bossDog : bossDogs) {
                if (bullet.getBounds().intersects(bossDog.getBounds())) {
                    bullet.setDead(true);
                    bossDog.takeDamage();
                    if (bossDog.getHealth() <= 0) {
                        bossDog.setDead(true);
                    }
                    score += 20;
                    scoreLabel.setText("Score: " + score);
                }
            }
        }

        if (score % 100 == 0 && score > 0 && !levelUpShown) {
            showTempMessage("Level Up!", 110, HEIGHT / 2, 2);
            levelUpShown = true;
        } else if (score % 100 != 0) {
            levelUpShown = false;
        }

        checkScore();

    }

    private void checkEnemiesReachingBottom() {
        List<Dog> dogs = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj instanceof Dog) {
                dogs.add((Dog) obj);
            }
        }

        for (Dog enemy : dogs) {
            if (enemy.getY() + enemy.getHeight() / 2 >= HEIGHT) {
                enemy.setDead(true);
                enemy.speed = enemy.speed + 0.4;
                numLives--;
                score -= 10;
                lifeLabel.setText("Lives: " + numLives);
                if (numLives < 0) {
//                    enemy.SPEED = 2;
                    resetGame();
                }
            }
        }
    }

    private void resetGame() {
        gameObjects.clear();
        numLives = 3;
        score = 0;
        lifeLabel.setText("Lives: " + numLives);
        scoreLabel.setText("Score: " + score);
        gameObjects.add(monkey);
        reset = true;
        Text lostMessage = new Text("You lost! The game has been reset.");
        lostMessage.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lostMessage.setFill(Color.RED);
        lostMessage.setX((WIDTH - lostMessage.getLayoutBounds().getWidth()) / 2);
        lostMessage.setY(HEIGHT / 2);
        root.getChildren().add(lostMessage);

        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> {
            root.getChildren().remove(lostMessage);
            initEventHandlers(scene);
        });
        pause.play();
    }

    private void initEventHandlers(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case A:
                case LEFT:
                    monkey.setHasMovedLeft(true);
                    break;
                case D:
                case RIGHT:
                    monkey.setHasMovedRight(true);
                    break;
                case S:
                    monkey.setHasMovedBackward(true);
                    break;
                case W:
                    monkey.setHasMovedForward(true);
                    break;
                case SPACE:
                    monkey.shoot(newObjects);
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case A:
                case LEFT:
                    monkey.setHasMovedLeft(false);
                    break;
                case D:
                case RIGHT:
                    monkey.setHasMovedRight(false);
                    break;
                case S:
                    monkey.setHasMovedBackward(false);
                    break;
                case W:
                    monkey.setHasMovedForward(false);
                    break;
            }
        });

    }

    private void spawnSmallDog() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - SmallDog.WIDTH) + SmallDog.WIDTH / 2;
        SmallDog powerUp = new SmallDog(x, -SmallDog.HEIGHT / 2);
        gameObjects.add(powerUp);
    }

    private void spawnBossEnemy() {
        if (gameObjects.stream().noneMatch(obj -> obj instanceof BossDog)) {
            BossDog bossEnemy = new BossDog(WIDTH / 2, -40);
            gameObjects.add(bossEnemy);
        }
    }


    private void checkScore() {
        if (this.score >= 100) {
            Text lostMessage = new Text("Now I dare you to pass 1000 :)");
            lostMessage.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            lostMessage.setFill(Color.RED);
            lostMessage.setX((WIDTH - lostMessage.getLayoutBounds().getWidth()) / 2);
            lostMessage.setY(HEIGHT / 2 - 100);
            root.getChildren().add(lostMessage);
            PauseTransition pause = new PauseTransition(Duration.seconds(2));
            pause.setOnFinished(event -> {
                root.getChildren().remove(lostMessage);
            });
            pause.play();
        }

    }

    private Pane createMenu() {
        Pane menuPane = new Pane();
        menuPane.setStyle("-fx-background-image: url('" + getClass().getResource("/pic/bg_menu.png").toExternalForm() + "');");

//        Media sound = new Media(getClass().getResource("sound/sneaking-out_by_victor-cooper.mp3").toExternalForm());
//        MediaPlayer mediaPlayer = new MediaPlayer(sound);
//        mediaPlayer.play();

//        Text welcomeText = new Text("   Welcome to \nDog VS Monkey!");
//        welcomeText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
//        welcomeText.setFill(Color.WHITE);
//        welcomeText.setX((WIDTH - welcomeText.getLayoutBounds().getWidth()) / 2);
//        welcomeText.setY(100); // Move welcome message higher on the screen

        Button startButton = createButton("START", 200);
        startButton.setOnAction(event -> startGame());

        Button instructionsButton = createButton("Show Instructions", 300);
        instructionsButton.setOnAction(event -> showInstructions());

        Button quitButton = createButton("QUIT", 400);
        quitButton.setOnAction(event -> System.exit(0));

        // Create a Contributor button
        Button contributorButton = createButton("Contributor", 500);
        contributorButton.setOnAction(event -> showContributor());

        VBox buttonsContainer = new VBox(20); // Add buttons container to center-align the buttons
        buttonsContainer.setLayoutX((WIDTH - startButton.getPrefWidth()) / 2 - 100);
        buttonsContainer.setLayoutY(200);
        buttonsContainer.getChildren().addAll(startButton, instructionsButton, quitButton, contributorButton);

        menuPane.getChildren().addAll(buttonsContainer);

        return menuPane;
    }

    private Button createButton(String text, double y) {
        Button button = new Button(text);
        button.setLayoutX((WIDTH - button.getPrefWidth()) / 2);
        button.setLayoutY(y);
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-font-size: 20; -fx-font-weight: bold; -fx-padding: 10 20;");
        button.setOnMouseEntered(event -> {
            button.setTextFill(Color.YELLOW);
            button.setEffect(new Glow());
        });
        button.setOnMouseExited(event -> {
            button.setTextFill(Color.WHITE);
            button.setEffect(null);
        });
        return button;
    }

    private void showContributor() {
        // Create a new Pane for the contributor
        Pane contributorPane = new Pane();

        // Create a Label with the contributor information
        Label contributorLabel = new Label("Contributor: AnAn JoJo MyMy");
        contributorLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        contributorLabel.setLayoutX(0); // Position the label at the left of the pane
        contributorLabel.setLayoutY(0); // Position the label at the top of the pane

        // Create a "Back" button
        Button backButton = new Button("Back");
        backButton.setLayoutX(0);
        backButton.setLayoutY(HEIGHT - 50); // Position the button at the bottom of the pane
        backButton.setOnAction(event -> primaryStage.setScene(menuScene));

        // Add the Label and the "Back" button to the Pane
        contributorPane.getChildren().addAll(contributorLabel, backButton);

        // Create a new Scene for the contributor
        Scene contributorScene = new Scene(contributorPane, WIDTH, HEIGHT);

        // Switch to the contributor Scene
        primaryStage.setScene(contributorScene);
    }

    private void showInstructions() {
        // Create a new Pane for the instructions
        Pane instructionsPane = new Pane();

        // Create a Label with the instructions
        Label instructionsLabel = new Label("Hello world");
        instructionsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        instructionsLabel.setLayoutX(0); // Position the label at the left of the pane
        instructionsLabel.setLayoutY(0); // Position the label at the top of the pane

        // Create a "Back" button
        Button backButton = new Button("Back");
        backButton.setLayoutX(0);
        backButton.setLayoutY(HEIGHT - 50); // Position the button at the bottom of the pane
        backButton.setOnAction(event -> primaryStage.setScene(menuScene));

        // Add the Label and the "Back" button to the Pane
        instructionsPane.getChildren().addAll(instructionsLabel, backButton);

        // Create a new Scene for the instructions
        Scene instructionsScene = new Scene(instructionsPane, WIDTH, HEIGHT);

        // Switch to the instructions Scene
        primaryStage.setScene(instructionsScene);
    }

    private void showTempMessage(String message, double x, double y, double duration) {
        Text tempMessage = new Text(message);
        tempMessage.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        tempMessage.setFill(Color.RED);
        tempMessage.setX(x);
        tempMessage.setY(y);
        root.getChildren().add(tempMessage);

        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
        pause.setOnFinished(event -> root.getChildren().remove(tempMessage));
        pause.play();
    }

    private void startGame() {
        // Stop the current background music
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        // Play the new background music
        playBackgroundMusic("res/sound/playsong.mp3");

        primaryStage.setScene(scene);
    }

    private void playBackgroundMusic(String musicFile) {
        // path to the music file
        Media sound = new Media(Paths.get(musicFile).toUri().toString());
        backgroundMusic = new MediaPlayer(sound);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // loop indefinitely
        backgroundMusic.play();
    }

}
