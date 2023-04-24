package uk.ac.soton.comp1206.scene;

import javafx.beans.property.SimpleListProperty;
import javafx.scene.control.ScrollPane;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.game.Game;
import uk.ac.soton.comp1206.network.Communicator;
import uk.ac.soton.comp1206.ui.GameWindow;

public class ScoresScene extends BaseScene {

  private static final Logger logger = LogManager.getLogger(ScoresScene.class);
  protected Game game;
  private Communicator communicator;

  private SimpleListProperty localScores = new SimpleListProperty<>();
  private Pair<String, Integer> myScore;

  private Multimedia audioPlayer, musicPlayer;

  /**
   * @param gameWindow the game window this will be displayed in
   * @param game the final game state
   */
  public ScoresScene(GameWindow gameWindow, Game game) {
    super(gameWindow);
    this.communicator = gameWindow.getCommunicator();
    this.game = game;
    logger.info("Creating scores scene");
  }

  @Override
  public void initialise() {
    logger.info("Initialising" + this.getClass().getName());
    audioPlayer.playAudioFile("explode.wav");
    musicPlayer.playMusic("end.wav");
  }

  /**
   * Build the scores layout
   */
  @Override
  public void build() {
  }

}
