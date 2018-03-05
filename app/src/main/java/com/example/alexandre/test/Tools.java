package com.example.alexandre.test;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;

public class Tools extends AppCompatActivity {

    private boolean changed = false;

    private EditText weeklyGroceries, monthlyGroceries, weeklyRandom, monthlyRandom, weeklyGoingOut, monthlyGoingOut;

    public static final String W_GROCERIES = "Weekly Groceries Limit";
    public static final String M_GROCERIES = "Monthly Groceries Limit";
    public static final String W_RANDOM = "Weekly Random Limit";
    public static final String M_RANDOM = "Monthly Random Limit";
    public static final String W_GOING_OUT = "Weekly Going Out Limit";
    public static final String M_GOING_OUT = "Monthly Going Out Limit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        weeklyGroceries = findViewById(R.id.weeklyGroceries);
        monthlyGroceries = findViewById(R.id.monthlyGroceries);
        weeklyRandom = findViewById(R.id.weeklyRandom);
        monthlyRandom = findViewById(R.id.monthlyRandom);
        weeklyGoingOut = findViewById(R.id.weeklyGoingOut);
        monthlyGoingOut = findViewById(R.id.monthlyGoingOut);

        SharedPreferences pref = getSharedPreferences("limits", Context.MODE_PRIVATE);
        float wGroceries = pref.getFloat(W_GROCERIES, 0);
        float mGroceries = pref.getFloat(M_GROCERIES, 0);
        float wRandom = pref.getFloat(W_RANDOM, 0);
        float mRandom = pref.getFloat(M_RANDOM, 0);
        float wGoingOut = pref.getFloat(W_GOING_OUT, 0);
        float mGoingOut = pref.getFloat(M_GOING_OUT, 0);

        DecimalFormat format = new DecimalFormat("#.00");
        if(wGroceries != 0) weeklyGroceries.setText(format.format(wGroceries));
        if(mGroceries != 0) monthlyGroceries.setText(format.format(mGroceries));
        if(wRandom != 0) weeklyRandom.setText(format.format(wRandom));
        if(mRandom != 0) monthlyRandom.setText(format.format(mRandom));
        if(wGoingOut != 0) weeklyGoingOut.setText(format.format(wGoingOut));
        if(mGoingOut != 0) monthlyGoingOut.setText(format.format(mGoingOut));

        TextWatcher watcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                changed = true;
            }
        };

        weeklyGroceries.addTextChangedListener(watcher);
        monthlyGroceries.addTextChangedListener(watcher);
        weeklyRandom.addTextChangedListener(watcher);
        monthlyRandom.addTextChangedListener(watcher);
        weeklyGoingOut.addTextChangedListener(watcher);
        monthlyGoingOut.addTextChangedListener(watcher);
        setTitle("Limits");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(changed)
        {
            SharedPreferences pref = getSharedPreferences("limits", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = pref.edit();
            if(weeklyGroceries.getText().length() > 0) edit.putFloat(W_GROCERIES, Float.parseFloat(weeklyGroceries.getText().toString()));
            else edit.putFloat(W_GROCERIES, 0);
            if(monthlyGroceries.getText().length() > 0) edit.putFloat(M_GROCERIES, Float.parseFloat(monthlyGroceries.getText().toString()));
            else edit.putFloat(M_GROCERIES, 0);
            if(weeklyRandom.getText().length() > 0) edit.putFloat(W_RANDOM, Float.parseFloat(weeklyRandom.getText().toString()));
            else edit.putFloat(W_RANDOM, 0);
            if(monthlyRandom.getText().length() > 0) edit.putFloat(M_RANDOM, Float.parseFloat(monthlyRandom.getText().toString()));
            else edit.putFloat(M_RANDOM, 0);
            if(weeklyGoingOut.getText().length() > 0) edit.putFloat(W_GOING_OUT, Float.parseFloat(weeklyGoingOut.getText().toString()));
            else edit.putFloat(W_GOING_OUT, 0);
            if(monthlyGoingOut.getText().length() > 0) edit.putFloat(M_GOING_OUT, Float.parseFloat(monthlyGoingOut.getText().toString()));
            else edit.putFloat(M_GOING_OUT, 0);
            edit.commit();
        }
    }
}
