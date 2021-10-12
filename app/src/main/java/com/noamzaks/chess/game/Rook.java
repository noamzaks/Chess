package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class Rook extends Piece {
    public Rook(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "R" : "r";
    }

    @Override
    public boolean canMove(Point<Integer> from, Point<Integer> to, Board board) {
        int dx = to.first - from.first;
        int dy = to.second - from.second;

        if (dx == 0) {
            int step = (to.second - from.second > 0) ? 1 : -1;
            for (int currentY = from.second + step; currentY != to.second; currentY += step) {
                if (board.get(new Point<>(from.first, currentY)) != null) {
                    return false;
                }
            }
            return true;
        }

        if (dy == 0) {
            int step = (to.first - from.first > 0) ? 1 : -1;
            for (int currentX = from.first + step; currentX != to.first; currentX += step) {
                if (board.get(new Point<>(currentX, from.second)) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
