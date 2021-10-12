package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class King extends Piece {
    public King(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "K" : "k";
    }

    @Override
    public boolean canMove(Point<Integer> from, Point<Integer> to, Board board) {
        if (board.canCastleKingside(isWhite()) && to.first == from.first && to.second == 6 && board.get(new Point<>(from.first, 5)) == null) {
            return true;
        }
        if (board.canCastleQueenside(isWhite()) && to.first == from.first && to.second == 2 && board.get(new Point<>(from.first, 3)) == null && board.get(new Point<>(from.first, 1)) == null) {
            return true;
        }

        int dx = to.first - from.first;
        int dy = to.second - from.second;
        return -1 <= dx && dx <= 1 && -1 <= dy && dy <= 1;
    }
}
