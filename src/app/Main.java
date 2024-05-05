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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

// Main class is the main entry point of the game application
public class Main extends Application {
    public static final int WIDTH = 300; // width of the game screen
    public static final int HEIGHT = 600; // height of the game screen
    public static int numLives = 5; // number of lives the monkey has
    private AnimationTimer gameLoop; // animation timer for the game loop
    private boolean isRunning = false; // boolean to check if the game is running
    private Button pauseButton; // button to pause the game
    private int score = 0; // score of the player
    private boolean reset = false; // boolean to check if the game is reset
    private final Label scoreLabel = new Label("Score: " + score); // label to display the score
    private final Label lifeLabel = new Label("Lives: " + numLives); // label to display the number of lives
    private final List<GameObject> gameObjects = new ArrayList<>(); // list of game objects
    private final List<GameObject> newObjects = new ArrayList<>(); // list of new game objects
    private Monkey monkey = new Monkey(WIDTH / 2, HEIGHT - 40); // create a new monkey object
    private Pane root = new Pane(); // create a new pane
    private Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK); // create a new scene
    private Scene menuScene; // create a new menu scene
    private Stage primaryStage; // create a new stage
    private MediaPlayer backgroundMusic; // media player for background music
    private Label welcomeLabel; // label to display welcome message

    // main method
    public static void main(String[] args) {
        launch(args);
    }

    // start method
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);

        playBackgroundMusic("/sound/bgmusic/mainsong.mp3");

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

        // create a new game loop
        gameLoop = new AnimationTimer() {
            private long lastDogSpawned = 0;
            private long lastSmallDogSpawned = 0;

            // Called in each frame while the AnimationTimer is active.
            // The now parameter represents the timestamp of the current frame given in nanoseconds
            @Override
            public void handle(long now) {
                // reset the game if the reset flag is true
                if (reset) {
                    this.start();
                    reset = false;
                }
                gc.clearRect(0, 0, WIDTH, HEIGHT); // clear the canvas to draw the new frame

                // checks if enough time has passed since the last dog was spawned
                // spawn a new dog every second
                if (now - lastDogSpawned > 1_000_000_000) {
                    spawnDog();
                    lastDogSpawned = now;
                }

                // spawn a new small dog every 10 seconds
                if (now - lastSmallDogSpawned > 10_000_000_000L) {
                    spawnSmallDog();
                    lastSmallDogSpawned = now;
                }

                // spawn a boss dog every 200 points
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

                // check for collisions between bullets and dogs
                checkCollisions();

                // check if any enemies have reached the bottom of the screen
                checkDogsReachingBottom();

                // add new objects to the game objects list that were created during this frame
                gameObjects.addAll(newObjects);
                // clear the new objects list
                newObjects.clear();

                // remove dead objects from the game objects list
                Iterator<GameObject> iterator = gameObjects.iterator();
                while (iterator.hasNext()) {
                    GameObject obj = iterator.next();
                    if (obj.isDead()) {
                        iterator.remove();
                    }
                }

                // draw the background image
                Image backgroundImage = new Image(getClass().getResource("/pic/bg/bg_without_logo.png").toExternalForm());
                gc.drawImage(backgroundImage, 0, 0, WIDTH, HEIGHT);

                // update and render all game objects
                for (GameObject obj : gameObjects) {
                    obj.move();
                    obj.render(gc);
                }

            }

        };

        // create a pause button in the game scene
        pauseButton = createButton("Pause", 20);
        pauseButton.setPrefWidth(100);
        pauseButton.setPrefSize(90, 30);
        pauseButton.setPadding(new Insets(5, 5, 5, 5));
        pauseButton.setLayoutX(202);
        pauseButton.setLayoutY(10);
        pauseButton.setOnAction(event -> {
            // pause the game if it is running
            playEffectSound("/sound/effect/click.mp3");
            if (isRunning) {
                gameLoop.stop();
                if (backgroundMusic != null) {
                    backgroundMusic.pause();
                }
                pauseButton.setText("Resume"); // change the text of the button to "Resume"
                root.requestFocus();
                isRunning = false;
            } else {
                gameLoop.start();
                if (backgroundMusic != null) {
                    backgroundMusic.play();
                }
                pauseButton.setText("Pause"); // change the text of the button to "Pause"
                root.requestFocus();
                isRunning = true;
            }
        });

        // create a back button in the game scene to go back to the menu scene
        Button backButton = createButton("Back", 20);
        backButton.setPrefSize(70, 30);
        backButton.setPadding(new Insets(5, 5, 5, 5));
        backButton.setLayoutX(126);
        backButton.setLayoutY(10);
        backButton.setOnAction(event -> {
                    if (backgroundMusic != null) {
                        backgroundMusic.stop();
                    }
                    playBackgroundMusic("/sound/bgmusic/mainsong.mp3");

                    primaryStage.setScene(menuScene);
                    playEffectSound("/sound/effect/click.mp3");
                    scene.getRoot().requestFocus();
                    gameObjects.clear();
                    newObjects.clear();
                    numLives = 5;
                    score = 0;
                    lifeLabel.setText("Lives: " + numLives);
                    scoreLabel.setText("Score: " + score);
                    monkey = new Monkey(WIDTH / 2.0, HEIGHT - 40);
                    gameObjects.add(monkey);

                    // pause the game if it is running
                    if (isRunning) {
                        gameLoop.stop();
                        if (backgroundMusic != null) {
                            backgroundMusic.play();
                        }
                        pauseButton.setText("Pause");
                        isRunning = false;
                    } else {
                        pauseButton.setText("Pause");
                    }
                }
        );

        // add the pause button and back button to the root pane
        root.getChildren().add(pauseButton);
        root.getChildren().add(backButton);

        gameLoop.stop();
        root.requestFocus();
        isRunning = false;

        primaryStage.show();
    }

    // spawnDog method to spawn a new dog at a random position
    private void spawnDog() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - 50) + 25;

        // check if a boss dog already exists
        boolean bossExists = false;
        for (GameObject obj : gameObjects) {
            if (obj instanceof BossDog) {
                bossExists = true;
                break;
            }
        }

        // spawn a boss dog if the score is a multiple of 200 and a boss dog does not already exist
        if (!bossExists && score % 200 == 0 && score > 0) {
            BossDog boss = new BossDog(x, -50);
            gameObjects.add(boss);
        } else {
            Dog enemy = new NormalDog(x, -40);
            gameObjects.add(enemy);
        }

    }

    // spawnSmallDog method to spawn a new small dog at a random position
    private void spawnSmallDog() {
        Random random = new Random();
        int x = random.nextInt(WIDTH - SmallDog.WIDTH) + SmallDog.WIDTH / 2;
        SmallDog smallDog = new SmallDog(x, -SmallDog.HEIGHT / 2.0);
        gameObjects.add(smallDog);
    }

    // spawnBossDog method to spawn a new boss dog at a random position
    private void spawnBossDog() {
        if (gameObjects.stream().noneMatch(obj -> obj instanceof BossDog)) {
            BossDog bossEnemy = new BossDog(WIDTH / 2.0, -40);
            gameObjects.add(bossEnemy);
            showTempMessage("Boss dog is coming!!!", 55, HEIGHT / 2.0, 3);
            playEffectSound("/sound/effect/howlingdog.wav");
        }
    }

    // checkCollisions method to check for collisions between bullets and dogs
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
                    enemy.playDeathSound(); // play death sound for each types of dogs when they die (represent POLYMORPHISM)
                    if (enemy instanceof BossDog) {
                        ((BossDog) enemy).takeDamage();
                        if (((BossDog) enemy).getHealth() <= 0) {
                            enemy.setDead(true);
                            score += 100; // increase the score by 100 when the boss dog is killed
                        }
                    } else if (enemy instanceof SmallDog) {
                        enemy.setDead(true);
                        score += 50; // increase the score by 50 when the small dog is killed
                        numLives++; // increase the number of lives by 1 when the small dog is killed
                    } else if (enemy instanceof NormalDog) {
                        enemy.setDead(true);
                        score += 10; // increase the score by 10 when the normal dog is killed
                    }

                    // update the score and number of lives on the screen
                    new Thread(() -> {
                        javafx.application.Platform.runLater(() -> {
                            scoreLabel.setText("Score: " + score);
                            lifeLabel.setText("Lives: " + numLives);
                        });
                    }).start();

                }
            }
        }
    }

    // checkDogsReachingBottom method to check if any enemies have reached the bottom of the screen
    private void checkDogsReachingBottom() {
        List<Dog> dogs = new ArrayList<>();
        for (GameObject obj : gameObjects) {
            if (obj instanceof Dog) {
                dogs.add((Dog) obj);
            }
        }

        for (Dog dog : dogs) {
            if (dog.getY() + dog.getHeight() / 2 >= HEIGHT && !dog.isDead()) {
                dog.setDead(true);

                // monkey will be dead if a BossDog reaches the bottom of the screen
                if (dog instanceof BossDog) {
                    monkey.setDead(true);
                    gameLoop.stop(); // stop the game loop
                    resetGame(); // reset the game
                } else {
                    numLives--; // decrease the number of lives by 1 when a normal or small dog reaches the bottom of the screen
                }

                // update the number of lives on the screen
                new Thread(() -> {
                    javafx.application.Platform.runLater(() -> {
                        lifeLabel.setText("Lives: " + numLives);
                        if (numLives <= 0) {
                            resetGame();
                        }
                    });
                }).start();
            }
        }
    }

    // resetGame method to reset the game when the player loses
    private void resetGame() {
        gameObjects.clear();
        score = 0;
        numLives = 5;
        scoreLabel.setText("Score: " + score);
        lifeLabel.setText("Lives: " + numLives);
        monkey = new Monkey(WIDTH / 2.0, HEIGHT - 40);
        gameObjects.add(monkey);

        // play the game over sound effect
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
        playBackgroundMusic("/sound/bgmusic/diesong.mp3");

        getWelcomeLabel().setText("Game Over!");
        getWelcomeLabel().setTextFill(Color.ORANGERED);
        getWelcomeLabel().setLayoutX(80);

        if (isRunning) {
            gameLoop.stop();
            isRunning = false;
        }

        primaryStage.setScene(menuScene);
    }

    // initEventHandlers method to initialize the event handlers for the game
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
                case DOWN:
                    monkey.setHasMovedBackward(true);
                    break;
                case W:
                case UP:
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
                case DOWN:
                    monkey.setHasMovedBackward(false);
                    break;
                case W:
                case UP:
                    monkey.setHasMovedForward(false);
                    break;
            }
        });

    }

    // createMenu method to create the main menu of the game
    private Pane createMenu() {
        Pane menuPane = new Pane();
        // set the background image of the menu
        menuPane.setStyle("-fx-background-image: url('" + getClass().getResource("/pic/bg/bg_menu.png").toExternalForm() + "');");

        // create a welcome label
        setWelcomeLabel(new Label("Welcome!"));
        getWelcomeLabel().setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 25));
        getWelcomeLabel().setTextFill(Color.INDIANRED);
        getWelcomeLabel().setLayoutX(90);
        getWelcomeLabel().setLayoutY(170);

        double buttonWidth = 180;

        // create buttons for the main menu
        // start button
        Button startButton = createButton("Start", 200);
        startButton.setPrefWidth(buttonWidth);
        startButton.setOnAction(event -> {
            startGame();
            playEffectSound("/sound/effect/click.mp3");
        });

        // instructions button
        Button instructionsButton = createButton("Instructions", 300);
        instructionsButton.setPrefWidth(buttonWidth);
        instructionsButton.setOnAction(event -> {
            showInstructions();
            playEffectSound("/sound/effect/click.mp3");
        });

        // quit button
        Button quitButton = createButton("Quit", 400);
        quitButton.setPrefWidth(buttonWidth);
        quitButton.setOnAction(event -> {
            System.exit(0);
            playEffectSound("/sound/effect/click.mp3");
        });

        // contributors button
        Button contributorButton = createButton("Contributors", 500);
        contributorButton.setPrefWidth(buttonWidth);
        contributorButton.setOnAction(event -> {
            showContributor();
            playEffectSound("/sound/effect/click.mp3");
        });
        // create a container for the buttons
        VBox buttonsContainer = new VBox(20);
        buttonsContainer.setLayoutY(220);
        buttonsContainer.setAlignment(Pos.CENTER);
        buttonsContainer.getChildren().addAll(startButton, instructionsButton, contributorButton, quitButton);
        buttonsContainer.layoutXProperty().bind(menuPane.widthProperty().subtract(buttonsContainer.widthProperty()).divide(2));

        // add the welcome label and buttons to the menu pane
        menuPane.getChildren().add(getWelcomeLabel());
        menuPane.getChildren().addAll(buttonsContainer);


        return menuPane;
    }

    // createButton method to create a button with the given text and y position
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
            playEffectSound("/sound/effect/hover.wav");
        });
        button.setOnMouseExited(event -> {
            button.setTextFill(Color.WHITE);
            button.setEffect(null);
        });
        return button;
    }

    // showContributor method to show the contributors of the game
    private void showContributor() {
        Pane contributorPane = new Pane();

        // set the background image of the contributor pane
        Image backgroundImage = new Image(getClass().getResource("/pic/bg/bg_without_tree.png").toExternalForm());
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        contributorPane.setBackground(new Background(background));

        // create a label for the contributors
        Label contributorLabel = new Label("Contributors");
        contributorLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 25));
        contributorLabel.setTextFill(Color.INDIANRED);
        contributorLabel.setLayoutX(80);
        contributorLabel.setLayoutY(10);

        //mymy
        ImageView mymyImage = new ImageView(new Image(getClass().getResource("/pic/contributor/mymy.png").toExternalForm()));
        mymyImage.setFitWidth(100);
        mymyImage.setFitHeight(100);
        mymyImage.setLayoutX(100);
        mymyImage.setLayoutY(50 + 10);
        Label mymyLabel = new Label("MyMy 6633287021");
        mymyLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        mymyLabel.setTextFill(Color.BLACK);
        mymyLabel.setLayoutX(65);
        mymyLabel.setLayoutY(150 + 10);

        //anan
        ImageView ananImage = new ImageView(new Image(getClass().getResource("/pic/contributor/anan.png").toExternalForm()));
        ananImage.setFitWidth(100);
        ananImage.setFitHeight(100);
        ananImage.setLayoutX(100);
        ananImage.setLayoutY(50 + 10 + 300);
        Label ananLabel = new Label("AnAn 6633033021");
        ananLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        ananLabel.setTextFill(Color.BLACK);
        ananLabel.setLayoutX(65);
        ananLabel.setLayoutY(150 + 10 + 300);

        //jojo
        ImageView jojoImage = new ImageView(new Image(getClass().getResource("/pic/contributor/jojo.png").toExternalForm()));
        jojoImage.setFitWidth(100);
        jojoImage.setFitHeight(100);
        jojoImage.setLayoutX(100);
        jojoImage.setLayoutY(50 + 10 + 150);
        Label jojoLabel = new Label("JoJo 6633109021");
        jojoLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        jojoLabel.setTextFill(Color.BLACK);
        jojoLabel.setLayoutX(65);
        jojoLabel.setLayoutY(150 + 10 + 150);

        // create a back button to go back to the menu scene
        Button backButton = createButton("Back", HEIGHT - 90);
        backButton.setLayoutX(110);
        backButton.setLayoutY(HEIGHT - 90);
        backButton.setOnAction(event -> {
            primaryStage.setScene(menuScene);
            playEffectSound("/sound/effect/click.mp3");
        });

        // add the labels, images, and back button to the contributor pane
        contributorPane.getChildren().addAll(contributorLabel, ananImage, ananLabel, jojoImage, jojoLabel, mymyLabel, mymyImage, backButton);

        Scene contributorScene = new Scene(contributorPane, WIDTH, HEIGHT);

        primaryStage.setScene(contributorScene);
    }

    // showInstructions method to show the instructions of the game
    private void showInstructions() {
        Pane instructionsPane = new Pane();

        // set the background image of the instructions pane
        Image backgroundImage = new Image(getClass().getResource("/pic/bg/bg_without_tree.png").toExternalForm());
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        instructionsPane.setBackground(new Background(background));

        // create a label for the instructions
        Label instructionLabel = new Label("Instructions");
        instructionLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 25));
        instructionLabel.setTextFill(Color.INDIANRED);
        instructionLabel.setLayoutX(80);
        instructionLabel.setLayoutY(10);

        // create a label for the instructions detail
        Label instructionsDetailLabel01 = new Label(">> WASD or arrow keys = move\n>> Space bar = shoot" +
                "\n\nIn Dog-VS-Monkey, you play as a monkey \nshooting down dogs moving toward you.\n" +
                "If a dog reaches the bottom edge of \nthe game screen, you'll lose a life! \n>> Survive as long as possible!\n" +
                ">> You have 5 lives to start with. \n>> Good luck :D");
        instructionsDetailLabel01.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
        instructionsDetailLabel01.setLayoutX(0);
        instructionsDetailLabel01.setLayoutY(40);
        instructionsDetailLabel01.setPadding(new Insets(20, 20, 20, 25));

        // create an ImageView for the monkey image
        ImageView monkeyImageView = new ImageView(new Image(getClass().getResource("/pic/character/monkey_head_red.png").toExternalForm()));
        monkeyImageView.setFitWidth(50);
        monkeyImageView.setFitHeight(50);
        monkeyImageView.setLayoutX(225);
        monkeyImageView.setLayoutY(55);

        // create ImageViews for the normal dog images and Labels for their details
        for (int i = 0; i < 3; i++) {
            ImageView dogImageView = new ImageView(new Image(getClass().getResource("/pic/character/normalDog0" + (i + 1) + ".png").toExternalForm()));
            dogImageView.setFitWidth(65);
            dogImageView.setFitHeight(65);
            dogImageView.setLayoutX(35 + i * 80);
            dogImageView.setLayoutY(250);

            instructionsPane.getChildren().addAll(dogImageView);
        }
        Label normalDogDetailLabel = new Label(">> Normal Dog, Score +10");
        normalDogDetailLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
        normalDogDetailLabel.setLayoutX(70);
        normalDogDetailLabel.setLayoutY(315);
        instructionsPane.getChildren().addAll(normalDogDetailLabel);

        // create ImageViews for the small dog image and Labels for its details
        ImageView smallDogImageView = new ImageView(new Image(getClass().getResource("/pic/character/smallDog.png").toExternalForm()));
        smallDogImageView.setFitWidth(65);
        smallDogImageView.setFitHeight(65);
        smallDogImageView.setLayoutX(35);
        smallDogImageView.setLayoutY(342);

        Label smallDogDetailLabel = new Label(">> Small Dog, \nScore +50, Lives +1");
        smallDogDetailLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
        smallDogDetailLabel.setLayoutX(120);
        smallDogDetailLabel.setLayoutY(352);

        instructionsPane.getChildren().addAll(smallDogImageView, smallDogDetailLabel);

        // create ImageViews for the boss dog image and Labels for its details
        ImageView bossDogImageView = new ImageView(new Image(getClass().getResource("/pic/character/monkey_head_green.png").toExternalForm()));
        bossDogImageView.setFitWidth(65);
        bossDogImageView.setFitHeight(65);
        bossDogImageView.setLayoutX(35);
        bossDogImageView.setLayoutY(420);

        Label bossDogDetailLabel = new Label(">> Boss Dog (?), \nIt has 3 lives. Score +100");
        bossDogDetailLabel.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
        bossDogDetailLabel.setLayoutX(120);
        bossDogDetailLabel.setLayoutY(415);

        Label bossDogDetailLabel2 = new Label("If it reaches the bottom,\nyou will die immediately!!!");
        bossDogDetailLabel2.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 12));
        bossDogDetailLabel2.setLayoutX(120);
        bossDogDetailLabel2.setLayoutY(452);
        bossDogDetailLabel2.setTextFill(Color.ORANGERED);

        instructionsPane.getChildren().addAll(bossDogImageView, bossDogDetailLabel, bossDogDetailLabel2);

        // create a back button to go back to the menu scene
        Button backButton = createButton("Back", HEIGHT - 90);
        backButton.setLayoutX(110);
        backButton.setLayoutY(HEIGHT - 90);
        backButton.setOnAction(event -> {
            primaryStage.setScene(menuScene);
            playEffectSound("/sound/effect/click.mp3");
        });

        // add the labels and back button to the instructions pane
        instructionsPane.getChildren().addAll(instructionLabel, instructionsDetailLabel01, monkeyImageView, backButton);

        Scene instructionsScene = new Scene(instructionsPane, WIDTH, HEIGHT);

        primaryStage.setScene(instructionsScene);
    }

    // showTempMessage method to show a temporary message on the screen
    private void showTempMessage(String message, double x, double y, double duration) {
        Text tempMessage = new Text(message);
        tempMessage.setFont(Font.font("Comic Sans MS", FontWeight.BOLD, 20));
        tempMessage.setFill(Color.RED);
        tempMessage.setX(x);
        tempMessage.setY(y);
        root.getChildren().add(tempMessage);

        // create a pause transition to remove the message after a certain duration
        PauseTransition pause = new PauseTransition(Duration.seconds(duration));
        pause.setOnFinished(event -> root.getChildren().remove(tempMessage));
        pause.play();
    }

    // startGame method to start the game
    private void startGame() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }

        getWelcomeLabel().setText("Welcome!");
        getWelcomeLabel().setTextFill(Color.INDIANRED);
        getWelcomeLabel().setLayoutX(90);

        // play the game start sound effect
        playBackgroundMusic("/sound/bgmusic/playsong.mp3");

        primaryStage.setScene(scene);
        gameLoop.start();
        isRunning = true;
    }

    // playBackgroundMusic method to play the background music
    private void playBackgroundMusic(String musicFile) {
        Media sound = new Media(getClass().getResource(musicFile).toExternalForm());
        backgroundMusic = new MediaPlayer(sound);
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.play();
    }

    // playEffectSound method to play the sound effect
    public static void playEffectSound(String soundFile) {
        Media sound = new Media(Main.class.getResource(soundFile).toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    // Getter and setter methods for welcomeLabel
    public Label getWelcomeLabel() {
        return welcomeLabel;
    }

    public void setWelcomeLabel(Label welcomeLabel) {
        this.welcomeLabel = welcomeLabel;
    }
}
