package myproject.minesweeper;

import static org.junit.jupiter.api.Assertions.*;
import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HexCellTest {

    private GameBoard board;
    private HexCell hexCell;

    // @BeforeAll
    // public static void setupClass() {
    // Platform.startup(() -> {
    // });
    // }

    @BeforeEach
    public void init() {
        board = new GameBoard(Difficulty.EASY);
        hexCell = new HexCell(5, 5, board);
    }

    @Test
    public void testHexCellInitialization() {
        Platform.runLater(() -> {
            assertEquals(5, hexCell.getCol());
            assertEquals(5, hexCell.getRow());
            assertFalse(hexCell.isFlagged());
            assertFalse(hexCell.isRevealed());
            assertFalse(hexCell.hasMine());
        });
    }

    @Test
    public void testToggleFlag() {
        Platform.runLater(() -> {
            hexCell.toggleFlag();
            assertTrue(hexCell.isFlagged());

            hexCell.toggleFlag();
            assertFalse(hexCell.isFlagged());
        });
    }

    @Test
    public void testRevealCell() {
        Platform.runLater(() -> {
            hexCell.reveal();
            assertTrue(hexCell.isRevealed());
        });
    }

    @Test
    public void testNeighborMineCount() {
        Platform.runLater(() -> {
            for (HexCell neighbor : board.getNeighbors(hexCell)) {
                neighbor.addMine();
            }
            assertEquals(hexCell.neighborMinesCount(), board.getNeighbors(hexCell).size());
        });
    }

    @Test
    public void testAddMine() {
        Platform.runLater(() -> {
            hexCell.addMine();
            assertTrue(hexCell.hasMine());
        });
    }

    @Test
    public void testCellContainsMouseXY() {
        Platform.runLater(() -> {
            assertFalse(hexCell.ifContainsMouseXY(5, 5));
            assertFalse(hexCell.ifContainsMouseXY(-5, -5));
        });
    }

}
