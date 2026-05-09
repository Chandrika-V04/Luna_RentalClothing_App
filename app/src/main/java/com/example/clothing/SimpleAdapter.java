package com.example.clothing;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Unified SimpleAdapter for HomeActivity and CategoryActivity
 * Handles displaying products and sending data to BookingActivity
 */
public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

    private final Context context;
    private final String[] names;
    private final String[] desc;
    private final String[] price;
    private final int[] images;

    public SimpleAdapter(Context context, String[] names, String[] desc, String[] price, int[] images) {
        this.context = context;
        this.names = names;
        this.desc = desc;
        this.price = price;
        this.images = images;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Set product data
        holder.name.setText(names[position]);
        holder.descText.setText(desc[position]);
        holder.priceText.setText(price[position]);
        holder.image.setImageResource(images[position]);

        // Handle Book Now button click
        holder.btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingActivity.class);
            intent.putExtra("name", names[position]);
            intent.putExtra("desc", desc[position]);
            intent.putExtra("price", price[position]);
            intent.putExtra("image", images[position]);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return names.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name, descText, priceText;
        Button btnBookNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
            name = itemView.findViewById(R.id.itemName);
            descText = itemView.findViewById(R.id.itemDesc);
            priceText = itemView.findViewById(R.id.itemPrice);
            btnBookNow = itemView.findViewById(R.id.btnBookNow);
        }
    }
}