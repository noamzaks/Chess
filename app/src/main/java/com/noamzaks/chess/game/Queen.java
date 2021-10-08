package com.noamzaks.chess.game;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    @Override
    public String toString() {
        return isWhite() ? "White" : "Black" + " queen";
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY) {
        int dx = toX - fromX;
        int dy = toY - fromY;
        return dx == dy || dx == -dy || dx == 0 || dy == 0;
    }
}
