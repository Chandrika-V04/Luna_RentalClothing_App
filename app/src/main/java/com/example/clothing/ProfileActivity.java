package com.example.clothing;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {
    Button btnSettings, btnLogout, btnBookings, btnUpdate;
    BottomNavigationView bottomNav;
    LinearLayout layoutSettings, layoutBookings;
    TextView tvName, tvEmail;
    EditText etName, etEmail, etAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");}
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // 🔹 Views
        btnSettings = findViewById(R.id.btnSettings);
        btnBookings = findViewById(R.id.btnBookings);
        btnLogout = findViewById(R.id.btnLogout);

        layoutSettings = findViewById(R.id.layoutSettings);
        layoutBookings = findViewById(R.id.layoutBookings);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etAddress = findViewById(R.id.etAddress);
        btnUpdate = findViewById(R.id.btnUpdate);

        bottomNav = findViewById(R.id.bottomNav);

        // 🔥 Load user from SQLite
        loadUserDataFromDB();
        // 📦 BOOKINGS CLICK
        btnBookings.setOnClickListener(v -> {
            if (layoutBookings.getVisibility() == LinearLayout.GONE) {
                layoutBookings.setVisibility(LinearLayout.VISIBLE);
                layoutSettings.setVisibility(LinearLayout.GONE);
                loadBookings();
            } else {
                layoutBookings.setVisibility(LinearLayout.GONE);}
        });

        // ⚙️ SETTINGS CLICK
        btnSettings.setOnClickListener(v -> {
            if (layoutSettings.getVisibility() == LinearLayout.GONE) {
                layoutSettings.setVisibility(LinearLayout.VISIBLE);
                layoutBookings.setVisibility(LinearLayout.GONE);
                loadUserDataFromDB();
            } else {
                layoutSettings.setVisibility(LinearLayout.GONE);
            }
        });

        // 🚪 LOGOUT
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
            prefs.edit().clear().apply();
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        // =========================
        // 🔻 BOTTOM NAV
        // =========================
        bottomNav.setSelectedItemId(R.id.nav_profile);
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_category) {
                startActivity(new Intent(this, CategoryActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_cart) {
                startActivity(new Intent(this, BookingActivity.class));
                finish();
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                return true;
            }
            return false;
        });

        // 🔄 UPDATE PROFILE CLICK
        btnUpdate.setOnClickListener(v -> updateUserData());
    }

    // 🔹 Load user from SQLite
    private void loadUserDataFromDB() {
        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String userEmail = prefs.getString("email", ""); // logged-in email

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{userEmail});
        if (cursor.moveToFirst()) {
            String name = cursor.getString(0);
            String email = cursor.getString(1);
            String address = "";
            try { address = cursor.getString(3); } catch (Exception ignored) {}

            tvName.setText(name);
            tvEmail.setText(email);

            etName.setText(name);
            etEmail.setText(email);
            etAddress.setText(address);
        }
        cursor.close();
    }

    // 🔹 Update user data in SQLite + SharedPreferences
    private void updateUserData() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String address = etAddress.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Name and Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences prefs = getSharedPreferences("user", MODE_PRIVATE);
        String oldEmail = prefs.getString("email", "");

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.execSQL("UPDATE users SET fullname=?, email=?, address=? WHERE email=?",
                new String[]{name, email, address, oldEmail});
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("address", address);
        editor.apply();

        tvName.setText(name);
        tvEmail.setText(email);

        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
    }

    // 🔹 Load bookings
    private void loadBookings() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM bookings", null);
        layoutBookings.removeAllViews();
        if (cursor.getCount() == 0) {
            TextView tv = new TextView(this);
            tv.setText("No bookings yet");
            tv.setPadding(10, 20, 10, 20);
            layoutBookings.addView(tv);
        } else {
            while (cursor.moveToNext()) {
                String product = cursor.getString(1);
                String date = cursor.getString(3);
                int days = cursor.getInt(4);
                String total = cursor.getString(6);

                TextView tv = new TextView(this);
                tv.setText("• " + product +
                        "\nDate: " + date +
                        "\nDays Needed: " + days +
                        "\nTotal: " + total);
                tv.setPadding(10, 20, 10, 20);

                layoutBookings.addView(tv);
            }
        }
        cursor.close();
    }
    // 🔄 Refresh user data
    @Override
    protected void onResume() {
        super.onResume();
        loadUserDataFromDB();
    }
}