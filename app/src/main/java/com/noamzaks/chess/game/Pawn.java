package com.noamzaks.chess.game;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.utilities.Point;

public class Pawn extends Piece {
    public Pawn(boolean white) {
        super(white);
    }

    @Override
    public String toLetter() {
        return isWhite() ? "P" : "p";
    }

    @Override
    public boolean canMove(Point<Integer> from, Point<Integer> to, Board board) {
        int dx = to.first - from.first;
        int dy = to.second - from.second;

        var last = board.getLastMove();

//        if (last != null) {
//        System.out.println(last.from.first);
//        System.out.println(last.from.second);
//        System.out.println(last.to.first);
//        System.out.println(last.to.second);
//        System.out.println(to.first);
//        System.out.println(to.second);
//        System.out.println((last != null && last.moving instanceof Pawn && last.to.second.equals(to.second) && Math.abs(last.to.first - to.first) == 1 && dx == (isWhite() ? -1 : 1) && Math.abs(last.to.first - last.from.first) == 2));
//        System.out.println((last != null && last.moving instanceof Pawn && last.to.second.equals(to.second) && Math.abs(last.to.first - to.first) == 1 && dx == (isWhite() ? -1 : 1)));
//        System.out.println((last != null && last.moving instanceof Pawn && last.to.second.equals(to.second) && Math.abs(last.to.first - to.first) == 1));
//        System.out.println((last != null && last.moving instanceof Pawn && last.to.second.equals(to.second)));
//        System.out.println(last.captured);}

        return (dy == 0 && dx == (isWhite() ? -1 : 1) && board.get(to) == null) ||
                (dy == 0 && dx == -2 && isWhite() && from.first == 6 && board.get(new Point<>(5, from.second)) == null && board.get(new Point<>(4, from.second)) == null) ||
                (dy == 0 && (dx == 2 && !isWhite() && from.first == 1 && board.get(new Point<>(2, from.second)) == null && board.get(new Point<>(3, from.second)) == null)) ||
                ((dy == 1 || dy == -1) && dx == (isWhite() ? -1 : 1) && board.get(to) != null) ||
                (last != null && last.moving instanceof Pawn && last.to.second.equals(to.second) && Math.abs(last.to.first - to.first) == 1 && dx == (isWhite() ? -1 : 1) && Math.abs(last.to.first - last.from.first) == 2);
    }
}
