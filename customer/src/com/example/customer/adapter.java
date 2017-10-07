package com.example.customer;

import java.net.URL;
import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;

public class adapter extends ArrayAdapter<product> {
	private final List<product> list;
	private final Activity context;
	  private final adapter adapter;
	
  public adapter(Activity context, List<product> list) {
    super(context, R.layout.adapter, list);
    this.context = context;
    this.list = list;
    this.adapter = this;
  }

  static class ViewHolder {
	  TextView name;
	  TextView description;
	  TextView discount;
	  RelativeLayout rl;
	  
  }

  @Override
  public product getItem(int position) {
    return list.get(position);
  }
  
  @SuppressLint("NewApi") @Override
  public View getView(final int position, View convertView, ViewGroup parent) {
    View view = null;
    
    if (convertView == null) {
    final ViewHolder viewHolder = new ViewHolder();
      LayoutInflater inflator = context.getLayoutInflater();
      view = inflator.inflate(R.layout.adapter, null);
      viewHolder.name = (TextView)view.findViewById(R.id.pname);
      viewHolder.description = (TextView)view.findViewById(R.id.pdesc);
      viewHolder.discount = (TextView)view.findViewById(R.id.pdiscount);

      //viewHolder.rl = (RelativeLayout)view.findViewById(R.id.rowlayout);
      view.setTag(viewHolder);
      viewHolder.name.setTag(list.get(position));
    } else {
      view = convertView;
      ((ViewHolder) view.getTag()).name.setTag(list.get(position));
    }
    
    final ViewHolder holder = (ViewHolder) view.getTag();
   holder.name.setText(list.get(position).getName());
   holder.description.setText(list.get(position).getDescription());
   holder.discount.setText(list.get(position).getDiscount());
	
	
    return view;
  }
  

  
} 