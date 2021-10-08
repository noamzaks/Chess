package com.noamzaks.chess;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
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
            LinearLayout row = new LinearLayout(getApplicationContext());
            row.setLayoutParams(EQUAL);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 8; j++) {
                ImageView image = new ImageView(getApplicationContext());
                image.setOnClickListener(this);
                image.setBackgroundColor(((i + j) % 2 == 0) ? Colors.BACKGROUND_LIGHT : Colors.BACKGROUND_DARK);
                if (Board.INITIAL_BOARD[i][j] != null) {
                    image.setImageResource(Board.INITIAL_BOARD[i][j].getResource());
                }
                image.setTag(Board.INITIAL_BOARD[i][j]);
                image.setLayoutParams(EQUAL);
                row.addView(image);
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
                Toast.makeText(getApplicationContext(), "It's not your turn!", Toast.LENGTH_SHORT).show();
                return;
            }

            selected = view;
            return;
        }

        if (view == selected) {
            selected = null;
            return;
        }

        try {
            board.move(getX(selected), getY(selected), getX(view), getY(view));
        } catch (Board.InvalidMoveException exception) {
            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
            selected = null;
            return;
        }

        isWhitesTurn = !isWhitesTurn;
        selected = null;
    }

    private int getY(View view) {
        return ((LinearLayout) view.getParent()).indexOfChild(view);
    }

    private int getX(View view) {
        View parent = (View) view.getParent();
        return ((LinearLayout) parent.getParent()).indexOfChild(parent);
    }

    @Override
    public void onSet(int x, int y, Piece old, Piece current) {
        ImageView image = ((ImageView) ((LinearLayout) root.getChildAt(x)).getChildAt(y));
        image.setImageResource(current == null ? -1 : current.getResource());
        image.setTag(current);
    }
}