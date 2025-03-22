package com.example.farmmate;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FarmerActivity extends AppCompatActivity {
    Button btnMarket;
    CardView cardWeather,cardReminder,cardNews,cardPrice,cardCrop,cardHealth,cardScheme,cardHelp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farmer);

        String mobileNumber = getIntent().getStringExtra("mobile_number");

        // Display it or use it as needed (e.g., for a Toast message)
        if (mobileNumber != null) {
            Toast.makeText(this, "Logged in as: " + mobileNumber, Toast.LENGTH_LONG).show();
        }
    btnMarket = findViewById(R.id.btnMarket);
//        cardWeather = findViewById(R.id.cardWeather);
//        cardReminder = findViewById(R.id.cardReminder);
//        cardNews = findViewById(R.id.cardNews);
//        cardPrice = findViewById(R.id.cardPrice);
//        cardCrop = findViewById(R.id.cardCrop);
        cardHealth = findViewById(R.id.cardHealth);
//
//        cardHelp = findViewById(R.id.cardHelp);

        cardScheme = findViewById(R.id.cardScheme);
//        cardHelp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(FarmerActivity.this,ActivityHelp.class);
//                startActivity(i);
//            }
//        });

        cardScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FarmerActivity.this, logi.class);
                i.putExtra("mobile_number", mobileNumber); // Pass the mobile number further if needed
                startActivity(i);
            }
        });

        cardHealth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FarmerActivity.this,add_new_crop.class);
                i.putExtra("mobile_number", mobileNumber);
                startActivity(i);
            }
        });

//        cardCrop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(FarmerActivity.this,CropDetailMain.class);
//                startActivity(i);
//            }
//        });
//

//        cardPrice.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(FarmerActivity.this,ActivityCropPrices.class);
//                startActivity(i);
//            }
//        });

//        cardNews.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(FarmerActivity.this,NewsActivity.class);
//                startActivity(i);
//            }
//        });
//        cardWeather.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(FarmerActivity.this,WeatherActivity.class);
//                startActivity(i);
//
//            }
//        });
//        cardReminder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(FarmerActivity.this,MyCustomeCalender.class);
//                startActivity(i);
//            }
//        });


        btnMarket.setOnClickListener(new View.OnClickListener() {
          @Override
           public void onClick(View view) {
              Intent i = new Intent(FarmerActivity.this,fetch_product.class);
                startActivity(i);
           }
        });
    }
}