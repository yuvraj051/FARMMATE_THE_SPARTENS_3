package com.example.farmmate;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class add_new_crop extends AppCompatActivity {

    private static final int PICK_PRODUCT_IMAGE_REQUEST = 1;
    private static final int PICK_CERTIFICATE_IMAGE_REQUEST = 2;

    private EditText etMobileNumber, etDetails, etVillages, etStock, etCertificateNumber;
    private Spinner spProductType, spProductName, spPrice;
    private ImageView ivProductPhoto, ivCertificatePhoto;
    private Button btnChooseProductPhoto, btnChooseCertificatePhoto, btnSubmit;
    private Uri productImageUri, certificateImageUri;

    private Map<String, List<String>> productMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_crop);

        // Initialize views
        etMobileNumber = findViewById(R.id.etMobileNumber);
        spProductType = findViewById(R.id.spProductType);
        spProductName = findViewById(R.id.spProductName);
        etDetails = findViewById(R.id.etDetails);
        spPrice = findViewById(R.id.spPrice);
        etVillages = findViewById(R.id.etVillages);
        etStock = findViewById(R.id.etStock);
        etCertificateNumber = findViewById(R.id.etCertificateNumber);
        ivProductPhoto = findViewById(R.id.ivProductPhoto);
        ivCertificatePhoto = findViewById(R.id.ivCertificatePhoto);
        btnChooseProductPhoto = findViewById(R.id.btnChooseProductPhoto);
        btnChooseCertificatePhoto = findViewById(R.id.btnChooseCertificatePhoto);
        btnSubmit = findViewById(R.id.btnSubmit);
        String mobileNumber = getIntent().getStringExtra("mobile_number");

        etMobileNumber.setText(mobileNumber);


        // Set up dropdowns
        setupDropdowns();

        // Open image chooser for product photo
        btnChooseProductPhoto.setOnClickListener(v -> openImageChooser(PICK_PRODUCT_IMAGE_REQUEST));

        // Open image chooser for certificate photo
        btnChooseCertificatePhoto.setOnClickListener(v -> openImageChooser(PICK_CERTIFICATE_IMAGE_REQUEST));

        // Submit form data
        btnSubmit.setOnClickListener(v -> uploadProduct());
    }

    private void setupDropdowns() {
        // Define product names for each type
        productMap = new HashMap<>();
        productMap.put("Vegetable", Arrays.asList("Tomato", "Potato", "Carrot", "Onion"));
        productMap.put("Fruits", Arrays.asList("Apple", "Banana", "Orange", "Mango"));

        // Set up Product Type Spinner
        ArrayAdapter<String> productTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new ArrayList<>(productMap.keySet()));
        productTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spProductType.setAdapter(productTypeAdapter);

        // Update Product Name Spinner based on selected Product Type
        spProductType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = (String) parent.getItemAtPosition(position);
                List<String> productNames = productMap.get(selectedType);
                ArrayAdapter<String> productNameAdapter = new ArrayAdapter<>(add_new_crop.this, android.R.layout.simple_spinner_item, productNames);
                productNameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spProductName.setAdapter(productNameAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // Set up Price Spinner (1 to 1000)
        List<String> prices = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            prices.add(String.valueOf(i));
        }
        ArrayAdapter<String> priceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, prices);
        priceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPrice.setAdapter(priceAdapter);
    }

    private void openImageChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_PRODUCT_IMAGE_REQUEST) {
                productImageUri = data.getData();
                ivProductPhoto.setImageURI(productImageUri);
            } else if (requestCode == PICK_CERTIFICATE_IMAGE_REQUEST) {
                certificateImageUri = data.getData();
                ivCertificatePhoto.setImageURI(certificateImageUri);
            }
        }
    }

    private void uploadProduct() {
        // Get form data
        String mobileNumber = etMobileNumber.getText().toString();
        String productType = spProductType.getSelectedItem().toString();
        String productName = spProductName.getSelectedItem().toString();
        String details = etDetails.getText().toString();
        String price = spPrice.getSelectedItem().toString();
        String villages = etVillages.getText().toString();
        String stock = etStock.getText().toString();
        String certificateNumber = etCertificateNumber.getText().toString();

        // Validate input fields
        if (mobileNumber.isEmpty() || productType.isEmpty() || productName.isEmpty() || details.isEmpty() || price.isEmpty() || villages.isEmpty() || stock.isEmpty() || certificateNumber.isEmpty() || productImageUri == null || certificateImageUri == null) {
            Toast.makeText(this, "Please fill all fields and choose images.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the real file paths from the URIs
        String productImagePath = getRealPathFromURI(productImageUri);
        String certificateImagePath = getRealPathFromURI(certificateImageUri);

        // Log file paths for debugging
        Log.d("File Paths", "Product Image Path: " + productImagePath);
        Log.d("File Paths", "Certificate Image Path: " + certificateImagePath);

        if (productImagePath == null || certificateImagePath == null) {
            Toast.makeText(this, "Failed to get file paths.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create OkHttp client
        OkHttpClient client = new OkHttpClient();

        // Create multipart request body
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mobile_number", mobileNumber)
                .addFormDataPart("product_type", productType)
                .addFormDataPart("product_name", productName)
                .addFormDataPart("details", details)
                .addFormDataPart("price", price)
                .addFormDataPart("villages", villages)
                .addFormDataPart("stock", stock)
                .addFormDataPart("certificate_number", certificateNumber)
                .addFormDataPart("product_image", "product_image.jpg", RequestBody.create(new File(productImagePath), MediaType.parse("image/jpeg")))
                .addFormDataPart("certificate_photo", "certificate_photo.jpg", RequestBody.create(new File(certificateImagePath), MediaType.parse("image/jpeg")))
                .build();

        // Create request
        Request request = new Request.Builder()
                .url("http://192.168.74.37/farmmate/jay/insert_product.php") // Replace with your server URL
                .post(requestBody)
                .build();

        // Execute request in a background thread
        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                runOnUiThread(() -> Toast.makeText(add_new_crop.this, responseBody, Toast.LENGTH_SHORT).show());
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(add_new_crop.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // Helper method to get the real file path from URI
    private String getRealPathFromURI(Uri uri) {
        String filePath = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (columnIndex != -1) {
                        String fileName = cursor.getString(columnIndex);
                        File file = new File(getCacheDir(), fileName);
                        try {
                            // Copy the file to the app's cache directory
                            java.io.InputStream inputStream = getContentResolver().openInputStream(uri);
                            java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file);

                            byte[] buffer = new byte[1024];
                            int length;
                            while ((length = inputStream.read(buffer)) > 0) {
                                outputStream.write(buffer, 0, length);
                            }

                            outputStream.close();
                            inputStream.close();

                            filePath = file.getAbsolutePath();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            filePath = uri.getPath();
        }
        return filePath;
    }
}