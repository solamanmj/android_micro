package com.example.vehicleservicetracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;

public class ServiceHistoryActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private LinearLayout recordsContainer;
    private TextView totalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = UiUtils.createRoot(this);
        root.addView(UiUtils.title(this, "Service History"));

        totalText = new TextView(this);
        totalText.setTextSize(20);
        totalText.setGravity(Gravity.CENTER);
        totalText.setPadding(0, 0, 0, 12);
        root.addView(totalText);

        ScrollView scrollView = new ScrollView(this);
        recordsContainer = new LinearLayout(this);
        recordsContainer.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(recordsContainer);
        root.addView(scrollView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        root.addView(UiUtils.button(this, "Back", v -> finish()));
        setContentView(root);
        loadHistory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recordsContainer != null) loadHistory();
    }

    private void loadHistory() {
        recordsContainer.removeAllViews();
        totalText.setText(String.format(Locale.getDefault(),
                "Total service expenses: ₹%.2f", databaseHelper.getTotalExpenses()));

        int count = 0;
        try (Cursor cursor = databaseHelper.getServiceHistory()) {
            while (cursor.moveToNext()) {
                count++;
                String vehicle = cursor.getString(cursor.getColumnIndexOrThrow("vehicle_name"));
                String registration = cursor.getString(cursor.getColumnIndexOrThrow("registration_number"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("service_date"));
                String type = cursor.getString(cursor.getColumnIndexOrThrow("service_type"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                double parts = cursor.getDouble(cursor.getColumnIndexOrThrow("parts_cost"));
                double labour = cursor.getDouble(cursor.getColumnIndexOrThrow("labour_cost"));
                double total = cursor.getDouble(cursor.getColumnIndexOrThrow("total_cost"));
                String nextDate = cursor.getString(cursor.getColumnIndexOrThrow("next_service_date"));

                TextView item = new TextView(this);
                item.setText(String.format(Locale.getDefault(),
                        "%s (%s)\nDate: %s\nService: %s\nDescription: %s\n" +
                                "Parts: ₹%.2f | Labour: ₹%.2f\nTotal: ₹%.2f\nNext service: %s",
                        vehicle, registration, date, type,
                        description == null || description.isEmpty() ? "—" : description,
                        parts, labour, total,
                        nextDate == null || nextDate.isEmpty() ? "Not set" : nextDate));
                item.setTextSize(16);
                item.setPadding(12, 14, 12, 14);
                recordsContainer.addView(item);

                android.view.View divider = new android.view.View(this);
                divider.setBackgroundColor(0xFFCCCCCC);
                recordsContainer.addView(divider, new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, 1));
            }
        }

        if (count == 0) {
            TextView empty = new TextView(this);
            empty.setText("No service records yet. Add a vehicle, then add a service record.");
            empty.setTextSize(16);
            empty.setPadding(8, 20, 8, 20);
            recordsContainer.addView(empty);
        }
    }
}
