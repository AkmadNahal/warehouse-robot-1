package com.example.car;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyCountDownTimer countDownTimer = new MyCountDownTimer(9000000, 500);
        countDownTimer.start();
    }

    public class MyCountDownTimer extends CountDownTimer {
  	  public MyCountDownTimer(long startTime, long interval) {
  	   super(startTime, interval);
  	  }

  	  @Override
  	  public void onFinish() {
  		  
  	  }

  	  @Override
  	  public void onTick(long millisUntilFinished) {
  		onboard task = new onboard();
        task.execute(new String[] {});
  	  }
  	 }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private class onboard extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... numbers) {
        	try{
        	
     
			String URL = "http://192.168.2.2:8181/manoj/getorder";
			HttpGet get =new HttpGet(URL);
			BasicHttpResponse order; 
			HttpClient client = new DefaultHttpClient();
			order = (BasicHttpResponse) client.execute(get);
			String orderid = order.getFirstHeader("orderid").getValue().toString();
			if(orderid.length()<2)
			{
				return "failure";
			}
			else{
				return orderid;
			}
         	
        	}
        	catch(Exception e){
        		e.printStackTrace();
        		return "failure";
        	}
        }

        @Override
        protected void onPostExecute(String result) {
        	ImageView iv = (ImageView)findViewById(R.id.imageView1);
        	if(result.compareToIgnoreCase("failure")!=0)
        	{
        		String qrData = result;
        		int qrCodeDimention = 500;

        		QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrData, null,
        		        Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);

        		try {
        		    Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
        		    iv.setImageBitmap(bitmap);
        		} catch (WriterException e) {
        		    e.printStackTrace();
        		}
        		
        		//Bitmap bmp = QRCode.from(result).bitmap();
        		//iv.setImageBitmap(bmp);
        	}
        	else{
        		iv.setImageBitmap(BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.noorders));
        	}
        }
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
