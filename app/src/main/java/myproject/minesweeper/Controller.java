package myproject.minesweeper;

import java.util.*;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 * The controller class responsible for handling user interactions and managing the game interface.
 */
public class Controller {

    /**
     * The default game difficulty level is set to medium.
     */

    private Difficulty gameDifficulty = Difficulty.MEDIUM;

    private int seconds = 0;

    @FXML
    private Label countDisplay;

    @FXML
    private Label timerLabel;

    private Timeline timeline;

    @FXML
    private VBox root;

    @FXML
    public Pane boardContainer;

    private GameBoard gameBoard;

    @FXML
    private Button restartButton;

    @FXML
    public ComboBox<String> difficultyDropdown;

    /**
     * Initializes the game interface and sets up the initial game state.
     */
    @FXML
    public void initialize() {
        gameBoard = new GameBoard(gameDifficulty);
        resetTimer();
        gameBoard.setLayoutX(400);
        gameBoard.setLayoutY(325);
        boardContainer.getChildren().add(gameBoard);
        boardContainer.getChildren().add(timerLabel);
        timerLabel.setLayoutX(360);
        boardContainer.setOnMouseClicked(event -> handleHexagonClick(event, gameBoard));
        difficultyDropdown.getItems().add("easy");
        difficultyDropdown.getItems().add("medium");
        difficultyDropdown.getItems().add("hard");
        difficultyDropdown.getItems().add("extreme");
        countDisplay = new Label(Integer.toString(gameBoard.getMineCount()));
        countDisplay.setFont(Font.font("Arial", 30));
        countDisplay.setLayoutX(10);
        boardContainer.getChildren().add(countDisplay);
    }

    /**
     * Restarts the game by resetting the game board and timer.
     */
    public void restartGame() {
        boardContainer.getChildren().remove(gameBoard);
        gameBoard = new GameBoard(gameDifficulty);
        resetTimer();
        timerLabel.setLayoutX(360);
        displayMineCount(gameBoard.getMineCount());
        gameBoard.setLayoutX(400);
        gameBoard.setLayoutY(325);
        boardContainer.getChildren().add(gameBoard);
    }

    /**
     * Sets the game difficulty based on the selected option in the difficulty dropdown. Restarts
     * the game with the new difficulty.
     */
    public void setDifficulty() {
        String difficultySelected = difficultyDropdown.getSelectionModel().getSelectedItem();
        if (difficultySelected == "easy") {
            gameDifficulty = Difficulty.EASY;
        }
        if (difficultySelected == "medium") {
            gameDifficulty = Difficulty.MEDIUM;
        }
        if (difficultySelected == "hard") {
            gameDifficulty = Difficulty.HARD;
        }
        if (difficultySelected == "extreme") {
            gameDifficulty = Difficulty.EXTREME;
        }
        restartGame();
    }

    /**
     * Handles the user's click on a hexagonal cell on the game board. Reveals the cell or toggles a
     * flag depending on the mouse button clicked.
     *
     * @param event the MouseEvent representing the user's click.
     * @param gameBoard the GameBoard instance representing the game board.
     */
    public void handleHexagonClick(MouseEvent event, GameBoard gameBoard) {
        double mouseX = event.getX() - gameBoard.getLayoutX();
        double mouseY = event.getY() - gameBoard.getLayoutY();
        for (List<HexCell> col : gameBoard.getCellList()) {
            for (HexCell cell : col) {
                if (cell.ifContainsMouseXY(mouseX, mouseY)) {
                    if (event.getButton() == MouseButton.PRIMARY) {
                        gameBoard.revealCell(cell);
                        if (gameBoard.isGameStopped()) {
                            timeline.stop();
                        }
                    } else if (event.getButton() == MouseButton.SECONDARY) {
                        cell.toggleFlag();
                        displayMineCount(gameBoard.getMineCount());
                    }
                }
            }
        }
    }

    /**
     * Displays the current mine count on the game interface.
     *
     * @param mineCount the current number of remaining mines.
     */
    public void displayMineCount(int mineCount) {
        countDisplay.setText(Integer.toString(mineCount));
    }

    /**
     * Resets the timer to zero and starts counting elapsed time.
     */
    public void resetTimer() {
        if (timeline != null) {
            timeline.stop();
        }
        seconds = 0;
        timerLabel.setText("00:00");
        timerLabel.setLayoutX(400);
        timerLabel.setFont(Font.font("Arial", 30));
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            seconds++;
            timerLabel.setText(convertToMinuteSeconds(seconds));
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    /**
     * Converts a time in seconds to a formatted string in "mm:ss" format.
     *
     * @param seconds the time in seconds.
     * @return the formatted time string.
     */
    public String convertToMinuteSeconds(int seconds) {
        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%02d:%02d", minutes, remainingSeconds);
    }

}

