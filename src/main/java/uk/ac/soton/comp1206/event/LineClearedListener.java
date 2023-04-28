package uk.ac.soton.comp1206.event;

import java.util.Set;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * Listens for any lines being cleared fully
 */
public interface LineClearedListener {

  /**
   * @param set takes in gameBlock coords set
   */
  public void setOnLineCleared(Set<GameBlockCoordinate> set);
}
