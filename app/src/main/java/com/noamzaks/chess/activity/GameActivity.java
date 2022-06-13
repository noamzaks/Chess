package com.noamzaks.chess.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.noamzaks.chess.Board;
import com.noamzaks.chess.Constants;
import com.noamzaks.chess.R;
import com.noamzaks.chess.game.King;
import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.utilities.Point;
import com.noamzaks.chess.utilities.Toast;

import java.util.Arrays;
import java.util.Collections;

public class GameActivity extends AppCompatActivity implements Board.OnSetListener, Board.OnGameOverListener {
    public static final String EXTRAS_LENGTH = "length";
    public static final String EXTRAS_MODE = "mode";

    private LinearLayout root;
    private String theme;
    protected LinearLayout aboveBlack, belowWhite;
    private TextView whiteTimerText, blackTimerText;
    private Button showAnalysis;

    protected Board board;
    protected long whiteTime, blackTime;
    protected CountDownTimer whiteTimer, blackTimer;

    private static final LinearLayout.LayoutParams EQUAL = new LinearLayout.LayoutParams(
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
                    image.setImageBitmap(null);
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
        board.addSetListener(this);
        board.addGameOverListener(this);
        root = findViewById(R.id.game_root);
        aboveBlack = findViewById(R.id.game_above_black);
        belowWhite = findViewById(R.id.game_below_white);
        whiteTimerText = findViewById(R.id.game_white_timer);
        blackTimerText = findViewById(R.id.game_black_timer);
        showAnalysis = findViewById(R.id.game_show_analysis);

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


        var extras = getIntent().getExtras();

        var time = extras.getInt(EXTRAS_LENGTH, 0);
        if (extras.containsKey(EXTRAS_MODE)) {
            setGameMode(extras.getString(EXTRAS_MODE));
        }

        update();
        setWhiteTime(time * 1000);
        setBlackTime(time * 1000);
        whiteTime = time * 1000;
        blackTime = time * 1000;

        if (time == 0) {
            whiteTimerText.setVisibility(View.GONE);
            blackTimerText.setVisibility(View.GONE);
        }

        whiteTimerText.setText("" + time);
        blackTimerText.setText("" + time);

        showAnalysis.setOnClickListener((v) -> {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.lichess.org/analysis/" + board.toFEN())));
        });
    }

    protected void setGameMode(String mode) {
        switch (mode) {
            case "3-Check":
                var listener = new Board.OnCheckListener() {
                    public int whiteChecks = 0, blackChecks = 0;

                    @Override
                    public void onCheck(boolean player, boolean mate) {
                        System.out.println(player + "," + mate);
                        if (player == Constants.WHITE) {
                            whiteChecks++;
                            if (whiteChecks == 3) {
                                board.over(Constants.BLACK, "White was checked three times");
                            }
                        } else {
                            blackChecks++;
                            if (blackChecks == 3) {
                                board.over(Constants.WHITE, "Black was checked three times");
                            }
                        }
                    }
                };
                board.addCheckListener(listener);
                board.addGameOverListener((__, ___) -> {
                    listener.whiteChecks = 0;
                    listener.blackChecks = 0;
                });
                break;
            case "King of the Hill":
                board.addSetListener((x, y, old, current) -> {
                    if ((x == 3 || x == 4) && (y == 3 || y == 4) && current instanceof King) {
                        board.over(current.isWhite() ? Constants.WHITE : Constants.BLACK, "Reached the center");
                    }
                });
            case "Atomic":
                board.addCaptureListener((location, piece) -> {
                    int x = location.first, y = location.second;
                    board.set(new Point<>(x, y), null);
                    if (y - 1 >= 0)
                        board.set(new Point<>(x, y - 1), null);
                    if (y + 1 <= 7)
                        board.set(new Point<>(x, y + 1), null);
                    if (x + 1 <= 7 && y - 1 >= 0)
                        board.set(new Point<>(x + 1, y - 1), null);
                    if (x + 1 <= 7)
                        board.set(new Point<>(x + 1, y), null);
                    if (x + 1 <= 7 && y + 1 <= 7)
                        board.set(new Point<>(x + 1, y + 1), null);
                    if (x - 1 >= 0 && y - 1 >= 0)
                        board.set(new Point<>(x - 1, y - 1), null);
                    if (x - 1 >= 0)
                        board.set(new Point<>(x - 1, y), null);
                    if (x - 1 >= 0 && y + 1 <= 7)
                        board.set(new Point<>(x - 1, y + 1), null);
                });
            case "Chess 960":
                board.setNewBoardFactory(() -> {
                    var result = new Piece[8][8];
                    for (int i = 0; i < 8; i++) {
                        var row = new Piece[8];
                        for (int j = 0; j < 8; j++) {
                            row[j] = Constants.INITIAL_BOARD[i][j];
                        }
                        if (i == 0 || i == 7) {
                            Collections.shuffle(Arrays.asList(row));
                        }
                        for (int j = 0; j < 8; j++) {
                            result[i][j] = row[j];
                        }
                    }
                    return result;
                });
                board.reset();
            default:
                break;
        }
    }

    protected void setWhiteTime(long time) {
        if (whiteTimer != null) {
            whiteTimer.cancel();
        }
        if (time != 0) {
            whiteTimerText.setVisibility(View.VISIBLE);
            blackTimerText.setVisibility(View.VISIBLE);
        }
        whiteTimer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                whiteTime = millisUntilFinished;
                whiteTimerText.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                try {
                    if (time != 0) {
                        board.over(Constants.BLACK, "White ran out of time");
                    }
                } catch (WindowManager.BadTokenException __) {
                }
            }
        };
    }

    protected void setBlackTime(long time) {
        if (blackTimer != null) {
            blackTimer.cancel();
        }
        blackTimer = new CountDownTimer(time, 1000) {
            public void onTick(long millisUntilFinished) {
                blackTime = millisUntilFinished;
                blackTimerText.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                if (time != 0) {
                    try {
                        board.over(Constants.WHITE, "Black ran out of time");
                    } catch (WindowManager.BadTokenException __) {
                    }
                }
            }
        };
    }

    private void move(Point<Integer> from, Point<Integer> to) {
        if (from.first == to.first && from.second == to.second) {
            return;
        }

        try {
            board.move(from, to);
            if (board.isOver) {
                return;
            }
        } catch (Board.InvalidMoveException exception) {
            Toast.show(this, exception.getMessage());
            return;
        }

        if (board.isChecked(board.getTurn())) {
            Toast.show(this, "Check!");
        }
        if (board.getTurn() == Constants.WHITE) {
            setBlackTime(Integer.parseInt(blackTimerText.getText().toString()) * 1000);
            whiteTimer.start();
        } else {
            setWhiteTime(Integer.parseInt(whiteTimerText.getText().toString()) * 1000);
            blackTimer.start();
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

    @Override
    public void onGameOver(boolean winner, String reason) {
        new AlertDialog.Builder(this).setTitle((winner == Constants.WHITE ? "White" : "Black") + " won the game!").setMessage(reason).setCancelable(false).setPositiveButton("Reset", (__, ___) -> {
            board.reset();
            update();
        }).show();
    }
}