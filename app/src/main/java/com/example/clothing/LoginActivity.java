package com.example.clothing;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    private EditText email, password;
    private Button loginBtn;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // 🔥 AUTO LOGIN CHECK
        if (isUserLoggedIn()) {
            openHomeActivity();
            return;
        }

        setupToolbar();
        setupWindowInsets();
        initViews();
        initDatabase();

        loginBtn.setOnClickListener(v -> loginUser());
        findViewById(R.id.signupText).setOnClickListener(v -> openSignup());
    }

    /** Check if user is already logged in */
    private boolean isUserLoggedIn() {
        String savedEmail = getSharedPreferences("user", MODE_PRIVATE)
                .getString("email", null);
        return savedEmail != null;
    }

    /** Open HomeActivity and clear back stack */
    private void openHomeActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");}
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    /** Apply window insets for edge-to-edge layout */
    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void initViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
    }

    /** Initialize SQLite database */
    private void initDatabase() {
        db = openOrCreateDatabase("myDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(fullname TEXT, email TEXT UNIQUE, password TEXT)");
    }

    /** Handle user login */
    private void loginUser() {
        String userEmail = email.getText().toString().trim();
        String userPassword = password.getText().toString().trim();

        if (userEmail.isEmpty() || userPassword.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT * FROM users WHERE email=? AND password=?",
                    new String[]{userEmail, userPassword}
            );

            if (cursor != null && cursor.moveToFirst()) {
                String name = cursor.getString(0);
                String emailFromDB = cursor.getString(1);

                // Save login details
                getSharedPreferences("user", MODE_PRIVATE)
                        .edit()
                        .putString("name", name)
                        .putString("email", emailFromDB)
                        .apply();

                openHomeActivity();
            } else {
                Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            }

            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Toast.makeText(this, "Database Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /** Open Signup Activity */
    private void openSignup() {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}