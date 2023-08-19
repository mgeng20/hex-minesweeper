package myproject.minesweeper;

import java.util.*;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * Represents a hexagonal cell in a Minesweeper game.
 */

public class HexCell extends Pane {
    private int col;
    private int row;
    GameBoard gameBoard;
    private final int RADIUS = 15;
    private boolean hasMine;
    private boolean isRevealed;
    private boolean isFlagged;
    private Polygon baseCell;
    private Polygon cellCover;
    @FXML
    public Label neighborMineCount;
    @FXML
    static Image mineImg = null;
    @FXML
    private ImageView mineImgView;
    @FXML
    static Image flagImg = null;
    @FXML
    private ImageView flagImgView;

    /**
     * Gets the mine image. If the mine image hasn't been loaded yet, it loads it from the resource
     * file.
     *
     * @return the mine image.
     */
    public static Image getMineImg() {
        if (mineImg == null) {
            mineImg = new Image(HexCell.class.getResource("mine.png").toString(), 20, 20, false,
                    false);
        }
        return mineImg;
    }

    /**
     * Gets the flag image. If the flag image hasn't been loaded yet, it loads it from the resource
     * file.
     *
     * @return the flag image.
     */
    public static Image getFlagImg() {
        if (flagImg == null) {
            flagImg = new Image(HexCell.class.getResource("flag.png").toString(), 20, 20, false,
                    false);
        }
        return flagImg;
    }

    /**
     * Creates a hexagon shape with the specified center coordinates.
     *
     * @param centerX the x-coordinate of the center.
     * @param centerY the y-coordinate of the center.
     * @return the hexagon shape.
     */
    private Polygon createHexagon(double centerX, double centerY) {
        Polygon hexagon = new Polygon();

        for (int i = 0; i < 6; i++) {
            double angle = 2.0 * Math.PI / 6 * i;
            double x = centerX + RADIUS * Math.cos(angle);
            double y = centerY + RADIUS * Math.sin(angle);
            hexagon.getPoints().addAll(x, y);
        }

        hexagon.setFill(Color.LIGHTGRAY);
        hexagon.setStroke(Color.BLACK);
        hexagon.setStrokeWidth(1);

        return hexagon;
    }

    /**
     * Creates a base cell polygon with the specified center coordinates and color.
     *
     * @param centerX the x-coordinate of the center.
     * @param centerY the y-coordinate of the center.
     * @return the base cell polygon.
     */
    public Polygon createBaseCell(double centerX, double centerY) {
        Polygon baseCell = createHexagon(centerX, centerY);
        baseCell.setFill(Color.LIGHTGRAY);
        baseCell.setStroke(Color.BLACK);
        baseCell.setStrokeWidth(1);
        return baseCell;
    }

    /**
     * Creates a cell cover polygon with the specified center coordinates and color.
     *
     * @param centerX the x-coordinate of the center.
     * @param centerY the y-coordinate of the center.
     * @return the cell cover polygon.
     */
    public Polygon createCellCover(double centerX, double centerY) {
        Polygon cellCover = createHexagon(centerX, centerY);
        cellCover.setFill(Color.LIGHTBLUE);
        cellCover.setStroke(Color.BLACK);
        cellCover.setStrokeWidth(1);
        return cellCover;
    }

    /**
     * Creates a new hexagonal cell with the specified column, row, and game board. The HexCell is
     * an extension from javafx Panel and it adds all the components to itself. Mine imageview and
     * flag imageview remain invisible until the cell is flagged or has mine placed.
     *
     * @param col the column index of the cell.
     * @param row the row index of the cell.
     * @param gameBoard the game board the cell belongs to.
     */
    public HexCell(int col, int row, GameBoard gameBoard) {
        this.col = col;
        this.row = row;
        this.gameBoard = gameBoard;
        double[] actualXY = getActualCoordinates(col, row);

        baseCell = createBaseCell(actualXY[0], actualXY[1]);
        cellCover = createCellCover(actualXY[0], actualXY[1]);

        neighborMineCount = new Label("");
        neighborMineCount.setLayoutX(actualXY[0] - RADIUS * 0.4);
        neighborMineCount.setLayoutY(actualXY[1] - RADIUS * 0.8);
        mineImgView = new ImageView(getMineImg());
        mineImgView.setLayoutX(actualXY[0] - RADIUS * 0.70);
        mineImgView.setLayoutY(actualXY[1] - RADIUS * 0.60);
        mineImgView.setVisible(false);
        flagImgView = new ImageView(getFlagImg());
        flagImgView.setLayoutX(actualXY[0] - RADIUS / 2);
        flagImgView.setLayoutY(actualXY[1] - RADIUS / 2);
        flagImgView.setVisible(false);

        getChildren().add(baseCell);
        getChildren().add(neighborMineCount);
        getChildren().add(mineImgView);
        getChildren().add(cellCover);
        getChildren().add(flagImgView);
    }

