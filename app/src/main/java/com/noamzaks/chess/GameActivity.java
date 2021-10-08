package com.noamzaks.chess;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipDescription;
import android.os.Build;
import android.os.Bundle;
import android.view.DragEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.noamzaks.chess.game.Piece;

public class GameActivity extends AppCompatActivity implements View.OnClickListener, Board.OnSetListener {
    private LinearLayout root;

    private View selected;
    private boolean isWhitesTurn = true;
    private Board board;

    private final LinearLayout.LayoutParams EQUAL = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            1
    );

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        board = new Board();
        board.onSet(this);
        root = findViewById(R.id.game_root);

        for (int i = 0; i < 8; i++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(EQUAL);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 8; j++) {
                FrameLayout frame = new FrameLayout(this);
                ImageView image = new ImageView(this);
                image.setOnClickListener(this);
                if (Board.INITIAL_BOARD[i][j] != null) {
                    image.setImageResource(Board.INITIAL_BOARD[i][j].getResource());
                }
                image.setTag(Board.INITIAL_BOARD[i][j]);

                int finalI = i;
                int finalJ = j;
                image.setOnTouchListener((view, event) -> {
                    if (((Piece) view.getTag()).isWhite() != isWhitesTurn) {
                        Toast.makeText(this, "It's not your turn!", Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    selected = null;

                    ClipData data = new ClipData("Chess Piece Position", new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, new ClipData.Item(String.valueOf(finalI)));
                    data.addItem(new ClipData.Item(String.valueOf(finalJ)));
                    view.startDrag(
                            data,
                            new View.DragShadowBuilder(image),
                            null,
                            0
                    );

                    return true;
                });

                image.setOnDragListener((view, event) -> {
                    switch (event.getAction()) {
                        case DragEvent.ACTION_DRAG_STARTED:
                            return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                        case DragEvent.ACTION_DROP:
                            ClipData data = event.getClipData();
                            move(Integer.parseInt(data.getItemAt(0).getText().toString()), Integer.parseInt(data.getItemAt(1).getText().toString()), getX(view), getY(view));
                            return true;
                    }
                    return false;
                });

                ImageView background = new ImageView(this);
                background.setBackgroundColor(((i + j) % 2 == 0) ? Colors.BACKGROUND_LIGHT : Colors.BACKGROUND_DARK);

                frame.addView(background);
                frame.addView(image);
                frame.setLayoutParams(EQUAL);

                row.addView(frame);
            }

            root.addView(row);
        }
    }

    @Override
    public void onClick(View view) {
        Piece target = (Piece) view.getTag();
        if (selected == null) {
            if (target == null) {
                return;
            }

            if (target.isWhite() != isWhitesTurn) {
                Toast.makeText(this, "It's not your turn!", Toast.LENGTH_SHORT).show();
                return;
            }

            selected = view;
            return;
        }

        if (view == selected) {
            selected = null;
            return;
        }

        move(getX(selected), getY(selected), getX(view), getY(view));
    }

    private void move(int fromX, int fromY, int toX, int toY) {
        try {
            board.move(fromX, fromY, toX, toY);
        } catch (Board.InvalidMoveException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            selected = null;
            return;
        }

        isWhitesTurn = !isWhitesTurn;
        selected = null;
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
        image.setImageResource(current == null ? -1 : current.getResource());
        image.setTag(current);
    }
}