package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Listens for right click
 */
public interface RightClickedListener {

  /**
   * Call to notify of a right click
   */
  void onRightClicked();
}
