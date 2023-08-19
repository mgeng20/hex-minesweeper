package myproject.minesweeper;

import java.lang.reflect.Array;
import java.util.*;

import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The hexagonal grid is stored in a 2-dimensional nested array. In the context of this class: -
 * 'size' refers to the number of tiles in the centered column. All size and coordinate calculations
 * are based on this number.
 * 
 * - 'x' refers to the column of a specific tile, with x = 0 on the left side and x = 2 * size - 2
 * on the right side. - 'y' refers to the vertical position of a tile. y = 0 always refers to the
 * first tile in a given column. The highest y coordinate changes depending on the x coordinate, but
 * it changes between size - 1 and 2 * size - 2.
 * 
 * 
 * * x coordinates in a hexagonal grid (size = 5): 2 3 0 1 2 3 4 0 1 2 3 4 0 1 2 3 4 1 2
 * 
 * y coordinates in a hexagonal grid (size = 5): 0 0 0 0 1 1 0 1 1 2 2 1 2 2 3 3 2 3 4
 * 
 * centre tile: (size - 1, size - 1)
 * 
 * This is how the number of cells in a column is calculated: col 0 count = size + 0 (size + x) col
 * 1 count size + 1 (size + x) col 2 count = size + 2 (size + x or size + (2 * size - 2) - x) col 3
 * count = size + 1 (size + (2 * size - 2) - x) col 4 count = size + 0 (size + (2 * size - 2) - x)
 * 
 * In general: count = size + min(x, 2 * size - 2 - x)
 * 
 * Finally, the total number of tiles in a hexagonal grid of size s is 3s^2 - 3s + 1.
 * 
 */

/**
 * Represents the game board for the Minesweeper game.
 */
public class GameBoard extends Pane {

    private int size;
    private List<ArrayList<HexCell>> hexCells;
    private int mineCount;
    private boolean gameStopped;

    /**
     * Constructs a GameBoard instance with the specified difficulty level. A list of cells are
     * generated and random mines are distributed. Cells with no mine are labeled with neighbors
     * mine count.
     *
     * @param difficulty the difficulty level of the game.
     */
    public GameBoard(Difficulty difficulty) {
        switch (difficulty) {
            case EASY:
                this.size = 5;
                this.mineCount = 10;
                break;
            case MEDIUM:
                this.size = 7;
                this.mineCount = 25;
                break;
            case HARD:
                this.size = 9;
                this.mineCount = 60;
                break;
            case EXTREME:
                this.size = 11;
                this.mineCount = 100;
                break;
            default:
                this.size = 7;
                this.mineCount = 25;
                break;
        }
        hexCells = generateCells();
        distributeMines(mineCount);
        labelNeighbors();
    }

    /**
     * Generates the list of hexagonal cells for the game board.
     *
     * @return the list of hexagonal cell columns.
     */
    private List<ArrayList<HexCell>> generateCells() {
        List<ArrayList<HexCell>> list = new ArrayList<ArrayList<HexCell>>();
        for (int col = 0; col < colCount(); col++) {
            int cellCountInCurrCol = cellCountInCol(col);
            ArrayList<HexCell> currCol = new ArrayList<>();
            for (int row = 0; row < cellCountInCurrCol; row++) {
                HexCell newCell = new HexCell(col, row, this);
                currCol.add(newCell);
                getChildren().add(newCell);
            }
            list.add(currCol);
        }
        return list;
    }

    /**
     * Retrieves the list of hexagonal cell columns.
     *
     * @return the list of hexagonal cell columns.
     */
    public List<ArrayList<HexCell>> getCellList() {
        return hexCells;
    }

    /**
     * 
     * @return total number of columns of the board
     */
    public int colCount() {
        return 2 * size - 1;
    }

    /**
     * 
     * @param x - the xth column on the board
     * @return total number of cells in the given column
     */
    public int cellCountInCol(int x) {
        return size + Math.min(x, 2 * size - 2 - x);
    }

    /**
     * 
     * @return the number of cells in the longest column (middle)
     */
    public int highestColCount() {
        return 2 * size - 1;
    }

    /**
     * Distributes mines randomly on the game board.
     *
     * @param totalMines the total number of mines to be distributed.
     */
    public void distributeMines(int totalMines) {
        Random rand = new Random();
        int placedMines = 0;

        while (placedMines < totalMines) {
            int randomX = rand.nextInt(colCount());
            int randomY = rand.nextInt(cellCountInCol(randomX));

            HexCell cell = hexCells.get(randomX).get(randomY);
            if (!cell.hasMine()) {
                cell.addMine();
                placedMines++;
            }
        }
    }

    /**
     * Labels the neighboring cells of each hexagonal cell with the count of neighboring mines. Sets
     * font styles and text colors based on the number of neighboring mines.
     */
    public void labelNeighbors() {
        for (List<HexCell> col : hexCells) {
            for (HexCell cell : col) {
                if (!cell.hasMine()) {
                    int count = cell.neighborMinesCount();
                    if (count > 0) {
                        cell.neighborMineCount.setText(Integer.toString(count));
                        cell.neighborMineCount.setFont(Font.font("Arial", FontWeight.BOLD, 20));
                        if (count == 1) {
                            cell.neighborMineCount.setTextFill(Color.BLUE);
                        } else if (count == 2) {
                            cell.neighborMineCount.setTextFill(Color.GREEN);
                        } else if (count == 3) {
                            cell.neighborMineCount.setTextFill(Color.RED);
                        } else if (count == 4) {
                            cell.neighborMineCount.setTextFill(Color.PURPLE);
                        } else if (count == 5) {
                            cell.neighborMineCount.setTextFill(Color.MAROON);
                        } else {
                            cell.neighborMineCount.setTextFill(Color.TURQUOISE);
                        }

                    }
                }
            }
        }
    }

