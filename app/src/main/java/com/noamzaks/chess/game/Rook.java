package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;

public class Rook extends Piece {
    public Rook(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY, Board board) {
        int dx = toX - fromX;
        int dy = toY - fromY;

        if (dx == 0) {
            int step = (toY - fromY > 0) ? 1 : -1;
            for (int currentY = fromY + step; currentY != toY; currentY += step) {
                if (board.get(fromX, currentY) != null) {
                    return false;
                }
            }
            return true;
        }

        if (dy == 0) {
            int step = (toX - fromX > 0) ? 1 : -1;
            for (int currentX = fromX + step; currentX != toX; currentX += step) {
                if (board.get(currentX, fromY) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
