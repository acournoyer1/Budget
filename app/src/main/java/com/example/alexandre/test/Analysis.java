package com.example.alexandre.test;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.TextureView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Analysis extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        refreshWeekly();

        Spinner s = findViewById(R.id.spinner);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0) {
                    refreshWeekly();
                } else if(i == 1) {
                    refreshMonthly();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public boolean isBetween(Calendar c1, Calendar c2, Calendar c3) {
        return c1.compareTo(c2) >= 0 && c1.compareTo(c3) <= 0;
    }

    public void refreshWeekly() {
        double groceriesSum = 0, randomSum = 0, goingOutSum = 0;
        for(Transaction t: DatabaseTools.getWeekTransactions(getBaseContext())) {
            if(t.getType() == MainActivity.GROCERIES) {
                groceriesSum += t.getCost();
            } else if(t.getType() == MainActivity.RANDOM) {
                randomSum += t.getCost();
            } else if(t.getType() == MainActivity.GOING_OUT) {
                goingOutSum += t.getCost();
            }
        }

        TextView groceriesText = findViewById(R.id.GroceriesProgress);
        TextView randomText = findViewById(R.id.RandomProgress);
        TextView goingOutText = findViewById(R.id.GoingOutProgress);

        SharedPreferences pref = getSharedPreferences("limits", Context.MODE_PRIVATE);
        double groceriesLimit = pref.getFloat(Tools.W_GROCERIES, 0);
        if(groceriesLimit == 0) groceriesLimit = pref.getFloat(Tools.M_GROCERIES, 0);
        double randomLimit = pref.getFloat(Tools.W_RANDOM, 0);
        if(randomLimit == 0) randomLimit = pref.getFloat(Tools.M_RANDOM, 0);
        double goingOutLimit = pref.getFloat(Tools.W_GOING_OUT, 0);
        if(goingOutLimit == 0) goingOutLimit = pref.getFloat(Tools.M_GOING_OUT, 0);

        DecimalFormat format = new DecimalFormat("0.00");
        groceriesText.setText(format.format(groceriesSum) + "/" + format.format(groceriesLimit));
        randomText.setText(format.format(randomSum) + "/" + format.format(randomLimit));
        goingOutText.setText(format.format(goingOutSum) + "/" + format.format(goingOutLimit));

        ProgressBar groceriesBar = findViewById(R.id.groceriesBar);
        ProgressBar randomBar = findViewById(R.id.randomBar);
        ProgressBar goingOutBar = findViewById(R.id.goingOutBar);

        groceriesBar.setMax((int)groceriesLimit);
        randomBar.setMax((int)randomLimit);
        goingOutBar.setMax((int)goingOutLimit);

        groceriesBar.setProgress((int)groceriesSum);
        if(groceriesSum > 0.75 * groceriesLimit) groceriesBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else groceriesBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        randomBar.setProgress((int)randomSum);
        if(randomSum > 0.75 * randomLimit) randomBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else randomBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        goingOutBar.setProgress((int)goingOutSum);
        if(goingOutSum > 0.75 * goingOutLimit) goingOutBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else goingOutBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));

        drawWeekly();
    }

    public void drawWeekly() {
        TextureView view = findViewById(R.id.textureView);
        Bitmap bitmap = Bitmap.createBitmap(200, 200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        canvas.drawCircle(50, 50, 50, p);
        view.draw(canvas);
    }

    public void refreshMonthly() {
        double groceriesSum = 0, randomSum = 0, goingOutSum = 0;
        for(Transaction t: DatabaseTools.getWeekTransactions(getBaseContext())) {
            if(t.getType() == MainActivity.GROCERIES) {
                groceriesSum += t.getCost();
            } else if(t.getType() == MainActivity.RANDOM) {
                randomSum += t.getCost();
            } else if(t.getType() == MainActivity.GOING_OUT) {
                goingOutSum += t.getCost();
            }
        }

        TextView groceriesText = findViewById(R.id.GroceriesProgress);
        TextView randomText = findViewById(R.id.RandomProgress);
        TextView goingOutText = findViewById(R.id.GoingOutProgress);

        SharedPreferences pref = getSharedPreferences("limits", Context.MODE_PRIVATE);
        double groceriesLimit = pref.getFloat(Tools.M_GROCERIES, 0);
        double randomLimit = pref.getFloat(Tools.M_RANDOM, 0);
        double goingOutLimit = pref.getFloat(Tools.M_GOING_OUT, 0);

        DecimalFormat format = new DecimalFormat("0.00");
        groceriesText.setText(format.format(groceriesSum) + "/" + format.format(groceriesLimit));
        randomText.setText(format.format(randomSum) + "/" + format.format(randomLimit));
        goingOutText.setText(format.format(goingOutSum) + "/" + format.format(goingOutLimit));

        ProgressBar groceriesBar = findViewById(R.id.groceriesBar);
        ProgressBar randomBar = findViewById(R.id.randomBar);
        ProgressBar goingOutBar = findViewById(R.id.goingOutBar);

        groceriesBar.setMax((int)groceriesLimit);
        randomBar.setMax((int)randomLimit);
        goingOutBar.setMax((int)goingOutLimit);

        groceriesBar.setProgress((int)groceriesSum);
        if(groceriesSum > 0.75 * groceriesLimit) groceriesBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else groceriesBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        randomBar.setProgress((int)randomSum);
        if(randomSum > 0.75 * randomLimit) randomBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else randomBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        goingOutBar.setProgress((int)goingOutSum);
        if(goingOutSum > 0.75 * goingOutLimit) goingOutBar.setProgressTintList(ColorStateList.valueOf(Color.RED));
        else goingOutBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.analysis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;
        if (id == R.id.nav_data) {
            intent = new Intent(this, DataActivity.class);
        } else if (id == R.id.nav_bill) {
            intent = new Intent(this, MainActivity.class);
        } else if (id == R.id.nav_analysis) {
            intent = new Intent(this, Analysis.class);
        } else if (id == R.id.nav_tools) {
            intent = new Intent(this, Tools.class);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if(intent != null)startActivity(intent);
        return true;
    }
}
