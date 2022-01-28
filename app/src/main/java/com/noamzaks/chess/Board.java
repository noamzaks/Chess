package com.noamzaks.chess;

import com.noamzaks.chess.game.Bishop;
import com.noamzaks.chess.game.King;
import com.noamzaks.chess.game.Knight;
import com.noamzaks.chess.game.Move;
import com.noamzaks.chess.game.Pawn;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.game.Queen;
import com.noamzaks.chess.game.Rook;
import com.noamzaks.chess.utilities.Point;

import java.util.ArrayList;
import java.util.List;

public class Board {
    public interface OnSetListener {
        void onSet(int x, int y, Piece old, Piece current);
    }

    public interface MoveVerifier {
        void can(Move move) throws InvalidMoveException;
    }

    public interface OnCheckListener {
        void onCheck(boolean player, boolean mate);
    }

    public interface OnGameOverListener {
        void onGameOver(boolean winner, String reason);
    }

    public interface OnCaptureListener {
        void onCapture(Point<Integer> location, Piece captured);
    }

    public interface NewBoardFactory {
        Piece[][] createNewBoard();
    }

    public static class InvalidMoveException extends Exception {
        public InvalidMoveException(String reason) {
            super(reason);
        }
    }

    public boolean isOver = false;

    private Piece[][] pieces = new Piece[8][8];
    private boolean turn = Constants.WHITE;
    private List<Move> history = new ArrayList<>();
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
    private final List<MoveVerifier> verifiers = new ArrayList<>();
    private final List<OnCheckListener> onCheckListeners = new ArrayList<>();
    private final List<OnGameOverListener> onGameOverListeners = new ArrayList<>();
    private final List<OnCaptureListener> onCaptureListeners = new ArrayList<>();
    private NewBoardFactory newBoardFactory = new NewBoardFactory() {
        @Override
        public Piece[][] createNewBoard() {
            var result = new Piece[8][8];
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    result[i][j] = Constants.INITIAL_BOARD[i][j];
                }
            }
            return result;
        }
    };

    public Board() {
        reset();
    }

    public void reset() {
        pieces = newBoardFactory.createNewBoard();
        history = new ArrayList<>();
        turn = Constants.WHITE;
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

    public void move(Point<Integer> from, Point<Integer> to) throws InvalidMoveException {
        Piece moving = pieces[from.first][from.second];
        var current = get(to);

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
        var isChecking = isChecked(!turn);
        if (isChecking) {
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
        var move = new Move(from, to, this);
        for (var verifier : verifiers) {
            verifier.can(move);
        }
        if (isChecking) {
            for (var listener : onCheckListeners) {
                listener.onCheck(!turn, checkmate);
            }
        }
        history.add(move);
        turn = !turn;
        set(to.first, to.second, moving);
        set(from.first, from.second, null);
        if (current != null) {
            for (var listener : onCaptureListeners) {
                listener.onCapture(to, current);
            }
        }
        if (checkmate) {
            over(!turn, (turn == Constants.WHITE ? "White" : "Black") + " was checkmated");
        }
    }

    public void set(Point<Integer> location, Piece piece) {
        var current = get(location);
        if (piece == null && current instanceof King) {
            over(current.isWhite() ? Constants.BLACK : Constants.WHITE, "King destroyed");
        }
        set(location.first, location.second, piece);
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

    public void over(boolean player, String reason) {
        for (var listener : onGameOverListeners) {
            listener.onGameOver(player, reason);
        }
        isOver = true;
    }

    public void addSetListener(OnSetListener listener) {
        listeners.add(listener);
    }
    public void addVerifier(MoveVerifier verifier) {
        verifiers.add(verifier);
    }
    public void addCheckListener(OnCheckListener listener) { onCheckListeners.add(listener); }
    public void addGameOverListener(OnGameOverListener listener) { onGameOverListeners.add(listener); }
    public void addCaptureListener(OnCaptureListener listener) { onCaptureListeners.add(listener); }
    public void setNewBoardFactory(NewBoardFactory factory) { newBoardFactory = factory; pieces = newBoardFactory.createNewBoard(); }

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
        if (last != null && last.moving instanceof Pawn && last.from.first == 6 && last.to.first == 4) {
            builder.append(Constants.LETTERS[last.from.second]).append("3");
        } else if (last != null && last.moving instanceof Pawn && last.from.first == 1 && last.to.first == 3) {
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

    public void setFEN(String game) {
        String[] parts = game.split(" ");

        String[] rows = parts[0].split("\\/");
        for (int i = 0; i < rows.length; i++) {
            var row = rows[i];
            int letter = 0;
            int rowIndex = 0;
            while (letter < row.length()) {
                char current = row.charAt(letter);
                if (Character.isDigit(current)) {
                    var skip = Character.digit(current, 10);
                    for (int j = 0; j < skip; j++) {
                        set(i, rowIndex + j, null);
                    }
                    rowIndex += skip;
                } else {
                    var white = Character.isUpperCase(current);
                    current = Character.toLowerCase(current);

                    switch (current) {
                        case 'p': set(i, rowIndex, new Pawn(white)); break;
                        case 'n': set(i, rowIndex, new Knight(white)); break;
                        case 'b': set(i, rowIndex, new Bishop(white)); break;
                        case 'r': set(i, rowIndex, new Rook(white)); break;
                        case 'q': set(i, rowIndex, new Queen(white)); break;
                        case 'k':
                            set(i, rowIndex, new King(white));
                            if (white) {
                                whiteKingPosition = new Point<>(i, rowIndex);
                            } else {
                                blackKingPosition = new Point<>(i, rowIndex);
                            }
                            break;
                    }
                    rowIndex++;
                }
                letter++;
            }
        }

        turn = parts[1].equals("w") ? Constants.WHITE : Constants.BLACK;
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
