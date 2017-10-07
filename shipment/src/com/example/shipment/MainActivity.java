package com.example.shipment;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
	HttpClient base;
	TextView pname,pversion,cname,cphone,caddr;
	String csrf = "";
	String orderid = "";
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pname = (TextView)findViewById(R.id.oname);
        pversion = (TextView)findViewById(R.id.version);
        cname = (TextView)findViewById(R.id.cname);
        cphone = (TextView)findViewById(R.id.phone);
        caddr = (TextView)findViewById(R.id.address);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
    }
    
    public class MySSLSocketFactory extends SSLSocketFactory {
        SSLContext sslContext = SSLContext.getInstance("TLS");

        public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };

            sslContext.init(null, new TrustManager[] { tm }, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }
    
    public HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
    
    


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void scan(View v)
    {
    	Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        intent.setPackage("com.google.zxing.client.android");
        intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    	//fetch the order details and customer details with id
    	//populate the UI
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                orderid = contents;
                Toast.makeText(getApplicationContext(), contents, Toast.LENGTH_LONG).show();
                try{
          		  String url = "https://192.168.2.5:8083/gateway/odata/SAP/TOLASERVICE;v=1/productDetails('"+contents+"')?$format=json";
      			  HttpGet get = new HttpGet(url);
      			  get.addHeader("Content-Type", "application/atom+xml");
      			  get.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
      			  get.addHeader("x-csrf-token","fetch");
      			  HttpClient client = getNewHttpClient();
      			  HttpResponse response2 = client.execute(get);
      			  String data = EntityUtils.toString(response2.getEntity());
      			  JSONObject obj = new JSONObject(data);
      			  JSONObject d = obj.getJSONObject("d");
      			  csrf = response2.getFirstHeader("x-csrf-token").getValue();
      			  pname.setText(d.getString("productName"));
      			  pversion.setText(d.getString("productVersion"));
      			//response2.getEntity().getContent().close();
      			  
      			String url2 = "https://192.168.2.5:8083/gateway/odata/SAP/TOLASERVICE;v=1/orderDetails('"+contents+"')?$format=json";
    			  HttpGet get2 = new HttpGet(url2);
    			  get2.addHeader("Content-Type", "application/atom+xml");
    			  get2.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
    			  
    			  
    			  HttpResponse response = client.execute(get2);
    			  String data2 = EntityUtils.toString(response.getEntity());
    			  JSONObject obj2 = new JSONObject(data2);
    			  JSONObject d2 = obj2.getJSONObject("d");
    			  cname.setText(d2.getString("customerName"));
    			  cphone.setText(d2.getString("customerPhone"));
    			  caddr.setText(d2.getString("customerAddress"));
    			  response.getEntity().getContent().close();
      			  
          		}catch(Exception e)
          		{
          			e.printStackTrace();
          		}
            } else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }
    }
    
    public void finishorder(View v)
    {
    	String url2 = "http://192.168.2.2:8181/manoj/finishorder";
		  HttpPost get2 = new HttpPost(url2);
		  HttpClient client = new DefaultHttpClient();
		  HttpResponse response;
		try {
			response = client.execute(get2);
			String data2 = EntityUtils.toString(response.getEntity());
		
	    		  String url = "https://192.168.2.5:8083/gateway/odata/SAP/TOLASERVICE;v=1/productDetails('101')?$format=json";
				  HttpGet get = new HttpGet(url);
				  get.addHeader("Content-Type", "application/atom+xml");
				  get.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
				  get.addHeader("x-csrf-token","fetch");
				  HttpClient client2 = getNewHttpClient();
				  HttpResponse response2 = client2.execute(get);
				  String data22 = EntityUtils.toString(response2.getEntity());
				  csrf = response2.getFirstHeader("x-csrf-token").getValue().toString();
				  
				  
		HttpDelete del = new HttpDelete("https://192.168.2.5:8083/gateway/odata/SAP/TOLASERVICE;v=1/orderDetails('"+orderid+"')");
		del.addHeader("x-csrf-token",csrf);
		del.addHeader("Content-Type", "application/atom+xml");
		  del.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
		  HttpResponse response22 = client2.execute(del);
		//response22.getEntity().getContent().close();
		
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
