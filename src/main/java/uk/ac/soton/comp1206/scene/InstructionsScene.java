package uk.ac.soton.comp1206.scene;

import java.util.ArrayList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.PieceBoard;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The instructions menu of the game. Provides the user info on how to play.
 */
public class InstructionsScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  private Multimedia audioPlayer, musicPlayer;
  private ScrollPane scrollPane;

  /**
   * Creates a new instructions scene
   *
   * @param gameWindow the GameWindow this will be displayed in
   */
  public InstructionsScene(GameWindow gameWindow) {
    super(gameWindow);
    logger.info("Creating instructions scene");
  }

  @Override
  public void initialise() {
    logger.info("Initialising" + this.getClass().getName());
    musicPlayer = new Multimedia();
    musicPlayer.playMusic("menu.mp3");
    scene.setOnKeyPressed(this::keyboard);
  }

  /**
   * Build the instructions layout
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

    var topBar = new HBox();
    topBar.setAlignment(Pos.CENTER);
    BorderPane.setMargin(topBar, new Insets(10, 0, 0, 0));
    mainPane.setTop(topBar);

    // Instructions title
    var instructionsText = new Text("Instructions");
    instructionsText.getStyleClass().add("instructionstitle");
    topBar.getChildren().add(instructionsText);

    // Grid of all pieces
    var pieceBoardsGrid = new VBox();
    pieceBoardsGrid.setSpacing(10);
    pieceBoardsGrid.setAlignment(Pos.CENTER);

    var instructionsBox = new HBox();
    instructionsBox.setAlignment(Pos.CENTER);
    instructionsBox.setPadding(new Insets(10,0,0,0));
    instructionsBox.setSpacing(10);
    mainPane.setCenter(instructionsBox);

    for (int x = 0; x < 5; x++) {
      var hBox = new HBox();
      pieceBoardsGrid.getChildren().add(hBox);
      hBox.setAlignment(Pos.CENTER);
      hBox.setSpacing(10);
      for (int y = 0; y < 3; y++) {
        var pieceBoard = new PieceBoard(50, 50);
        GamePiece gamePiece = GamePiece.createPiece(x + y * 5);
        pieceBoard.displayPiece(gamePiece);
        pieceBoard.getStyleClass().add("sideBox");
        hBox.getChildren().add(pieceBoard);
      }
    }
    ImageView instructionsImage = new ImageView(Multimedia.class.getResource("/images/Instructions.png").toExternalForm());
    instructionsImage.setFitHeight(320);
    instructionsImage.setPreserveRatio(true);
    instructionsBox.getChildren().addAll(instructionsImage, pieceBoardsGrid);
  }
  public void keyboard(KeyEvent event) {
    switch (event.getCode()) {
      case ESCAPE:
        musicPlayer.stopMusic();
        gameWindow.startMenu();
    }
  }
}