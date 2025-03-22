package com.example.farmmate;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class logi extends AppCompatActivity {

    private TextView firstName, lastName, mobileNumber, village, jilla, certificateNumber;
    private ImageView certificateImage;
    private Button logoutButton;
    private String fetchUrl = "http://192.168.74.37/farmmate/farmer/getDetails.php";
    private String userMobile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logi);

        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        mobileNumber = findViewById(R.id.mobileNumber);
        village = findViewById(R.id.village);
        jilla = findViewById(R.id.jilla);
        certificateNumber = findViewById(R.id.certificateNumber);
        certificateImage = findViewById(R.id.certificateImage);
        logoutButton = findViewById(R.id.logoutButton);

        // **Retrieve mobile number from intent**
        userMobile = getIntent().getStringExtra("mobile_number");

        // Check if mobile number is received
        if (userMobile != null && !userMobile.isEmpty()) {
            fetchUserDetails();
        } else {
            Toast.makeText(this, "Mobile number not found", Toast.LENGTH_SHORT).show();
        }

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(logi.this, login.class));
                finish();
            }
        });
    }

    private void fetchUserDetails() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, fetchUrl + "?mobilenumber=" + userMobile,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            firstName.setText(jsonObject.getString("firstname"));
                            lastName.setText(jsonObject.getString("lastname"));
                            mobileNumber.setText(jsonObject.getString("mobilenumber"));
                            village.setText(jsonObject.getString("village"));
                            jilla.setText(jsonObject.getString("jilla"));
                            certificateNumber.setText(jsonObject.getString("certificatenumber"));

                            // **Decode and set image**
                            String imageString = jsonObject.getString("certificate_image");
                            if (!imageString.isEmpty()) {
                                byte[] decodedString = Base64.decode(imageString, Base64.DEFAULT);
                                Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                certificateImage.setImageBitmap(decodedImage);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(logi.this, "Error parsing JSON", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(logi.this, "Error fetching data", Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
