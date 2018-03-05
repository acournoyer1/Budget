package com.example.alexandre.test;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AddDataActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
        Calendar calendar = new GregorianCalendar();
        Intent intent = getIntent();
        type = intent.getIntExtra(MainActivity.TYPE, -1);
        DataReaderDbHelper mDbHelper = new DataReaderDbHelper(getBaseContext());
        db = mDbHelper.getWritableDatabase();
        setUpButton();

        final NumberPicker dayPicker = findViewById(R.id.day);
        NumberPicker monthPicker = findViewById(R.id.month);
        NumberPicker yearPicker = findViewById(R.id.year);

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int iO, int i) {
                if(i == 1 || i == 3 || i == 5 || i == 7 || i == 8 || i == 10 || i == 12) { dayPicker.setMaxValue(31); }
                else if(i == 4 || i == 6 || i == 9 || i == 11) { dayPicker.setMaxValue(30); }
                else dayPicker.setMaxValue(28);
            }
        });

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMinValue(2016);
        yearPicker.setMaxValue(2020);
        dayPicker.setMinValue(1);
        int i = calendar.get(Calendar.MONTH);
        if(i == 0 || i == 2 || i == 4 || i == 6 || i == 7 || i == 9 || i == 11) { dayPicker.setMaxValue(31); }
        else if(i == 3 || i == 5 || i == 8 || i == 10) { dayPicker.setMaxValue(30); }
        else dayPicker.setMaxValue(28);

        monthPicker.setValue(calendar.get(Calendar.MONTH) + 1);
        yearPicker.setValue(calendar.get(Calendar.YEAR));
        dayPicker.setValue(calendar.get(Calendar.DATE));

        setTitle("Add Bill");
    }

    public void setUpButton() {
        Button b = (Button) findViewById(R.id.addButton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NumberPicker dayPicker = findViewById(R.id.day);
                NumberPicker monthPicker = findViewById(R.id.month);
                NumberPicker yearPicker = findViewById(R.id.year);
                EditText name = (EditText) findViewById(R.id.nameText);
                EditText cost = (EditText) findViewById(R.id.costText);

                if(name.getText().length() == 0 || cost.getText().length() == 0) {
                    Snackbar.make(view, "Fill fields to add to the database", Snackbar.LENGTH_LONG).show();
                } else {
                    ContentValues values = new ContentValues();
                    values.put(DataReaderDbHelper.DataEntry.COLUMN_NAME_DATE, yearPicker.getValue() + "-" + (monthPicker.getValue() - 1) + "-" + dayPicker.getValue());
                    values.put(DataReaderDbHelper.DataEntry.COLUMN_NAME_NAME, name.getText().toString());
                    values.put(DataReaderDbHelper.DataEntry.COLUMN_NAME_PRICE, cost.getText().toString());
                    values.put(DataReaderDbHelper.DataEntry.COLUMN_NAME_TYPE, type);
                    db.insert(DataReaderDbHelper.DataEntry.TABLE_NAME, null, values);
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    Snackbar.make(view, "Data added to the database", Snackbar.LENGTH_LONG).show();
                    cost.setText("");
                    name.setText("");

                    double groceriesWSum = 0, randomWSum = 0, goingOutWSum = 0, groceriesMSum = 0, randomMSum = 0, goingOutMSum = 0;
                    for(Transaction t: DatabaseTools.getWeekTransactions(getBaseContext())) {
                        if(t.getType() == MainActivity.GROCERIES) {
                            groceriesWSum += t.getCost();
                        } else if(t.getType() == MainActivity.RANDOM) {
                            randomWSum += t.getCost();
                        } else if(t.getType() == MainActivity.GOING_OUT) {
                            goingOutWSum += t.getCost();
                        }
                    }
                    for(Transaction t: DatabaseTools.getWeekTransactions(getBaseContext())) {
                        if(t.getType() == MainActivity.GROCERIES) {
                            groceriesMSum += t.getCost();
                        } else if(t.getType() == MainActivity.RANDOM) {
                            randomMSum += t.getCost();
                        } else if(t.getType() == MainActivity.GOING_OUT) {
                            goingOutMSum += t.getCost();
                        }
                    }

                    SharedPreferences pref = getSharedPreferences("limits", Context.MODE_PRIVATE);
                    double groceriesWLimit = pref.getFloat(Tools.W_GROCERIES, 0);
                    double groceriesMLimit = pref.getFloat(Tools.M_GROCERIES, 0);
                    double randomWLimit = pref.getFloat(Tools.W_RANDOM, 0);
                    double randomMLimit = pref.getFloat(Tools.M_RANDOM, 0);
                    double goingOutWLimit = pref.getFloat(Tools.W_GOING_OUT, 0);
                    double goingOutMLimit = pref.getFloat(Tools.M_GOING_OUT, 0);

                    checkLimits(0, "Weekly groceries", groceriesWSum, groceriesWLimit, true);
                    checkLimits(0, "Monthly groceries", groceriesMSum, groceriesMLimit, false);
                    checkLimits(1, "Weekly random", randomWSum, randomWLimit, true);
                    checkLimits(1, "Monthly random", randomMSum, randomMLimit, false);
                    checkLimits(2, "Weekly going out", goingOutWSum, goingOutWLimit, true);
                    checkLimits(2, "Monthly going out", goingOutMSum, goingOutMLimit, false);
                }
            }
        });
    }

    private void checkLimits(int id, String name, double sum, double limit, boolean weekly) {
        if(limit <= 0) return;


        SharedPreferences pref = getSharedPreferences("reached", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        Calendar startDate = null, endDate = null;
        if(weekly) {
            startDate = DatabaseTools.getWeekStart();
            endDate = DatabaseTools.getWeekEnd();
        }
        else {
            startDate = DatabaseTools.getMonthStart();
            endDate = DatabaseTools.getMonthEnd();
        }

        String CHANNEL_ID = "budget_channel";

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(sum >= 0.5*limit) {
            String date = pref.getString(name + " 50", "");
            if(date != "") {
                Calendar data = DatabaseTools.makeCalendar(date);
                if(!DatabaseTools.isBetween(data, startDate, endDate)) {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Budget")
                                    .setContentText(name + " spending has reached 50%");
                    Notification n = builder.build();
                    manager.notify(id*10, builder.build());
                    GregorianCalendar current = new GregorianCalendar();
                    edit.putString(name + " 50", DatabaseTools.getCalendarString(current));
                }
            }
            else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Budget")
                                .setContentText(name + " spending has reached 50%");
                manager.notify(id*10, builder.build());
                GregorianCalendar current = new GregorianCalendar();
                edit.putString(name + " 50", DatabaseTools.getCalendarString(current));
            }
        }
        if(sum >= 0.75*limit) {
            String date = pref.getString(name + " 75", "");
            if(date != "") {
                Calendar data = DatabaseTools.makeCalendar(date);
                if(!DatabaseTools.isBetween(data, startDate, endDate)) {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this, CHANNEL_ID)
                                    //.setSmallIcon(R.mipmap.ic_launcher)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                                    .setContentTitle("Budget")
                                    .setContentText(name + " spending has reached 75%");
                    manager.notify(id*10 + 1, builder.build());
                    GregorianCalendar current = new GregorianCalendar();
                    edit.putString(name + " 75", DatabaseTools.getCalendarString(current));
                }
            }
            else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Budget")
                                .setContentText(name + " spending has reached 75%");
                manager.notify(id*10 + 1, builder.build());
                GregorianCalendar current = new GregorianCalendar();
                edit.putString(name + " 75", DatabaseTools.getCalendarString(current));
            }
        }
        if(sum >= limit) {
            String date = pref.getString(name + " 100", "");
            if(date != "") {
                Calendar data = DatabaseTools.makeCalendar(date);
                if(!DatabaseTools.isBetween(data, startDate, endDate)) {
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this, CHANNEL_ID)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .setContentTitle("Budget")
                                    .setContentText(name + " spending has reached 100%");
                    manager.notify(id*10 + 2, builder.build());
                    GregorianCalendar current = new GregorianCalendar();
                    edit.putString(name + " 100", DatabaseTools.getCalendarString(current));

                }
            }
            else {
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this, CHANNEL_ID)
                                .setSmallIcon(R.mipmap.ic_launcher)
                                .setContentTitle("Budget")
                                .setContentText(name + " spending has reached 100%");
                manager.notify(id*10 + 2, builder.build());
                GregorianCalendar current = new GregorianCalendar();
                edit.putString(name + " 100", DatabaseTools.getCalendarString(current));
            }
        }
        edit.commit();
    }
}
