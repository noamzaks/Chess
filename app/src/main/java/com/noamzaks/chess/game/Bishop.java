package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;

public class Bishop extends Piece {
    public Bishop(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "B" : "b";
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY, Board board) {
        int dx = toX - fromX;
        int dy = toY - fromY;

        if (dx == dy) {
            int step = dx > 0 ? 1 : -1;
            for (int currentX = fromX + step; currentX != toX; currentX += step) {
                if (board.get(currentX, fromY + currentX - fromX) != null) {
                    return false;
                }
            }
            return true;
        }

        if (dx == -dy) {
            int step = dx > 0 ? 1 : -1;
            for (int currentX = fromX + step; currentX != toX; currentX += step) {
                if (board.get(currentX, fromY + fromX - currentX) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
