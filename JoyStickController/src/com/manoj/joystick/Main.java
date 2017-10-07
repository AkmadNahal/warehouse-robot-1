package com.manoj.joystick;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.DefaultClientConnection;
import org.apache.http.message.BasicHttpResponse;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import app.akexorcist.joystickcontroller.R;

public class Main extends Activity {
	RelativeLayout layout_joystick;
	ImageView image_joystick, image_border;
	TextView textView1, textView2, textView3, textView4, textView5;
	HttpClient cl;
	JoyStickClass js;
	String dir1="1";
	String dir2="2";
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
		
        textView1 = (TextView)findViewById(R.id.textView1);
        textView2 = (TextView)findViewById(R.id.textView2);
        textView3 = (TextView)findViewById(R.id.textView3);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        final WebView webView = (WebView)findViewById(R.id.webView1);
        webView.loadUrl("http://192.168.2.2:8080/image.html");
	    layout_joystick = (RelativeLayout)findViewById(R.id.layout_joystick);

        js = new JoyStickClass(getApplicationContext()
        		, layout_joystick, R.drawable.image_button);
	    js.setStickSize(150, 150);
	    js.setLayoutSize(500, 500);
	    js.setLayoutAlpha(150);
	    js.setStickAlpha(100);
	    js.setOffset(90);
	    js.setMinimumDistance(50);
	    
	    
	    layout_joystick.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View arg0, MotionEvent arg1) {
				sendreq sendc = new sendreq();
				js.drawStick(arg1);
				if(arg1.getAction() == MotionEvent.ACTION_DOWN
						|| arg1.getAction() == MotionEvent.ACTION_MOVE) {
					textView1.setText("X : " + String.valueOf(js.getX()));
					textView2.setText("Y : " + String.valueOf(js.getY()));
					textView3.setText("Angle : " + String.valueOf(js.getAngle()));
					textView4.setText("Distance : " + String.valueOf(js.getDistance()));
					
					int direction = js.get8Direction();
					if(direction == JoyStickClass.STICK_UP) {
						textView5.setText("Direction : Up");
						dir1 = "up";
					} else if(direction == JoyStickClass.STICK_UPRIGHT) {
						textView5.setText("Direction : Up Right");
						dir1 = "up";
					} else if(direction == JoyStickClass.STICK_RIGHT) {
						textView5.setText("Direction : Right");
						dir1 = "right";
					} else if(direction == JoyStickClass.STICK_DOWNRIGHT) {
						textView5.setText("Direction : Down Right");
						dir1 = "rightdown";
					} else if(direction == JoyStickClass.STICK_DOWN) {
						textView5.setText("Direction : Down");
						dir1 = "down";
					} else if(direction == JoyStickClass.STICK_DOWNLEFT) {
						textView5.setText("Direction : Down Left");
						dir1 = "leftdown";
					} else if(direction == JoyStickClass.STICK_LEFT) {
						textView5.setText("Direction : Left");
						dir1 = "left";
					} else if(direction == JoyStickClass.STICK_UPLEFT) {
						textView5.setText("Direction : Up Left");
						dir1 = "left";
					} else if(direction == JoyStickClass.STICK_NONE) {
						textView5.setText("Direction : Center");
						dir1 = "stop";
					}
				} else if(arg1.getAction() == MotionEvent.ACTION_UP) {
					dir1 = "stop";
					textView1.setText("X :");
					textView2.setText("Y :");
					textView3.setText("Angle :");
					textView4.setText("Distance :");
					textView5.setText("Direction :");
				}
				if(!dir2.equals(dir1))
				{
					sendc.execute(new String []{dir1,""});
					dir2 = dir1;
				}
				return true;
			}
        });
    }
    

	private class sendreq extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... numbers) {
        	String com = numbers[0];
        	String url = "http://192.168.2.2:1398/"+com;
        	HttpGet get = new HttpGet(url);
        	try {
        		HttpClient cl = new DefaultHttpClient();
				HttpResponse resp = cl.execute(get);
				System.out.println(resp.getStatusLine().getStatusCode());
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
			return url;    	
        }

        @Override
        protected void onPostExecute(String result) {
        	
        }
      }
}
