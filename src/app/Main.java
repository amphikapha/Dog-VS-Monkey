package app;

import component.*;
import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
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
    public static int numLives = 20;

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
    private MediaPlayer backgroundMusic;
    private Label welcomeLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Shooter");
        primaryStage.setResizable(false);

        playBackgroundMusic("res/sound/bgmusic/mainsong.mp3");

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
            private long lastDogSpawned = 0;

            private long lastSmallDogSpawned = 0;

            @Override
            public void handle(long now) {
                if (reset) {
                    this.start();
                    reset = false;
                }
                gc.clearRect(0, 0, WIDTH, HEIGHT);

                if (now - lastDogSpawned > 1_000_000_000) {
                    spawnDog();
                    lastDogSpawned = now;
                }

                if (now - lastSmallDogSpawned > 10_000_000_000L) {
                    spawnSmallDog();
                    lastSmallDogSpawned = now;
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
                        spawnBossDog();
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


                gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

                for (GameObject obj : gameObjects) {
                    obj.move();
                    obj.render(gc);
                }

            }


        };

        pauseButton = createButton("Pause", 20);
        pauseButton.setPrefWidth(100);
        pauseButton.setPrefSize(90, 30);
        pauseButton.setPadding(new Insets(5, 5, 5, 5));
        pauseButton.setLayoutX(202);
        pauseButton.setLayoutY(10);

        Button backButton = createButton("Back", 20);
        backButton.setPrefSize(70, 30);
        backButton.setPadding(new Insets(5, 5, 5, 5));
        backButton.setLayoutX(126);
        backButton.setLayoutY(10);
        backButton.setOnAction(event -> {
                    if (backgroundMusic != null) {
                        backgroundMusic.stop();
                    }
                    playBackgroundMusic("res/sound/bgmusic/mainsong.mp3");

                    primaryStage.setScene(menuScene);
                    scene.getRoot().requestFocus();
                    gameObjects.clear();
                    newObjects.clear();
                    numLives = 20;
                    score = 0;
                    lifeLabel.setText("Lives: " + numLives);
                    scoreLabel.setText("Score: " + score);
                    monkey = new Monkey(WIDTH / 2, HEIGHT - 40);
                    gameObjects.add(monkey);


                    if (isRunning) {
                        gameLoop.stop();
                        if (backgroundMusic != null) {
                            backgroundMusic.play();
                        }
                        pauseButton.setText("Pause");
                        isRunning = false;
                    }
                }
        );

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

        gameLoop.stop();
        root.requestFocus();
        isRunning = false;

        primaryStage.show();
    }

    private void spawnDog() {
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

    }

    private void spawnSmallDog() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - SmallDog.WIDTH) + SmallDog.WIDTH / 2;
        SmallDog smallDog = new SmallDog(x, -SmallDog.HEIGHT / 2);
        gameObjects.add(smallDog);
    }

    private void spawnBossDog() {
        if (gameObjects.stream().noneMatch(obj -> obj instanceof BossDog)) {
            BossDog bossEnemy = new BossDog(WIDTH / 2, -40);
            gameObjects.add(bossEnemy);
            showTempMessage("Boss dog is coming!!!", 55, HEIGHT / 2, 3);
            playEffectSound("res/sound/effect/howlingdog.wav");
        }
    }


    private void checkCollisions() {
        List<Bullet> bullets = new ArrayList<>();
        List<Dog> dogs = new ArrayList<>();

        for (GameObject obj : gameObjects) {
            if (obj instanceof Bullet) {
                bullets.add((Bullet) obj);
            } else if (obj instanceof Dog) {
                dogs.add((Dog) obj);
            }
        }

        for (Bullet bullet : bullets) {
            for (Dog enemy : dogs) {
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    bullet.setDead(true);
                    enemy.playDeathSound(); // play death sound for all types of dogs
                    if (enemy instanceof BossDog) {
                        ((BossDog) enemy).takeDamage();
                        if (((BossDog) enemy).getHealth() <= 0) {
                            enemy.setDead(true);
                            score += 100;
                        }
                    } else if (enemy instanceof SmallDog) {
                        enemy.setDead(true);
                        score += 50;
                        numLives++;
                    } else if (enemy instanceof NormalDog) {
                        enemy.setDead(true);
                        score += 10;
                    }

                    new Thread(() -> {
                        // Use Platform.runLater() to update the score on the JavaFX Application Thread
                        javafx.application.Platform.runLater(() -> {
                            scoreLabel.setText("Score: " + score);
                            lifeLabel.setText("Lives: " + numLives);
                        });
                    }).start();

                    if (score % 100 == 0) {
                        Dog.setSpeed(Dog.getSpeed() + 2);
                    }
                }
            }

        }

    }

    private void checkEnemiesReachingBottom() {
        List<Dog> dogs = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj instanceof Dog) {
                dogs.add((Dog) obj);
            }
        }

        for (Dog enemy : dogs) {
            if (enemy.getY() + enemy.getHeight() / 2 >= HEIGHT && !enemy.isDead()) {
                enemy.setDead(true);
                enemy.speed = enemy.speed + 0.4;
                numLives--;
                new Thread(() -> {
                    // Use Platform.runLater() to update the score and lives on the JavaFX Application Thread
                    javafx.application.Platform.runLater(() -> {
                        lifeLabel.setText("Lives: " + numLives);
                        if (numLives < 0) {
                            resetGame();
                        }
                    });
                }).start();
            }
        }
    }

    private void resetGame() {
        gameObjects.clear();
        score = 0;
        numLives = 20;
        scoreLabel.setText("Score: " + score);
        lifeLabel.setText("Lives: " + numLives);
        monkey = new Monkey(WIDTH / 2, HEIGHT - 40);
        gameObjects.add(monkey);

        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        playBackgroundMusic("res/sound/bgmusic/diesong.mp3");

        getWelcomeLabel().setText("Game Over!");
        getWelcomeLabel().setTextFill(Color.ORANGERED);
        getWelcomeLabel().setLayoutX(80);

        if (isRunning) {
            gameLoop.stop();
            isRunning = false;
        }

        primaryStage.setScene(menuScene);
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

    private Pane createMenu() {
        Pane menuPane = new Pane();
        menuPane.setStyle("-fx-background-image: url('" + getClass().getResource("/pic/bg_menu.png").toExternalForm() + "');");

        setWelcomeLabel(new Label("Welcome!"));
        getWelcomeLabel().setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 25));
        getWelcomeLabel().setTextFill(Color.INDIANRED);
        getWelcomeLabel().setLayoutX(90);
        getWelcomeLabel().setLayoutY(170);

        double buttonWidth = 180;

        Button startButton = createButton("Start", 200);
        startButton.setPrefWidth(buttonWidth);
        startButton.setOnAction(event -> startGame());

        Button instructionsButton = createButton("Instructions", 300);
        instructionsButton.setPrefWidth(buttonWidth);
        instructionsButton.setOnAction(event -> showInstructions());

        Button quitButton = createButton("Quit", 400);
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(event -> System.exit(0));

        // Create a Contributor button
        Button contributorButton = createButton("Contributors", 500);
        contributorButton.setPrefWidth(buttonWidth);
        contributorButton.setOnAction(event -> showContributor());

        VBox buttonsContainer = new VBox(20); // Add buttons container to center-align the buttons
        buttonsContainer.setLayoutY(220);
        buttonsContainer.setAlignment(Pos.CENTER); // This will center the buttons
        buttonsContainer.getChildren().addAll(startButton, instructionsButton, contributorButton, quitButton);

        // Bind the layoutX property of the buttonsContainer to the half of the width property of the menuPane minus half of the width property of the buttonsContainer
        buttonsContainer.layoutXProperty().bind(menuPane.widthProperty().subtract(buttonsContainer.widthProperty()).divide(2));

        menuPane.getChildren().add(getWelcomeLabel());
        menuPane.getChildren().addAll(buttonsContainer);

        return menuPane;
    }

    private Button createButton(String text, double y) {
        Button button = new Button(text);
        button.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        button.setLayoutX((WIDTH - button.getPrefWidth()) / 2);
        button.setLayoutY(y);
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: rgba(196, 164, 132 0.5); -fx-border-color: black; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");
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

        // Set the background image for the pane
        Image backgroundImage = new Image(getClass().getResource("/pic/bg_without_logo.png").toExternalForm());
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        contributorPane.setBackground(new Background(background));


        Label contributorLabel = new Label("Contributors");
        contributorLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 25));
        contributorLabel.setTextFill(Color.INDIANRED);
        contributorLabel.setLayoutX(80);
        contributorLabel.setLayoutY(10);

        //mymypic
        ImageView mymyImage = new ImageView(new Image(getClass().getResource("/pic/mymy.png").toExternalForm()));
        mymyImage.setFitWidth(100);
        mymyImage.setFitHeight(100);
        mymyImage.setLayoutX(100); // Position the image at the left of the pane
        mymyImage.setLayoutY(50 + 10); // Position the image at the top of the pane
        Label mymyLabel = new Label("MyMy 6633287021");
        mymyLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        mymyLabel.setTextFill(Color.BLACK);
        mymyLabel.setLayoutX(65); // Position the label at the left of the pane
        mymyLabel.setLayoutY(150 + 10); // Position the label at the top of the pane

        //ananpic
        ImageView ananImage = new ImageView(new Image(getClass().getResource("/pic/anan.png").toExternalForm()));
        ananImage.setFitWidth(100);
        ananImage.setFitHeight(100);
        ananImage.setLayoutX(100); // Position the image at the left of the pane
        ananImage.setLayoutY(50 + 10 + 300); // Position the image at the top of the pane
        Label ananLabel = new Label("AnAn 6633033021");
        ananLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        ananLabel.setTextFill(Color.BLACK);
        ananLabel.setLayoutX(65); // Position the label at the left of the pane
        ananLabel.setLayoutY(150 + 10 + 300); // Position the label at the top of the pane

        //jojopic
        ImageView jojoImage = new ImageView(new Image(getClass().getResource("/pic/jojo.png").toExternalForm()));
        jojoImage.setFitWidth(100);
        jojoImage.setFitHeight(100);
        jojoImage.setLayoutX(100); // Position the image at the left of the pane
        jojoImage.setLayoutY(50 + 10 + 150); // Position the image at the top of the pane
        Label jojoLabel = new Label("JoJo 6633109021");
        jojoLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        jojoLabel.setTextFill(Color.BLACK);
        jojoLabel.setLayoutX(65); // Position the label at the left of the pane
        jojoLabel.setLayoutY(150 + 10 + 150); // Position the label at the top of the pane


        // Create a "Back" button
        Button backButton = createButton("Back", HEIGHT - 100);
        backButton.setLayoutX(110);
        backButton.setLayoutY(HEIGHT - 100); // Position the button at the bottom of the pane
        backButton.setOnAction(event -> primaryStage.setScene(menuScene));

        // Add the Label, ImageView, and the "Back" button to the Pane
        contributorPane.getChildren().addAll(contributorLabel, ananImage, ananLabel, jojoImage, jojoLabel, mymyLabel, mymyImage, backButton);

        // Create a new Scene for the contributor
        Scene contributorScene = new Scene(contributorPane, WIDTH, HEIGHT);

        // Switch to the contributor Scene
        primaryStage.setScene(contributorScene);
    }

    private void showInstructions() {
        // Create a new Pane for the instructions
        Pane instructionsPane = new Pane();

        // Set the background image for the pane
        Image backgroundImage = new Image(getClass().getResource("/pic/bg_without_logo.png").toExternalForm());
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        instructionsPane.setBackground(new Background(background));

        Label instructionLabel = new Label("Instructions");
        instructionLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 25));
        instructionLabel.setTextFill(Color.INDIANRED);
        instructionLabel.setLayoutX(80);
        instructionLabel.setLayoutY(10);

        Label instructionsDetailLabel = new Label("- press A, W, S, D, or \narrow keys to move\n- press Space bar to shoot\nHello worldHello world\nHello worldHello world");
        instructionsDetailLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 17));
        instructionsDetailLabel.setLayoutX(0);
        instructionsDetailLabel.setLayoutY(20);
        instructionsDetailLabel.setPadding(new Insets(30, 30, 30, 30));

        // Create a "Back" button
        Button backButton = createButton("Back", HEIGHT - 100);
        backButton.setLayoutX(110);
        backButton.setLayoutY(HEIGHT - 100); // Position the button at the bottom of the pane
        backButton.setOnAction(event -> primaryStage.setScene(menuScene));

        // Add the Label and the "Back" button to the Pane
        instructionsPane.getChildren().addAll(instructionLabel, instructionsDetailLabel, backButton);

        // Create a new Scene for the instructions
        Scene instructionsScene = new Scene(instructionsPane, WIDTH, HEIGHT);

        // Switch to the instructions Scene
        primaryStage.setScene(instructionsScene);
    }

    private void showTempMessage(String message, double x, double y, double duration) {
        Text tempMessage = new Text(message);
        tempMessage.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
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

        getWelcomeLabel().setText("Welcome!");
        getWelcomeLabel().setTextFill(Color.INDIANRED);
        getWelcomeLabel().setLayoutX(90);
        playBackgroundMusic("res/sound/bgmusic/playsong.mp3");

        primaryStage.setScene(scene);
        gameLoop.start();
        isRunning = true;
    }

    private void playBackgroundMusic(String musicFile) {
        // path to the music file
        Media sound = new Media(Paths.get(musicFile).toUri().toString());
        backgroundMusic = new MediaPlayer(sound);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE); // loop indefinitely
        backgroundMusic.play();
    }

    public static void playEffectSound(String soundFile) {
        // path to the sound file
        Media sound = new Media(Paths.get(soundFile).toUri().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public Label getWelcomeLabel() {
        return welcomeLabel;
    }

    public void setWelcomeLabel(Label welcomeLabel) {
        this.welcomeLabel = welcomeLabel;
    }
}
