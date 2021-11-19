package com.noamzaks.chess;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
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
    private TextView blackName, whiteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        topBar = findViewById(R.id.game_top_bar);

        var extras = getIntent().getExtras();

        gameCode = extras.getString(EXTRAS_GAME_CODE);
        player = extras.getBoolean(EXTRAS_PLAYER);
        if (gameCode == null) {
            gameCode = getIntent().getData().getEncodedQuery().substring("game=".length());
            player = Constants.BLACK;
        }

        if (player == Constants.WHITE) {
            var button = new Button(this);
            button.setText("Invite Opponent");
            button.setOnClickListener((v) -> {
                var intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "Join my chess game!\nThe code is *" + gameCode + "*.\nYou may also join using this link:\nhttps://noamzaks.com/chess?game=" + gameCode);
                intent.setType("text/plain");
                startActivity(Intent.createChooser(intent, "Game Code"));
            });
            topBar.addView(button);
        }

        whiteName = new TextView(this);
        whiteName.setTextSize(28);
        whiteName.setTypeface(Typeface.DEFAULT_BOLD);
        whiteName.setTextColor(getResources().getColor(R.color.black));
        belowWhite.addView(whiteName);
        blackName = new TextView(this);
        blackName.setTextSize(20);
        blackName.setTypeface(Typeface.DEFAULT_BOLD);
        blackName.setTextColor(getResources().getColor(R.color.black));
        aboveBlack.addView(blackName);

        document = FirebaseFirestore.getInstance().document("/games/" + gameCode);

        document.addSnapshotListener((snapshot, __) -> {
            var data = snapshot.getData();
            if (data != null) {
                updating = true;
                board.setFEN((String) data.get("Game"));
                whiteName.setText((String) data.getOrDefault("White", ""));
                blackName.setText((String) data.getOrDefault("Black", ""));
                if (board.getTurn() == Constants.WHITE) {
                    animateText(whiteName, 20, 28, Color.valueOf(ContextCompat.getColor(this, R.color.black)), Color.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                    animateText(blackName, 28, 20, Color.valueOf(ContextCompat.getColor(this, R.color.yellow)), Color.valueOf(ContextCompat.getColor(this, R.color.black)));
                } else {
                    animateText(blackName, 20, 28, Color.valueOf(ContextCompat.getColor(this, R.color.black)), Color.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                    animateText(whiteName, 28, 20, Color.valueOf(ContextCompat.getColor(this, R.color.yellow)), Color.valueOf(ContextCompat.getColor(this, R.color.black)));
                }
                updating = false;
            }
        });

        document.set(
                Map.of(
                        "Game",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                        player ? "White" : "Black",
                        getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString("name", "Anonymous")
                ),
                SetOptions.merge()
        );

        board.addVerifier(this);
    }

    private void animateText(TextView text, float from, float to, Color fromColor, Color toColor) {
        var animator = ValueAnimator.ofFloat(0, 1);
        animator.addUpdateListener(__ -> {
            float value = (Float) animator.getAnimatedValue();
            text.setTextSize(from + (to - from) * value);
            text.setTextColor(Color.rgb(
                    fromColor.red() + (toColor.red() - fromColor.red()) * value,
                    fromColor.green() + (toColor.green() - fromColor.green()) * value,
                    fromColor.blue() + (toColor.blue() - fromColor.blue()) * value
                    )
            );
        });
        animator.start();
    }

    @Override
    public void onSet(int x, int y, Piece old, Piece current) {
        super.onSet(x, y, old, current);
        if (!updating) {
            document.set(Map.of("Game", board.toFEN()), SetOptions.merge());
        }
    }

    @Override
    public boolean can(Move move) {
        return board.getTurn() == player;
    }
}
