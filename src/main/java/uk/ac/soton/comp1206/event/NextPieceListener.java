package uk.ac.soton.comp1206.event;

import uk.ac.soton.comp1206.game.GamePiece;

/**
 * Listens for when a nextPiece is played
 */
public interface NextPieceListener {

  /**
   * Use this to notify of a next piece being played
   * @param nextPiece takes in the next piece
   * @param followingPiece takes in the piece after that
   */
  void nextPiece(GamePiece nextPiece, GamePiece followingPiece);
}
