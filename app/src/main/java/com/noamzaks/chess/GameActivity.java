package com.noamzaks.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

public class GameActivity extends AppCompatActivity {
    private LinearLayout root;

    private final LinearLayout.LayoutParams EIGHTH = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT,
            100 / 8
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        root = findViewById(R.id.game_root);

        for (int i = 0; i < 8; i++) {
            LinearLayout row = new LinearLayout(getApplicationContext());
            row.setLayoutParams(EIGHTH);
            row.setOrientation(LinearLayout.HORIZONTAL);

            for (int j = 0; j < 8; j++) {
                Button button = new Button(getApplicationContext());
                button.setBackgroundResource(R.drawable.black_bishop);
                button.setLayoutParams(EIGHTH);
                row.addView(button);
            }
            root.addView(row);
        }
    }
}