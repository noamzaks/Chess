package com.noamzaks.chess;

import com.noamzaks.chess.game.Bishop;
import com.noamzaks.chess.game.King;
import com.noamzaks.chess.game.Knight;
import com.noamzaks.chess.game.Pawn;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.game.Queen;
import com.noamzaks.chess.game.Rook;

public class Constants {
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

    public final static boolean WHITE = true;
    public final static boolean BLACK = !WHITE;

    public final static char[] LETTERS = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
}
