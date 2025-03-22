package com.example.farmmate;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    private Context context;
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = productList.get(position);

        // Bind data to views
        holder.productName.setText(product.getProductName());
        holder.productDetails.setText(product.getDetails());
        holder.productPrice.setText("₹" + product.getPrice());
        holder.productVillages.setText("Available in: " + product.getVillages());

        // Set stock status text and color
        if (product.getStock() > 0) {
            holder.productStock.setText("In Stock");
            holder.productStock.setTextColor(Color.GREEN); // Set text color to green
        } else {
            holder.productStock.setText("Out of Stock");
            holder.productStock.setTextColor(Color.RED); // Set text color to red
        }

        // Load product image using Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder) // Placeholder image while loading
                .error(R.drawable.error) // Error image if loading fails
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public void updateList(List<Product> newList) {
        //productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }


    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productDetails, productPrice, productVillages, productStock;
        ImageView productImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            productName = itemView.findViewById(R.id.productName);
            productDetails = itemView.findViewById(R.id.productDetails);
            productPrice = itemView.findViewById(R.id.productPrice);
            productVillages = itemView.findViewById(R.id.productVillages);
            productStock = itemView.findViewById(R.id.productStock);
            productImage = itemView.findViewById(R.id.productImage);
        }
    }
}