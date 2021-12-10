package com.noamzaks.chess.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.noamzaks.chess.Constants;
import com.noamzaks.chess.R;

import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    EditText name;
    SharedPreferences.Editor editor;
    Spinner theme;

    private static final List<String> THEMES = List.of("Alpha", "California", "Cardinal", "Cburnett", "Chess7", "Chessnut", "Companion", "Dubrovny", "Fantasy", "Fresca", "Gioco", "Governor", "Horsey", "Icpieces", "Kosal", "Leipzig", "Letter", "Libra", "Maestro", "Merida", "Pirouetti", "Pixel", "Reillycraig", "Riohacha", "Shapes", "Spatial", "Staunty", "Tatiana");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        var preferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        editor = preferences.edit();

        name = findViewById(R.id.settings_name);
        theme = findViewById(R.id.settings_theme);

        name.setText(preferences.getString("name", ""));
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                editor.putString("name", s.toString());
                editor.apply();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        var adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, THEMES);
        theme.setAdapter(adapter);
        theme.setSelection(THEMES.indexOf(preferences.getString("theme", "Alpha")));

        theme.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        editor.putString("theme", THEMES.get(position));
        editor.apply();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        editor.remove("theme");
        editor.apply();
    }
}