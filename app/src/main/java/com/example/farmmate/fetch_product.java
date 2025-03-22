package com.example.farmmate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class fetch_product extends AppCompatActivity {
    RecyclerView recyclerView;
    List<Product> productList;
    List<Product> filteredList;
    ProductAdapter productAdapter;
    Spinner filterSpinner;
    SearchView searchView;

    private static final String URL = "http://192.168.74.37/farmmate/jay/fetch_products.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_product);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize product lists and adapter
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, filteredList);
        recyclerView.setAdapter(productAdapter);

        // Initialize filter spinner and search view
        filterSpinner = findViewById(R.id.filterSpinner);
        searchView = findViewById(R.id.searchView);

        // Fetch products from the backend
        fetchProducts();

        // Setup search view
        setupSearchView();
    }

    private void fetchProducts() {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Create a JsonObjectRequest to fetch data from the backend
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        productList.clear();
                        try {
                            Log.d("API Response", response.toString()); // Log the API response

                            // Check if the status is "success"
                            if (response.getString("status").equals("success")) {
                                // Get the "products" array from the response
                                JSONArray productsArray = response.getJSONArray("products");

                                Set<String> productTypes = new HashSet<>();
                                for (int i = 0; i < productsArray.length(); i++) {
                                    JSONObject productObject = productsArray.getJSONObject(i);

                                    // Parse product data from JSON
                                    int id = productObject.getInt("id");
                                    String mobileNumber = productObject.getString("mobile_number");
                                    String productType = productObject.getString("product_type");
                                    String productName = productObject.getString("product_name");
                                    String details = productObject.getString("details");
                                    double price = productObject.getDouble("price");
                                    String imageUrl = productObject.getString("product_image");
                                    String villages = productObject.getString("villages");
                                    int stock = productObject.getInt("stock");

                                    // Create a Product object and add it to the list
                                    Product product = new Product(id, mobileNumber, productType, productName, details, price, imageUrl, villages, stock);
                                    productList.add(product);
                                    productTypes.add(productType);
                                }

                                Log.d("Product List Size", "Size: " + productList.size()); // Log the size of the product list

                                // Setup filter spinner with product types
                                setupFilterSpinner(new ArrayList<>(productTypes));

                                // Apply initial filters
                                applyFilters();
                            } else {
                                Toast.makeText(fetch_product.this, "Failed to fetch products.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(fetch_product.this, "Error parsing JSON!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(fetch_product.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                        Log.e("Volley Error", error.toString());
                    }
                });

        // Add the request to the RequestQueue
        queue.add(jsonObjectRequest);
    }

    private void setupFilterSpinner(List<String> productTypes) {
        // Add "All" option to show all products
        productTypes.add(0, "All");

        // Create an ArrayAdapter for the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        // Set item selected listener for the spinner
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupSearchView() {
        // Set query text listener for the search view
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                applyFilters();
                return true;
            }
        });
    }

    private void applyFilters() {
        // Get the search query and selected product type
        String query = searchView.getQuery().toString().toLowerCase();
        String selectedType = filterSpinner.getSelectedItem().toString();

        // Clear the filtered list
        filteredList.clear();

        // Filter the product list based on the search query and selected type
        for (Product product : productList) {
            boolean matchesName = product.getProductName().toLowerCase().contains(query);
            boolean matchesType = selectedType.equals("All") || product.getProductType().equals(selectedType);

            if (matchesName && matchesType) {
                filteredList.add(product);
            }
        }

        // Notify the adapter of data changes
        productAdapter.notifyDataSetChanged();
    }
}