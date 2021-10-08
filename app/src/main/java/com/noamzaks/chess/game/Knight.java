package com.noamzaks.chess.game;

public class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY) {
        int adx = Math.abs(toX - fromX);
        int ady = Math.abs(toY - fromY);
        return (adx == 1 || adx == 2) && (adx + ady == 3);
    }
}
