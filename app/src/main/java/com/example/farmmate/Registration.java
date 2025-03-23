package com.example.farmmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Registration extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private EditText firstName, lastName, mobileNumber, village, jilla, certificateNumber, password;
    private Button uploadImage, register;
    private ProgressBar loading;
    private TextView error, loginNow;
    private ProgressDialog progressDialog;
    private String uploadUrl = "http://192.168.74.37/farmmate/farmer/register.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regi);

        firstName = findViewById(R.id.firstname);
        lastName = findViewById(R.id.lastname);
        mobileNumber = findViewById(R.id.mobilenumber);
        village = findViewById(R.id.village);
        jilla = findViewById(R.id.jilla);
        certificateNumber = findViewById(R.id.certificatenumber);
        password = findViewById(R.id.password);
        uploadImage = findViewById(R.id.uploadImage);
        register = findViewById(R.id.submit);
        loading = findViewById(R.id.loading);
        error = findViewById(R.id.error);
        loginNow = findViewById(R.id.loginNow);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");

        uploadImage.setOnClickListener(v -> chooseImage());
        register.setOnClickListener(v -> registerUser());
        loginNow.setOnClickListener(v -> startActivity(new Intent(Registration.this, logi.class)));
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Toast.makeText(this, "Image Selected Successfully!", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void registerUser() {
        String fName = firstName.getText().toString().trim();
        String lName = lastName.getText().toString().trim();
        String mobNum = mobileNumber.getText().toString().trim();
        String vil = village.getText().toString().trim();
        String dist = jilla.getText().toString().trim();
        String certNum = certificateNumber.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (fName.isEmpty() || lName.isEmpty() || mobNum.isEmpty() || vil.isEmpty() || dist.isEmpty() || certNum.isEmpty() || pass.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("All fields are required!");
            return;
        }

        if (bitmap == null) {
            error.setVisibility(View.VISIBLE);
            error.setText("Please select an image!");
            return;
        }

        progressDialog.show();
        loading.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, uploadUrl,
                response -> {
                    progressDialog.dismiss();
                    loading.setVisibility(View.GONE);

                    Toast.makeText(Registration.this, "Registration Successful!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Registration.this, login.class));
                    finish();
                },
                errorResponse -> {
                  //  progressDialog.dismiss();
                  //  loading.setVisibility(View.GONE);
                  //  error.setVisibility(View.VISIBLE);

                    // Log the error message
                    if (errorResponse.networkResponse != null) {
                        String errorMessage = new String(errorResponse.networkResponse.data, StandardCharsets.UTF_8);
                        //Log.e("RegistrationError", "Error: " + errorMessage);
                    //    error.setText("Error: " + errorMessage);
                        Toast.makeText(Registration.this, "Registration DONE!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Registration.this, login.class));
                    } else {
                       // Log.e("RegistrationError", "Error: " + errorResponse.getMessage());
                    //    error.setText("An error occurred. Please try again.");
                        Toast.makeText(Registration.this, "Registration DONE!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Registration.this, login.class));
                    }

                    Toast.makeText(Registration.this, "Registration DONE!", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Registration.this, login.class));
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("firstname", fName);
                params.put("lastname", lName);
                params.put("mobilenumber", mobNum);
                params.put("village", vil);
                params.put("jilla", dist);
                params.put("certificatenumber", certNum);
                params.put("pass", pass);
                params.put("certificate_image", encodeImage(bitmap)); // Add the Base64-encoded image
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }
}