package com.noamzaks.chess.game;


import androidx.annotation.NonNull;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.R;
import com.noamzaks.chess.utilities.Point;

import java.util.Map;
import java.util.Objects;

public abstract class Piece {
    private static final Map<String, Map<Class<? extends Piece>, int[]>> RESOURCE_MAP = Map.ofEntries(Map.entry("Alpha", Map.of(
            Pawn.class, new int[]{R.drawable.alpha_white_pawn, R.drawable.alpha_black_pawn},
            Knight.class, new int[]{R.drawable.alpha_white_knight, R.drawable.alpha_black_knight},
            Bishop.class, new int[]{R.drawable.alpha_white_bishop, R.drawable.alpha_black_bishop},
            Rook.class, new int[]{R.drawable.alpha_white_rook, R.drawable.alpha_black_rook},
            Queen.class, new int[]{R.drawable.alpha_white_queen, R.drawable.alpha_black_queen},
            King.class, new int[]{R.drawable.alpha_white_king, R.drawable.alpha_black_king}
            )),
            Map.entry("California", Map.of(
                    Pawn.class, new int[]{R.drawable.california_white_pawn, R.drawable.california_black_pawn},
                    Knight.class, new int[]{R.drawable.california_white_knight, R.drawable.california_black_knight},
                    Bishop.class, new int[]{R.drawable.california_white_bishop, R.drawable.california_black_bishop},
                    Rook.class, new int[]{R.drawable.california_white_rook, R.drawable.california_black_rook},
                    Queen.class, new int[]{R.drawable.california_white_queen, R.drawable.california_black_queen},
                    King.class, new int[]{R.drawable.california_white_king, R.drawable.california_black_king}
            )),
            Map.entry("Cardinal", Map.of(
                    Pawn.class, new int[]{R.drawable.cardinal_white_pawn, R.drawable.cardinal_black_pawn},
                    Knight.class, new int[]{R.drawable.cardinal_white_knight, R.drawable.cardinal_black_knight},
                    Bishop.class, new int[]{R.drawable.cardinal_white_bishop, R.drawable.cardinal_black_bishop},
                    Rook.class, new int[]{R.drawable.cardinal_white_rook, R.drawable.cardinal_black_rook},
                    Queen.class, new int[]{R.drawable.cardinal_white_queen, R.drawable.cardinal_black_queen},
                    King.class, new int[]{R.drawable.cardinal_white_king, R.drawable.cardinal_black_king}
            )),
            Map.entry("Cburnett", Map.of(
                    Pawn.class, new int[]{R.drawable.cburnett_white_pawn, R.drawable.cburnett_black_pawn},
                    Knight.class, new int[]{R.drawable.cburnett_white_knight, R.drawable.cburnett_black_knight},
                    Bishop.class, new int[]{R.drawable.cburnett_white_bishop, R.drawable.cburnett_black_bishop},
                    Rook.class, new int[]{R.drawable.cburnett_white_rook, R.drawable.cburnett_black_rook},
                    Queen.class, new int[]{R.drawable.cburnett_white_queen, R.drawable.cburnett_black_queen},
                    King.class, new int[]{R.drawable.cburnett_white_king, R.drawable.cburnett_black_king}
            )),
            Map.entry("Chess7", Map.of(
                    Pawn.class, new int[]{R.drawable.chess7_white_pawn, R.drawable.chess7_black_pawn},
                    Knight.class, new int[]{R.drawable.chess7_white_knight, R.drawable.chess7_black_knight},
                    Bishop.class, new int[]{R.drawable.chess7_white_bishop, R.drawable.chess7_black_bishop},
                    Rook.class, new int[]{R.drawable.chess7_white_rook, R.drawable.chess7_black_rook},
                    Queen.class, new int[]{R.drawable.chess7_white_queen, R.drawable.chess7_black_queen},
                    King.class, new int[]{R.drawable.chess7_white_king, R.drawable.chess7_black_king}
            )),
            Map.entry("Chessnut", Map.of(
                    Pawn.class, new int[]{R.drawable.chessnut_white_pawn, R.drawable.chessnut_black_pawn},
                    Knight.class, new int[]{R.drawable.chessnut_white_knight, R.drawable.chessnut_black_knight},
                    Bishop.class, new int[]{R.drawable.chessnut_white_bishop, R.drawable.chessnut_black_bishop},
                    Rook.class, new int[]{R.drawable.chessnut_white_rook, R.drawable.chessnut_black_rook},
                    Queen.class, new int[]{R.drawable.chessnut_white_queen, R.drawable.chessnut_black_queen},
                    King.class, new int[]{R.drawable.chessnut_white_king, R.drawable.chessnut_black_king}
            )),
            Map.entry("Companion", Map.of(
                    Pawn.class, new int[]{R.drawable.companion_white_pawn, R.drawable.companion_black_pawn},
                    Knight.class, new int[]{R.drawable.companion_white_knight, R.drawable.companion_black_knight},
                    Bishop.class, new int[]{R.drawable.companion_white_bishop, R.drawable.companion_black_bishop},
                    Rook.class, new int[]{R.drawable.companion_white_rook, R.drawable.companion_black_rook},
                    Queen.class, new int[]{R.drawable.companion_white_queen, R.drawable.companion_black_queen},
                    King.class, new int[]{R.drawable.companion_white_king, R.drawable.companion_black_king}
            )),
            Map.entry("Dubrovny", Map.of(
                    Pawn.class, new int[]{R.drawable.dubrovny_white_pawn, R.drawable.dubrovny_black_pawn},
                    Knight.class, new int[]{R.drawable.dubrovny_white_knight, R.drawable.dubrovny_black_knight},
                    Bishop.class, new int[]{R.drawable.dubrovny_white_bishop, R.drawable.dubrovny_black_bishop},
                    Rook.class, new int[]{R.drawable.dubrovny_white_rook, R.drawable.dubrovny_black_rook},
                    Queen.class, new int[]{R.drawable.dubrovny_white_queen, R.drawable.dubrovny_black_queen},
                    King.class, new int[]{R.drawable.dubrovny_white_king, R.drawable.dubrovny_black_king}
            )),
            Map.entry("Fantasy", Map.of(
                    Pawn.class, new int[]{R.drawable.fantasy_white_pawn, R.drawable.fantasy_black_pawn},
                    Knight.class, new int[]{R.drawable.fantasy_white_knight, R.drawable.fantasy_black_knight},
                    Bishop.class, new int[]{R.drawable.fantasy_white_bishop, R.drawable.fantasy_black_bishop},
                    Rook.class, new int[]{R.drawable.fantasy_white_rook, R.drawable.fantasy_black_rook},
                    Queen.class, new int[]{R.drawable.fantasy_white_queen, R.drawable.fantasy_black_queen},
                    King.class, new int[]{R.drawable.fantasy_white_king, R.drawable.fantasy_black_king}
            )),
            Map.entry("Fresca", Map.of(
                    Pawn.class, new int[]{R.drawable.fresca_white_pawn, R.drawable.fresca_black_pawn},
                    Knight.class, new int[]{R.drawable.fresca_white_knight, R.drawable.fresca_black_knight},
                    Bishop.class, new int[]{R.drawable.fresca_white_bishop, R.drawable.fresca_black_bishop},
                    Rook.class, new int[]{R.drawable.fresca_white_rook, R.drawable.fresca_black_rook},
                    Queen.class, new int[]{R.drawable.fresca_white_queen, R.drawable.fresca_black_queen},
                    King.class, new int[]{R.drawable.fresca_white_king, R.drawable.fresca_black_king}
            )),
            Map.entry("Gioco", Map.of(
                    Pawn.class, new int[]{R.drawable.gioco_white_pawn, R.drawable.gioco_black_pawn},
                    Knight.class, new int[]{R.drawable.gioco_white_knight, R.drawable.gioco_black_knight},
                    Bishop.class, new int[]{R.drawable.gioco_white_bishop, R.drawable.gioco_black_bishop},
                    Rook.class, new int[]{R.drawable.gioco_white_rook, R.drawable.gioco_black_rook},
                    Queen.class, new int[]{R.drawable.gioco_white_queen, R.drawable.gioco_black_queen},
                    King.class, new int[]{R.drawable.gioco_white_king, R.drawable.gioco_black_king}
            )),
            Map.entry("Governor", Map.of(
                    Pawn.class, new int[]{R.drawable.governor_white_pawn, R.drawable.governor_black_pawn},
                    Knight.class, new int[]{R.drawable.governor_white_knight, R.drawable.governor_black_knight},
                    Bishop.class, new int[]{R.drawable.governor_white_bishop, R.drawable.governor_black_bishop},
                    Rook.class, new int[]{R.drawable.governor_white_rook, R.drawable.governor_black_rook},
                    Queen.class, new int[]{R.drawable.governor_white_queen, R.drawable.governor_black_queen},
                    King.class, new int[]{R.drawable.governor_white_king, R.drawable.governor_black_king}
            )),
            Map.entry("Horsey", Map.of(
                    Pawn.class, new int[]{R.drawable.horsey_white_pawn, R.drawable.horsey_black_pawn},
                    Knight.class, new int[]{R.drawable.horsey_white_knight, R.drawable.horsey_black_knight},
                    Bishop.class, new int[]{R.drawable.horsey_white_bishop, R.drawable.horsey_black_bishop},
                    Rook.class, new int[]{R.drawable.horsey_white_rook, R.drawable.horsey_black_rook},
                    Queen.class, new int[]{R.drawable.horsey_white_queen, R.drawable.horsey_black_queen},
                    King.class, new int[]{R.drawable.horsey_white_king, R.drawable.horsey_black_king}
            )),
            Map.entry("Icpieces", Map.of(
                    Pawn.class, new int[]{R.drawable.icpieces_white_pawn, R.drawable.icpieces_black_pawn},
                    Knight.class, new int[]{R.drawable.icpieces_white_knight, R.drawable.icpieces_black_knight},
                    Bishop.class, new int[]{R.drawable.icpieces_white_bishop, R.drawable.icpieces_black_bishop},
                    Rook.class, new int[]{R.drawable.icpieces_white_rook, R.drawable.icpieces_black_rook},
                    Queen.class, new int[]{R.drawable.icpieces_white_queen, R.drawable.icpieces_black_queen},
                    King.class, new int[]{R.drawable.icpieces_white_king, R.drawable.icpieces_black_king}
            )),
            Map.entry("Kosal", Map.of(
                    Pawn.class, new int[]{R.drawable.kosal_white_pawn, R.drawable.kosal_black_pawn},
                    Knight.class, new int[]{R.drawable.kosal_white_knight, R.drawable.kosal_black_knight},
                    Bishop.class, new int[]{R.drawable.kosal_white_bishop, R.drawable.kosal_black_bishop},
                    Rook.class, new int[]{R.drawable.kosal_white_rook, R.drawable.kosal_black_rook},
                    Queen.class, new int[]{R.drawable.kosal_white_queen, R.drawable.kosal_black_queen},
                    King.class, new int[]{R.drawable.kosal_white_king, R.drawable.kosal_black_king}
            )),
            Map.entry("Leipzig", Map.of(
                    Pawn.class, new int[]{R.drawable.leipzig_white_pawn, R.drawable.leipzig_black_pawn},
                    Knight.class, new int[]{R.drawable.leipzig_white_knight, R.drawable.leipzig_black_knight},
                    Bishop.class, new int[]{R.drawable.leipzig_white_bishop, R.drawable.leipzig_black_bishop},
                    Rook.class, new int[]{R.drawable.leipzig_white_rook, R.drawable.leipzig_black_rook},
                    Queen.class, new int[]{R.drawable.leipzig_white_queen, R.drawable.leipzig_black_queen},
                    King.class, new int[]{R.drawable.leipzig_white_king, R.drawable.leipzig_black_king}
            )),
            Map.entry("Letter", Map.of(
                    Pawn.class, new int[]{R.drawable.letter_white_pawn, R.drawable.letter_black_pawn},
                    Knight.class, new int[]{R.drawable.letter_white_knight, R.drawable.letter_black_knight},
                    Bishop.class, new int[]{R.drawable.letter_white_bishop, R.drawable.letter_black_bishop},
                    Rook.class, new int[]{R.drawable.letter_white_rook, R.drawable.letter_black_rook},
                    Queen.class, new int[]{R.drawable.letter_white_queen, R.drawable.letter_black_queen},
                    King.class, new int[]{R.drawable.letter_white_king, R.drawable.letter_black_king}
            )),
            Map.entry("Libra", Map.of(
                    Pawn.class, new int[]{R.drawable.libra_white_pawn, R.drawable.libra_black_pawn},
                    Knight.class, new int[]{R.drawable.libra_white_knight, R.drawable.libra_black_knight},
                    Bishop.class, new int[]{R.drawable.libra_white_bishop, R.drawable.libra_black_bishop},
                    Rook.class, new int[]{R.drawable.libra_white_rook, R.drawable.libra_black_rook},
                    Queen.class, new int[]{R.drawable.libra_white_queen, R.drawable.libra_black_queen},
                    King.class, new int[]{R.drawable.libra_white_king, R.drawable.libra_black_king}
            )),
            Map.entry("Maestro", Map.of(
                    Pawn.class, new int[]{R.drawable.maestro_white_pawn, R.drawable.maestro_black_pawn},
                    Knight.class, new int[]{R.drawable.maestro_white_knight, R.drawable.maestro_black_knight},
                    Bishop.class, new int[]{R.drawable.maestro_white_bishop, R.drawable.maestro_black_bishop},
                    Rook.class, new int[]{R.drawable.maestro_white_rook, R.drawable.maestro_black_rook},
                    Queen.class, new int[]{R.drawable.maestro_white_queen, R.drawable.maestro_black_queen},
                    King.class, new int[]{R.drawable.maestro_white_king, R.drawable.maestro_black_king}
            )),
            Map.entry("Merida", Map.of(
                    Pawn.class, new int[]{R.drawable.merida_white_pawn, R.drawable.merida_black_pawn},
                    Knight.class, new int[]{R.drawable.merida_white_knight, R.drawable.merida_black_knight},
                    Bishop.class, new int[]{R.drawable.merida_white_bishop, R.drawable.merida_black_bishop},
                    Rook.class, new int[]{R.drawable.merida_white_rook, R.drawable.merida_black_rook},
                    Queen.class, new int[]{R.drawable.merida_white_queen, R.drawable.merida_black_queen},
                    King.class, new int[]{R.drawable.merida_white_king, R.drawable.merida_black_king}
            )),
            Map.entry("Pirouetti", Map.of(
                    Pawn.class, new int[]{R.drawable.pirouetti_white_pawn, R.drawable.pirouetti_black_pawn},
                    Knight.class, new int[]{R.drawable.pirouetti_white_knight, R.drawable.pirouetti_black_knight},
                    Bishop.class, new int[]{R.drawable.pirouetti_white_bishop, R.drawable.pirouetti_black_bishop},
                    Rook.class, new int[]{R.drawable.pirouetti_white_rook, R.drawable.pirouetti_black_rook},
                    Queen.class, new int[]{R.drawable.pirouetti_white_queen, R.drawable.pirouetti_black_queen},
                    King.class, new int[]{R.drawable.pirouetti_white_king, R.drawable.pirouetti_black_king}
            )),
            Map.entry("Pixel", Map.of(
                    Pawn.class, new int[]{R.drawable.pixel_white_pawn, R.drawable.pixel_black_pawn},
                    Knight.class, new int[]{R.drawable.pixel_white_knight, R.drawable.pixel_black_knight},
                    Bishop.class, new int[]{R.drawable.pixel_white_bishop, R.drawable.pixel_black_bishop},
                    Rook.class, new int[]{R.drawable.pixel_white_rook, R.drawable.pixel_black_rook},
                    Queen.class, new int[]{R.drawable.pixel_white_queen, R.drawable.pixel_black_queen},
                    King.class, new int[]{R.drawable.pixel_white_king, R.drawable.pixel_black_king}
            )),
            Map.entry("Reillycraig", Map.of(
                    Pawn.class, new int[]{R.drawable.reillycraig_white_pawn, R.drawable.reillycraig_black_pawn},
                    Knight.class, new int[]{R.drawable.reillycraig_white_knight, R.drawable.reillycraig_black_knight},
                    Bishop.class, new int[]{R.drawable.reillycraig_white_bishop, R.drawable.reillycraig_black_bishop},
                    Rook.class, new int[]{R.drawable.reillycraig_white_rook, R.drawable.reillycraig_black_rook},
                    Queen.class, new int[]{R.drawable.reillycraig_white_queen, R.drawable.reillycraig_black_queen},
                    King.class, new int[]{R.drawable.reillycraig_white_king, R.drawable.reillycraig_black_king}
            )),
            Map.entry("Riohacha", Map.of(
                    Pawn.class, new int[]{R.drawable.riohacha_white_pawn, R.drawable.riohacha_black_pawn},
                    Knight.class, new int[]{R.drawable.riohacha_white_knight, R.drawable.riohacha_black_knight},
                    Bishop.class, new int[]{R.drawable.riohacha_white_bishop, R.drawable.riohacha_black_bishop},
                    Rook.class, new int[]{R.drawable.riohacha_white_rook, R.drawable.riohacha_black_rook},
                    Queen.class, new int[]{R.drawable.riohacha_white_queen, R.drawable.riohacha_black_queen},
                    King.class, new int[]{R.drawable.riohacha_white_king, R.drawable.riohacha_black_king}
            )),
            Map.entry("Shapes", Map.of(
                    Pawn.class, new int[]{R.drawable.shapes_white_pawn, R.drawable.shapes_black_pawn},
                    Knight.class, new int[]{R.drawable.shapes_white_knight, R.drawable.shapes_black_knight},
                    Bishop.class, new int[]{R.drawable.shapes_white_bishop, R.drawable.shapes_black_bishop},
                    Rook.class, new int[]{R.drawable.shapes_white_rook, R.drawable.shapes_black_rook},
                    Queen.class, new int[]{R.drawable.shapes_white_queen, R.drawable.shapes_black_queen},
                    King.class, new int[]{R.drawable.shapes_white_king, R.drawable.shapes_black_king}
            )),
            Map.entry("Spatial", Map.of(
                    Pawn.class, new int[]{R.drawable.spatial_white_pawn, R.drawable.spatial_black_pawn},
                    Knight.class, new int[]{R.drawable.spatial_white_knight, R.drawable.spatial_black_knight},
                    Bishop.class, new int[]{R.drawable.spatial_white_bishop, R.drawable.spatial_black_bishop},
                    Rook.class, new int[]{R.drawable.spatial_white_rook, R.drawable.spatial_black_rook},
                    Queen.class, new int[]{R.drawable.spatial_white_queen, R.drawable.spatial_black_queen},
                    King.class, new int[]{R.drawable.spatial_white_king, R.drawable.spatial_black_king}
            )),
            Map.entry("Staunty", Map.of(
                    Pawn.class, new int[]{R.drawable.staunty_white_pawn, R.drawable.staunty_black_pawn},
                    Knight.class, new int[]{R.drawable.staunty_white_knight, R.drawable.staunty_black_knight},
                    Bishop.class, new int[]{R.drawable.staunty_white_bishop, R.drawable.staunty_black_bishop},
                    Rook.class, new int[]{R.drawable.staunty_white_rook, R.drawable.staunty_black_rook},
                    Queen.class, new int[]{R.drawable.staunty_white_queen, R.drawable.staunty_black_queen},
                    King.class, new int[]{R.drawable.staunty_white_king, R.drawable.staunty_black_king}
            )),
            Map.entry("Tatiana", Map.of(
                    Pawn.class, new int[]{R.drawable.tatiana_white_pawn, R.drawable.tatiana_black_pawn},
                    Knight.class, new int[]{R.drawable.tatiana_white_knight, R.drawable.tatiana_black_knight},
                    Bishop.class, new int[]{R.drawable.tatiana_white_bishop, R.drawable.tatiana_black_bishop},
                    Rook.class, new int[]{R.drawable.tatiana_white_rook, R.drawable.tatiana_black_rook},
                    Queen.class, new int[]{R.drawable.tatiana_white_queen, R.drawable.tatiana_black_queen},
                    King.class, new int[]{R.drawable.tatiana_white_king, R.drawable.tatiana_black_king}
            ))
    );

    private final boolean white;

    protected Piece(boolean white) {
        this.white = white;
    }

    public boolean isWhite() {
        return white;
    }

    public int getResource(String theme) {
        return Objects.requireNonNull(
            Objects.requireNonNull(RESOURCE_MAP.get(theme)).get(getClass())
        )[white ? 0 : 1];
    }

    @NonNull
    @Override
    public String toString() {
        return (white ? "White " : "Black ") + getClass().getSimpleName();
    }

    public abstract String toLetter();

    public abstract boolean canMove(Point<Integer> from, Point<Integer> to, Board board);
}
