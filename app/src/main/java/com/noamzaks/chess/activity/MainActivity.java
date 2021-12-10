package com.noamzaks.chess.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.noamzaks.chess.Constants;
import com.noamzaks.chess.R;
import com.noamzaks.chess.utilities.Random;
import com.noamzaks.chess.utilities.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText gameCode,  length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameCode = findViewById(R.id.main_online_code);
        length = findViewById(R.id.main_length);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.main_menu_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return true;
    }

    public void createNewOnlineGame(View v) {
        var intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra(OnlineGameActivity.EXTRAS_GAME_CODE, Random.generateGameCode());
        intent.putExtra(OnlineGameActivity.EXTRAS_PLAYER, Constants.WHITE);
        intent.putExtra(GameActivity.EXTRAS_LENGTH, getLength());
        startActivity(intent);
    }

    public void joinOnlineGame(View v) {
        var game = gameCode.getText().toString();
        if (game.isEmpty()) {
            Toast.show(this, "Please enter the game code before joining");
            return;
        }

        var intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra(OnlineGameActivity.EXTRAS_GAME_CODE, game);
        intent.putExtra(OnlineGameActivity.EXTRAS_PLAYER, Constants.BLACK);
        intent.putExtra(GameActivity.EXTRAS_LENGTH, getLength());
        startActivity(intent);
    }

    public void createNewOfflineGame(View v) {
        var intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRAS_LENGTH, getLength());
        startActivity(intent);
    }

    private int getLength() {
        var text = length.getText().toString();
        if (text.isEmpty()) {
            return 0;
        }

        return Integer.parseInt(text);
    }
}