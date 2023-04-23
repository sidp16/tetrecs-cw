package uk.ac.soton.comp1206.scene;

import java.util.HashSet;
import java.util.Set;
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

        // Vbox to hold the main board
        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        // VBox to hold text objects
        var leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPadding(new Insets(0,0,0,15));

        // Vbox to hold the board displaying the next piece, and following piece
        var rightPane = new VBox();
        rightPane.setAlignment(Pos.CENTER);
        rightPane.setPadding(new Insets(0,15,0,0));

        // Top row
        var topBar = new HBox(125);
        topBar.setAlignment(Pos.CENTER);
        BorderPane.setMargin(topBar, new Insets(10,0,0,0));
        mainPane.setTop(topBar);

        // The main tetris board
        board = new GameBoard(game.getGrid(), gameWindow.getWidth() / 2,
            gameWindow.getWidth() / 2);
        mainPane.setCenter(board);
        board.setOnBlockClick(this::blockClicked);
        board.setOnRightClicked(this::onRightClicked);

        // The secondary board displaying the next piece
        nextPieceBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 5, gameWindow.getWidth() / 5);
        nextPieceBoard.setOnRightClicked(this);
        nextPieceBoard.blocks[1][1].center();
        nextPieceBoard.setOnBlockClick(this::rotatePiece);
        nextPieceBoard.setPadding(new Insets(5,0,0,0));

        // Tertiary board displaying the piece after the next one
        tertiaryBoard = new PieceBoard(3, 3,
            gameWindow.getWidth() / 7, gameWindow.getWidth() / 7);
        tertiaryBoard.setOnRightClicked(this);
        tertiaryBoard.setOnBlockClick(this::swapPiece);
        tertiaryBoard.setPadding(new Insets(15,0,0,0));

        buildText();

        // Score info
        var scoreInfo = new VBox();
        scoreInfo.setAlignment(Pos.CENTER);
        scoreInfo.getChildren().add(scoreLabel);

        // Lives info
        var livesInfo = new VBox();
        livesInfo.setAlignment(Pos.CENTER);
        livesInfo.getChildren().add(livesLabel);

        // Title
        var title = new VBox();
        title.setAlignment(Pos.CENTER);
        title.getChildren().add(titleLabel);

        topBar.getChildren().addAll(scoreInfo,title, livesInfo);


        // Adding spacing and each side's children to the main pane
        rightPane.getChildren().addAll(nextPieceBoard, tertiaryBoard);
        leftPane.getChildren().addAll(levelLabel, multiplierLabel);
        mainPane.setLeft(leftPane);
        mainPane.setRight(rightPane);


    }


    private void fadeLine(Set<GameBlockCoordinate> allBlockCoordinates) {
        board.fadeOut((HashSet<GameBlockCoordinate>) allBlockCoordinates);
        logger.info("Line clear animation done");
    }


    private void swapPiece(GameBlock gameBlock) {
        game.swapCurrentPiece();
        nextPieceBoard.clear();
        tertiaryBoard.clear();
        nextPiece(game.getCurrentPiece(), game.getFollowingPiece());
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
        livesLabel = new Text();
        titleLabel = new Text("TETRECS");

        scoreLabel.getStyleClass().add("score");
        levelLabel.getStyleClass().add("level");
        multiplierLabel.getStyleClass().add("multiplier");
        livesLabel.getStyleClass().add("lives");
        titleLabel.getStyleClass().add("title");

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