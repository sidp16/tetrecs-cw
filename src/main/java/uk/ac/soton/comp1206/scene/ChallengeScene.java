package uk.ac.soton.comp1206.scene;

import java.util.HashSet;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene implements NextPieceListener, RightClickedListener {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    /**
     * The game state
     */
    protected Game game;

    /**
     * Text for score
     */
    private Text scoreLabel;
    /**
     * Text for the current level
     */
    private Text levelLabel;
    /**
     * Text for the multiplier
     */
    private Text multiplierLabel;
    /**
     * Text for the number of lives left
     */
    private Text livesLabel;

    /**
     * The main tetris board
     */
    protected GameBoard board;
    /**
     * The next piece, and the one after it - their respective boards
     */
    protected PieceBoard nextPieceBoard, tertiaryBoard;
    /**
     * Text for the TETRECS title
     */
    private Text titleLabel;

    /**
     * Separate Multimedia objects for audio and music
     */
    private Multimedia audioPlayer, musicPlayer;

    /**
     * timerBar for the visual timer
     */
    private Rectangle timerBar;

    /**
     * Timer box
     */
    protected HBox timer;

    /**
     * The score value
     */
    protected IntegerProperty score = new SimpleIntegerProperty(0);
    /**
     * High score value (used for animation)
     */
    protected IntegerProperty hiscore = new SimpleIntegerProperty(0);

    /**
     * Keyboard input
     */
    private int x = 0, y = 0;
    /**
     * Create a new Single Player challenge scene
     *
     * @param gameWindow the Game Window
     */
    public ChallengeScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Challenge Scene");
    }

    /**
     * Build the Challenge window
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        setupGame();

        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());


        StackPane challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);


        musicPlayer = new Multimedia();
        audioPlayer = new Multimedia();
        musicPlayer.playMusic("game.wav");

        // Vbox to hold the main board
        BorderPane mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // VBox to hold text objects
        VBox leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(0,0,0,10));

        // Vbox to hold the board displaying the next piece, and following piece
        VBox rightPane = new VBox(10);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(0,10,0,0));

        // Top bar for socre, title and lives
        HBox topBar = new HBox(50);
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10,0,0,0));
        mainPane.setTop(topBar);

        // The main tetris board
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2,
            gameWindow.getWidth() / 2);
        board.getStyleClass().add("gameBox");
        mainPane.setCenter(board);


        // The secondary board displaying the next piece
        Text currentPieceText = new Text("Current Piece:");
        currentPieceText.getStyleClass().add("heading");
        nextPieceBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 6, gameWindow.getWidth() / 6);
        nextPieceBoard.blocks[1][1].center();
        nextPieceBoard.setPadding(new Insets(5,0,0,0));
        nextPieceBoard.getStyleClass().add("sideBox");

        // Tertiary board displaying the piece after the next one
        var nextPieceText = new Text("Next:");
        nextPieceText.getStyleClass().add("heading");
        tertiaryBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 7, gameWindow.getWidth() / 7);
        tertiaryBoard.setPadding(new Insets(15,0,0,0));
        tertiaryBoard.getStyleClass().add("sideBox");

        buildText();

        // Score info
        VBox scoreInfo = new VBox();
        Text scoreTitle = new Text("Score");
        scoreTitle.getStyleClass().add("score");
        scoreInfo.setAlignment(Pos.CENTER);
        scoreInfo.getChildren().addAll(scoreTitle, scoreLabel);

        // Lives info
        VBox livesInfo = new VBox();
        Text livesTitle = new Text("Lives");
        livesTitle.getStyleClass().add("lives");
        livesInfo.setAlignment(Pos.CENTER);
        livesInfo.getChildren().addAll(livesTitle,livesLabel);

        // Image title
        ImageView titleImage = new ImageView(Multimedia.class.getResource("/images/TetrECS.png").toExternalForm());
        titleImage.setFitHeight(75);
        titleImage.setPreserveRatio(true);

        // Title
        VBox title = new VBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(titleImage);


        topBar.getChildren().addAll(scoreInfo,title, livesInfo);

        // Visual timer
        timer = new HBox();
        timerBar = new Rectangle();
        timerBar.setHeight(15);
        timer.getChildren().add(timerBar);
        mainPane.setBottom(timer);

        var clearGridText = new Text("Clear grid");
        clearGridText.getStyleClass().add("menuItem");
        clearGridText.setOnMouseClicked(e -> game.clearAll());
        var clearGridTextPoints = new Text("400 points");
        clearGridTextPoints.getStyleClass().add("channelItem");

        var addLifeText = new Text("Add Life");
        addLifeText.getStyleClass().add("menuItem");
        addLifeText.setOnMouseClicked(e -> game.addLife());
        var addLifeTextPoints = new Text("500 points");
        addLifeTextPoints.getStyleClass().add("channelItem");


        // Adding spacing and each side's children to the main pane
        rightPane.getChildren().addAll(currentPieceText,nextPieceBoard,nextPieceText, tertiaryBoard);
        leftPane.getChildren().addAll(levelLabel, multiplierLabel, clearGridText,clearGridTextPoints,
            addLifeText,addLifeTextPoints);
        leftPane.setSpacing(10);
        mainPane.setLeft(leftPane);
        mainPane.setRight(rightPane);

    }


    /**
     * @param allBlockCoordinates takes in a block coordinates that need to be cleared
     */
    private void fadeLine(Set<GameBlockCoordinate> allBlockCoordinates) {
        board.fadeOut((HashSet<GameBlockCoordinate>) allBlockCoordinates);
        audioPlayer.playAudioFile("clear.wav");
        logger.info("Line clear animation done");
    }


    /**
     * Swaps the current piece with the one after
     * @param gameBlock gameBlock passed
     */
    private void swapPiece(GameBlock gameBlock) {
        game.swapCurrentPiece();
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
        audioPlayer.playAudioFile("pling.wav");
    }

    /**
     * Rotate the piece right
     * @param gameBlock gameBlock passed
     */
    private void rotatePiece(GameBlock gameBlock) {
        game.rotateCurrentPiece(1);
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
        audioPlayer.playAudioFile("rotate.wav");
    }

    /**
     * Rotates the piece left
     */
    private void rotatePieceLeft() {
        game.rotateCurrentPiece(3);
    }

    /**
     * Builds all text required for the challengeScene
     */
    public void buildText() {
        scoreLabel = new Text();
        levelLabel = new Text();
        multiplierLabel = new Text();
        livesLabel = new Text();
        titleLabel = new Text("TETRECS");

        scoreLabel.getStyleClass().add("score");
        levelLabel.getStyleClass().add("level");
        multiplierLabel.getStyleClass().add("multiplier");
        livesLabel.getStyleClass().add("lives");
        titleLabel.getStyleClass().add("title");

        // Might need to use listeners here instead
        scoreLabel.textProperty().bind(Bindings.concat(this.score.asString()));
        levelLabel.textProperty().bind(Bindings.concat("Level: ", game.levelProperty().asString()));
        multiplierLabel.textProperty()
            .bind(Bindings.concat("Multiplier: ", game.multiplierProperty().asString()));
        livesLabel.textProperty().bind(Bindings.concat(game.livesProperty().asString()));
    }

    /**
     * Handle when a block is clicked
     *
     * @param gameBlock the Game Block that was clicked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
    }

    /**
     * Setup the game object and model
     */
    public void setupGame() {
        logger.info("Starting a new challenge");

        //Start new game
        game = new Game(5, 5);
    }

    /**
     * Initialise the scene and start the game
     */
    @Override
    public void initialise() {
        logger.info("Initialising Challenge");
        game.start();

        scene.setOnKeyPressed(this::keyboardInputs);

        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this::onRightClicked);

        nextPieceBoard.setOnRightClicked(this);
        nextPieceBoard.setOnBlockClick(this::rotatePiece);

        tertiaryBoard.setOnRightClicked(this);
        tertiaryBoard.setOnBlockClick(this::swapPiece);

        game.setOnGameLoop(this::timer);
        game.registerNextPieceListener(this);
        game.setOnLineCleared(this::fadeLine);
        this.game.scoreProperty().addListener(this::setScore);

        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());

        game.setOnGameOver(() -> {
            game.stopTimer();
            audioPlayer.stopAudio();
            musicPlayer.stopMusic();
            gameWindow.startScores(game);
        });
    }

    /**
     * Animation to represent score going up
     * @param observable represents a value that can change over time
     * @param oldValue the old score value
     * @param newValue the new score value
     */
    protected void setScore(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
        if (newValue.intValue() > this.hiscore.get()) {
            this.hiscore.set(newValue.intValue());
        }

        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO,
            new KeyValue(this.score, oldValue)), new KeyFrame(new Duration(500.0),
            new KeyValue(this.score, newValue)));
        timeline.play();
    }

    /**
     * @param nextPiece      passes the next piece to be displayed by the pieceBoard
     * @param followingPiece passes the following piece to be displayed by the pieceBoard
     */
    @Override
    public void nextPiece(GamePiece nextPiece, GamePiece followingPiece) {
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPieceBoard.displayPiece(nextPiece);
        tertiaryBoard.displayPiece(followingPiece);
    }

    /**
     * Rotates the piece right on a right click
     */
    @Override
    public void onRightClicked() {
        game.rotateCurrentPiece(1);
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
    }

    /**
     * Animates the timerBar by using a timeLine
     * @param number the duration
     */
    protected void timer(int number) {
        KeyValue start = new KeyValue(timerBar.widthProperty(), timer.getWidth());
        KeyValue green = new KeyValue(timerBar.fillProperty(), Color.GREEN);
        KeyValue yellow = new KeyValue(timerBar.fillProperty(), Color.YELLOW);
        KeyValue red = new KeyValue(timerBar.fillProperty(), Color.RED);
        KeyValue end = new KeyValue(timerBar.widthProperty(), 0);

        Timeline colourChange = new Timeline();
        colourChange.getKeyFrames().add(new KeyFrame(new Duration(0), start));
        colourChange.getKeyFrames().add(new KeyFrame(new Duration(0), green));
        colourChange.getKeyFrames().add(new KeyFrame(new Duration((float) number / 2), yellow));
        colourChange.getKeyFrames().add(new KeyFrame(new Duration((float) number * 3 / 4), red));
        colourChange.getKeyFrames().add(new KeyFrame(new Duration(number), end));
        colourChange.play();
    }

    /**
     * @param key the key pressed by user
     */
    private void keyboardInputs(KeyEvent key) {
        switch (key.getCode()) {
            case W:
            case UP:
                if (y > 0) {
                    y--;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case A:
            case LEFT:
                if (x > 0) {
                    x--;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case S:
            case DOWN:
                if (y < game.getRows() - 1) {
                    y++;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case D:
            case RIGHT:
                if (x < game.getCols() - 1) {
                    x++;
                    board.hover(board.getBlock(x, y));
                }
                break;
            case ENTER:
            case X:
                blockClicked(board.getBlock(x, y));
                break;
            case R:
            case SPACE:
                swapPiece(new GameBlock(board,1,1,1,1));
                break;
            case ESCAPE:
                game.stopTimer();
                musicPlayer.stopMusic();
                audioPlayer.stopAudio();
                this.game.stop();
                gameWindow.startMenu();
                break;
            case E:
            case C:
            case CLOSE_BRACKET:
                rotatePieceLeft();
                break;
            case Q:
            case Z:
            case OPEN_BRACKET:
                rotatePiece(new GameBlock(board,1,1,1,1));
        }
    }
}
