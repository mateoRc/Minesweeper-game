package com.codegym.games.minesweeper;
import com.codegym.engine.cell.*;

public class MinesweeperGame extends Game {
    private static final int SIDE = 11;
    private static final String MINE = "\uD83D\uDCA3";
    private static final String FLAG = "\uD83D\uDEA9";

    private boolean isGameStopped;
    private int countClosedTiles = SIDE * SIDE;
    private int countFlags;
    private int countMinesOnField = 0;
    private int score = 0;

    @Override
    public void initialize() {
        int row = SIDE, column = SIDE;
        setScreenSize(row, column);
        createGame();
    }

    private GameObject[][] gameField = new GameObject[SIDE][SIDE];

    private void createGame() {
        for (int i = 0; i < SIDE; i++) {
            for (int j = 0; j < SIDE; j++) {
                int n = getRandomNumber(20);             //15% of mines
                if (n == 9 || n == 7 || n == 5) {
                    this.countMinesOnField++;
                    gameField[i][j] = new GameObject(j, i, true);
                    setCellValue(j, i, "");
                    setCellColor(j, i, Color.GREEN);
                }
                else {
                    gameField[i][j] = new GameObject(j, i, false);
                    setCellValue(j, i, "");
                    setCellColor(j, i, Color.GREEN);
                }
            }
        }
        countFlags = countMinesOnField;
        countMineNeighbors();
    }

    private void countMineNeighbors() {
        for (int i = 0; i < gameField.length; i++) {
            for (int j = 0; j < gameField.length; j++) {
                if (!gameField[i][j].isMine){
                    GameObject gameObject = gameField[i][j];
                    getNeighbors(gameObject);
                }
            }
        }
    }

    boolean isValid(int x, int y) {              //checks if x and y are in range
        return (x >= 0 && x < SIDE) && (y >= 0 && y < SIDE);
    }

    //checks number of mines around the tile
    public void getNeighbors(GameObject gameObject) {
        int column = gameObject.x;
        int row = gameObject.y;
        int count = 0;

        for(int i = row-1 ;i <= row+1 ; i++) {
            for (int j = column - 1; j <= column + 1; j++) {
                if (isValid(i, j) && gameField[i][j].isMine) count++;
            }
        }
        gameObject.countMineNeighbors = count;
    }

    private void openTile(int x, int y) {
        GameObject chosenTile = gameField[y][x];
        if (!chosenTile.isOpen && !chosenTile.isFlag && !isGameStopped) {       //don't do anything if the tile is open, is flagged or the game is stopped
            chosenTile.isOpen = true;
            countClosedTiles--;                                             //each time a tile is opened, decrease number of closed tiles by 1
            if (chosenTile.isMine) {
                setCellValueEx(x, y, Color.RED, MINE);
                gameOver();
            }
            if (!chosenTile.isMine) {
                score += 5;
                setScore(score);
                if (chosenTile.countMineNeighbors == 0){
                    for(int i = x-1 ;i <= x+1 ; i++) {
                        for (int j = y - 1; j <= y + 1; j++) {
                            if (isValid(i, j)) openTile(i, j);           //if the element is not a mine and has no mined neighbors, the openTile() method is called recursively on each neighbor
                        }
                    }
                }
                setCellNumber(x, y, chosenTile.countMineNeighbors);
                setCellColor(x, y, Color.BLUEVIOLET);
                if (chosenTile.countMineNeighbors == 0 && !chosenTile.isMine) {
                    setCellValue(x, y, "");
                }
                if (countClosedTiles == countMinesOnField) {
                    win();
                }
            }
        }
    }

    private void markTile(int x, int y) {
        GameObject markedTile = gameField[y][x];
        if (!isGameStopped) {
            if (!markedTile.isOpen) {
                if (countFlags != 0 && !markedTile.isFlag) {
                    markedTile.isFlag = true;
                    countFlags--;
                    setCellValue(x, y, FLAG);
                    setCellColor(x, y, Color.YELLOW);
                } else if (markedTile.isFlag) {
                    markedTile.isFlag = false;
                    countFlags++;
                    setCellValue(x, y, "");
                    setCellColor(x, y, Color.GREEN);
                }
            }
        }
    }

    private void gameOver() {
        isGameStopped = true;
        showMessageDialog(Color.YELLOW, "GAME OVER!", Color.BLACK, 50);
    }

    private void win() {
        isGameStopped = true;
        showMessageDialog(Color.CORAL, "YOU WIN!", Color.BLACK, 50);
    }

    private void restart() {
        isGameStopped = false;
        countClosedTiles = SIDE * SIDE;
        score = 0;
        countMinesOnField = 0;
        setScore(score);
        createGame();
    }

    @Override
    public void onMouseLeftClick(int x, int y) {
        super.onMouseLeftClick(x, y);
        try {
            if (isGameStopped) restart();
            else openTile(x, y);

        } catch (ArrayIndexOutOfBoundsException e) {}
    }

    @Override
    public void onMouseRightClick(int x, int y) {
        super.onMouseRightClick(x, y);
        try {
            markTile(x, y);
        } catch (ArrayIndexOutOfBoundsException e) {}
    }
}
