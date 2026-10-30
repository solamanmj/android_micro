package com.example.vehicleservicetracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class AddServiceActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private Spinner vehicleSpinner;
    private final ArrayList<Integer> vehicleIds = new ArrayList<>();
    private EditText dateInput, typeInput, descriptionInput, partsInput, labourInput, nextDateInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = UiUtils.createRoot(this);
        root.addView(UiUtils.title(this, "Add Service Record"));

        vehicleSpinner = new Spinner(this);
        root.addView(vehicleSpinner);
        loadVehicles();

        dateInput = addDateField(root, "Service date (tap to choose)");
        typeInput = UiUtils.editText(this, "Service type (Oil Change, Repair, etc.)");
        root.addView(typeInput);
        descriptionInput = UiUtils.editText(this, "Description of work");
        root.addView(descriptionInput);
        partsInput = UiUtils.editText(this, "Spare parts cost (₹)");
        partsInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(partsInput);
        labourInput = UiUtils.editText(this, "Labour cost (₹)");
        labourInput.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(labourInput);
        nextDateInput = addDateField(root, "Next service date (optional)");

        root.addView(UiUtils.button(this, "Save Service", v -> saveService()));
        root.addView(UiUtils.button(this, "Cancel", v -> finish()));
        setContentView(root);
    }

    private EditText addDateField(LinearLayout root, String hint) {
        EditText edit = UiUtils.editText(this, hint);
        edit.setFocusable(false);
        edit.setOnClickListener(v -> UiUtils.showDatePicker(this, edit));
        root.addView(edit);
        return edit;
    }

    private void loadVehicles() {
        ArrayList<String> labels = new ArrayList<>();
        vehicleIds.clear();
        try (Cursor cursor = databaseHelper.getVehicles()) {
            while (cursor.moveToNext()) {
                vehicleIds.add(cursor.getInt(cursor.getColumnIndexOrThrow("vehicle_id")));
                labels.add(cursor.getString(cursor.getColumnIndexOrThrow("vehicle_name")) +
                        " (" + cursor.getString(cursor.getColumnIndexOrThrow("registration_number")) + ")");
            }
        }
        if (labels.isEmpty()) labels.add("No vehicles saved - add a vehicle first");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, labels);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vehicleSpinner.setAdapter(adapter);
    }

    private void saveService() {
        if (vehicleIds.isEmpty()) {
            Toast.makeText(this, "Please add a vehicle first.", Toast.LENGTH_LONG).show();
            return;
        }

        String date = dateInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String nextDate = nextDateInput.getText().toString().trim();

        if (date.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please choose a service date and enter a service type.", Toast.LENGTH_LONG).show();
            return;
        }

        double parts, labour;
        try {
            parts = parseCost(partsInput.getText().toString());
            labour = parseCost(labourInput.getText().toString());
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "Enter valid non-negative costs.", Toast.LENGTH_LONG).show();
            return;
        }

        int selectedPosition = vehicleSpinner.getSelectedItemPosition();
        if (selectedPosition < 0 || selectedPosition >= vehicleIds.size()) {
            Toast.makeText(this, "Please select a valid vehicle.", Toast.LENGTH_SHORT).show();
            return;
        }

        long result = databaseHelper.addService(vehicleIds.get(selectedPosition),
                date, type, description, parts, labour, nextDate);

        if (result != -1) {
            Toast.makeText(this, String.format(Locale.getDefault(),
                    "Service saved. Total cost: ₹%.2f", parts + labour), Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save service record.", Toast.LENGTH_LONG).show();
        }
    }

    private double parseCost(String value) {
        if (value == null || value.trim().isEmpty()) return 0;
        double amount = Double.parseDouble(value.trim());
        if (amount < 0 || Double.isInfinite(amount) || Double.isNaN(amount)) {
            throw new NumberFormatException();
        }
        return amount;
    }
}
