package com.example.alexandre.test;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by alexandre on 29/10/17.
 */

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private static final int RED =   0xff000080;
    private static final int GREEN = 0x00ff0080;
    private static final int BLUE =  0x0000ff80;

    public TransactionAdapter(Context context, ArrayList<Transaction> transactions) {
        super(context, 0, transactions);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Transaction transaction = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_transaction, parent, false);
        }

        TextView transactionDate = (TextView) convertView.findViewById(R.id.date);
        TextView transactionName = (TextView) convertView.findViewById(R.id.name);
        TextView transactionCost = (TextView) convertView.findViewById(R.id.cost);
        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout);


        if(transaction.getType() == MainActivity.GROCERIES) { layout.setBackgroundColor(getContext().getResources().getColor(R.color.green)); }
        else if(transaction.getType() == MainActivity.RANDOM) { layout.setBackgroundColor(getContext().getResources().getColor(R.color.blue)); }
        else if(transaction.getType() == MainActivity.GOING_OUT) { layout.setBackgroundColor(getContext().getResources().getColor(R.color.red)); }

        DecimalFormat format = new DecimalFormat("0.00");

        transactionDate.setText(transaction.getDateString());
        transactionName.setText(transaction.getName());
        transactionCost.setText("$" + format.format(transaction.getCost()));

        return convertView;
    }
}