    /**
     * Calculates the actual coordinates of the cell based on its column and row.
     *
     * @param col the column index of the cell.
     * @param row the row index of the cell.
     * @return an array containing the actual x and y coordinates.
     */
    public double[] getActualCoordinates(int col, int row) {

        double xCoordinate = col * (getHexWidth() / 2 + getSideLength() / 2);
        double yCoordinate = (row * 2 + Math.abs(gameBoard.getSize() - 1 - col)) * getHexApothem();

        double actualX = xCoordinate - getXOffset();
        double actualY = yCoordinate - getYOffset();

        return new double[] {actualX, actualY};
    }

    /**
     * Calculates the x-offset of the cell based on the game board size.
     *
     * @return the x-offset.
     */
    public double getXOffset() {
        return (gameBoard.getSize() - 1) * (RADIUS + getSideLength() / 2);
    }

    /**
     * Calculates the y-offset of the cell based on the game board size.
     *
     * @return the y-offset.
     */
    public double getYOffset() {
        return (gameBoard.getSize() - 1) * getHexApothem() * 2;
    }

    /**
     * Calculates the length of the sides of the hexagon.
     *
     * @return the side length.
     */
    public double getSideLength() {
        return 2 * RADIUS * Math.sin(Math.PI / 6);
    }

    /**
     * Calculates the width of the hexagon.
     *
     * @return the hexagon width.
     */
    public double getHexWidth() {
        return 2 * RADIUS;
    }

    /**
     * Calculates the apothem of the hexagon.
     *
     * @return the hexagon apothem.
     */
    public double getHexApothem() {
        return RADIUS * Math.cos(Math.PI / 6);
    }

    /**
     * Determines if the cell contains a mine.
     *
     * @return true if the cell contains a mine, false otherwise.
     */
    public boolean hasMine() {
        return hasMine;
    }

    /**
     * Checks if the cell has been revealed.
     *
     * @return true if the cell has been revealed, false otherwise.
     */
    public boolean isRevealed() {
        return isRevealed;
    }

    /**
     * Checks if the cell is flagged.
     *
     * @return true if the cell is flagged, false otherwise.
     */
    public boolean isFlagged() {
        return isFlagged;
    }

    /**
     * Counts the number of neighboring cells with mines.
     *
     * @return the count of neighboring cells with mines.
     */
    public int neighborMinesCount() {
        int count = 0;
        for (HexCell neighbor : gameBoard.getNeighbors(this)) {
            if (neighbor.hasMine()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if the cell has neighboring cells with mines.
     *
     * @return true if the cell has neighboring cells with mines, false otherwise.
     */
    public boolean hasNeighborMines() {
        return neighborMinesCount() > 0;
    }

    /**
     * Adds a mine to the cell and makes the mine imageview visible.
     */
    public void addMine() {
        hasMine = true;
        mineImgView.setVisible(true);
    }

    /**
     * Flags the cell.
     */
    public void flag() {
        if (gameBoard.getMineCount() <= 0) {
            return;
        }
        isFlagged = true;
        flagImgView.setVisible(true);
        gameBoard.setMineCount(-1);
    }

    /**
     * Unflags the cell.
     */
    public void unflag() {
        isFlagged = false;
        flagImgView.setVisible(false);
        gameBoard.setMineCount(1);

    }

    /**
     * Toggles the cell's flag state.
     */
    public void toggleFlag() {
        if (isRevealed()) {
            return;
        }
        if (isFlagged()) {
            unflag();

        } else {
            flag();
        }
    }

    /**
     * Reveals the cell.
     */
    public void reveal() {
        if (isRevealed() || isFlagged()) {
            return;
        }
        isRevealed = true;
        cellCover.setVisible(false);
    }

    /**
     * Gets the ImageView displaying the mine image.
     *
     * @return the ImageView displaying the mine image.
     */
    public ImageView getMineImgView() {
        return mineImgView;
    }

    /**
     * Gets the cell's cover polygon.
     *
     * @return the cell's cover polygon.
     */
    public Polygon getCellCover() {
        return cellCover;
    }

    /**
     * Gets the basecell polygon.
     *
     * @return the basecell polygon.
     */
    public Polygon getBaseCell() {
        return baseCell;
    }

    /**
     * Gets the column index of the cell.
     *
     * @return the column index of the cell.
     */
    public int getCol() {
        return col;
    }

    /**
     * Gets the row index of the cell.
     *
     * @return the row index of the cell.
     */
    public int getRow() {
        return row;
    }

    /**
     * Gets the label displaying the count of neighboring mines.
     *
     * @return the label displaying the count of neighboring mines.
     */
    public Label getNeighborMineCount() {
        return neighborMineCount;
    }

    /**
     * Checks if the cell's base polygon contains the given mouse coordinates.
     *
     * @param mouseX the x-coordinate of the mouse.
     * @param mouseY the y-coordinate of the mouse.
     * @return true if the cell's base polygon contains the mouse coordinates, false otherwise.
     */
    public boolean ifContainsMouseXY(double mouseX, double mouseY) {
        return baseCell.contains(mouseX, mouseY);
    }

}
