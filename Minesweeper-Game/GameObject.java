package com.codegym.games.minesweeper;


public class GameObject extends MinesweeperGame {

    public int x, y;
    public boolean isMine;
    public boolean isOpen;
    public boolean isFlag;
    public int countMineNeighbors;

    public GameObject(int x, int y, boolean isMine) {
        this.x = x;
        this.y = y;
        this.isMine = isMine;
    }


}
