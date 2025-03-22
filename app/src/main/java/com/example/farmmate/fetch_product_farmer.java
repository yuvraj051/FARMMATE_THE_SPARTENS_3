package com.example.farmmate;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
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

public class fetch_product_farmer extends AppCompatActivity {

    private static final String URL = "http://192.168.74.37/farmmate/jay/fetch_products_farmer.php";
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList, filteredList;
    private Spinner filterSpinner;
    private SearchView searchView;
    private String mobileNumber;
    private List<String> productTypes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_product_farmer);

        recyclerView = findViewById(R.id.recyclerView);
        filterSpinner = findViewById(R.id.filterSpinner);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        filteredList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, filteredList);
        recyclerView.setAdapter(productAdapter);

        // Get mobile number from Intent
        mobileNumber = getIntent().getStringExtra("mobile_number");

        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            fetchProducts();
        } else {
            Toast.makeText(this, "Mobile number is required!", Toast.LENGTH_SHORT).show();
        }

        setupSearchView();
    }

    private void fetchProducts() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String requestUrl = URL + "?mobile_number=" + mobileNumber;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, requestUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        productList.clear();
                        filteredList.clear();
                        Set<Integer> productIds = new HashSet<>(); // To prevent duplicate entries
                        productTypes = new ArrayList<>();
                        try {
                            Log.d("API Response", response.toString());

                            if (response.getString("status").equals("success")) {
                                JSONArray productsArray = response.getJSONArray("products");

                                for (int i = 0; i < productsArray.length(); i++) {
                                    JSONObject productObject = productsArray.getJSONObject(i);

                                    int id = productObject.getInt("id");
                                    if (productIds.contains(id)) continue; // Prevent duplicates

                                    String mobileNumber = productObject.getString("mobile_number");
                                    String productType = productObject.getString("product_type");
                                    String productName = productObject.getString("product_name");
                                    String details = productObject.getString("details");
                                    double price = productObject.getDouble("price");
                                    String imageUrl = productObject.getString("product_image");
                                    String villages = productObject.getString("villages");
                                    int stock = productObject.getInt("stock");

                                    Product product = new Product(id, mobileNumber, productType, productName, details, price, imageUrl, villages, stock);
                                    productList.add(product);
                                    filteredList.add(product);
                                    productIds.add(id);

                                    if (!productTypes.contains(productType)) {
                                        productTypes.add(productType);
                                    }
                                }

                                setupFilterSpinner();
                                productAdapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(fetch_product_farmer.this, "No products found.", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(fetch_product_farmer.this, "Error parsing JSON!", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(fetch_product_farmer.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                        Log.e("Volley Error", error.toString());
                    }
                });

        queue.add(jsonObjectRequest);
    }

    private void setupFilterSpinner() {
        productTypes.add(0, "All"); // Add "All" option at the beginning
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, productTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(adapter);

        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                applyFilters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void applyFilters() {
        String selectedType = filterSpinner.getSelectedItem().toString();
        filteredList.clear();

        if (selectedType.equals("All")) {
            filteredList.addAll(productList);
        } else {
            for (Product product : productList) {
                if (product.getProductType().equals(selectedType)) {
                    filteredList.add(product);
                }
            }
        }

        productAdapter.notifyDataSetChanged();
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProducts(newText);
                return false;
            }
        });
    }

    private void filterProducts(String query) {
        query = query.toLowerCase();
        filteredList.clear();

        for (Product product : productList) {
            if (product.getProductName().toLowerCase().contains(query)) {
                filteredList.add(product);
            }
        }

        productAdapter.notifyDataSetChanged();
    }
}
