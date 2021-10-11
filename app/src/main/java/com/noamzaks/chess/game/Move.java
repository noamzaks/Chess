package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;

public class Move {
    public Class<? extends Piece> moving, captured;
    public int fromX, fromY, toX, toY;

    public Move(Class<? extends Piece> moving, int fromX, int fromY, Class<? extends Piece> captured, int toX, int toY) {
        this.moving = moving;
        this.fromX = fromX;
        this.fromY = fromY;
        this.captured = captured;
        this.toX = toX;
        this.toY = toY;
    }

    public Move(int fromX, int fromY, int toX, int toY, Board board) {
        Class<? extends Piece> moving = null, captured = null;

        if (board.get(fromX, fromY) != null) {
            moving = board.get(fromX, fromY).getClass();
        }
        if (board.get(toX, toY) != null) {
            captured = board.get(toX, toY).getClass();
        }

        this.moving = moving;
        this.fromX = fromX;
        this.fromY = fromY;
        this.captured = captured;
        this.toX = toX;
        this.toY = toY;
    }
}
