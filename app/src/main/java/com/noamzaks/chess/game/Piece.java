package com.noamzaks.chess.game;

import com.noamzaks.chess.R;

import java.util.Map;
import java.util.Objects;

public abstract class Piece {
    private static final Map<Class<? extends Piece>, int[]> RESOURCE_MAP = Map.of(
            Pawn.class, new int[]{R.drawable.white_pawn, R.drawable.black_pawn},
            Knight.class, new int[]{R.drawable.white_knight, R.drawable.black_knight},
            Bishop.class, new int[]{R.drawable.white_bishop, R.drawable.black_bishop},
            Rook.class, new int[]{R.drawable.white_rook, R.drawable.black_rook},
            Queen.class, new int[]{R.drawable.white_queen, R.drawable.black_queen},
            King.class, new int[]{R.drawable.white_king, R.drawable.black_king}
    );

    private boolean white;

    protected Piece(boolean white) {
        this.white = white;
    }

    public boolean isWhite() { return white; }

    public int getResource() {
        return Objects.requireNonNull(RESOURCE_MAP.get(getClass()))[white ? 0 : 1];
    }

    public String toString() {
        return (white ? "White " : "Black ") + getClass().getSimpleName();
    }

    public abstract boolean canMove(int fromX, int fromY, int toX, int toY);
}
