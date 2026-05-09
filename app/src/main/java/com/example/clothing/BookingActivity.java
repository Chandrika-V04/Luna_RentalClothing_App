package com.example.clothing;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

public class BookingActivity extends AppCompatActivity {

    // Views
    BottomNavigationView bottomNav;
    EditText etName, etDate, etDays, etAddress;
    TextView txtTotal, tvProductName, tvPrice, tvNoBooking;
    ImageView imgProduct;
    Button btnConfirm;
    RadioGroup paymentGroup;
    LinearLayout bookingLayout;

    int pricePerDay = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        // Toolbar setup
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Booking");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Find views
        etName = findViewById(R.id.etName);
        etDate = findViewById(R.id.etDate);
        etDays = findViewById(R.id.etDays);
        etAddress = findViewById(R.id.etAddress);
        txtTotal = findViewById(R.id.txtTotal);

        tvProductName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvPrice);
        imgProduct = findViewById(R.id.imgProduct);

        btnConfirm = findViewById(R.id.btnConfirm);
        paymentGroup = findViewById(R.id.paymentGroup);

        bookingLayout = findViewById(R.id.bookingLayout);
        tvNoBooking = findViewById(R.id.tvNoBooking);
        bottomNav = findViewById(R.id.bottomNav);

        // Get Intent data from HomeActivity / Adapter
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String price = intent.getStringExtra("price");
        int image = intent.getIntExtra("image", 0);
        if (name == null || price == null) {
            bookingLayout.setVisibility(LinearLayout.GONE);
            tvNoBooking.setVisibility(TextView.VISIBLE);
            tvNoBooking.setText("No item selected");
        } else {
            bookingLayout.setVisibility(LinearLayout.VISIBLE);
            tvNoBooking.setVisibility(TextView.GONE);
            tvProductName.setText(name);
            tvPrice.setText(price);
            if (image != 0) imgProduct.setImageResource(image);
            try {
                pricePerDay = Integer.parseInt(price.replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                pricePerDay = 0;
            }
        }

        // Date picker
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(
                    BookingActivity.this,
                    (view, year, month, dayOfMonth) ->
                            etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year),
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Calculate total dynamically
        etDays.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) { calculateTotal(); }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        // Confirm booking button
        btnConfirm.setOnClickListener(v -> {
            String userName = etName.getText().toString().trim();
            String date = etDate.getText().toString().trim();
            String daysStr = etDays.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            int selectedPaymentId = paymentGroup.getCheckedRadioButtonId();

            if (userName.isEmpty() || date.isEmpty() || daysStr.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedPaymentId == -1) {
                Toast.makeText(this, "Select payment method", Toast.LENGTH_SHORT).show();
                return;
            }

            int days;
            try { days = Integer.parseInt(daysStr); }
            catch (Exception e) { Toast.makeText(this, "Invalid days", Toast.LENGTH_SHORT).show(); return; }

            // Save to SQLite database (myDB)
            DBHelper dbHelper = new DBHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("product", tvProductName.getText().toString());
            values.put("price", tvPrice.getText().toString());
            values.put("date", date);
            values.put("days", days);
            values.put("address", address);
            values.put("total", txtTotal.getText().toString());

            RadioButton selectedPayment = findViewById(selectedPaymentId);
            String paymentMethod = selectedPayment.getText().toString();
            values.put("payment", paymentMethod);

            long result = db.insert("bookings", null, values);

            if (result != -1) Toast.makeText(this, "Booking Saved!", Toast.LENGTH_SHORT).show();
            else { Toast.makeText(this, "Error saving booking", Toast.LENGTH_SHORT).show(); return; }

            // Booking confirmation toast
            Toast.makeText(this,
                    "Booking Confirmed!\n" +
                            tvProductName.getText() +
                            "\nPayment: " + paymentMethod,
                    Toast.LENGTH_LONG).show();

            // Clear fields
            etName.setText("");
            etDate.setText("");
            etDays.setText("");
            etAddress.setText("");
            paymentGroup.clearCheck();
            txtTotal.setText("Total: ₹0");

            // Redirect to Profile
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        });

        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_cart);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
            } else if (id == R.id.nav_category) {
                startActivity(new Intent(this, CategoryActivity.class));
                finish();
            } else if (id == R.id.nav_cart) {
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }

    // Calculate total
    private void calculateTotal() {
        String daysStr = etDays.getText().toString();
        if (!daysStr.isEmpty()) {
            try {
                int days = Integer.parseInt(daysStr);
                int total = days * pricePerDay;
                txtTotal.setText("Total: ₹" + total);
            } catch (Exception e) {
                txtTotal.setText("Total: ₹0");
            }
        } else {
            txtTotal.setText("Total: ₹0");
        }
    }
}