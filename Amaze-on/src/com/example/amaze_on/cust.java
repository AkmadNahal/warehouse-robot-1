package com.example.amaze_on;

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
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
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
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class cust extends ActionBarActivity {

	EditText cname;
	EditText cphone;
	EditText caddress;
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cust);
        cname = (EditText)findViewById(R.id.ename);
        cphone = (EditText)findViewById(R.id.editText1);
        caddress = (EditText)findViewById(R.id.editText2);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy); 
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
    
    public void order(View v)
    {
    	String csrf = "";
    	try{
    		  String url = "https://192.168.2.5:8083/gateway/odata/SAP/TOLASERVICE;v=1/productDetails('101')?$format=json";
			  HttpGet get = new HttpGet(url);
			  get.addHeader("Content-Type", "application/atom+xml");
			  get.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
			  get.addHeader("x-csrf-token","fetch");
			  HttpClient client = getNewHttpClient();
			  HttpResponse response2 = client.execute(get);
			  String data2 = EntityUtils.toString(response2.getEntity());
			  csrf = response2.getFirstHeader("x-csrf-token").getValue().toString();
			  //response2.getEntity().getContent().close();
			  
			String url2 = "https://192.168.2.5:8083/gateway/odata/SAP/TOLASERVICE;v=1/orderDetails";
			  HttpPost post = new HttpPost(url2);
			  post.addHeader("Content-Type", "application/atom+xml");
			  post.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
			  post.addHeader("x-csrf-token", csrf);
			  String data = "<entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:m=\"http://schemas.microsoft.com/ado/2007/08/dataservices/metadata\" xmlns:d=\"http://schemas.microsoft.com/ado/2007/08/dataservices\" xml:base=\"https://inln50868870a.apj.global.corp.sap:8083/gateway/odata/SAP/TOLASERVICE;v=1/\">"+
					  "<id>"+
					  "https://inln50868870a.apj.global.corp.sap:8083/gateway/odata/SAP/TOLASERVICE;v=1/orderDetails('"+helper.orderid+"')"+
					  "</id>"+
					  "<title type=\"text\">orderDetails</title>"+
					  "<updated>2014-11-14T15:32:41.969+05:30</updated>"+
					  "<category term=\"ODataDemo.orderDetail\" scheme=\"http://schemas.microsoft.com/ado/2007/08/dataservices/scheme\"/>"+
					  "<link href=\"orderDetails('"+helper.orderid+"')\" rel=\"edit\" title=\"orderDetail\"/>"+
					  "<content type=\"application/xml\">"+
					  "<m:properties>"+
					  "<d:orderId>"+helper.orderid+"</d:orderId>"+
					  "<d:customerName>"+cname.getText().toString()+"</d:customerName>"+
					  "<d:customerAddress>"+caddress.getText().toString()+"</d:customerAddress>"+
					  "<d:customerPhone>"+cphone.getText().toString()+"</d:customerPhone>"+
					  "</m:properties>"+
					  "</content>"+
					  "</entry>";
			  StringEntity input = new StringEntity(data,"UTF-8");
			  post.setEntity(input);
			  HttpResponse response = client.execute(post);
			  response.getEntity().getContent().close();
			  
			  HttpPost pst = new HttpPost("http://192.168.2.2:8181/manoj/neworder");
			  pst.addHeader("orderid",helper.orderid);
			  HttpClient cl = new DefaultHttpClient();
			  HttpResponse response3 = cl.execute(pst);
			System.out.println("status = " +response3.getStatusLine().getStatusCode());
			  
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    	finish();
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
