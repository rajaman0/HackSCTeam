package com.company.chirag.eventize;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.net.MalformedURLException;

/**
 * Created by Chirag on 11/15/2015.
 */
public class TrendingActivity extends AppCompatActivity {

    public ListAdapter customAdapter;
    private MobileServiceClient mClient;
    private MobileServiceTable<ToDoItem> mToDoTable;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listactivity);
        ListView yourListView = (ListView) findViewById(R.id.lity);
        customAdapter = new ListAdapter(this, R.layout.row_item, this);

        yourListView.setAdapter(customAdapter);
        try {
            mClient = new MobileServiceClient(
                    "https://eventeyes.azure-mobile.net/",
                    "ykaEaIPEUWYxjiDlfowHUrSiuyGeJt17",
                    this
            );


        }
        catch(MalformedURLException e)
        {
        }
        mToDoTable = mClient.getTable(ToDoItem.class);



    }

    public void onResume(){
        super.onResume();
        update();


    }

    public void update() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {

                    mToDoTable.orderBy("counter", QueryOrder.Descending).execute().get();
                    final MobileServiceList<ToDoItem> result = mToDoTable.execute().get();
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            customAdapter.clear();
                            for (ToDoItem item : result) {
                                customAdapter.add(item);
                            }
                        }
                    });
                } catch (Exception exception) {

                }
                return null;

            }


        } .execute();

    }

    public void goBack(){
        finishActivity(0);
    }

}
