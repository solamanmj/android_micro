package com.example.vehicleservicetracker;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Locale;

public final class UiUtils {
    private UiUtils() {}

    public static LinearLayout createRoot(Context context) {
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(20, 20, 20, 20);
        root.setBackgroundColor(Color.rgb(245, 247, 250));
        return root;
    }

    public static TextView title(Context context, String text) {
        TextView title = new TextView(context);
        title.setText(text);
        title.setTextSize(25);
        title.setTextColor(Color.rgb(23, 54, 93));
        title.setPadding(0, 8, 0, 20);
        title.setGravity(android.view.Gravity.CENTER);
        title.setTypeface(null, android.graphics.Typeface.BOLD);
        return title;
    }

    public static EditText editText(Context context, String hint) {
        EditText edit = new EditText(context);
        edit.setHint(hint);
        edit.setSingleLine(true);
        edit.setPadding(12, 8, 12, 8);
        return edit;
    }

    public static Button button(Context context, String text, View.OnClickListener listener) {
        Button button = new Button(context);
        button.setText(text);
        button.setAllCaps(false);
        button.setOnClickListener(listener);
        return button;
    }

    public static void showDatePicker(Context context, EditText target) {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(context, (view, year, month, day) ->
                target.setText(String.format(Locale.US, "%04d-%02d-%02d",
                        year, month + 1, day)),
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
