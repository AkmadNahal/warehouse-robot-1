package com.example.customer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends Activity {
static EditText name;
static EditText password;
static EditText mobile;

    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = (EditText)findViewById(R.id.name);
        password = (EditText)findViewById(R.id.password);
        mobile = (EditText)findViewById(R.id.mobile);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
    }


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
    
    public void register(View v)
    {
    	Toast.makeText(getApplicationContext(), name.getText().toString() + mobile.getText().toString() + password.getText().toString() , Toast.LENGTH_LONG).show();
    	//register goes here
    	
    	Intent goToNextActivity = new Intent(MainActivity.this,screen2.class);
		startActivity(goToNextActivity);
    }
    
    public void login(View v)
    {
    	Toast.makeText(getApplicationContext(), "login", Toast.LENGTH_LONG).show();
    	//login goes here
    	
    	Intent goToNextActivity = new Intent(MainActivity.this,screen2.class);
		startActivity(goToNextActivity);
    }
}