    /**
     * Retrieves the list of neighboring hexagonal cells for a given hexagonal cell.
     *
     * @param hexCell the hexagonal cell for which to retrieve neighbors.
     * @return a list of neighboring hexagonal cells.
     */
    public List<HexCell> getNeighbors(HexCell hexCell) {
        int col = hexCell.getCol();
        int row = hexCell.getRow();

        int[][] potentialNeighbors = null;

        int[][] centerNeighbors = {{col - 1, row - 1}, {col, row - 1}, {col + 1, row - 1},
                {col - 1, row}, {col + 1, row}, {col, row + 1}};
        int[][] leftNeighbors = {{col - 1, row - 1}, {col, row - 1}, {col + 1, row}, {col - 1, row},
                {col, row + 1}, {col + 1, row + 1}};
        int[][] rightNeighbors = {{col - 1, row}, {col, row - 1}, {col + 1, row - 1},
                {col - 1, row + 1}, {col, row + 1}, {col + 1, row}};

        if (col == size - 1) {
            potentialNeighbors = centerNeighbors;
        } else if (col < size - 1) {
            potentialNeighbors = leftNeighbors;
        } else {
            potentialNeighbors = rightNeighbors;
        }

        List<HexCell> neighbors = Arrays.asList(potentialNeighbors).stream()
                .filter(pos -> isCellValid(pos[0], pos[1])).map(pos -> {
                    return hexCells.get(pos[0]).get(pos[1]);
                }).toList();

        return neighbors;
    }

    /**
     * Checks whether a cell at the given coordinates is valid on the game board.
     *
     * @param col the column index of the cell.
     * @param row the row index of the cell.
     * @return true if the cell is valid, false otherwise.
     */
    public boolean isCellValid(int col, int row) {
        if (col < 0 || col >= colCount() || row >= cellCountInCol(col) || row < 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Reveals a given hexagonal cell and its neighbors if any that has no neighbors with a mine.
     *
     * @param hexCell the hexagonal cell to reveal neighbors for.
     */
    public void revealCell(HexCell cell) {
        if (cell.isRevealed()) {
            return;
        }

        cell.reveal();

        if (cell.hasMine()) {
            cell.getBaseCell().setFill(Color.RED);
            endGame(false);
            Label label = new Label("Game Over");
            label.setFont(Font.font("Arial", 72)); // Set font size
            label.setTextFill(Color.BROWN); // Set text color
            label.setLayoutX(-180);
            label.setLayoutY(-60);
            label.setOpacity(0.7);
            getChildren().add(label);
            setGameToStop();
            return;
        }

        if (!cell.hasNeighborMines()) {
            for (HexCell neighbor : getNeighbors(cell)) {
                if (!neighbor.isRevealed()) {
                    revealCell(neighbor);
                }
            }
        }

        if (checkWin()) {
            endGame(true);
            Label label = new Label("You Win");
            label.setFont(Font.font("Arial", 72)); // Set font size
            label.setTextFill(Color.BROWN); // Set text color
            label.setLayoutX(-150);
            label.setLayoutY(-60);
            label.setOpacity(0.7);
            getChildren().add(label);
            setGameToStop();
            return;
        }
    }

    /**
     * Checks whether the player has won the game.
     *
     * @return true if all non-mine cells are revealed, indicating a win; false otherwise.
     */
    public boolean checkWin() {
        for (ArrayList<HexCell> column : hexCells) {
            for (HexCell cell : column) {
                if (!cell.hasMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Ends the game by revealing all cells or flagging remaining mines.
     *
     * @param win true if the game is won, false if it's lost.
     */
    public void endGame(boolean win) {
        for (ArrayList<HexCell> column : hexCells) {
            for (HexCell cell : column) {
                if (win && cell.hasMine() && !cell.isFlagged()) {
                    cell.flag();
                } else if (!win) {
                    cell.reveal();
                }
            }
        }

    }

    /**
     * Retrieves the size of the game board (number of rows/columns).
     *
     * @return the size of the game board.
     */
    public int getSize() {
        return size;
    }

    /**
     * Retrieves the current count of mines on the game board.
     *
     * @return the count of mines.
     */
    public int getMineCount() {
        return mineCount;
    }

    /**
     * Modifies the mine count on the game board by the given value.
     *
     * @param n the value to modify the mine count by.
     */
    public void setMineCount(int n) {
        mineCount = mineCount + n;
    }

    /**
     * Checks whether the game has been stopped.
     *
     * @return true if the game is stopped, false otherwise.
     */
    public boolean isGameStopped() {
        return gameStopped;
    }

    /**
     * Sets the game to a stopped state.
     */
    public void setGameToStop() {
        gameStopped = true;
    }

}
