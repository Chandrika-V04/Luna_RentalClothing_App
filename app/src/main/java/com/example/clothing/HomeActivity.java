package com.example.clothing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    RecyclerView recyclerProducts;
    BottomNavigationView bottomNav;
    LinearLayout catWomen, catMen, catKids, catParty, catTraditional;

    // SLIDER VARIABLES
    ViewPager2 imageSlider;
    SliderAdapter sliderAdapter;
    Handler handler = new Handler(Looper.getMainLooper());

    Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = imageSlider.getCurrentItem();
            int totalItems = sliderAdapter.getItemCount();
            if (currentItem < totalItems - 1) {
                imageSlider.setCurrentItem(currentItem + 1);
            } else {
                imageSlider.setCurrentItem(0);}
            handler.postDelayed(this, 3000); // 3 sec
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // 🔥 IMAGE SLIDER SETUP
        imageSlider = findViewById(R.id.imageSlider);
        List<Integer> images = Arrays.asList(
                R.drawable.img1,
                R.drawable.img2,
                R.drawable.img3
        );
        sliderAdapter = new SliderAdapter(images);
        imageSlider.setAdapter(sliderAdapter);
        handler.postDelayed(sliderRunnable, 3000);

        // CATEGORY LAYOUTS
        catWomen = findViewById(R.id.catWomen);
        catMen = findViewById(R.id.catMen);
        catKids = findViewById(R.id.catKids);
        catParty = findViewById(R.id.catParty);
        catTraditional = findViewById(R.id.catTraditional);

        catWomen.setOnClickListener(v -> openCategory("Women"));
        catMen.setOnClickListener(v -> openCategory("Men"));
        catKids.setOnClickListener(v -> openCategory("Kids"));
        catParty.setOnClickListener(v -> openCategory("Party"));
        catTraditional.setOnClickListener(v -> openCategory("Traditional"));

        // TRENDING PRODUCTS
        recyclerProducts = findViewById(R.id.recyclerProducts);
        String[] productNames = {"Blazer", "Saree", "Overcoat", "Gown"};
        String[] productDesc = {
                "Black Gold-Embroidered Blazer",
                "Ethereal Maroon Georgette Saree",
                "Burgundy Wool Overcoat",
                "Wine Velvet A-Line Gown"
        };
        String[] productPrice = {"₹999/day", "₹1999/day", "₹599/day", "₹799/day"};
        int[] productImages = {
                R.drawable.men1,
                R.drawable.women1,
                R.drawable.men2,
                R.drawable.women2
        };

        SimpleAdapter adapter = new SimpleAdapter(this, productNames, productDesc, productPrice, productImages);
        recyclerProducts.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerProducts.setLayoutManager(layoutManager);
        recyclerProducts.setNestedScrollingEnabled(false);

        // BOTTOM NAVIGATION
        bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_category) {
                startActivity(new Intent(this, CategoryActivity.class));
                finish();
                return true;
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
    // STOP SLIDER WHEN APP PAUSES
    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(sliderRunnable);
    }
    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(sliderRunnable, 3000);
    }
    private void openCategory(String category) {
        Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}