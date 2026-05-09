package com.example.clothing;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BottomNavigationView bottomNav;

    // Sample product data
    private final String[] names = {"Blazer", "Doctor Costume","Saree","Halloween Costume", "Overcoat", "Gown","Ivory Floral Lehenga","Dhoti","Girls' Maroon Lehenga","Boys' Kurti Set",
                                    "Bronze Blazer","Green & Gold Lehenga","Blue Sherwani","Wine Anarkali Gown","Army Costume"};
    private final String[] desc = {
            "Black Gold-Embroidered Blazer",
            "Doctor Costume for kids",
            "Ethereal Maroon Georgette Saree",
            "Halloween Costume for kids",
            "Burgundy Wool Overcoat",
            "Burgundy Wool Overcoat",
            "Wine Velvet A-Line Gown",
            "A ivory lehenga with embroidery",
            "Classic South Indian white shirt and Dhoti",
            "Modern ethnic set with a skirt and blouse.",
            "A kurta-pyjama and maroon embroidered coat.",
            "Dark Bronze Textured Blazer with black embroidery.",
            "A heavy bridal-style lehenga",
            "A premium deep blue silk Embroidered Sherwani",
            "A flowy, floor-length wine Anarkali",
            "Army Officer Costume"
    };
    private final String[] price = {"₹999/day","₹399/day", "₹1999/day", "₹499/day", "₹799/day","₹499/day","₹599/day","₹999/day","₹599/day","₹999/day","₹2999/day","₹999/day","₹1999/day","₹799/day","₹399/day"};
    private final int[] images = {
            R.drawable.men1,
            R.drawable.t1,
            R.drawable.women1,
            R.drawable.t6,
            R.drawable.men2,
            R.drawable.women2,
            R.drawable.kid2,
            R.drawable.kid3,
            R.drawable.kid4,
            R.drawable.kid5,
            R.drawable.men3,
            R.drawable.women3,
            R.drawable.men4,
            R.drawable.women4,
            R.drawable.t2
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ImageButton backBtn = findViewById(R.id.btnBack);
        backBtn.setOnClickListener(v -> finish());

        // 📂 RECEIVE CATEGORY FROM HOME
        String category = getIntent().getStringExtra("category");
        if (category != null) {
            setTitle(category);
        }

        // 🛍 RECYCLER VIEW SETUP
        recyclerView = findViewById(R.id.recyclerProducts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SimpleAdapter adapter = new SimpleAdapter(this, names, desc, price, images);
        recyclerView.setAdapter(adapter);

        // 🔻 BOTTOM NAVIGATION
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_category);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(this, HomeActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_category) {
                return true; // Already in category
            } else if (id == R.id.nav_cart) {
                startActivity(new Intent(this, BookingActivity.class));
                finish();
                return true;
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
                return true;
            }
            return false;
        });
    }
}