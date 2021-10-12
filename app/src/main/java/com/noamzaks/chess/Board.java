package com.noamzaks.chess;

import com.noamzaks.chess.game.King;
import com.noamzaks.chess.game.Move;
import com.noamzaks.chess.game.Pawn;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.game.Rook;
import com.noamzaks.chess.utilities.Point;
import com.noamzaks.chess.utilities.Toast;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public interface OnSetListener {
        void onSet(int x, int y, Piece old, Piece current);
    }

    public static class InvalidMoveException extends Exception {
        public InvalidMoveException(String reason) {
            super(reason);
        }
    }

    private final Piece[][] pieces = new Piece[8][8];
    private boolean turn = Constants.WHITE;
    private final List<Move> history = new ArrayList<>();
    private Point<Integer> whiteKingPosition = new Point<>(7, 4);
    private Point<Integer> blackKingPosition = new Point<>(0, 4);
    private boolean canWhiteCastleKingside = true;
    private boolean canWhiteCastleQueenside = true;
    private boolean canBlackCastleKingside = true;
    private boolean canBlackCastleQueenside = true;

    public boolean canCastleKingside(boolean player) {
        return player == Constants.WHITE ? canWhiteCastleKingside : canBlackCastleKingside;
    }

    public boolean canCastleQueenside(boolean player) {
        return player == Constants.WHITE ? canWhiteCastleQueenside : canBlackCastleQueenside;
    }

    private final List<OnSetListener> listeners = new ArrayList<>();

    public Board() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                pieces[i][j] = Constants.INITIAL_BOARD[i][j];
            }
        }
    }

    public Piece get(Point<Integer> position) {
        return pieces[position.first][position.second];
    }

    private void set(int x, int y, Piece piece) {
        Piece old = pieces[x][y];
        pieces[x][y] = piece;

        for (var listener : listeners) {
            listener.onSet(x, y, old, piece);
        }
    }

    private void silentMove(Point<Integer> from, Point<Integer> to) {
        history.add(new Move(from, to, this));
        Piece moving = pieces[from.first][from.second];
        pieces[to.first][to.second] = moving;
        pieces[from.first][from.second] = null;

        if (moving instanceof King) {
            if (moving.isWhite()) {
                whiteKingPosition = to;
            } else {
                blackKingPosition = to;
            }
        }
    }

    private void silentUndo() {
        var move = getLastMove();
        pieces[move.to.first][move.to.second] = move.captured;
        pieces[move.from.first][move.from.second] = move.moving;

        if (move.moving instanceof King) {
            if (move.moving.isWhite()) {
                whiteKingPosition = move.from;
            } else {
                blackKingPosition = move.from;
            }
        }

        history.remove(history.size() - 1);
    }

    public boolean move(Point<Integer> from, Point<Integer> to) throws InvalidMoveException {
        Piece moving = pieces[from.first][from.second];

        if (moving == null) {
            throw new InvalidMoveException("No piece selected");
        }

        if (pieces[to.first][to.second] != null && moving.isWhite() == pieces[to.first][to.second].isWhite()) {
            throw new InvalidMoveException("You can't capture your own pieces");
        }

        if (!moving.canMove(from, to, this)) {
            throw new InvalidMoveException("A " + moving.getClass().getSimpleName().toLowerCase() + " can't move in this way");
        }

        if (moving instanceof King) {
            if (moving.isWhite()) {
                canWhiteCastleKingside = false;
                canWhiteCastleQueenside = false;
            } else {
                canBlackCastleKingside = false;
                canBlackCastleQueenside = false;
            }
        } else if (moving instanceof Rook) {
            if (from.first == 0 && from.second == 0) {
                canBlackCastleQueenside = false;
            } else if (from.first == 0 && from.second == 7) {
                canBlackCastleKingside = false;
            } else if (from.first == 7 && from.second == 0) {
                canWhiteCastleQueenside = false;
            } else if (from.first == 7 && from.second == 7) {
                canWhiteCastleKingside = false;
            }
        }

        silentMove(from, to);
        var checked = isChecked(turn);
        silentUndo();
        if (checked) {
            throw new InvalidMoveException("You can't end your turn checked!");
        }

        boolean checkmate = false;
        silentMove(from, to);
        if (isChecked(!turn)) {
            checkmate = true;
            for (int i = 0; i < pieces.length && checkmate; i++) {
                for (int j = 0; j < pieces[i].length && checkmate; j++) {
                    if (pieces[i][j] != null && pieces[i][j].isWhite() == (!turn == Constants.WHITE)) {
                        for (int k = 0; k < pieces.length && checkmate; k++) {
                            for (int l = 0; l < pieces[k].length; l++) {
                                if (i == k && j == l) {
                                    continue;
                                }

                                if (pieces[i][j].canMove(new Point<>(i, j), new Point<>(k, l), this) && (pieces[k][l] == null || pieces[k][l].isWhite() != pieces[i][j].isWhite())) {
                                    silentMove(new Point<>(i, j), new Point<>(k, l));
                                    checked = isChecked(!turn);
                                    silentUndo();
                                    if (!checked) {
                                        checkmate = false;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        silentUndo();

        if (moving instanceof Pawn && !to.second.equals(from.second) && get(to) == null) {
            set(to.first + (moving.isWhite() ? 1 : -1), to.second, null);
        }

        if (moving instanceof King && Math.abs(to.second - from.second) == 2) {
            var row = moving.isWhite() ? 7 : 0;
            var kingside = to.second == 6;

            if (isChecked(turn)) {
                throw new InvalidMoveException("You cannot castle while checked!");
            }

            silentMove(from, new Point<>(row, kingside ? 5 : 3));
            checked = isChecked(turn);
            silentUndo();
            if (checked) {
                throw new InvalidMoveException("You cannot castle through threatened squares!");
            }

            set(row, kingside ? 5 : 3, new Rook(moving.isWhite()));
            set(row, kingside ? 7 : 0, null);
        }

        silentMove(from, to);
        history.add(new Move(from, to, this));
        set(to.first, to.second, moving);
        set(from.first, from.second, null);
        turn = !turn;
        return checkmate;
    }

    public boolean isChecked(boolean player) {
        if (player == Constants.WHITE) {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    if (pieces[i][j] != null && !pieces[i][j].isWhite() && pieces[i][j].canMove(new Point<>(i, j), whiteKingPosition, this)) {
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    if (pieces[i][j] != null && pieces[i][j].isWhite() && pieces[i][j].canMove(new Point<>(i, j), blackKingPosition, this)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void onSet(OnSetListener listener) {
        listeners.add(listener);
    }

    public String toFEN() {
        StringBuilder builder = new StringBuilder();

        String[] rows = new String[pieces.length];
        for (int i = 0; i < pieces.length; i++) {
            int counter = 0;
            StringBuilder row = new StringBuilder();

            for (var piece : pieces[i]) {
                if (piece == null) {
                    counter++;
                    continue;
                }

                if (counter != 0) {
                    row.append(counter);
                    counter = 0;
                }

                row.append(piece.toLetter());
            }

            if (counter != 0) {
                row.append(counter);
            }

            rows[i] = row.toString();
        }

        builder.append(String.join("/", rows));
        builder.append(" ");
        builder.append(turn == Constants.WHITE ? 'w' : 'b');
        builder.append(" ");

        boolean castling = false;
        if (canWhiteCastleKingside) {
            builder.append("K");
            castling = true;
        }
        if (canWhiteCastleQueenside) {
            builder.append("Q");
            castling = true;
        }
        if (canBlackCastleKingside) {
            builder.append("k");
            castling = true;
        }
        if (canBlackCastleQueenside) {
            builder.append("q");
            castling = true;
        }

        if (!castling) {
            builder.append("-");
        }

        builder.append(" ");

        var last = getLastMove();
        if (last.moving instanceof Pawn && last.from.first == 6 && last.to.first == 4) {
            builder.append(Constants.LETTERS[last.from.second]).append("3");
        } else if (last.moving instanceof Pawn && last.from.first == 1 && last.to.first == 3) {
            builder.append(Constants.LETTERS[last.from.second]).append("6");
        } else {
            builder.append("-");
        }

        builder.append(" ");

        builder.append((history.size() - 1) / 2);

        builder.append(" ");

        int movesWithoutCapture = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            if (history.get(i).captured != null) {
                break;
            }
            movesWithoutCapture++;
        }

        movesWithoutCapture = 1 + movesWithoutCapture / 2;

        builder.append(movesWithoutCapture);

        return builder.toString();
    }

    public boolean getTurn() {
        return turn;
    }

    public Move getLastMove() {
        if (history.size() == 0) {
            return null;
        }

        return history.get(history.size() - 1);
    }
}
