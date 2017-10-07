package com.horizontalscrollviewwithpageindicator;

import java.util.Vector;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.Toast;

import com.fragmentadapter.FragmentAdapter;
import com.fragmentclass.FragmentOne;
import com.fragmentclass.FragmentThree;
import com.fragmentclass.FragmentTwo;

public class FragmentMainActivity extends FragmentActivity {
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_main);
		Vector<Fragment> fragments = new Vector<Fragment>();
		FragmentOne fragmentOne = new FragmentOne();
		Bundle bundle = new Bundle();
		bundle.putString(
				"url",
				"https://lh6.googleusercontent.com/-jZgveEqb6pg/"
						+ "T3R4kXScycI/AAAAAAAAAE0/xQ7CvpfXDzc/s1024/sample_image_01.jpg");
		fragmentOne.setArguments(bundle);
		fragments.add(fragmentOne);

		FragmentTwo fragmenttwo = new FragmentTwo();
		bundle.putString(
				"url",
				"https://lh3.googleusercontent.com/"
						+ "-WujkdYfcyZ8/T3R4qrIMGUI/AAAAAAAAAGk/277LIdgvnjg/s1024/sample_image_15.jpg");
		fragmenttwo.setArguments(bundle);
		fragments.add(fragmenttwo);

		FragmentThree fragmenthree = new FragmentThree();
		fragmenthree.setArguments(bundle);
		fragments.add(fragmenthree);

		ViewPager myPager = (ViewPager) findViewById(R.id.myfivepanelpager);
		FragmentAdapter adapter = new FragmentAdapter(
				getSupportFragmentManager(), fragments);
		myPager.setAdapter(adapter);
		myPager.setCurrentItem(0);

		myPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				Toast.makeText(FragmentMainActivity.this,
						"Page Selected " + arg0, Toast.LENGTH_LONG).show();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
}
