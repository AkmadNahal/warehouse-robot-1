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
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.horizontalscrollviewwithpageindicator.R;
import com.loadingmanager.AhmadDownloadManager;
import com.loadingmanager.Utils;

@SuppressLint("HandlerLeak")
public class FragmentOne extends android.support.v4.app.Fragment {
	AhmadDownloadManager ahmad;
	Activity activity;
	private int height = 0, widht = 0;
	String imageArray;

	public FragmentOne() {
	}

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		imageArray = args.getString("url");
	}
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = getActivity();
		ahmad = new AhmadDownloadManager(activity);
		Display display = activity.getWindowManager().getDefaultDisplay();
		height = display.getHeight();
		widht = display.getWidth();
		ahmad.setCustomHeight(widht, height);
		pd = new ProgressDialog(getActivity());
		pd.setMessage("Fragment One...");
		pd.show();
		ThreadDownloadImage th = new ThreadDownloadImage();
		th.start();
	}

	ProgressDialog pd = null;

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
				Toast.makeText(activity, "All Data Loaded For Fragment Two",
						Toast.LENGTH_LONG).show();
				break;
			case 0:
				Toast.makeText(activity, "Error Loading Data for Fragment Two",
						Toast.LENGTH_LONG).show();
				break;
			default:
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.one_element, null);
		ImageView img = (ImageView) view.findViewById(R.id.imageView);
		ProgressBar bar = (ProgressBar) view.findViewById(R.id.bar);
		img.setTag(imageArray);
		ahmad.DisplayImage(imageArray, activity, img, bar);
		return view;
	}
}
