package com.example.alexandre.test;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by alexandre on 29/10/17.
 */

public class Transaction {
    private int id;
    private Calendar calendar;
    private String name;
    private double cost;
    private int type;

    public Transaction(int id, String date, String name, double cost, int type){
        this.id = id;
        this.calendar = DatabaseTools.makeCalendar(date);
        this.name = name;
        this.cost =cost;
        this.type = type;
    }

    public Transaction(double cost, int type) {
        this.cost = cost;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return calendar.get(Calendar.YEAR) + "-" + calendar.get(Calendar.MONTH) + "-" + calendar.get(Calendar.DATE);
    }

    public String getDateString() {
        return calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + calendar.get(Calendar.DATE);
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    public int getType() { return type; }

}
