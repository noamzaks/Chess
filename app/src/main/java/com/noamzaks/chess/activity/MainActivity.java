package com.noamzaks.chess.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.firebase.firestore.FirebaseFirestore;
import com.noamzaks.chess.Constants;
import com.noamzaks.chess.R;
import com.noamzaks.chess.utilities.Random;
import com.noamzaks.chess.utilities.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText gameCode,  length;
    private Spinner mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gameCode = findViewById(R.id.main_online_code);
        length = findViewById(R.id.main_length);
        mode = findViewById(R.id.main_mode);
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
        if (!verifyBattery()) {
            Toast.show(this, "You can't play online games without sufficient battery");
            return;
        }

        var intent = new Intent(this, OnlineGameActivity.class);
        intent.putExtra(OnlineGameActivity.EXTRAS_GAME_CODE, Random.generateGameCode());
        intent.putExtra(OnlineGameActivity.EXTRAS_PLAYER, Constants.WHITE);
        intent.putExtra(GameActivity.EXTRAS_LENGTH, getLength());
        intent.putExtra(GameActivity.EXTRAS_MODE, (String) mode.getSelectedItem());
        startActivity(intent);
    }

    public void joinOnlineGame(View v) throws InterruptedException {
        if (!verifyBattery()) {
            Toast.show(this, "You can't play online games without sufficient battery");
            return;
        }

        var game = gameCode.getText().toString();
        if (game.isEmpty()) {
            Toast.show(this, "Please enter the game code before joining");
            return;
        }

        var task = FirebaseFirestore.getInstance().document("/games/" + game).get();
        task.addOnSuccessListener((result) -> {
            var intent = new Intent(this, OnlineGameActivity.class);
            intent.putExtra(OnlineGameActivity.EXTRAS_GAME_CODE, game);
            intent.putExtra(OnlineGameActivity.EXTRAS_PLAYER, Constants.BLACK);
            intent.putExtra(GameActivity.EXTRAS_LENGTH, getLength());
            intent.putExtra(GameActivity.EXTRAS_MODE, result.getString("Mode"));
            startActivity(intent);
        });
    }

    private boolean verifyBattery() {
        BatteryManager bm = (BatteryManager) getApplicationContext().getSystemService(BATTERY_SERVICE);
        int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        return percentage > 20 || bm.isCharging();
    }

    public void createNewOfflineGame(View v) {
        var intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.EXTRAS_LENGTH, getLength());
        intent.putExtra(GameActivity.EXTRAS_MODE, (String) mode.getSelectedItem());
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