package uk.ac.soton.comp1206.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The Grid is a model which holds the state of a game board. It is made up of a set of Integer values arranged in a 2D
 * arrow, with rows and columns.
 *
 * Each value inside the Grid is an IntegerProperty can be bound to enable modification and display of the contents of
 * the grid.
 *
 * The Grid contains functions related to modifying the model, for example, placing a piece inside the grid.
 *
 * The Grid should be linked to a GameBoard for it's display.
 */
public class Grid {

    private static final Logger logger = LogManager.getLogger(Grid.class);

    /**
     * The number of columns in this grid
     */
    private final int cols;

    /**
     * The number of rows in this grid
     */
    private final int rows;

    /**
     * The grid is a 2D arrow with rows and columns of SimpleIntegerProperties.
     */
    private final SimpleIntegerProperty[][] grid;

    /**
     * Create a new Grid with the specified number of columns and rows and initialise them
     * @param cols number of columns
     * @param rows number of rows
     */
    public Grid(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create the grid itself
        grid = new SimpleIntegerProperty[cols][rows];

        //Add a SimpleIntegerProperty to every block in the grid
        for(var y = 0; y < rows; y++) {
            for(var x = 0; x < cols; x++) {
                grid[x][y] = new SimpleIntegerProperty(0);
            }
        }
    }

    /**
     * Get the Integer property contained inside the grid at a given row and column index. Can be used for binding.
     * @param x column
     * @param y row
     * @return the IntegerProperty at the given x and y in this grid
     */
    public IntegerProperty getGridProperty(int x, int y) {
        return grid[x][y];
    }

    /**
     * Update the value at the given x and y index within the grid
     * @param x column
     * @param y row
     * @param value the new value
     */
    public void set(int x, int y, int value) {
        grid[x][y].set(value);
    }

    /**
     * Get the value represented at the given x and y index within the grid
     * @param x column
     * @param y row
     * @return the value
     */
    public int get(int x, int y) {
        try {
            //Get the value held in the property at the x and y index provided
            return grid[x][y].get();
        } catch (ArrayIndexOutOfBoundsException e) {
            //No such index
            return -1;
        }
    }

    /**
     * Get the number of columns in this game
     * @return number of columns
     */
    public int getCols() {
        return cols;
    }

    /**
     * Get the number of rows in this game
     * @return number of rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * Check whether a piece can be played in the grid at the given x,y
     * @param piece the piece to play
     * @param placeX placement X
     * @param placeY placement Y
     * @return whether the piece can be played or not
     */
    public boolean canPlayPiece(GamePiece piece, int placeX, int placeY) {
        logger.info("Checking if we can play the piece {} at {}, {}", piece, placeX, placeY);

        int topX = placeX - 1; // Adjusting to make sure pieces are played from their center
        int topY = placeY - 1;

        int[][] blocks = piece.getBlocks(); // 2D array representation of the array

        for (var blockX = 0; blockX < blocks.length; blockX++) {
            for (var blockY = 0; blockY < blocks.length; blockY++) {
                // BlockX and blockY coordinate inside the blocks 3x3 array
                var blockValue = blocks[blockX][blockY];
                if (blockValue > 0) {
                    // Checking if we can place this block on our grid
                    var gridValue = get(topX + blockX, topY + blockY);
                    if (gridValue != 0) {
                        logger.info("Unable to place piece, conflict at {}, {}",
                            placeX+blockX, placeY+blockY);
                        return false;
                    }
                }
            }
        }
        // Nothing in the way
        return true;
    }

    /**
     * Play a piece by updating the grid with the piece blocks
     * @param piece the piece to play
     * @param placeX placement X
     * @param placeY placement Y
     */
    public void playPiece(GamePiece piece, int placeX, int placeY) {
        logger.info("Playing the piece {} at {}, {}", piece, placeX, placeY);

        int topX = placeX - 1; // Adjusting to make sure pieces are played from their center
        int topY = placeY - 1;

        int value = piece.getValue(); // colour of the piece
        int[][] blocks = piece.getBlocks(); // 2D array representation of the array

        // Return if we can't play the piece
        if (!canPlayPiece(piece, placeX, placeY)) return;

        for (var blockX = 0; blockX < blocks.length; blockX++) {
            for (var blockY = 0; blockY < blocks.length; blockY++) {
                // BlockX and blockY coordinate inside the blocks 3x3 array
                var blockValue = blocks[blockX][blockY];
                if (blockValue > 0) {
                    set(topX + blockX, topY + blockY, value);
                }
            }
        }
    }

    public void clear() {
        for (var x = 0; x < getCols(); x++) {
            for (var y = 0; y < getRows(); y++) {
                set(x,y,0);
            }
        }
    }

}
