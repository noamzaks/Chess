package com.noamzaks.chess;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.noamzaks.chess.game.Move;
import com.noamzaks.chess.game.Piece;

import java.util.Map;

public class OnlineGameActivity extends GameActivity implements Board.MoveVerifier {
    public static final String EXTRAS_GAME_CODE = "game_code";
    public static final String EXTRAS_PLAYER = "player";

    private LinearLayout topBar;

    private DocumentReference document;
    private String gameCode;
    private boolean player;
    private boolean updating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topBar = findViewById(R.id.game_top_bar);

        var extras = getIntent().getExtras();
        gameCode = extras.getString(EXTRAS_GAME_CODE);
        player = extras.getBoolean(EXTRAS_PLAYER);

        var label = new TextView(this);
        label.setTextSize(16);
        label.setText("Code: " + gameCode + "\n\nPlayer: " + ((player == Constants.WHITE) ? "White" : "Black"));
        topBar.addView(label);

        document = FirebaseFirestore.getInstance().document("/games/" + gameCode);

        document.addSnapshotListener((snapshot, __) -> {
            var data = snapshot.getData();
            if (data != null) {
                updating = true;
                board.setFEN((String) data.get("Game"));
                updating = false;
            }
        });

        document.set(Map.of("Game", "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1"));

        board.addVerifier(this);
    }

    @Override
    public void onSet(int x, int y, Piece old, Piece current) {
        super.onSet(x, y, old, current);
        if (!updating) {
            document.set(Map.of("Game", board.toFEN()));
        }
    }

    @Override
    public boolean can(Move move) {
        return board.getTurn() == player;
    }
}
