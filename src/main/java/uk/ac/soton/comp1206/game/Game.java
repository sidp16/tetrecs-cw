package uk.ac.soton.comp1206.game;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ac.soton.comp1206.ui.Multimedia;
import uk.ac.soton.comp1206.component.GameBlock;
import uk.ac.soton.comp1206.component.GameBlockCoordinate;
import uk.ac.soton.comp1206.event.GameLoopListener;
import uk.ac.soton.comp1206.event.GameOverListener;
import uk.ac.soton.comp1206.event.LineClearedListener;
import uk.ac.soton.comp1206.event.NextPieceListener;

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
     * The current piece and the next piece to be played
     */
    private GamePiece currentPiece, followingPiece;

    private ScheduledExecutorService timer = null;

    /**
     * Score variable
     */
    private final IntegerProperty score;
    /**
     * Level variable
     */
    private final IntegerProperty level;
    /**
     * Lives variable
     */
    private final IntegerProperty lives;
    /**
     * Muiltplier variable
     */
    private final IntegerProperty multiplier;

    private NextPieceListener nextPieceListener;
    private LineClearedListener lineClearedListener;
    private GameLoopListener gameLoopListener = null;
    private GameOverListener gameOverListener;

    /**
     * Play sounds when events have occured
     */
    private Multimedia audioPlayer;
    /**
     * Game loop
     */
    private ScheduledFuture loop;
    /**
     * To track whether a level up has occured
     */
    private int oldLevel;

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }

    /**
     * ArrayList of all local scores
     */
    public ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

    public void setScore(int score) {
        this.score.set(score);
    }

    /**
     * Gets the current level
     * @return the current level is returned
     */
    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(int level) {
        this.level.set(level);
    }

    public IntegerProperty livesProperty() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives.set(lives);
    }

    public IntegerProperty multiplierProperty() {
        return multiplier;
    }

    /**
     * Registers a class wanting to listen to if a next piece is played
     * @param listener any class implementing NextPieceListener is passed
     */
    public void registerNextPieceListener(NextPieceListener listener) {
        this.nextPieceListener = listener;
    }

    /**
     * @return returns the current piece
     */
    public GamePiece getCurrentPiece() {
        return currentPiece;
    }

    /**
     * Returns the gamePiece object, followingPiece
     * @return returns the following piece
     */
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

    /**
     * Listens for the end of the timer
     */
    private void gameLoopListener() {
        if (gameLoopListener != null) {
            gameLoopListener.setOnGameLoop(getTimerDelay());
        }
    }

    /**
     * Listens for the next piece
     * @param listener next piece listener
     */
    public void setOnGameLoop(GameLoopListener listener) {
        gameLoopListener = listener;
    }

    /**
     * Listens for line cleared
     * @param listener cleared line listener
     */
    public void setOnLineCleared(LineClearedListener listener) {
        lineClearedListener = listener;
    }

    /**
     * Sets the multiplier to a specific value
     * @param multiplier sets the multiplier to the value passed into this parameter
     */
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

        timer = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Start the game
     */
    public void start() {
        logger.info("Starting game");
        initialiseGame();
        loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
        gameLoopListener();
    }

    /**
     * Ends the game safely by shutting down the timer
     */
    public void stop() {
        logger.info("Ending game");
        this.timer.shutdownNow();
    }

    /**
     * Initialise a new game and set up anything that needs to be done at the start
     */
    public void initialiseGame() {
        logger.info("Initialising game");
        this.followingPiece = spawnPiece();
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
            afterPiece();
            nextPiece();
            loop.cancel(false);
            loop = timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
            gameLoopListener();
            logger.info("Timer reset!");
        } else {
            // Cannot play the piece
            audioPlayer.playAudioFile("fail.wav");
        }
    }

    /**
     * Handles the checks of if lines need to be cleared and calls any necessary methods
     * to continue the game such as scoring
     */
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
        levelSounds(level.get());
    }

    /**
     * Checks divisibility by 1000, adjusts level depending on which 1000 is reached
     */
    public void changeLevel() {
        int value = score.get() / 1000;
        level.set(value);
    }

    /**
     * Stops the timer
     */
    public void stopTimer() {
        this.timer.shutdownNow();
    }

    /**
     * Plays an audio file indicating a level up
     * @param currentLevel takes in the current level the user is on
     */
    public void levelSounds(int currentLevel) {
        if (currentLevel != oldLevel) {
            logger.info("Leveled up");
            audioPlayer.playAudioFile("level.wav");
            oldLevel = currentLevel;
        }
    }


    /**
     * Increments multiplier by 1 if > 0, resets if no lines cleared
     * @param linesCleared the number of lines cleared in the turn
     * @param blocksToClear the number of blocks to clear
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
     * Clear the whole grid with 500 points
     */
    public void clearAll() {
        if (score.get() >= 400) {
            score.set(score.get() - 400);
            grid.clear();
            multiplier.set(multiplier.add(1).get());
            audioPlayer.playAudioFile("explode.wav");
            logger.info("Grid cleaned");
        } else {
            audioPlayer.playAudioFile("fail.wav");
            logger.info("Not enough points");
        }
    }

    public void addLife() {
        if (score.get() >= 500) {
            score.set(score.get() - 500);
            lives.set(lives.get() + 1);
            audioPlayer.playAudioFile("lifegain.wav");
            logger.info("1 Life added");
        } else {
            audioPlayer.playAudioFile("fail.wav");
            logger.info("Not enough points");
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
        logger.info("All necessary blocks cleared");
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

    /**
     * Rotates the current piece 90 degrees clockwise
     * @param num number of rotations
     */
    public void rotateCurrentPiece(int num) {
        currentPiece.rotate(num);
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

    /**
     * Replace the current piece with the next piece, while generating a new piece
     * for the followingPiece
     */
    public void nextPiece() {
        currentPiece = followingPiece;
        followingPiece = spawnPiece();
        logger.info("The next piece is: {}", currentPiece);
        notifyNextPieceListener(currentPiece, followingPiece);
    }

    /**
     * Spawns a piece randomly
     * @return a GamePiece object, after creating a piece
     */
    public GamePiece spawnPiece() {
        var maxPieces = GamePiece.PIECES;
        var randomPiece = random.nextInt(maxPieces);
        logger.info("Picking random piece: {}", randomPiece);
        var piece = GamePiece.createPiece(randomPiece);
        return piece;
    }

    /**
     * Swaps the current piece with the next piece
     */
    public void swapCurrentPiece() {
        GamePiece tempPiece = currentPiece; // Store current piece in a temporary variable
        currentPiece = followingPiece; // Set current piece to the following piece
        followingPiece = tempPiece; // Set following piece to the temporary variable
        logger.info("{} and {} have been swapped", currentPiece, followingPiece);
    }

    /**
     * Calculates the timerDelay
     * @return the timer length dependent on which level you are currently on
     */
    public int getTimerDelay() {
        int delay;
        delay = 12000 - (500 * this.getLevel());
        if (delay <= 2500) {
            delay = 2500;
        }
        return delay;
    }

    /**
     * Removes a life from the user
     */
    public void removeLife() {
        lives.set(lives.get() - 1);
        logger.info("Lost a life");
    }

    /**
     * Checks if user is alive
     * @return returns a boolean, true if lives >= 0 and false if below 0
     */
    public boolean isAlive() {
        boolean alive;
        if (lives.get() > 0) {
            alive = true;
        } else {
            alive = false;
            logger.info("Game over");
            if (gameOverListener != null) {
                Platform.runLater(() -> gameOverListener.setOnGameOver());
            }
        }
        return alive;
    }

    /**
     * Loop through certain events when the timer ends
     */
    public void gameLoop() {
        if (isAlive()) removeLife();
        nextPiece();
        audioPlayer.playAudioFile("lifelose.wav");
        setMultiplier(1);
        gameLoopListener();
        loop = this.timer.schedule(this::gameLoop, getTimerDelay(), TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the listener
     * @param listener listener from another class
     */
    public void setOnGameOver(GameOverListener listener) {
        gameOverListener = listener;
    }
}

