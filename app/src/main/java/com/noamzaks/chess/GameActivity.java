package com.noamzaks.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.noamzaks.chess.game.Piece;
import com.noamzaks.chess.utilities.Point;
import com.noamzaks.chess.utilities.Toast;

public class GameActivity extends AppCompatActivity implements Board.OnSetListener {
    private LinearLayout root;
    protected LinearLayout aboveBlack, belowWhite;

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
                    image.setImageResource(piece.getResource());
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

        if (board.isChecked(board.getTurn())) {
            Toast.show(this, "Check!");
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
            image.setImageResource(current.getResource());
        }
        image.setTag(current);
    }
}