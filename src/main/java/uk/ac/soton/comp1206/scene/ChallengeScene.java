package uk.ac.soton.comp1206.scene;

import java.nio.file.Paths;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
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

    private PieceBoard nextPieceBoard;
    private PieceBoard tertiaryBoard;

    private Multimedia musicPlayer;
    private Multimedia audioPlayer;


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

        musicPlayer = new Multimedia();
        audioPlayer = new Multimedia();

        setupGame();

        musicPlayer.playBackgroundMusic(Paths.get("src/main/resources/music/game.wav").toUri()
            .toString());


        root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        // Vbox to hold the main board
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // VBox to hold text objects
        var leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER_LEFT);
        leftPane.setSpacing(10);

        // Vbox to hold the board displaying the next piece, and following piece
        var rightPane = new VBox();
        rightPane.setAlignment(Pos.CENTER_RIGHT);
        rightPane.setSpacing(10);

        // The main tetris board
        var board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2,
            gameWindow.getWidth() / 2);
        mainPane.setCenter(board);

        buildText();

        // The secondary board displaying the next piece
        nextPieceBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 5, gameWindow.getWidth() / 5);

        // Tertiary board displaying the piece after the next one
        tertiaryBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 7, gameWindow.getWidth() / 7);

        game.registerNextPieceListener(this);
        nextPieceBoard.setOnRightClicked(this);

        // Adding spacing and each side's children to the main pane
        rightPane.getChildren().addAll(nextPieceBoard, tertiaryBoard);
        leftPane.getChildren().addAll(levelLabel, livesLabel, scoreLabel, multiplierLabel);
        rightPane.setPadding(new Insets(0, 20, 0, 0));
        leftPane.setPadding(new Insets(0, 0, 0, 20));
        mainPane.setLeft(leftPane);
        mainPane.setRight(rightPane);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this::onRightClicked);
        nextPieceBoard.setOnBlockClick(this::rotatePiece);
    }

    private void rotatePiece(GameBlock gameBlock) {
        game.rotateCurrentPiece();
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
    }

    public void buildText() {
        scoreLabel = new Text();
        levelLabel = new Text();
        multiplierLabel = new Text();
        livesLabel =
            new Text();

        scoreLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        levelLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        multiplierLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        livesLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));

        scoreLabel.setFill(Color.WHITE);
        levelLabel.setFill(Color.WHITE);
        multiplierLabel.setFill(Color.WHITE);
        livesLabel.setFill(Color.WHITE);

        // Might need to use listeners here instead
        scoreLabel.textProperty().bind(Bindings.concat("Score: ", game.scoreProperty().asString()));
        levelLabel.textProperty().bind(Bindings.concat("Level: ", game.levelProperty().asString()));
        multiplierLabel.textProperty()
            .bind(Bindings.concat("Multiplier: ", game.multiplierProperty().asString()));
        livesLabel.textProperty().bind(Bindings.concat("Lives: ", game.livesProperty().asString()));
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
    }

    /**
     * @param nextPiece      passes the next piece to be displayed by the pieceBoard
     * @param followingPiece passes the following piece to be displayed by the pieceBoard
     */
    @Override
    public void nextPiece(GamePiece nextPiece, GamePiece followingPiece) {
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

}