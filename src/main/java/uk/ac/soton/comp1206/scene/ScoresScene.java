package uk.ac.soton.comp1206.scene;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.Multimedia;
import uk.ac.soton.comp1206.component.ScoresList;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.ui.GamePane;
import uk.ac.soton.comp1206.ui.GameWindow;

/**
 * The final scores scene after the game is over
 */
public class ScoresScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ScoresScene.class);
  /**
   * The game state
   */
  protected Game game;

  private BorderPane mainPane;

  private boolean getAllScores = true;

  /**
   * Name to enter if high score
   */
  private StringProperty name = new SimpleStringProperty("");
  /**
   * Whether it is a new local high score
   */
  private final BooleanProperty ifScore = new SimpleBooleanProperty(false);

  /**
   * Holds the current list of scores in the scene
   */
  private ObservableList<Pair<String, Integer>> observableLocalScores;

  /**
   * Container for all UI elements
   */
  private VBox centerBox;
  private boolean newHighScore = false;

  /**
   * Local score list
   */
  private ScoresList localScores;
  private Multimedia audioPlayer, musicPlayer;

  /**
   * Creates scoresScene
   * @param gameWindow the game window this will be displayed in
   * @param game       the final game state
   */
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.game = game;
    logger.info("Creating scores scene");
  }

  /**
   * Iniitalise the game and set it up
   */
  @Override
  public void initialise() {
    logger.info("Initialising" + this.getClass().getName());
    audioPlayer = new Multimedia();
    musicPlayer = new Multimedia();
    audioPlayer.playAudioFile("explode.wav");
    musicPlayer.playMusic("end.wav");
    Platform.runLater(this::revealMethod);
  }

  /**
   * Build the scores layout
   */
  public void build() {
    logger.info("Building " + this.getClass().getName());

    root = new GamePane(gameWindow.getWidth(), gameWindow.getHeight());

    StackPane scoresPane = new StackPane();
    scoresPane.setMaxWidth(gameWindow.getWidth());
    scoresPane.setMaxHeight(gameWindow.getHeight());
    root.getChildren().add(scoresPane);
    scoresPane.getStyleClass().add("menu-background");
    BorderPane mainPane = new BorderPane();
    scoresPane.getChildren().add(mainPane);

    HBox topBar = new HBox();
    topBar.setAlignment(Pos.CENTER);
    BorderPane.setMargin(topBar, new Insets(10, 0, 0, 0));
    mainPane.setTop(topBar);

    Text gameOverText = new Text("Game Over!");
    gameOverText.getStyleClass().add("bigtitle");
    topBar.getChildren().add(gameOverText);

    /* Center */
    centerBox = new VBox();
    centerBox.setAlignment(Pos.CENTER);
    centerBox.setSpacing(10);
    mainPane.setCenter(centerBox);

    Text highScoreText = new Text("High Scores");
    highScoreText.getStyleClass().add("title");
    highScoreText.visibleProperty().bind(ifScore);

    GridPane gridPane = new GridPane();
    gridPane.setAlignment(Pos.CENTER);
    gridPane.visibleProperty().bind(ifScore);

    localScores = new ScoresList();
    localScores.setAlignment(Pos.CENTER);
    gridPane.getChildren().add(localScores);

    centerBox.getChildren().addAll(highScoreText, gridPane);

    observableLocalScores = FXCollections.observableArrayList(loadScores());
    observableLocalScores.sort((score1, score2) -> (score2.getValue().compareTo(score1.getValue())));
    SimpleListProperty<Pair<String, Integer>> localScore = new SimpleListProperty<>(observableLocalScores);
    localScores.nameProperty.bind(name);
    localScores.scores.bind(localScore);

    HBox bottomBar = new HBox(80);
    bottomBar.setAlignment(Pos.CENTER);
    BorderPane.setMargin(bottomBar, new Insets(0, 0, 20, 0));
    mainPane.setBottom(bottomBar);

    // Menu option
    Text backText = new Text("Menu");
    backText.getStyleClass().add("menuItem");
    backText.setOnMouseClicked(e -> {
      gameWindow.startMenu();
    });

    bottomBar.getChildren().addAll(backText);
  }

  /**
   * Adds random scores if file does not exist
   * @return loads scores into an ArrayList
   */
  public static ArrayList<Pair<String, Integer>> loadScores() {
    ArrayList<Pair<String, Integer>> score = new ArrayList<>();
    File scoresFile = new File("scores.txt");

    if (!scoresFile.exists()) {
      ArrayList<Pair<String, Integer>> randomScores = new ArrayList<>();
      randomScores.add(new Pair<>("Rohit", 400));
      randomScores.add(new Pair<>("Shuruthy", 250));
      randomScores.add(new Pair<>("Praj", 150));
      randomScores.add(new Pair<>("Janahan", 50));
      randomScores.add(new Pair<>("Kian", 50));
      randomScores.add(new Pair<>("Asher", 50));
      randomScores.add(new Pair<>("Mitun", 40));
      randomScores.add(new Pair<>("Mitkumar", 30));
      randomScores.add(new Pair<>("Reece", 20));
      randomScores.add(new Pair<>("Daniel", 10));
      writeScores(randomScores);
    } else {
      try {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(new FileInputStream(scoresFile)));
        try {
          String line;
          while ((line = br.readLine()) != null) {
            String[] nameScore = line.split(":");
            score.add(new Pair<>(nameScore[0], Integer.parseInt(nameScore[1])));
          }
          br.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      } catch (
          FileNotFoundException e) {
        e.printStackTrace();
        logger.info("File not found");
      }
    }
    return score;
  }

  /**
   * Writes scores to file
   * @param scores writes scores to file - scores.txt
   */
  public static void writeScores(List<Pair<String, Integer>> scores) {
    scores.sort((x, y) -> (y.getValue()).compareTo(x.getValue()));
    try {
      if (new File("scores.txt").createNewFile()) {
        logger.info("File created");
      }
    } catch (IOException e) {
      e.printStackTrace();
      logger.info("Error in creating file");
    }
    try {
      BufferedWriter scoreWriter = new BufferedWriter(new FileWriter("scores.txt"));
      int scoresNumber = 0;
      for (Pair<String, Integer> score : scores) {
        scoreWriter.write(score.getKey() + ":" + score.getValue() + "\n");
        scoresNumber++;
        if (scoresNumber > 9) {
          break;
        }
      }
      scoreWriter.close();
      logger.info("Scores saved");
    } catch (IOException e) {
      e.printStackTrace();
      logger.info("Scores save error");
    }
  }


  /**
   * Checks for new high score in the top 10 of all entries
   */
  public void newHighScore() {
    if (!game.scores.isEmpty()) {
      ifScore.set(true);
      localScores.reveal();
      logger.info("No new score");
      return;
    }
    int scoreNumber = 0;
    int finalScoreNumber = scoreNumber;
    int currentScore = game.getScore();
    TextField nameField = new TextField();
    int lowestLocalScore = observableLocalScores.get(observableLocalScores.size() - 1).getValue();
    nameField.setMaxWidth(200);
    nameField.setPromptText("Enter your name");

    highScoreInterface r = () -> {
      name.set(nameField.getText().replace(":", ""));
      centerBox.getChildren().remove(1);
      centerBox.getChildren().remove(1);
      centerBox.getChildren().remove(1);

      if (newHighScore) {
        observableLocalScores.add(finalScoreNumber, new Pair<>(nameField.getText().replace(":", ""), currentScore));
      }
      writeScores(observableLocalScores);
      Platform.runLater(this::revealMethod);
      newHighScore = false;
    };
    // Score comparison
    if (currentScore > lowestLocalScore) {
      for (Pair<String, Integer> score : observableLocalScores) {
        if (currentScore > score.getValue()) {
          newHighScore = true;
        }
        scoreNumber++;
      }
    }
    // New high score prompt
    if (newHighScore) {
      Text newScoreText = new Text("New Score Recorded!");
      newScoreText.getStyleClass().add("title");
      centerBox.getChildren().add(1, newScoreText);

      // Text field
      nameField.setOnKeyPressed(e -> {
        if (e.getCode().equals(KeyCode.ENTER)) {
          r.run();
        }
      });
      nameField.setAlignment(Pos.CENTER);
      nameField.requestFocus();
      centerBox.getChildren().add(2, nameField);

      // Confirm
      Text saveText = new Text("Confirm");
      saveText.getStyleClass().add("menuItem");
      saveText.setOnMouseClicked(e -> r.run());
      centerBox.getChildren().add(3, saveText);
    }
    // Show past scores if no new high score
    else {
      audioPlayer.playAudioFile("fail.mp3");
      logger.info("High score not achieved");
      ifScore.set(true);
      localScores.reveal();
    }
  }


  /**
   * Reveals all scores
   */
  public void revealMethod() {
    if (getAllScores) {
      newHighScore();
      getAllScores = false;
      return;
    }
    ifScore.set(true);
    localScores.reveal();
  }
}

interface highScoreInterface {
  void run();
}
