package com.noamzaks.chess.utilities;

import android.content.Context;

public class Toast {
    private static android.widget.Toast toast;

    public static void show(Context context, String text) {
        if (toast != null) {
            toast.cancel();
        }

        toast = android.widget.Toast.makeText(context, text, android.widget.Toast.LENGTH_SHORT);
        toast.show();
    }
}
