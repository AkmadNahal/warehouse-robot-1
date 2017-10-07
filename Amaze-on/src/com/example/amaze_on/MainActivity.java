package com.example.amaze_on;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void nexus5(View v)
    {
    	helper.orderid="101";
    	call_intent();
    }
    public void iphone616(View v)
    {
    	helper.orderid="103";
    	call_intent();
    }
    public void iphone4(View v)
    {
    	helper.orderid="102";
    	call_intent();
    }
    public void iphone632(View v)
    {
    	helper.orderid="104";
    	call_intent();
    }
    
    public void call_intent()
    {
    	Intent intent = new Intent(MainActivity.this, cust.class);
        startActivity(intent);
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
