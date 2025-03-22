package com.example.farmmate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;

public class review_cheak extends AppCompatActivity {
    private EditText phoneNumber;
    private Button submit;
    private TextView errorMessage;
    private ProgressDialog progressDialog;
    private static final String URL = "http://192.168.74.37/farmmate/farmer/review.php"; // Replace with your PHP script URL

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_cheak);

        phoneNumber = findViewById(R.id.email);
        submit = findViewById(R.id.submit);
        errorMessage = findViewById(R.id.error);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking...");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPhoneNumber();
            }
        });
    }

    private void checkPhoneNumber() {
        final String phone = phoneNumber.getText().toString().trim();
        if (phone.isEmpty()) {
            Toast.makeText(this, "Please enter a phone number", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        if (response.equals("request_farmer")) {
                            errorMessage.setText("STATUS : PENDING");
                        } else if (response.equals("approved_farmer")) {
                            errorMessage.setText("STATUS :WELCOME TO FARMMATE YOU CAN LOGIN NOW !! ");
                        } else if (response.equals("users")) {
                            errorMessage.setText("STATUS : YOUR ACCOUNT REJECTED ");
                        } else {
                            errorMessage.setText("Phone number not found, proceed");
                        }
                        errorMessage.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                errorMessage.setText("Error connecting to server");
                errorMessage.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("phone", phone);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
