package uk.ac.soton.comp1206.scene;

import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBoard;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The Single Player challenge scene. Holds the UI for the single player challenge mode in the game.
 */
public class ChallengeScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);
    protected Game game;

    private Text scoreLabel;
    private Text levelLabel;
    private Text multiplierLabel;
    private Text livesLabel;

    /**
     * Create a new Single Player challenge scene
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

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());



        var challengePane = new StackPane();
        challengePane.setMaxWidth(gameWindow.getWidth());
        challengePane.setMaxHeight(gameWindow.getHeight());
        challengePane.getStyleClass().add("menu-background");
        root.getChildren().add(challengePane);

        var mainPane = new BorderPane();
        challengePane.getChildren().add(mainPane);

        var leftPane = new VBox();
        leftPane.setAlignment(Pos.CENTER_LEFT);
        leftPane.setSpacing(10);

        var board = new GameBoard(game.getGrid(),gameWindow.getWidth()/2,gameWindow.getWidth()/2);
        mainPane.setCenter(board);

        buildText();

        leftPane.getChildren().addAll(levelLabel, livesLabel, scoreLabel, multiplierLabel);
        leftPane.setPadding(new Insets(0,20,0,20));
        mainPane.setLeft(leftPane);

        //Handle block on gameboard grid being clicked
        board.setOnBlockClick(this::blockClicked);
    }

    public void buildText() {
        scoreLabel = new Text(); levelLabel = new Text(); multiplierLabel = new Text(); livesLabel =
            new Text();
        scoreLabel.setFont(Font.font("Courier New", FontWeight.BOLD,20));
        levelLabel.setFont(Font.font("Courier New", FontWeight.BOLD,20));
        multiplierLabel.setFont(Font.font("Courier New", FontWeight.BOLD,20));
        livesLabel.setFont(Font.font("Courier New", FontWeight.BOLD,20));

        scoreLabel.setFill(Color.WHITE);
        levelLabel.setFill(Color.WHITE);
        multiplierLabel.setFill(Color.WHITE);
        livesLabel.setFill(Color.WHITE);

        scoreLabel.textProperty().bind(Bindings.concat("Score: ", game.scoreProperty().asString()));
        levelLabel.textProperty().bind(Bindings.concat("Level: ", game.levelProperty().asString()));
        multiplierLabel.textProperty().bind(Bindings.concat("Multiplier: ", game.multiplierProperty().asString()));
        livesLabel.textProperty().bind(Bindings.concat("Lives: ", game.livesProperty().asString()));
    }

    /**
     * Handle when a block is clicked
     * @param gameBlock the Game Block that was clicked
     */
    private void blockClicked(GameBlock gameBlock) {
        game.blockClicked(gameBlock);
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

}
