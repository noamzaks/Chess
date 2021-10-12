package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class Knight extends Piece {
    public Knight(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "N" : "n";
    }

    @Override
    public boolean canMove(Point<Integer> from, Point<Integer> to, Board board) {
        int adx = Math.abs(to.first - from.first);
        int ady = Math.abs(to.second - from.second);
        return (adx == 1 || adx == 2) && (adx + ady == 3);
    }
}
