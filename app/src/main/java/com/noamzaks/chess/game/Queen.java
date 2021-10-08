package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;

public class Queen extends Piece {
    public Queen(boolean white) {
        super(white);
    }

    @Override
    public boolean canMove(int fromX, int fromY, int toX, int toY, Board board) {
        return new Bishop(isWhite()).canMove(fromX, fromY, toX, toY, board) || new Rook(isWhite()).canMove(fromX, fromY, toX, toY, board);
    }
}
