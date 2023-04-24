package uk.ac.soton.comp1206.scene;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The main menu of the game. Provides a gateway to the rest of the game.
 */
public class MenuScene extends BaseScene {

    private static final Logger logger = LogManager.getLogger(MenuScene.class);

    private Text play, instructions, controls;

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

        // Title
        var titleBox = new VBox();
        titleBox.setAlignment(Pos.CENTER);
        topBar.getChildren().add(titleBox);
        var title = new Text("TetrECS");
        title.getStyleClass().add("menutitle");
        titleBox.getChildren().add(title);
        titleBox.setPadding(new Insets(20,0,0,0));

        // Menu items
        var menuItems = new VBox(10);
        menuItems.setPadding(new Insets(15));
        menuItems.setAlignment(Pos.CENTER);
        mainPane.setBottom(menuItems);

        play = new Text("Play");
        play.getStyleClass().add("menuItem");
        play.setOnMouseClicked(event -> {
            musicPlayer.stopMusic();
            gameWindow.startChallenge();
        });

        instructions = new Text("Instructions");
        instructions.getStyleClass().add("menuItem");
        instructions.setOnMouseClicked(event -> {
            logger.info("Instructions button clicked");
            musicPlayer.stopMusic();
            gameWindow.startInstructions();
        });

        controls = new Text("Controls");
        controls.getStyleClass().add("menuItem");
        controls.setOnMouseClicked(event -> {
            logger.info("Controls button clicked");
        });
        menuItems.getChildren().addAll(play, instructions, controls);

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
