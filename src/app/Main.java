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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    public static final int WIDTH = 300;
    public static final int HEIGHT = 600;
    public static int numLives = 300;
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

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.setTitle("Space Shooter");
        primaryStage.setResizable(false);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        scoreLabel.setTranslateX(10);
        scoreLabel.setTranslateY(10);
        scoreLabel.setTextFill(Color.BLACK);
        scoreLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 18));
        root.getChildren().addAll(canvas, scoreLabel, lifeLabel);
        lifeLabel.setTranslateX(10);
        lifeLabel.setTranslateY(40);
        lifeLabel.setTextFill(Color.BLACK);
        lifeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gameObjects.add(monkey);
        Pane menuPane = createMenu();
        Scene menuScene = new Scene(menuPane, WIDTH, HEIGHT);
        primaryStage.setScene(menuScene);
        primaryStage.setTitle("Dog VS Monkey");
        primaryStage.setResizable(false);
        initEventHandlers(scene);

        AnimationTimer gameLoop = new AnimationTimer() {
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

                Image backgroundImage = new Image(getClass().getResource("/pic/bg_game.png").toExternalForm());

                // Draw the image that covers the entire canvas
                gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

                for (GameObject obj : gameObjects) {
                    obj.move();
                    obj.render(gc);
                }

            }


        };

        gameLoop.start();

        primaryStage.show();
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
        }
        else {
            Dog enemy = new NormalDog(x, -40);
            gameObjects.add(enemy);
        }

    }



    private void checkCollisions() {
        List<Bullet> bullets = new ArrayList<>();
        List<NormalDog> dogs = new ArrayList<>();
        List<SmallDog> powerUps = new ArrayList<>();

        for (GameObject obj : gameObjects) {
            if (obj instanceof Bullet) {
                bullets.add((Bullet) obj);
            } else if (obj instanceof NormalDog) {
                dogs.add((NormalDog) obj);
            } else if (obj instanceof SmallDog) {
                powerUps.add((SmallDog) obj);
            }
        }

        for (Bullet bullet : bullets) {
            for (Dog enemy : dogs) {
                if (bullet.getBounds().intersects(enemy.getBounds())) {
                    bullet.setDead(true);
                    if (enemy instanceof BossDog) {
                        ((BossDog) enemy).takeDamage();
                        score += 20;
                    }
                    else {
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
        for (GameObject obj: gameObjects) {
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
        SmallDog powerUp = new SmallDog(x, - SmallDog.HEIGHT / 2);
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
        contributorButton.setOnAction(event -> {
            // Create a new Stage for the new page
            Stage newStage = new Stage();
            newStage.setTitle("Contributor");

            // Create a Label to display the name of the contributor
            Label label = new Label("Name of Contributor");
            label.setFont(Font.font("Arial", FontWeight.BOLD, 20));

            // Create a new Scene with the Label and Hyperlink
            VBox vbox = new VBox(10, label);
            Scene newScene = new Scene(vbox, 300, 200);
            newStage.setScene(newScene);

            // Show the new Stage
            newStage.show();
        });

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

    private void showInstructions() {
        Alert instructionsAlert = new Alert(Alert.AlertType.INFORMATION);
        instructionsAlert.setTitle("Instructions");
        instructionsAlert.setHeaderText("Dog VS Monkey Instructions");
        instructionsAlert.setContentText("Use the A, W, S, and D keys or the arrow keys to move your monkey.\n" +
                "Press SPACE to shoot bullets and destroy the dogs.\n" +
                "If a dog reaches the bottom of the screen, you lose a life.\n" +
                "The game resets if you lose all lives.");
        instructionsAlert.showAndWait();
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
        primaryStage.setScene(scene);
    }
}
