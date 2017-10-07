package com.example.customer;

import java.io.IOException;
import java.util.ArrayList;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class screen2 extends Activity {


	GoogleCloudMessaging gcm;
	
	adapter adapter;
	ListView dataGrid;
	ArrayList<product> products = new ArrayList<product>();
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
        
        if (gcm == null) {
            gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
        }
        
        try {
			String regid = gcm.register("place sender id here from google dev account");
			
			//regid contains the guid post it to the server
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
	    	      new IntentFilter("update-view"));
        
    	
        
        //details from server to be populated
        for(Integer i=0;i<=10;i++)
        {
        	product product = new product(i.toString(),i.toString(),"discount " + i.toString());
        	products.add(product);
        }
        
        //to display the list
        adapter = new adapter(this,products);
        dataGrid = (ListView) findViewById(R.id.listView1);
    	dataGrid.setAdapter(adapter);
        
    	
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
			 
				adapter.notifyDataSetChanged();
				//dataGrid.setAdapter(adapter);
		  }
		};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
   
}
