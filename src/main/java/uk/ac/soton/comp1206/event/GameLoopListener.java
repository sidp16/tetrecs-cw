package uk.ac.soton.comp1206.event;

/**
 * Listens for when the game needs to be looped
 */
public interface GameLoopListener {

  /**
   * Listens for gameLoops
   * @param n number
   */
  void setOnGameLoop(int n);
}
