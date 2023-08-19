package myproject.minesweeper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class GameBoardTest {
    private GameBoard board;

    @BeforeAll
    public static void setup() {
        Platform.startup(() -> {
        });
    }



    @Test
    public void testInitialMineCountForEasyDifficulty() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertEquals(10, board.getMineCount());
        });
    }

    @Test
    public void testInitialMineCountForMediumDifficulty() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.MEDIUM);
            assertEquals(25, board.getMineCount());
        });
    }

    @Test
    public void testInitialMineCountForHardDifficulty() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.HARD);
            assertEquals(60, board.getMineCount());
        });
    }

    @Test
    public void testInitialMineCountForExtremeDifficulty() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EXTREME);
            assertEquals(100, board.getMineCount());
        });
    }

    @Test
    public void testIsCellValidForInvalidCells() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertFalse(board.isCellValid(-1, 0));
            assertFalse(board.isCellValid(0, -1));
            assertFalse(board.isCellValid(board.colCount(), 0));
            assertFalse(board.isCellValid(0, board.highestColCount()));
        });
    }

    @Test
    public void testIsCellValidForValidCells() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertTrue(board.isCellValid(0, 0));
            assertTrue(board.isCellValid(1, 1));
        });
    }

    @Test
    public void testGetNeighborsForCentralCell() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            int expectedNeighbors = 6;
            HexCell centralCell = board.getCellList().get(2).get(2);
            assertEquals(expectedNeighbors, board.getNeighbors(centralCell).size());
        });
    }

    @Test
    public void testGetNeighborsForTopLeftCell() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            HexCell topLeftCell = board.getCellList().get(0).get(0);
            assertTrue(board.getNeighbors(topLeftCell).size() <= 6);
        });
    }

    @Test
    public void testWinningGame() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            for (ArrayList<HexCell> column : board.getCellList()) {
                for (HexCell cell : column) {
                    if (!cell.hasMine()) {
                        board.revealCell(cell);
                    }
                }
            }

            boolean hasWinLabel = board.getChildren().stream().anyMatch(
                    child -> child instanceof Label && ((Label) child).getText().equals("You Win"));
            assertTrue(hasWinLabel);
        });
    }

    @Test
    public void testColCount() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertEquals(9, board.colCount());
        });
    }

    @Test
    public void testCellCountInCol() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertEquals(5, board.cellCountInCol(0));
            assertEquals(6, board.cellCountInCol(1));
            assertEquals(7, board.cellCountInCol(2));
            assertEquals(8, board.cellCountInCol(3));
            assertEquals(9, board.cellCountInCol(4));
        });
    }

    @Test
    public void testHighestColCount() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertEquals(9, board.highestColCount());
        });
    }

    @Test
    public void testRevealCell() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            HexCell cellToReveal = board.getCellList().get(2).get(2);
            board.revealCell(cellToReveal);
            assertTrue(cellToReveal.isRevealed());
        });
    }

    @Test
    public void testEndGameWithWin() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            for (ArrayList<HexCell> column : board.getCellList()) {
                for (HexCell cell : column) {
                    if (!cell.hasMine()) {
                        cell.reveal();
                    }
                }
            }
            board.endGame(true);
            assertTrue(board.isGameStopped());
        });
    }

    @Test
public void testEndGameWithLoss() {
    Platform.runLater(() -> {
        board = new GameBoard(Difficulty.EASY);
        HexCell mineCell = board.getCellList().stream()
                .flatMap(column -> column.stream()) 
                .filter(HexCell::hasMine)
                .findFirst()
                .orElse(null);

        if (mineCell != null) {
            board.revealCell(mineCell);
            board.endGame(false);
            assertTrue(mineCell.isRevealed());
            assertTrue(board.isGameStopped());
        }
    });
}


    @Test
    public void testCheckWin() {
        Platform.runLater(() -> {
            board = new GameBoard(Difficulty.EASY);
            assertFalse(board.checkWin());
            for (ArrayList<HexCell> column : board.getCellList()) {
                for (HexCell cell : column) {
                    if (!cell.hasMine()) {
                        cell.reveal();
                    }
                }
            }
            assertTrue(board.checkWin());
        });
    }
    
}
