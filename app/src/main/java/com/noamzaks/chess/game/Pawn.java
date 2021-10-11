package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "P" : "p";
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY, Board board) {
        int dx = toX - fromX;
        int dy = toY - fromY;

        return (dy == 0 && dx == (isWhite() ? -1 : 1) && board.get(toX, toY) == null) ||
                (dy == 0 && dx == -2 && isWhite() && fromX == 6 && board.get(5, fromY) == null && board.get(4, fromY) == null) ||
                (dy == 0 && (dx == 2 && !isWhite() && fromX == 1 && board.get(2, fromY) == null && board.get(3, fromY) == null)) ||
                ((dy == 1 || dy == -1) && dx == (isWhite() ? -1 : 1) && board.get(toX, toY) != null);
    }
}
