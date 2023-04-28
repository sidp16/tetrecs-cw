package uk.ac.soton.comp1206.component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.event.RightClickedListener;
import uk.ac.soton.comp1206.game.GamePiece;
import uk.ac.soton.comp1206.game.Grid;

/**
 * Used to represent the next piece and the following piece to the user
 */
public class PieceBoard extends GameBoard {

  private static final Logger logger = LogManager.getLogger(PieceBoard.class);

  /**
   * Forced 3x3 as this is the only use for a PieceBoard
   * @param width width
   * @param height height
   */
  public PieceBoard(double width, double height) {
    super(3,3, width, height);
  }

  /**
   * PieceBoard constructor
   * @param cols number of columns
   * @param rows number of rows
   * @param width width
   * @param height height
   */
  public PieceBoard(int cols, int rows, double width, double height) {
    super(cols, rows, width, height);
  }

  /**
   * Uses playPiece to show the piece on the board
   * @param piece the piece to be displayed
   */
  public void displayPiece(GamePiece piece) {
    logger.info("Piece to display: {}", piece.toString());
    grid.playPiece(piece,1,1);
    }

  /**
   * Clears the grid of any pieces to allow for another piece to be shown
   */
    public void clear() {
    for (var x = 0; x < grid.getCols(); x++) {
      for (var y = 0; y < grid.getRows(); y++) {
        grid.set(x,y,0);
      }
    }
    }

}
