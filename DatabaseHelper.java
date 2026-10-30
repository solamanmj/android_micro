package com.example.vehicleservicetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "VehicleService.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE vehicles (" +
                "vehicle_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "vehicle_name TEXT NOT NULL, " +
                "registration_number TEXT NOT NULL UNIQUE, " +
                "vehicle_model TEXT NOT NULL, " +
                "vehicle_type TEXT NOT NULL)");

        db.execSQL("CREATE TABLE service_records (" +
                "service_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "vehicle_id INTEGER NOT NULL, " +
                "service_date TEXT NOT NULL, " +
                "service_type TEXT NOT NULL, " +
                "description TEXT, " +
                "parts_cost REAL NOT NULL DEFAULT 0, " +
                "labour_cost REAL NOT NULL DEFAULT 0, " +
                "next_service_date TEXT, " +
                "FOREIGN KEY(vehicle_id) REFERENCES vehicles(vehicle_id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Add future database migrations here. Avoid dropping tables so saved data is preserved.
    }

    public long addVehicle(String name, String registration, String model, String type) {
        ContentValues values = new ContentValues();
        values.put("vehicle_name", name.trim());
        values.put("registration_number", registration.trim().toUpperCase());
        values.put("vehicle_model", model.trim());
        values.put("vehicle_type", type.trim());
        return getWritableDatabase().insert("vehicles", null, values);
    }

    public Cursor getVehicles() {
        return getReadableDatabase().rawQuery(
                "SELECT vehicle_id, vehicle_name, registration_number, vehicle_model, vehicle_type " +
                        "FROM vehicles ORDER BY vehicle_name COLLATE NOCASE", null);
    }

    public long addService(int vehicleId, String date, String serviceType, String description,
                           double partsCost, double labourCost, String nextServiceDate) {
        ContentValues values = new ContentValues();
        values.put("vehicle_id", vehicleId);
        values.put("service_date", date);
        values.put("service_type", serviceType.trim());
        values.put("description", description.trim());
        values.put("parts_cost", partsCost);
        values.put("labour_cost", labourCost);
        values.put("next_service_date", nextServiceDate);
        return getWritableDatabase().insert("service_records", null, values);
    }

    public Cursor getServiceHistory() {
        return getReadableDatabase().rawQuery(
                "SELECT s.service_id, v.vehicle_name, v.registration_number, " +
                        "s.service_date, s.service_type, s.description, s.parts_cost, " +
                        "s.labour_cost, (s.parts_cost + s.labour_cost) AS total_cost, " +
                        "s.next_service_date " +
                        "FROM service_records s INNER JOIN vehicles v ON s.vehicle_id = v.vehicle_id " +
                        "ORDER BY s.service_date DESC, s.service_id DESC", null);
    }

    public double getTotalExpenses() {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COALESCE(SUM(parts_cost + labour_cost), 0) FROM service_records", null)) {
            return cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        }
    }

    public double getTotalExpensesForVehicle(int vehicleId) {
        try (Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COALESCE(SUM(parts_cost + labour_cost), 0) FROM service_records WHERE vehicle_id=?",
                new String[]{String.valueOf(vehicleId)})) {
            return cursor.moveToFirst() ? cursor.getDouble(0) : 0;
        }
    }
}
