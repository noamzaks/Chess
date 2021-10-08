package com.noamzaks.chess.game;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY) {
        int dx = toX - fromX;
        int dy = toY - fromY;
        return dx == 0 && dy == (isWhite() ? 1 : -1);
    }
}
