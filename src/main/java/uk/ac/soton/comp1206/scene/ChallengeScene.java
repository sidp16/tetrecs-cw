package uk.ac.soton.comp1206.scene;

import java.util.HashSet;
import java.util.Set;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
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
    protected Game game;

    private Text scoreLabel;
    private Text levelLabel;
    private Text multiplierLabel;
    private Text livesLabel;

    protected GameBoard board;
    protected PieceBoard nextPieceBoard, tertiaryBoard;
    private Text titleLabel;

    private Multimedia audioPlayer, musicPlayer;

    private Rectangle timerBar;

    protected HBox timer;

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

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        game.registerNextPieceListener(this);
        game.setOnLineCleared(this::fadeLine);

        musicPlayer = new Multimedia();
        audioPlayer = new Multimedia();
//        musicPlayer.playMusic("game.wav");

        // Vbox to hold the main board
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // VBox to hold text objects
        var leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(0,0,0,15));

        // Vbox to hold the board displaying the next piece, and following piece
        var rightPane = new VBox(10);
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(0,15,0,0));

        // Top bar for socre, title and lives
        var topBar = new HBox(125);
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10,0,0,0));
        mainPane.setTop(topBar);

        // The main tetris board
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2,
            gameWindow.getWidth() / 2);
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this::onRightClicked);
        board.getStyleClass().add("gameBox");
        mainPane.setCenter(board);


        // The secondary board displaying the next piece
        var currentPieceText = new Text("Current Piece:");
        currentPieceText.getStyleClass().add("heading");
        nextPieceBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 5, gameWindow.getWidth() / 5);
        nextPieceBoard.setOnRightClicked(this);
        nextPieceBoard.blocks[1][1].center();
        nextPieceBoard.setOnBlockClick(this::rotatePiece);
        nextPieceBoard.setPadding(new Insets(5,0,0,0));
        nextPieceBoard.getStyleClass().add("sideBox");

        // Tertiary board displaying the piece after the next one
        var nextPieceText = new Text("Next:");
        nextPieceText.getStyleClass().add("heading");
        tertiaryBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 7, gameWindow.getWidth() / 7);
        tertiaryBoard.setOnRightClicked(this);
        tertiaryBoard.setOnBlockClick(this::swapPiece);
        tertiaryBoard.setPadding(new Insets(15,0,0,0));
        tertiaryBoard.getStyleClass().add("sideBox");

        buildText();

        // Score info
        var scoreInfo = new VBox();
        var scoreTitle = new Text("Score");
        scoreTitle.getStyleClass().add("score");
        scoreInfo.setAlignment(Pos.CENTER);
        scoreInfo.getChildren().addAll(scoreTitle, scoreLabel);

        // Lives info
        var livesInfo = new VBox();
        var livesTitle = new Text("Lives");
        livesTitle.getStyleClass().add("lives");
        livesInfo.setAlignment(Pos.CENTER);
        livesInfo.getChildren().addAll(livesTitle,livesLabel);

        // Title
        var title = new VBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(titleLabel);

        topBar.getChildren().addAll(scoreInfo,title, livesInfo);

        // Visual timer
        timer = new HBox();
        timerBar = new Rectangle();
        timerBar.setHeight(10);
        timer.getChildren().add(timerBar);
        mainPane.setBottom(timer);


        // Adding spacing and each side's children to the main pane
        rightPane.getChildren().addAll(currentPieceText,nextPieceBoard,nextPieceText, tertiaryBoard);
        leftPane.getChildren().addAll(levelLabel, multiplierLabel);
        leftPane.setSpacing(20);
        mainPane.setLeft(leftPane);
        mainPane.setRight(rightPane);


    }


    private void fadeLine(Set<GameBlockCoordinate> allBlockCoordinates) {
        board.fadeOut((HashSet<GameBlockCoordinate>) allBlockCoordinates);
        audioPlayer.playAudioFile("clear.wav");
        logger.info("Line clear animation done");
    }


    private void swapPiece(GameBlock gameBlock) {
        game.swapCurrentPiece();
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
        audioPlayer.playAudioFile("pling.wav");
    }
    private void rotatePiece(GameBlock gameBlock) {
        game.rotateCurrentPiece();
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
        audioPlayer.playAudioFile("rotate.wav");
    }

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
        scoreLabel.textProperty().bind(Bindings.concat(game.scoreProperty().asString()));
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
        nextPieceBoard.clear();
        tertiaryBoard.clear();
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
        game.setOnGameLoop(this::timer);
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

    @Override
    public void onRightClicked() {
        game.rotateCurrentPiece();
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
    }

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
}
