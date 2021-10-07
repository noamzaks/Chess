package com.noamzaks.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

public class MainActivity extends AppCompatActivity {
    TableLayout board;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        board = findViewById(R.id.board);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        System.out.println(board.getWidth());

        for (int i = 0; i < 8; i++) {
            TableRow row = new TableRow(getApplicationContext());
            row.setMinimumWidth(64 * 8);
            row.setMinimumHeight(64);

            for (int j = 0; j < 8; j++) {
                ImageView image = new ImageView(getApplicationContext());
                image.setImageResource(R.drawable.black_bishop);
                image.setMinimumHeight(64);
                image.setMinimumWidth(64);
                image.setMaxHeight(64);
                image.setMaxWidth(64);

                row.addView(image);
            }

            board.addView(row);
        }
    }
}