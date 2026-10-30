package com.example.vehicleservicetracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class AddVehicleActivity extends Activity {
    private DatabaseHelper databaseHelper;
    private EditText nameInput, registrationInput, modelInput, typeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = UiUtils.createRoot(this);
        root.addView(UiUtils.title(this, "Add Vehicle"));

        nameInput = addField(root, "Vehicle name (e.g., Honda Activa)");
        registrationInput = addField(root, "Registration number (e.g., KL-35-AB-1234)");
        modelInput = addField(root, "Vehicle model");
        typeInput = addField(root, "Vehicle type (Bike, Car, etc.)");

        root.addView(UiUtils.button(this, "Save Vehicle", v -> saveVehicle()));
        root.addView(UiUtils.button(this, "Cancel", v -> finish()));
        setContentView(root);
    }

    private EditText addField(LinearLayout root, String hint) {
        EditText edit = UiUtils.editText(this, hint);
        root.addView(edit);
        return edit;
    }

    private void saveVehicle() {
        String name = nameInput.getText().toString().trim();
        String registration = registrationInput.getText().toString().trim();
        String model = modelInput.getText().toString().trim();
        String type = typeInput.getText().toString().trim();

        if (name.isEmpty() || registration.isEmpty() || model.isEmpty() || type.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = databaseHelper.addVehicle(name, registration, model, type);
        if (id != -1) {
            Toast.makeText(this, "Vehicle saved successfully.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save. Registration number may already exist.", Toast.LENGTH_LONG).show();
        }
    }
}
