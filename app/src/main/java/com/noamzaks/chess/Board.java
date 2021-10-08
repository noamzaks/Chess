package com.noamzaks.chess;

import com.noamzaks.chess.game.Bishop;
import com.noamzaks.chess.game.King;
import com.noamzaks.chess.game.Knight;
import com.noamzaks.chess.game.Pawn;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.game.Queen;
import com.noamzaks.chess.game.Rook;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public static final Piece[][] INITIAL_BOARD = {
            {new Rook(false), new Knight(false), new Bishop(false), new Queen(false), new King(false), new Bishop(false), new Knight(false), new Rook(false)},
            {new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false), new Pawn(false)},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null},
            {new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true), new Pawn(true)},
            {new Rook(true), new Knight(true), new Bishop(true), new Queen(true), new King(true), new Bishop(true), new Knight(true), new Rook(true)},
    };

    public interface OnSetListener {
        void onSet(int x, int y, Piece old, Piece current);
    }

    public static class InvalidMoveException extends Exception {
        public InvalidMoveException(String reason) { super(reason); }
    }

    private Piece[][] pieces = INITIAL_BOARD.clone();
    private List<OnSetListener> listeners;

    public Board() {
        listeners = new ArrayList<>();
    }

    public Piece get(int x, int y) {
        return pieces[x][y];
    }

    private void set(int x, int y, Piece piece) {
        Piece old = pieces[x][y];
        pieces[x][y] = piece;
        for (OnSetListener listener: listeners) {
            listener.onSet(x, y, old, piece);
        }
    }

    public void move(int fromX, int fromY, int toX, int toY) throws InvalidMoveException {
        Piece moving = pieces[fromX][fromY];
        if (pieces[toX][toY] != null && moving.isWhite() == pieces[toX][toY].isWhite()) {
            throw new InvalidMoveException("You can't capture your own pieces");
        }

        if (!moving.canMove(fromX, fromY, toX, toY, this)) {
            throw new InvalidMoveException("A " + moving.getClass().getSimpleName().toLowerCase() + " can't move in this way");
        }

        set(toX, toY, moving);
        set(fromX, fromY, null);
    }

    public void onSet(OnSetListener listener) {
        listeners.add(listener);
    }
}
