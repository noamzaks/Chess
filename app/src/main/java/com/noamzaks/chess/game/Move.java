package com.noamzaks.chess.game;

import androidx.annotation.NonNull;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class Move {
    public Piece moving, captured;
    public Point<Integer> from, to;

    public Move(Piece moving, Point<Integer> from, Piece captured, Point<Integer> to) {
        this.moving = moving;
        this.from = from;
        this.captured = captured;
        this.to = to;
    }

    public Move(Point<Integer> from, Point<Integer> to, Board board) {
        this(board.get(from), from, board.get(to), to);
    }

    @Override
    @NonNull
    public String toString() {
        return "Movement of " + moving + " from " + from + " to " + to + " capturing " + captured;
    }
}
