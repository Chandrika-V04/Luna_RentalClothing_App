package com.example.clothing;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupActivity extends AppCompatActivity {

    private EditText fullname, email, password, confirmPassword;
    private Button signupBtn;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        setupToolbar();
        setupInsets();
        initViews();
        initDatabase();

        signupBtn.setOnClickListener(v -> registerUser());

        findViewById(R.id.loginText).setOnClickListener(v -> {
            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");}
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirmPassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.signupBtn);
    }

    private void initDatabase() {
        db = openOrCreateDatabase("myDB", MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS users(fullname TEXT, email TEXT UNIQUE, password TEXT)");
    }

    private void registerUser() {
        String name = fullname.getText().toString().trim();
        String mail = email.getText().toString().trim();
        String pass = password.getText().toString().trim();
        String confirm = confirmPassword.getText().toString().trim();

        if (name.isEmpty() || mail.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!pass.equals(confirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        if (pass.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mail).matches()) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        Cursor cursor = db.rawQuery("SELECT * FROM users WHERE email=?", new String[]{mail});
        if (cursor.moveToFirst()) {
            Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
            cursor.close();
            return;
        }
        if (cursor != null) cursor.close();

        // Insert user into DB
        db.execSQL("INSERT INTO users(fullname,email,password) VALUES(?,?,?)", new Object[]{name, mail, pass});
        Toast.makeText(this, "Signup Successful", Toast.LENGTH_SHORT).show();

        // Auto-login after signup
        getSharedPreferences("user", MODE_PRIVATE)
                .edit()
                .putString("name", name)
                .putString("email", mail)
                .apply();

        // Open HomeActivity and clear back stack
        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}