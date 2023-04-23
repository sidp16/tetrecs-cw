package uk.ac.soton.comp1206.game;

import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Random;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;
import uk.ac.soton.comp1206.scene.ChallengeScene;

/**
 * The Game class handles the main logic, state and properties of the TetrECS game. Methods to manipulate the game state
 * and to handle actions made by the player should take place inside this class.
 */
public class Game {

    /**
     * Random object used to generate the next random piece
     */
    private final Random random = new Random();
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

    /**
     * The current piece that is next to be played
     */
    private GamePiece currentPiece;

    private GamePiece followingPiece;

    private final IntegerProperty score;
    private final IntegerProperty level;
    private final IntegerProperty lives;
    private final IntegerProperty multiplier;

    private NextPieceListener nextPieceListener;
    private LineClearedListener lineClearedListener;

    private Multimedia audioPlayer;


    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public int getLives() {
        return lives.get();
    }

    public IntegerProperty livesProperty() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives.set(lives);
    }

    public int getMultiplier() {
        return multiplier.get();
    }

    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * @param listener any class implementing NextPieceListener is passed
     */
    public void registerNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    public GamePiece getFollowingPiece() {
        return followingPiece;
    }

    /**
     * Calls the nextPiece command specified by the interface
     * @param nextPiece the next piece to be played
     */
    private void notifyNextPieceListener(GamePiece nextPiece, GamePiece followingPiece) {
        if (nextPieceListener != null) {
            nextPieceListener.nextPiece(nextPiece, followingPiece);
        }
    }

    public void setOnLineCleared(LineClearedListener listener) {
        lineClearedListener = listener;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier.set(multiplier);
    }

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

        score = new SimpleIntegerProperty(0);
        level = new SimpleIntegerProperty(0);
        lives = new SimpleIntegerProperty(3);
        multiplier = new SimpleIntegerProperty(1);

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
        followingPiece = spawnPiece();
        nextPiece();
        audioPlayer = new Multimedia();
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
            audioPlayer.playAudioFile("place.wav");
            nextPiece();
            afterPiece();
        } else {
            // Cannot play the piece
            // TODO: Play sound to indicate error?
        }
    }

    public void afterPiece() {
        // Check if we need to clear any lines
        var columnToClear = 0;
        var rowToClear = 0;
        var linesCleared = 0;
        // Using a HashSet to eliminate chance of any duplicates
        HashSet<GameBlockCoordinate> blocksToClear = new HashSet<>();
        for (var x = 0; x < cols; x++) {
            var counter = 0;
            for (var y = 0; y < rows; y++) {
                // Vertical line check
                if (grid.get(x,y) == 0) break;
                counter++; // Increments each time a non-zero value is found in a row
                columnToClear = x; // Logs the column the loop is currently on
            }
            if (counter == rows) {
                logger.info("Column {} to be cleared", columnToClear);
                linesCleared++;
                for (var i = 0; i < 5;i++) {
                    GameBlockCoordinate tempBlock = new GameBlockCoordinate(columnToClear,i);
                    blocksToClear.add(tempBlock);
                }
                logger.info(blocksToClear.toString());
            }
        }
        for (var y = 0; y < cols; y++) {
            var counter = 0;
            for (var x = 0; x < rows; x++) {
                // Horizontal line check
                if (grid.get(x,y) == 0) break;
                counter++; // Increments each time a non-zero value is found in a row
                rowToClear = y; // Logs the row the loop is currently on
            }
            if (counter == cols) {
                logger.info("Row {} to be cleared", rowToClear);
                linesCleared++;
                for (var i = 0; i < 5;i++) {
                    GameBlockCoordinate tempBlock = new GameBlockCoordinate(i,rowToClear);
                    blocksToClear.add(tempBlock);
                }
                logger.info(blocksToClear.toString());
            }
        }
        score(linesCleared, blocksToClear.size());
        checkMultiplier(linesCleared, blocksToClear);
        clearBlocks(blocksToClear);
        changeLevel();
    }

    /**
     * Checks divisibility by 1000, adjusts level depending on which 1000 is reached
     */
    public void changeLevel() {
        int value = score.get() / 1000;
        level.set(value);
    }


    /**
     * Increments multiplier by 1 if > 0, resets if no lines cleared
     * @param linesCleared the number of lines cleared in the turn
     */
    public void checkMultiplier(int linesCleared, HashSet<GameBlockCoordinate> blocksToClear) {
        if (linesCleared > 0) {
            increaseMultiplier();
            if (lineClearedListener != null) {
                lineClearedListener.setOnLineCleared(blocksToClear);
            }
        } else {
            resetMultiplier();
        }
    }

    /**
     * Increments the value of the multiplier
     */
    public void increaseMultiplier() {
        multiplier.set(multiplier.add(1).get());
    }

    /**
     * Resets multiplier to 1
     */
    public void resetMultiplier() {
        multiplier.set(1);
    }
    /**
     * Get the blocks that need to be cleared and set their values to zero
     * @param blocksToClear a set of GameBlockCoordinate objects that need to be cleared
     */
    public void clearBlocks(HashSet<GameBlockCoordinate> blocksToClear) {
        for (GameBlockCoordinate i : blocksToClear) {
            int clearX = i.getX();
            int clearY = i.getY();
            grid.set(clearX,clearY,0);
        }
    }

    /**
     * Adds the calculated score based on the lines and blocks cleared
     * @param numOfLines number of lines cleared
     * @param numOfBlocks number of blocks cleared
     */
    public void score(int numOfLines, int numOfBlocks) {
        var addScore = numOfLines * numOfBlocks * 10 * multiplier.get();
        logger.info("Adding {} points!", addScore);
        score.set(score.add(addScore).get());
    }

    public void rotateCurrentPiece() {
        currentPiece.rotate();
        logger.info("{} has been rotated", currentPiece.toString());
    }
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
        currentPiece = followingPiece; // Once the
        followingPiece = spawnPiece();
        logger.info("The next piece is: {}", currentPiece);
        notifyNextPieceListener(currentPiece, followingPiece);
        return currentPiece;
    }
    public GamePiece spawnPiece() {
        var maxPieces = GamePiece.PIECES;
        var randomPiece = random.nextInt(maxPieces);
        logger.info("Picking random piece: {}", randomPiece);
        var piece = GamePiece.createPiece(randomPiece);
        return piece;
    }

    public void swapCurrentPiece() {
        GamePiece tempPiece = currentPiece; // Store current piece in a temporary variable
        currentPiece = followingPiece; // Set current piece to the following piece
        followingPiece = tempPiece; // Set following piece to the temporary variable
    }
}

