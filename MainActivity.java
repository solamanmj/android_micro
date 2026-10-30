package com.example.vehicleservicetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;

public class MainActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private TextView totalText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = UiUtils.createRoot(this);
        TextView title = UiUtils.title(this, "Vehicle Service Tracker");
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("Keep your vehicle records and maintenance expenses in one place.");
        subtitle.setTextSize(16);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setPadding(8, 8, 8, 18);
        root.addView(subtitle);

        totalText = new TextView(this);
        totalText.setTextSize(20);
        totalText.setGravity(Gravity.CENTER);
        totalText.setPadding(8, 14, 8, 14);
        root.addView(totalText);

        addButton(root, "Add Vehicle", () -> startActivity(new Intent(this, AddVehicleActivity.class)));
        addButton(root, "Add Service Record", () -> startActivity(new Intent(this, AddServiceActivity.class)));
        addButton(root, "View Service History", () -> startActivity(new Intent(this, ServiceHistoryActivity.class)));
        addButton(root, "View Vehicles", () -> startActivity(new Intent(this, VehicleListActivity.class)));
        addButton(root, "Refresh Total Expenses", this::refreshTotal);
        setContentView(root);
    }

    private void addButton(LinearLayout root, String label, Runnable action) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setOnClickListener(v -> action.run());
        root.addView(button);
    }

    private void refreshTotal() {
        totalText.setText(String.format(Locale.getDefault(),
                "Total service expenses: ₹%.2f", databaseHelper.getTotalExpenses()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (databaseHelper != null && totalText != null) refreshTotal();
    }
}
