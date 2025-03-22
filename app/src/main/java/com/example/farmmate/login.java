package com.example.farmmate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {

    private TextInputEditText email, password;
    private Button submit;
    private ProgressBar loading;
    private TextView error, registerNow, review;
    private ProgressDialog progressDialog;
    private String loginUrl = "http://192.168.74.37/farmmate/farmer/login.php"; // Change this to your actual API endpoint

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Ensure this matches your XML filename

        email = findViewById(R.id.email);
        review = findViewById(R.id.review);
        password = findViewById(R.id.password);
        submit = findViewById(R.id.submit);
        loading = findViewById(R.id.loading);
        error = findViewById(R.id.error);
        registerNow = findViewById(R.id.registerNow);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, review_cheak.class)); // Navigate to Registration page
            }
        });

        registerNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login.this, Registration.class)); // Navigate to Registration page
            }
        });
    }

    private void loginUser() {
        String phone = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if (phone.isEmpty() || pass.isEmpty()) {
            error.setVisibility(View.VISIBLE);
            error.setText("All fields are required!");
            return;
        }

        progressDialog.show();
        loading.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, loginUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        loading.setVisibility(View.GONE);

                        if (response.trim().equalsIgnoreCase("success")) {
                            Toast.makeText(login.this, "Login Successful!", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(login.this, FarmerActivity.class);
                            intent.putExtra("mobile_number", phone); // Pass the mobile number
                            startActivity(intent);
                            finish();
                        }
                        else {
                            error.setVisibility(View.VISIBLE);
                            error.setText("Invalid phone number or password!");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError errorResponse) {
                        progressDialog.dismiss();
                        loading.setVisibility(View.GONE);
                        error.setVisibility(View.VISIBLE);
                        error.setText("Network Error! Please try again.");
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                params.put("password", pass);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
