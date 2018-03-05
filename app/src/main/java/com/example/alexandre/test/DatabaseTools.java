package com.example.alexandre.test;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.alexandre.test.DataReaderDbHelper;
import com.example.alexandre.test.MainActivity;
import com.example.alexandre.test.Transaction;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;

/**
 * Created by alexandre on 02/11/17.
 */

public class DatabaseTools {

    public static boolean isBetween(Calendar current, Calendar start, Calendar end) {
        return current.compareTo(start) >= 0 && current.compareTo(end) <= 0;
    }

    public static void deleteTransaction(Transaction t, Context context) {
        DataReaderDbHelper mDbHelper = new DataReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String selection = DataReaderDbHelper.DataEntry._ID + " = ?";
        String[] selectionArgs = {"" + t.getId()};

        db.delete(DataReaderDbHelper.DataEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
    }

    public static ArrayList<Transaction> getTransactions(Calendar startCalendar, Calendar endCalendar, Context context) {
        DataReaderDbHelper mDbHelper = new DataReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DataReaderDbHelper.DataEntry.COLUMN_NAME_DATE,
                DataReaderDbHelper.DataEntry.COLUMN_NAME_PRICE,
                DataReaderDbHelper.DataEntry.COLUMN_NAME_TYPE
        };

        Cursor cursor = db.query(
                DataReaderDbHelper.DataEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<Transaction> entries = new ArrayList<Transaction>();
        while(cursor.moveToNext()) {
            String[] data = cursor.getString(0).split("-");
            Calendar calendarData = new GregorianCalendar(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));

            if(isBetween(calendarData, startCalendar, endCalendar)) {
                double cost = cursor.getDouble(1);
                int type = cursor.getInt(2);
                entries.add(new Transaction(cost, type));
            }
        }
        db.close();
        return entries;
    }

    public static ArrayList<Transaction> getAllTransactions(Context context) {
        DataReaderDbHelper mDbHelper = new DataReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] projection = {
                DataReaderDbHelper.DataEntry._ID,
                DataReaderDbHelper.DataEntry.COLUMN_NAME_DATE,
                DataReaderDbHelper.DataEntry.COLUMN_NAME_NAME,
                DataReaderDbHelper.DataEntry.COLUMN_NAME_PRICE,
                DataReaderDbHelper.DataEntry.COLUMN_NAME_TYPE
        };

        Cursor cursor = db.query(
                DataReaderDbHelper.DataEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        LinkedList<Transaction> entries = new LinkedList<>();
        while(cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String date = cursor.getString(1);
            String name = cursor.getString(2);
            double cost = cursor.getDouble(3);
            int type = cursor.getInt(4);
            entries.addFirst(new Transaction(id, date, name, cost, type));
        }
        db.close();
        return new ArrayList<Transaction>(entries);
    }

    public static ArrayList<Transaction> getWeekTransactions(Context context) {
        return getTransactions(getWeekStart(), getWeekEnd(), context);
    }

    public static ArrayList<Transaction> getMonthTransactions(Context context) {
        return getTransactions(getMonthStart(), getMonthEnd(), context);
    }

    public static Calendar getWeekStart() {
        Calendar calendar = new GregorianCalendar();

        int date = calendar.get(Calendar.DATE), year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), startDate = 0;
        if(date >= 1 && date <= 7) startDate = 1;
        else if(date >= 8 && date <= 14) startDate = 8;
        else if(date >= 15 && date <= 21) startDate = 15;
        else if(date >= 22 && date <= 31) startDate = 22;

        return new GregorianCalendar(year, month, startDate);
    }

    public static Calendar getWeekEnd() {
        Calendar calendar = new GregorianCalendar();
        int date = calendar.get(Calendar.DATE), year = calendar.get(Calendar.YEAR), month = calendar.get(Calendar.MONTH), endDate = 0;
        if(date >= 1 && date <= 7) endDate = 7;
        else if(date >= 8 && date <= 14) endDate = 14;
        else if(date >= 15 && date <= 21) endDate = 21;
        else if(date >= 22 && date <= 31) {
            if(month == 3 || month == 5 || month == 8 || month == 10) endDate = 30;
            else if(month == 1) endDate = 28;
            else endDate = 31;
        }

        return new GregorianCalendar(year, month, endDate);
    }

    public static Calendar getMonthStart() {
        Calendar calendar = new GregorianCalendar();
        int startDate = 1;
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        return new GregorianCalendar(year, month, startDate);
    }

    public static Calendar getMonthEnd() {
        Calendar calendar = new GregorianCalendar();
        int endDate = 31;
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        if(endDate == 31 && (month == 3 || month == 5 || month == 8 || month == 10)) {
            endDate = 30;
        } else if(endDate == 31 && month == 1) {
            endDate = 28;
        }

        return new GregorianCalendar(year, month, endDate);
    }

    public static Calendar makeCalendar(String s) {
        String[] data = s.split("-");
        return new GregorianCalendar(Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]));
    }

    public static String getCalendarString(Calendar c) {
        return c.get(Calendar.YEAR) + "-" + c.get(Calendar.MONTH) + "-" + c.get(Calendar.DATE);
    }

}
