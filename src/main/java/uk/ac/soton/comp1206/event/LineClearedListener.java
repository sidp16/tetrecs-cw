package uk.ac.soton.comp1206.event;

import java.util.Set;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

public interface LineClearedListener {
  public void setOnLineCleared(Set<GameBlockCoordinate> set);
}
