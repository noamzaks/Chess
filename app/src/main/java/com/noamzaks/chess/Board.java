package com.noamzaks.chess;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.noamzaks.chess.game.Bishop;
import com.noamzaks.chess.game.King;
import com.noamzaks.chess.game.Knight;
import com.noamzaks.chess.game.Pawn;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.game.Queen;
import com.noamzaks.chess.game.Rook;

import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.R)
public final class Board {
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
}
