package uk.ac.soton.comp1206.scene;

import javafx.scene.layout.GridPane;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.GameWindow;

public class InstructionsScene extends GridPane {
  private static final Logger logger = LogManager.getLogger(InstructionsScene.class);

  /**
   * Create a new instructions scene
   * @param gameWindow the gameWindow
   */
  public InstructionsScene(GameWindow gameWindow) {
//    super(gameWindow);
    logger.info("Creating instructions scene");
  }
}
