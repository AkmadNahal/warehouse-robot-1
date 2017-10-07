package com.fragmentclass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.horizontalscrollviewwithpageindicator.R;
import com.loadingmanager.Utils;

@SuppressLint("HandlerLeak")
public class FragmentThree extends android.support.v4.app.Fragment {

	String imageArray;
	ProgressDialog pd = null;
	EditText edit1;

	public FragmentThree() {
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		imageArray = args.getString("url");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.thirdfragment, null);
		edit1 = (EditText) view.findViewById(R.id.editText1);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pd = new ProgressDialog(getActivity());
		pd.setMessage("Fragment Three...");
		pd.show();
		ThreadDownloadImage th = new ThreadDownloadImage();
		th.start();
	}

	private class ThreadDownloadImage extends Thread {
		@Override
		public void run() {
			super.run();
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(imageArray);
				HttpResponse response = httpclient.execute(httppost);
				InputStream is = response.getEntity().getContent();
				File file = new File("/mnt/sdcard/my.png");
				OutputStream os = new FileOutputStream(file);
				Utils.CopyStream(is, os);
				os.close();
				handler.sendEmptyMessage(1);
			} catch (Exception ex) {
				ex.printStackTrace(); // Unknown Host Exception
				handler.sendEmptyMessage(1);
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			pd.dismiss();
			switch (msg.what) {
			case 1:
				edit1.setEnabled(false);
				edit1.setText("Data downloaded...");
				break;
			case 0:
				edit1.setEnabled(false);
				edit1.setText("Data downloaded not completed...");
				break;
			default:
				break;
			}
		};
	};
}
