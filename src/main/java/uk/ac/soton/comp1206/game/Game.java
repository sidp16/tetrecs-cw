package uk.ac.soton.comp1206.game;

import java.util.HashSet;
import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {
    private Random random = new Random();
    private static final Logger logger = LogManager.getLogger(Game.class);

    /**
     * Number of rows
     */
    protected final int rows;

    /**
     * Number of columns
     */
    protected final int cols;

    /**
     * The grid model linked to the game
     */
    protected final Grid grid;
    private GamePiece currentPiece;

    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     * @param cols number of columns
     * @param rows number of rows
     */
    public Game(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;

        //Create a new grid model to represent the game state
        this.grid = new Grid(cols,rows);
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        nextPiece();
    }

    /**
     * Handle what should happen when a particular block is clicked
     * @param gameBlock the block that was clicked
     */
    public void blockClicked(GameBlock gameBlock) {
        //Get the position of this block
        int x = gameBlock.getX();
        int y = gameBlock.getY();

        // Plays the piece in the x, y clicked by the user
        if (grid.canPlayPiece(currentPiece, x,y)) {
            // Can play the piece
            grid.playPiece(currentPiece, x, y);
            nextPiece();
            afterPiece();
        } else {
            // Cannot play the piece
        }
    }

    public void afterPiece() {
        // Check if we need to clear any lines
        for (var x = 0; x < cols; x++) {
            var counter = 0;
            var columnToClear = 0;
            for (var y = 0; y < rows; y++) {
                // Vertical line check
                if (grid.get(x,y) == 0) break;
                counter++;
                columnToClear = x;
            }
            if (counter == rows) {
                logger.info("Column {} to be cleared", columnToClear);
                // Clear vertical line
            }
        }
        for (var y = 0; y < cols; y++) {
            var counter = 0;
            var rowToClear = 0;
            for (var x = 0; x < rows; x++) {
                // Horizontal line check
                if (grid.get(x,y) == 0) break;
                counter++;
                rowToClear = y;
            }
            if (counter == cols) {
                logger.info("Row {} to be cleared", rowToClear);
                // Clear horizontal line
            }
        }
    }

//    public void clearColumn(x) {
        // Set all values in that column to 0 (i.e empty)
        // Sort out any scoring that is needed
//    }

//    public void clearRow(y) {
        // Set all values in that row to 0 (i.e empty)
        // Sort out any scoring that is needed
//    }

    /**
     * Get the grid model inside this game representing the game state of the board
     * @return game grid model
     */
    public Grid getGrid() {
        return grid;
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

    public GamePiece nextPiece() {
        currentPiece = spawnPiece();
        logger.info("The next piece is: {}", currentPiece);
        return currentPiece;
    }
    public GamePiece spawnPiece() {
        var maxPieces = GamePiece.PIECES;
        var randomPiece = random.nextInt(maxPieces);
        logger.info("Picking random piece: {}", randomPiece);
        var piece = GamePiece.createPiece(randomPiece);
        return piece;
    }

}
