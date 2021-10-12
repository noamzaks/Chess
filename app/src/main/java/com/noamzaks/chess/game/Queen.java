package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "Q" : "q";
    }

    @Override
    public boolean canMove(Point<Integer> from, Point<Integer> to, Board board) {
        return new Bishop(isWhite()).canMove(from, to, board) || new Rook(isWhite()).canMove(from, to, board);
    }
}
