package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class Bishop extends Piece {
    public Bishop(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "B" : "b";
    }

    @Override
    public boolean canMove(Point<Integer> from, Point<Integer> to, Board board) {
        int dx = to.first - from.first;
        int dy = to.second - from.second;

        if (dx == dy) {
            int step = dx > 0 ? 1 : -1;
            for (int currentX = from.first + step; currentX != to.first; currentX += step) {
                if (board.get(new Point<>(currentX, from.second + currentX - from.first)) != null) {
                    return false;
                }
            }
            return true;
        }

        if (dx == -dy) {
            int step = dx > 0 ? 1 : -1;
            for (int currentX = from.first + step; currentX != to.first; currentX += step) {
                if (board.get(new Point<>(currentX, from.second + from.first - currentX)) != null) {
                    return false;
                }
            }
            return true;
        }

        return false;
    }
}
