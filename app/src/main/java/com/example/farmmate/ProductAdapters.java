package com.example.farmmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProductAdapters extends RecyclerView.Adapter<ProductAdapters.ProductViewHolder> {

    private List<Products> productsList;
    private OnProductClickListener onProductClickListener;

    public ProductAdapters(List<Products> productsList, OnProductClickListener onProductClickListener) {
        this.productsList = productsList;
        this.onProductClickListener = onProductClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Products products = productsList.get(position);
        holder.tvProduct.setText(products.getProductName() + " - " + products.getStock());
        holder.itemView.setOnClickListener(v -> onProductClickListener.onProductSelected(products));
    }

    @Override
    public int getItemCount() {
        return productsList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView tvProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProduct = itemView.findViewById(android.R.id.text1);
        }
    }

    public interface OnProductClickListener {
        void onProductSelected(Products products);
    }
}