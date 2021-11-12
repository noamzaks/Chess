package com.noamzaks.chess;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.noamzaks.chess.utilities.Random;

public class MainActivity extends AppCompatActivity {
    private EditText gameCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameCode = findViewById(R.id.main_online_code);
    }

    public void createNewOnlineGame(View v) {
        var intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra(OnlineGameActivity.EXTRAS_GAME_CODE, Random.generateGameCode());
        intent.putExtra(OnlineGameActivity.EXTRAS_PLAYER, Constants.WHITE);
        startActivity(intent);
    }

    public void joinOnlineGame(View v) {
        var intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra(OnlineGameActivity.EXTRAS_GAME_CODE, gameCode.getText().toString());
        intent.putExtra(OnlineGameActivity.EXTRAS_PLAYER, Constants.BLACK);
        startActivity(intent);
    }

    public void createNewOfflineGame(View v) {
        startActivity(new Intent(this, GameActivity.class));
    }
}