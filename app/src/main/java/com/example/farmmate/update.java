package com.example.farmmate;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class update extends AppCompatActivity {

    private TextInputEditText etSearchMobileNumber, etStock;
    private Spinner spProducts;
    private List<Products> productsList = new ArrayList<>();
    private String selectedProductId; // To store the selected product ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize views
        etSearchMobileNumber = findViewById(R.id.etSearchMobileNumber);
        etStock = findViewById(R.id.etStock);
        spProducts = findViewById(R.id.spProducts);

        // Search button
        findViewById(R.id.btnSearch).setOnClickListener(v -> searchProducts());

        // Update button
        findViewById(R.id.btnUpdate).setOnClickListener(v -> updateProduct());

        // Delete button
        findViewById(R.id.btnDelete).setOnClickListener(v -> deleteProduct());

        // Spinner item selection listener
        spProducts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Products selectedProducts = productsList.get(position);
                selectedProductId = selectedProducts.getId(); // Store the selected product ID
                etStock.setText(selectedProducts.getStock()); // Populate the stock field
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void searchProducts() {
        String mobileNumber = etSearchMobileNumber.getText().toString().trim();
        if (mobileNumber.isEmpty()) {
            Toast.makeText(this, "Please enter a mobile number.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Fetch data from the server
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("http://192.168.74.37/farmmate/jay/fetchs_products.php?mobile_number=" + mobileNumber)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(update.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().string());
                        productsList.clear();
                        List<String> productNames = new ArrayList<>();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Products products = new Products(
                                    jsonObject.getString("id"),
                                    jsonObject.getString("product_name"),
                                    jsonObject.getString("stock")
                            );
                            productsList.add(products);
                            productNames.add(products.getProductName() + " (Stock: " + products.getStock() + ")");
                        }
                        runOnUiThread(() -> {
                            // Populate the Spinner
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                    update.this,
                                    android.R.layout.simple_spinner_item,
                                    productNames
                            );
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spProducts.setAdapter(adapter);
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(update.this, "No data found.", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void updateProduct() {
        String stock = etStock.getText().toString().trim();
        if (selectedProductId == null || stock.isEmpty()) {
            Toast.makeText(this, "Please select a product and enter stock.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Update stock on the server
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("id", selectedProductId)
                .add("stock", stock)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.74.37/farmmate/jay/update_product.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(update.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(update.this, response.body().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    searchProducts(); // Refresh data
                });
            }
        });
    }

    private void deleteProduct() {
        if (selectedProductId == null) {
            Toast.makeText(this, "Please select a product to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Delete product on the server
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = new FormBody.Builder()
                .add("id", selectedProductId)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.74.37/farmmate/jay/delete_product.php")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(update.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    try {
                        Toast.makeText(update.this, response.body().string(), Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    searchProducts(); // Refresh data
                });
            }
        });
    }
}