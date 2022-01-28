package com.noamzaks.chess.activity;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.noamzaks.chess.Board;
import com.noamzaks.chess.Constants;
import com.noamzaks.chess.R;
import com.noamzaks.chess.game.Move;
import com.noamzaks.chess.game.Piece;

import java.util.Map;

public class OnlineGameActivity extends GameActivity implements Board.MoveVerifier {
    public static final String EXTRAS_GAME_CODE = "game_code";
    public static final String EXTRAS_PLAYER = "player";

    private static final int TEXT_LARGE = 24;
    private static final int TEXT_SMALL = 18;
    
    private LinearLayout topBar;

    private DocumentReference document;
    private String gameCode;
    private boolean player;
    private boolean updating = false;
    private TextView blackName, whiteName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var extras = getIntent().getExtras();

        gameCode = extras.getString(EXTRAS_GAME_CODE);
        document = FirebaseFirestore.getInstance().document("/games/" + gameCode);
        player = extras.getBoolean(EXTRAS_PLAYER);
        if (gameCode == null) {
            gameCode = getIntent().getData().getEncodedQuery().substring("game=".length());
            player = Constants.BLACK;
        }

        topBar = findViewById(R.id.game_top_bar);

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
        whiteName.setTextSize(TEXT_LARGE);
        whiteName.setTypeface(Typeface.DEFAULT_BOLD);
        whiteName.setTextColor(getResources().getColor(R.color.black));
        belowWhite.addView(whiteName);
        blackName = new TextView(this);
        blackName.setTextSize(TEXT_SMALL);
        blackName.setTypeface(Typeface.DEFAULT_BOLD);
        blackName.setTextColor(getResources().getColor(R.color.black));
        aboveBlack.addView(blackName);

        document.addSnapshotListener((snapshot, __) -> {
            var data = snapshot.getData();
            if (data != null) {
                updating = true;
                board.setFEN((String) data.get("Game"));
                whiteName.setText((String) data.getOrDefault("White", ""));
                blackName.setText((String) data.getOrDefault("Black", ""));
                if (board.getTurn() == Constants.WHITE) {
                    animateText(whiteName, TEXT_SMALL, TEXT_LARGE, Color.valueOf(ContextCompat.getColor(this, R.color.black)), Color.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                    animateText(blackName, TEXT_LARGE, TEXT_SMALL, Color.valueOf(ContextCompat.getColor(this, R.color.yellow)), Color.valueOf(ContextCompat.getColor(this, R.color.black)));
                } else {
                    animateText(blackName, TEXT_SMALL, TEXT_LARGE, Color.valueOf(ContextCompat.getColor(this, R.color.black)), Color.valueOf(ContextCompat.getColor(this, R.color.yellow)));
                    animateText(whiteName, TEXT_LARGE, TEXT_SMALL, Color.valueOf(ContextCompat.getColor(this, R.color.yellow)), Color.valueOf(ContextCompat.getColor(this, R.color.black)));
                }
                updating = false;
            }
        });

        document.set(
                Map.of(
                        "Game",
                        "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1",
                        "Mode",
                        extras.getString(EXTRAS_MODE),
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
    public void can(Move move) throws Board.InvalidMoveException {
        if (board.getTurn() != player) {
            throw new Board.InvalidMoveException("You're not the " + (board.getTurn() == Constants.WHITE ? "white" : "black") + " player");
        }
    }
}
