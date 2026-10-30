package com.example.vehicleservicetracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VehicleListActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private LinearLayout listContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = UiUtils.createRoot(this);
        root.addView(UiUtils.title(this, "Saved Vehicles"));
        listContainer = new LinearLayout(this);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        root.addView(listContainer, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));
        root.addView(UiUtils.button(this, "Back", v -> finish()));
        setContentView(root);
        loadVehicles();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (listContainer != null) loadVehicles();
    }

    private void loadVehicles() {
        listContainer.removeAllViews();
        int count = 0;
        try (Cursor cursor = databaseHelper.getVehicles()) {
            while (cursor.moveToNext()) {
                count++;
                TextView item = new TextView(this);
                item.setText(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_name")) +
                        "\nRegistration: " + cursor.getString(cursor.getColumnIndexOrThrow("registration_number")) +
                        "\nModel: " + cursor.getString(cursor.getColumnIndexOrThrow("vehicle_model")) +
                        "\nType: " + cursor.getString(cursor.getColumnIndexOrThrow("vehicle_type")));
                item.setTextSize(17);
                item.setPadding(8, 14, 8, 14);
                listContainer.addView(item);
            }
        }
        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No vehicles saved yet.");
            empty.setTextSize(16);
            listContainer.addView(empty);
        }
    }
}
