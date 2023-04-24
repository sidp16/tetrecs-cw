package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

public class PieceBoard extends GameBoard {

  private static final Logger logger = LogManager.getLogger(PieceBoard.class);

  public PieceBoard(double width, double height) {
    super(3,3, width, height);
  }

  public PieceBoard(int cols, int rows, double width, double height) {
    super(cols, rows, width, height);
  }

  public void displayPiece(GamePiece piece) {
    logger.info("Piece to display: {}", piece.toString());
    grid.playPiece(piece,1,1);
    }

    public void clear() {
    for (var x = 0; x < grid.getCols(); x++) {
      for (var y = 0; y < grid.getRows(); y++) {
        grid.set(x,y,0);
      }
    }
    }

}
