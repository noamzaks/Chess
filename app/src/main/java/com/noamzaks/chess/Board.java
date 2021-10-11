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

    private final Piece[][] pieces = Constants.INITIAL_BOARD;
    private boolean turn = Constants.WHITE;
    private final List<Move> history = new ArrayList<>();
    private Point<Integer> whiteKingPosition = new Point<>(7, 4);
    private Point<Integer> blackKingPosition = new Point<>(0, 4);
    private boolean canWhiteCastleKingside = true;
    private boolean canWhiteCastleQueenside = true;
    private boolean canBlackCastleKingside = true;
    private boolean canBlackCastleQueenside = true;

    private final List<OnSetListener> listeners = new ArrayList<>();

    public Board() {
    }

    public Piece get(int x, int y) {
        return pieces[x][y];
    }

    private void set(int x, int y, Piece piece) {
        Piece old = pieces[x][y];
        pieces[x][y] = piece;

        for (var listener : listeners) {
            listener.onSet(x, y, old, piece);
        }
    }

    public boolean move(int fromX, int fromY, int toX, int toY) throws InvalidMoveException {
        Piece moving = pieces[fromX][fromY];

        if (moving == null) {
            throw new InvalidMoveException("No piece selected");
        }

        if (pieces[toX][toY] != null && moving.isWhite() == pieces[toX][toY].isWhite()) {
            throw new InvalidMoveException("You can't capture your own pieces");
        }

        if (!moving.canMove(fromX, fromY, toX, toY, this)) {
            throw new InvalidMoveException("A " + moving.getClass().getSimpleName().toLowerCase() + " can't move in this way");
        }

        Point<Integer> originalPosition = null;

        if (moving instanceof King) {
            if (moving.isWhite()) {
                canWhiteCastleKingside = false;
                canWhiteCastleQueenside = false;

                originalPosition = whiteKingPosition;
                whiteKingPosition = new Point<>(toX, toY);
            } else {
                canBlackCastleKingside = false;
                canBlackCastleQueenside = false;

                originalPosition = blackKingPosition;
                blackKingPosition = new Point<>(toX, toY);
            }
        } else if (moving instanceof Rook) {
            if (fromX == 0 && fromY == 0) {
                canBlackCastleQueenside = false;
            } else if (fromX == 0 && fromY == 7) {
                canBlackCastleKingside = false;
            } else if (fromX == 7 && fromY == 0) {
                canWhiteCastleQueenside = false;
            } else if (fromX == 7 && fromY == 7) {
                canWhiteCastleKingside = false;
            }
        }

        var originalPiece = pieces[toX][toY];
        pieces[toX][toY] = moving;
        pieces[fromX][fromY] = null;

        if (isChecked(turn)) {
            pieces[fromX][fromY] = pieces[toX][toY];
            pieces[toX][toY] = originalPiece;
            if (originalPosition != null) {
                if (moving.isWhite()) {
                    whiteKingPosition = originalPosition;
                } else {
                    blackKingPosition = originalPosition;
                }
            }
            throw new InvalidMoveException("You can't end your turn checked!");
        }

        boolean checkmate = false;
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

                                if (pieces[i][j].canMove(i, j, k, l, this) && (pieces[k][l] == null || pieces[k][l].isWhite() != pieces[i][j].isWhite())) {
                                    if (pieces[i][j] instanceof King) {
                                        if (pieces[i][j].isWhite()) {
                                            whiteKingPosition = new Point<>(k, l);
                                        } else {
                                            blackKingPosition = new Point<>(k, l);
                                        }
                                    }

                                    var original = pieces[k][l];
                                    pieces[k][l] = pieces[i][j];
                                    pieces[i][j] = null;
                                    boolean checked = isChecked(!turn);
                                    pieces[i][j] = pieces[k][l];
                                    pieces[k][l] = original;
                                    if (pieces[i][j] instanceof King) {
                                        if (pieces[i][j].isWhite()) {
                                            whiteKingPosition = new Point<>(i, j);
                                        } else {
                                            blackKingPosition = new Point<>(i, j);
                                        }
                                    }
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

        history.add(new Move(fromX, fromY, toX, toY, this));
        set(toX, toY, moving);
        set(fromX, fromY, null);
        turn = !turn;
        if (checkmate) {
            return true;
        }
        return false;
    }

    public boolean isChecked(boolean player) {
        if (player == Constants.WHITE) {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    if (pieces[i][j] != null && !pieces[i][j].isWhite() && pieces[i][j].canMove(i, j, whiteKingPosition.first, whiteKingPosition.second, this)) {
                        return true;
                    }
                }
            }
        } else {
            for (int i = 0; i < pieces.length; i++) {
                for (int j = 0; j < pieces[i].length; j++) {
                    if (pieces[i][j] != null && pieces[i][j].isWhite() && pieces[i][j].canMove(i, j, blackKingPosition.first, blackKingPosition.second, this)) {
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

        Move last = history.get(history.size() - 1);
        if (last.moving == Pawn.class && last.fromX == 6 && last.toX == 4) {
            builder.append(Constants.LETTERS[last.fromY]).append("3");
        } else if (last.moving == Pawn.class && last.fromX == 1 && last.toX == 3) {
            builder.append(Constants.LETTERS[last.fromY]).append("6");
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
}
