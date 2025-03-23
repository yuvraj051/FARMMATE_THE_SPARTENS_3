package com.example.farmmate






import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.util.Log
import android.view.View
import android.view.inputmethod.InputBinding
import android.widget.SearchView
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class WeatherActivity1 : AppCompatActivity() {
    private lateinit var textView7: TextView
    private lateinit var searchView: SearchView
    private lateinit var textView3: TextView
    private lateinit var textView2: TextView
    private lateinit var lottieAnimationView: LottieAnimationView
    private lateinit var textView4: TextView
    private lateinit var textView5: TextView
    private lateinit var textView6: TextView
    private lateinit var textView8: TextView
    private lateinit var textView9: TextView
    private lateinit var textView10: TextView
    private lateinit var textView11: TextView
    private lateinit var textView12: TextView
    private lateinit var textView13: TextView
    private lateinit var textView14: TextView
    private lateinit var textView15: TextView
    private lateinit var textView16: TextView
    private lateinit var textView17: TextView
    private lateinit var textView18: TextView
    private lateinit var textView19: TextView
    private lateinit var textView20: TextView
    private lateinit var textView21: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather1)

        textView7 = findViewById(R.id.textView7)
        searchView = findViewById(R.id.searchView)
        textView3 = findViewById(R.id.textView3)
        textView2 = findViewById(R.id.textView2)
        lottieAnimationView = findViewById(R.id.lottieAnimationView)
        textView4 = findViewById(R.id.textView4)
        textView5 = findViewById(R.id.textView5)
        textView6 = findViewById(R.id.textView6)
        textView8 = findViewById(R.id.textView8)
        textView9 = findViewById(R.id.textView9)
        textView10 = findViewById(R.id.textView10)
        textView11 = findViewById(R.id.textView11)
        textView12 = findViewById(R.id.textView12)
        textView13 = findViewById(R.id.textView13)
        textView14 = findViewById(R.id.textView14)
        textView15 = findViewById(R.id.textView15)
        textView16 = findViewById(R.id.textView16)
        textView17 = findViewById(R.id.textView17)
        textView18 = findViewById(R.id.textView18)
        textView19 = findViewById(R.id.textView19)
        textView20 = findViewById(R.id.textView20)
        textView21 = findViewById(R.id.textView21)

        fetchWeatherdata("surat") // Default city

        searchcity()
    }

    private fun searchcity() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherdata(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Return false to allow the SearchView to handle the text change
                return false
            }
        })
    }

    private fun fetchWeatherdata(cityname:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build()
        val apiInterface = retrofit.create(Apiinterface::class.java)
        val call: Call<Weatherapp> = apiInterface.getWeatherData(cityname, "cfc5b4e2f0e93df1c4f1190afc6ddcef", "metric")
        call.enqueue(object : Callback<Weatherapp> {
            override fun onResponse(call: Call<Weatherapp>, response: Response<Weatherapp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null) {
                    val temperature = responseBody.main.temp
                    val  humidity = responseBody.main.humidity
                    val WindSpeed = responseBody.wind.speed
                    val sunrise = responseBody.sys.sunrise
                    val sunset = responseBody.sys.sunset
                    val  sealevel = responseBody.main.pressure
                    val conditon = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxtemp =responseBody.main.temp_max
                    val mintemp = responseBody.main.temp_min
                    textView4.text = "$temperature  °c"
                    textView5.text=conditon
                    textView6.text=" Max Temp :$maxtemp °c"
                    textView7.text =" Min Temp :$mintemp °c"
                    textView11.text="$humidity %"
                    textView12.text="$WindSpeed m/s"
                    textView16.text="$sunrise"
                    textView18.text="$sunset"
                    textView20.text="$sealevel hPa"
                    textView14.text=conditon
                    //day
                    textView8.text=dayname(System.currentTimeMillis())
                    //date
                    textView9.text= date()
                    //city
                    textView3.text="$cityname"
                    //   Log.d("TAG", "onResponse: $temperature")
                    setWeatherBackground(conditon)
                }
            }

            override fun onFailure(call: Call<Weatherapp>, t: Throwable) {
                Log.e("TAG", "onFailure: ${t.message}", t)
            }
        })


    }

    private fun setWeatherBackground(conditon: String) {
        val drawableId = when (conditon.toLowerCase(Locale.getDefault())) {
            "clear" -> R.drawable.sunset
            "rain" -> R.drawable.sunrise
            "clouds" -> R.drawable.conditions
            // Add more cases for other weather conditions
            else -> R.drawable.cloudy// Default background image
        }
        findViewById<View>(android.R.id.content).setBackgroundResource(drawableId)
    }

    fun dayname (timestamp:Long):String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun date():String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }
}