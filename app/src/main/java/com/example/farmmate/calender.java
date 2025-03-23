package com.example.farmmate;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class calender extends AppCompatActivity {
    private mySQLiteDBHandler dbHandler;
    private EditText editText;
    private CalendarView calendarView;
    private String selectedDate;
    private SQLiteDatabase sqLiteDatabase;
    private Button buttonSave;
    private static final String CHANNEL_ID = "simple_notification_channel";
    private static final int NOTIFICATION_ID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        editText = findViewById(R.id.editText);
        calendarView = findViewById(R.id.calendarView);
        createNotificationChannel();

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                ReadDatabase(view);
            }
        });

        try {
            dbHandler = new mySQLiteDBHandler(this, "CalendarDatabase", null, 1);
            sqLiteDatabase = dbHandler.getWritableDatabase();
            sqLiteDatabase.execSQL("CREATE TABLE  EventCalendar(Date TEXT, Event TEXT)");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void InsertDatabase(View view) {
        sendNotification();
        String eventText = editText.getText().toString().trim();
        if (eventText.isEmpty()) {
            Toast.makeText(this, "Please enter an event", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("Date", selectedDate);
        contentValues.put("Event", eventText);
        long eventId = sqLiteDatabase.insert("EventCalendar", null, contentValues);

        if (eventId != -1) {
            // Set a notification for the event
            setEventNotification(selectedDate, eventText);
            Toast.makeText(this, "Event inserted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to insert event", Toast.LENGTH_SHORT).show();
        }
    }


    public void UpdateDatabase(View view) {
        String eventText = editText.getText().toString().trim();
        if (eventText.isEmpty()) {
            Toast.makeText(this, "Please enter an event to update", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put("Event", eventText);

        int rowsAffected = sqLiteDatabase.update("EventCalendar", contentValues, "Date = ?", new String[]{selectedDate});

        if (rowsAffected > 0) {
            Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No event found for the selected date", Toast.LENGTH_SHORT).show();
        }
    }
    public void deleteEvent(View view) {
        if (selectedDate == null) {
            Toast.makeText(this, "Select a date first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            sqLiteDatabase.delete("EventCalendar", "Date = ?", new String[]{selectedDate});
            editText.setText("");
            Toast.makeText(this, "Event deleted successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to delete event", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEventNotification(String selectedDate, String eventText) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata")); // Set time zone to Indian Standard Time

            Date date = sdf.parse(selectedDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);

            // Set the notification time to 8 PM
            calendar.set(Calendar.HOUR_OF_DAY, 20); // 8 PM
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);

            // Create an intent for the notification
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("event", eventText);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 3, intent, PendingIntent.FLAG_IMMUTABLE);

            // Set the alarm for the notification
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }



    public void ShowEvents(View view) {
        StringBuilder eventsText = new StringBuilder();

        String query = "SELECT * FROM EventCalendar";
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int dateIndex = cursor.getColumnIndex("Date");
            int eventIndex = cursor.getColumnIndex("Event");
            do {
                if (dateIndex != -1 && eventIndex != -1) {
                    String date = cursor.getString(dateIndex);
                    String event = cursor.getString(eventIndex);
                    eventsText.append(date).append(": ").append(event).append("\n");
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (eventsText.length() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Events");
            builder.setMessage(eventsText.toString());
            builder.setPositiveButton("OK", null);
            builder.show();
        } else {
            Toast.makeText(this, "No events found", Toast.LENGTH_SHORT).show();
        }
    }


    public void ReadDatabase (View view){
        String query = "SELECT Event FROM EventCalendar WHERE Date = '" + selectedDate + "'";
        try {
            Cursor cursor = sqLiteDatabase.rawQuery(query, null);
            if (cursor.moveToFirst()) {
                editText.setText(cursor.getString(0));
            } else {
                editText.setText("");
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            editText.setText("");
        }
    }



    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle("FarmMate")
                .setContentText("Your Event And Date Selected")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}