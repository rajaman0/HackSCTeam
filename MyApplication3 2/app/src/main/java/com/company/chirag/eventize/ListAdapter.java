package com.company.chirag.eventize;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.company.chirag.eventize.ToDoItem;
import com.company.chirag.eventize.R;
import com.company.chirag.eventize.ToDoItem;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;

/**
 * Created by Chirag on 11/15/2015.
 */
public class ListAdapter extends ArrayAdapter<ToDoItem> {

    TrendingActivity tv;

    public ListAdapter(Context context, int textViewResourceId, TrendingActivity t) {
        super(context, textViewResourceId);
        tv = t;
    }
    private MobileServiceClient mClient;
    private MobileServiceTable<ToDoItem> mToDoTable;


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.row_item, null);
        }

        ToDoItem p = getItem(position);//you have something to do with this vijay
        ((TextView) v.findViewById(R.id.event)).setText(p.getEvent());
        ((TextView) v.findViewById(R.id.location)).setText(p.getLocation());
        ((TextView) v.findViewById(R.id.From_Date)).setText(p.getFrom());
        ((TextView) v.findViewById(R.id.To_Date)).setText(p.getTo());

        ImageView upvote = (ImageView) v.findViewById(R.id.number);
        upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int j = position;
                ToDoItem a = getItem(j);
                a.setCounter(a.getCounter() + 1);
                try {
                    mClient = new MobileServiceClient(
                            "https://eventeyes.azure-mobile.net/",
                            "ykaEaIPEUWYxjiDlfowHUrSiuyGeJt17",tv

                    );


                } catch (Exception e) {
                    Log.v("OCR", "DOES NOT WORK");
                }
                mToDoTable = mClient.getTable(ToDoItem.class);
                mToDoTable.update(a);
                tv.update();
            }

        });


        return v;
    }

}