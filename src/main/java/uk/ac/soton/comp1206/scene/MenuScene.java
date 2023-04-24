package uk.ac.soton.comp1206.scene;

import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.App;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    private Text play, instructions, exit;

    private Multimedia audioPlayer, musicPlayer;

    /**
     * Create a new menu scene
     * @param gameWindow the Game Window this will be displayed in
     */
    public MenuScene(GameWindow gameWindow) {
        super(gameWindow);
        logger.info("Creating Menu Scene");
    }

    /**
     * Build the menu layout
     */
    @Override
    public void build() {
        logger.info("Building " + this.getClass().getName());

        root = new GamePane(gameWindow.getWidth(),gameWindow.getHeight());

        var menuPane = new StackPane();
        menuPane.setMaxWidth(gameWindow.getWidth());
        menuPane.setMaxHeight(gameWindow.getHeight());
        menuPane.getStyleClass().add("menu-background");
        root.getChildren().add(menuPane);

        var mainPane = new BorderPane();
        menuPane.getChildren().add(mainPane);

        // Box for title
        var topBar = new HBox();
        topBar.setAlignment(Pos.CENTER);
        mainPane.setTop(topBar);

        // Image title
        ImageView titleImage = new ImageView(Multimedia.class.getResource("/images/TetrECS.png").toExternalForm());
        titleImage.setFitHeight(150);
        titleImage.setPreserveRatio(true);

        // Title
        var titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER);
        topBar.getChildren().add(titleBox);
        titleBox.getChildren().add(titleImage);
        titleBox.setPadding(new Insets(100,0,0,0));

        RotateTransition rotater = new RotateTransition(new Duration(3000.0), titleImage);
        rotater.setCycleCount(-1);
        rotater.setFromAngle(-5.0);
        rotater.setToAngle(5.0);
        rotater.setAutoReverse(true);
        rotater.play();

        // Menu items
        var menuItems = new VBox(10);
        menuItems.setPadding(new Insets(0,0,20,0));
        menuItems.setAlignment(Pos.CENTER);
        mainPane.setCenter(menuItems);

        play = new Text("Play");
        play.getStyleClass().add("menuItem");
        play.setOnMouseClicked(event -> {
            musicPlayer.stopMusic();
            this.gameWindow.startChallenge();
        });

        instructions = new Text("How to Play");
        instructions.getStyleClass().add("menuItem");
        instructions.setOnMouseClicked(event -> {
            logger.info("Instructions button clicked");
            musicPlayer.stopMusic();
            gameWindow.startInstructions();
        });

        exit = new Text("Exit");
        exit.getStyleClass().add("menuItem");
        exit.setOnMouseClicked(event -> {
            logger.info("Exit button clicked");
            musicPlayer.stopMusic();
            App.getInstance().shutdown();
        });

        menuItems.getChildren().addAll(play, instructions, exit);

    }

    /**
     * Initialise the menu
     */
    @Override
    public void initialise() {
        logger.info("Initialising" + this.getClass().getName());
        musicPlayer = new Multimedia();
        musicPlayer.playMusic("menu.mp3");
    }

    /**
     * Handle when the Start Game button is pressed
     * @param event event
     */
    private void startGame(ActionEvent event) {
        gameWindow.startChallenge();
    }

}
