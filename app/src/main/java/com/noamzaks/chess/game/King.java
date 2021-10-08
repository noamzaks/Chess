package com.noamzaks.chess.game;

public class King extends Piece {
    public King(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY) {
        int dx = toX - fromX;
        int dy = toY - fromY;
        return -1 <= dx && dx <= 1 && -1 <= dy && dy <= 1;
    }
}
