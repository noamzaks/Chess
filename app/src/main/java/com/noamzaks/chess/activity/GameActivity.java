package com.noamzaks.chess.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.Constants;
import com.noamzaks.chess.R;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.utilities.Point;
import com.noamzaks.chess.utilities.Toast;

public class GameActivity extends AppCompatActivity implements Board.OnSetListener {
    public static final String EXTRAS_LENGTH = "length";

    private LinearLayout root;
    private String theme;
    protected LinearLayout aboveBlack, belowWhite;
    private TextView whiteTimerText, blackTimerText;
    private CountDownTimer whiteTimer, blackTimer;

    protected Board board;

    private final LinearLayout.LayoutParams EQUAL = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
    );

    private void update() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                var image = (ImageView) ((FrameLayout) ((LinearLayout) root.getChildAt(i)).getChildAt(j)).getChildAt(1);
                var piece = board.get(new Point<>(i, j));
                if (piece != null) {
                    image.setTag(piece);
                    image.setImageResource(piece.getResource(theme));
                } else {
                    image.setTag(null);
                    image.setImageResource(-1);
                }
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        board = new Board();
        board.onSet(this);
        root = findViewById(R.id.game_root);
        aboveBlack = findViewById(R.id.game_above_black);
        belowWhite = findViewById(R.id.game_below_white);
        whiteTimerText = findViewById(R.id.game_white_timer);
        blackTimerText = findViewById(R.id.game_black_timer);

        theme = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE).getString("theme", "Alpha");

        for (int i = 0; i < 8; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(EQUAL);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 8; j++) {
                FrameLayout frame = new FrameLayout(this);
                ImageView image = new ImageView(this);

                int finalI = i;
                int finalJ = j;
                image.setOnTouchListener((view, event) -> {
                    if (view.getTag() == null) {
                        return false;
                    }

                    if (((Piece) view.getTag()).isWhite() != board.getTurn()) {
                        Toast.show(this, "It's not your turn!");
                        return false;
                    }

                    ClipData data = new ClipData("Chess Piece Position", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(String.valueOf(finalI)));
                    data.addItem(new ClipData.Item(String.valueOf(finalJ)));
                    view.startDragAndDrop(
                            data,
                            new View.DragShadowBuilder(image),
                            null,
                            0
                    );

                    view.performClick();

                    return true;
                });

                image.setOnDragListener((view, event) -> {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                        case DragEvent.ACTION_DROP:
                            ClipData data = event.getClipData();
                            move(new Point<>(Integer.parseInt(data.getItemAt(0).getText().toString()), Integer.parseInt(data.getItemAt(1).getText().toString())), new Point<>(getX(view), getY(view)));
                            return true;
                    }
                    return false;
                });

                ImageView background = new ImageView(this);
                background.setBackgroundColor(((i + j) % 2 == 0) ? getResources().getColor(R.color.background_light) : getResources().getColor(R.color.background_dark));

                frame.addView(background);
                frame.addView(image);
                frame.setLayoutParams(EQUAL);

                row.addView(frame);
            }

            root.addView(row);
        }

        update();

        var extras = getIntent().getExtras();

        var time = extras.getInt(EXTRAS_LENGTH, 0);
        var self = this;
        whiteTimer = new CountDownTimer(time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                whiteTimerText.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                try {
                    if (time != 0) {
                        new AlertDialog.Builder(self).setTitle("Game Over").setMessage("Black won the game because white ran out of time!").setCancelable(false).setPositiveButton("Reset", (__, ___) -> {
                            board.reset();
                            update();
                        }).show();
                    }
                } catch (WindowManager.BadTokenException __) {
                }
            }
        }.start();
        blackTimer = new CountDownTimer(time * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                blackTimerText.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (time != 0) {
                    try {
                        new AlertDialog.Builder(self).setTitle("Game Over").setMessage("White won the game because black ran out of time!").setCancelable(false).setPositiveButton("Reset", (__, ___) -> {
                            board.reset();
                            update();
                        }).show();
                    } catch (WindowManager.BadTokenException __) {
                    }
                }
            }
        };

        whiteTimerText.setText("" + time);
        blackTimerText.setText("" + time);
    }

    private void move(Point<Integer> from, Point<Integer> to) {
        if (from.first == to.first && from.second == to.second) {
            return;
        }

        try {
            if (board.move(from, to)) {
                new AlertDialog.Builder(this).setTitle("Checkmate").setMessage((!board.getTurn() ? "White" : "Black") + " won the game!").setCancelable(false).setPositiveButton("Reset", (__, ___) -> {
                    board.reset();
                    update();
                }).show();
                return;
            }
        } catch (Board.InvalidMoveException exception) {
            Toast.show(this, exception.getMessage());
            return;
        }

        var self = this;
        if (board.isChecked(board.getTurn())) {
            Toast.show(this, "Check!");
        }
        if (board.getTurn() == Constants.WHITE) {
            whiteTimer.start();
            blackTimer.cancel();
            if (!blackTimerText.getText().toString().equals("0")) {
                blackTimer = new CountDownTimer(Integer.parseInt(blackTimerText.getText().toString()) * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        blackTimerText.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        try {
                            new AlertDialog.Builder(self).setTitle("Game Over").setMessage("White won the game because black ran out of time!").setCancelable(false).setPositiveButton("Reset", (__, ___) -> {
                                board.reset();
                                update();
                            }).show();
                        } catch (WindowManager.BadTokenException __) {
                        }
                    }
                };
            }
        } else {
            blackTimer.start();
            whiteTimer.cancel();
            if (!whiteTimerText.getText().toString().equals("0")) {
                whiteTimer = new CountDownTimer(Integer.parseInt(whiteTimerText.getText().toString()) * 1000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        whiteTimerText.setText("" + millisUntilFinished / 1000);
                    }

                    public void onFinish() {
                        try {
                            new AlertDialog.Builder(self).setTitle("Game Over").setMessage("Black won the game because white ran out of time!").setCancelable(false).setPositiveButton("Reset", (__, ___) -> {
                                board.reset();
                                update();
                            }).show();
                        } catch (WindowManager.BadTokenException __) {
                        }
                    }
                };
            }
        }
    }

    private int getY(View view) {
        view = (View) view.getParent();
        return ((LinearLayout) view.getParent()).indexOfChild(view);
    }

    private int getX(View view) {
        view = (View) view.getParent();
        View parent = (View) view.getParent();
        return ((LinearLayout) parent.getParent()).indexOfChild(parent);
    }

    @Override
    public void onSet(int x, int y, Piece old, Piece current) {
        ImageView image = (ImageView) ((FrameLayout) ((LinearLayout) root.getChildAt(x)).getChildAt(y)).getChildAt(1);
        if (current == null) {
            image.setImageBitmap(null);
        } else {
            image.setImageResource(current.getResource(theme));
        }
        image.setTag(current);
    }
}