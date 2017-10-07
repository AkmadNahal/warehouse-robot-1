//project name is jetty_new

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.SecurityHandler;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Credential;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.JSONArray;
import org.json.JSONException;

public class jetty_all extends HttpServlet {
	//static byte[] buf = new byte[5000]; 
	 static int len;
	 public long start,stop;
	 public StringBuffer strbuf;
	 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	if(req.getPathInfo().contains("getorder"))
    	{
    		String orderid = "";
    		BufferedReader br = null; 		 
    		try {
    			br = new BufferedReader(new FileReader("neworder.txt"));
    			while ((orderid = br.readLine()) != null) {
    				System.out.println(orderid);
    			}
    			br.close();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		resp.setHeader("orderid", orderid);
    		resp.setStatus(200);   		
    	}

    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
  	
  	
    }

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	if(req.getPathInfo().contains("neworder"))
    	{
    		HttpClient base = new DefaultHttpClient();
    		  try {
    	            SSLContext ctx = SSLContext.getInstance("TLS");
    	            X509TrustManager tm = new X509TrustManager() {
    	 
						public void checkClientTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws java.security.cert.CertificateException {
							// TODO Auto-generated method stub
							
						}

						public void checkServerTrusted(
								java.security.cert.X509Certificate[] arg0,
								String arg1)
								throws java.security.cert.CertificateException {
							// TODO Auto-generated method stub
							
						}

						public java.security.cert.X509Certificate[] getAcceptedIssuers() {
							// TODO Auto-generated method stub
							return null;
						}
    	            };
    	            X509HostnameVerifier verifier = new X509HostnameVerifier() {
    	 
						public boolean verify(String arg0, SSLSession arg1) {
							// TODO Auto-generated method stub
							return true;
						}

						public void verify(String arg0, SSLSocket arg1)
								throws IOException {
							// TODO Auto-generated method stub
							
						}

						public void verify(String arg0,
								java.security.cert.X509Certificate arg1)
								throws SSLException {
							// TODO Auto-generated method stub
							
						}

						public void verify(String arg0, String[] arg1,
								String[] arg2) throws SSLException {
							// TODO Auto-generated method stub
							
						}
    	            };
    	            ctx.init(null, new TrustManager[]{tm}, null);
    	            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
    	            ssf.setHostnameVerifier(verifier);
    	            ClientConnectionManager ccm = base.getConnectionManager();
    	            SchemeRegistry sr = ccm.getSchemeRegistry();
    	            sr.register(new Scheme("https", ssf, 443));
    	            base = new DefaultHttpClient(ccm, base.getParams());
    	        } catch (Exception ex) {
    	            ex.printStackTrace();
    	        }
    		String orderId = req.getHeader("orderid");
    		String data = "";
    		FileOutputStream fop = null;
    		File file;
    		try {
    			file = new File("neworder.txt");
    			fop = new FileOutputStream(file);
    			if (!file.exists()) {
    				file.createNewFile();
    			}
    			byte[] contentInBytes = orderId.getBytes();
    			fop.write(contentInBytes);
    			fop.flush();
    			fop.close();
    			System.out.println("Done writing to file");
     
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
    		//get the directions for orderId
    		try{
    		  String url = "https://localhost:8083/gateway/odata/SAP/TOLASERVICE;v=1/productDetails('"+orderId+"')?$format=json";
			  HttpGet get = new HttpGet(url);
			  get.addHeader("Content-Type", "application/atom+xml");
			  get.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
			  HttpResponse response2 = base.execute(get);
			  JSONObject jsonObject = (JSONObject) new JSONParser().parse(EntityUtils.toString(response2.getEntity()));
			  data = ((JSONObject)jsonObject.get("d")).get("productLocation").toString();
			  System.out.println(response2.getStatusLine().getStatusCode());
    		}catch(Exception e)
    		{
    			e.printStackTrace();
    		}
    		
    		try{
    		  HttpClient clientadd = new DefaultHttpClient();
  			  HttpPost post = new HttpPost("http://192.168.42.1:1339/");
  			  StringEntity input = new StringEntity(data,"UTF-8");
  			  post.setHeader("Content-Type", "application/atom+xml");
  			  post.setEntity(input);
  			  HttpResponse response2 = clientadd.execute(post);
  			  System.out.println(response2.getStatusLine().getStatusCode());
  			  Thread.sleep(6000);
    			}
  			catch (Exception e) {
  					e.printStackTrace();
  				}
    		try{
      		  HttpClient clientadd = new DefaultHttpClient();
    			  HttpPost post = new HttpPost("http://192.168.42.1:1339/");
    			  StringEntity input = new StringEntity("{\"route\":[{\"direction\":\"straight\",\"rotations\":\"2\"}]}","UTF-8");
    			  post.setHeader("Content-Type", "application/atom+xml");
    			  post.setEntity(input);
    			  HttpResponse response2 = clientadd.execute(post);
    			  System.out.println(response2.getStatusLine().getStatusCode());
    			  Thread.sleep(3000);
      			}
    			catch (Exception e) {
    					e.printStackTrace();
    			}
    		try{
        		  HttpClient clientadd = new DefaultHttpClient();
      			  HttpPost post = new HttpPost("http://192.168.42.1:1339/");
      			  StringEntity input = new StringEntity("{\"route\":[{\"direction\":\"back\",\"rotations\":\"2\"},{\"direction\":\"left\",\"rotations\":\"2\"}]}","UTF-8");
      			  post.setHeader("Content-Type", "application/atom+xml");
      			  post.setEntity(input);
      			  HttpResponse response2 = clientadd.execute(post);
      			  System.out.println(response2.getStatusLine().getStatusCode());
        			}
      			catch (Exception e) {
      					e.printStackTrace();
      			}
    		try{
      		  HttpClient clientadd = new DefaultHttpClient();
    			  HttpPost post = new HttpPost("http://192.168.42.1:1339/");
    			  data = data.replaceAll("right", "asdf");
    			  data = data.replaceAll("left", "right");
    			  data = data.replaceAll("asdf", "left");
    			  JSONObject jsonObject = (JSONObject) new JSONParser().parse(data.toString());
    			  org.json.simple.JSONArray oldarray  = (org.json.simple.JSONArray) jsonObject.get("route");
    			  org.json.simple.JSONArray newarray = new org.json.simple.JSONArray();
					int size = oldarray.size();
					ArrayList<String> array = new ArrayList<String>();
					size--;
					for (int i = size; i >= 0; i--) {
						JSONObject obj = (JSONObject) oldarray.get(i);
						newarray.add(obj);
					}
					JSONObject newjson = new JSONObject();
					newjson.put("route", newarray);
					String newdata = newjson.toString();
    			  StringEntity input = new StringEntity(newdata,"UTF-8");
    			  post.setHeader("Content-Type", "application/atom+xml");
    			  post.setEntity(input);
    			  HttpResponse response2 = clientadd.execute(post);
    			  System.out.println(response2.getStatusLine().getStatusCode());

      			}
    			catch (Exception e) {
    					e.printStackTrace();
    				}
    		try{
      		  HttpClient clientadd = new DefaultHttpClient();
    			  HttpPost post = new HttpPost("http://192.168.42.1:1339/");
    			  StringEntity input = new StringEntity("{\"route\":[{\"direction\":\"left\",\"rotations\":\"2\"}]}","UTF-8");
    			  post.setHeader("Content-Type", "application/atom+xml");
    			  post.setEntity(input);
    			  HttpResponse response2 = clientadd.execute(post);
    			  System.out.println(response2.getStatusLine().getStatusCode());
      			}
    			catch (Exception e) {
    					e.printStackTrace();
    			}
    	}
    	if(req.getPathInfo().contains("finishorder"))
    	{
    		FileOutputStream fop = null;
    		File file;
    		String orderId = "";
    		try {
    			file = new File("neworder.txt");
    			fop = new FileOutputStream(file);
    			if (!file.exists()) {
    				file.createNewFile();
    			}
    			byte[] contentInBytes = orderId.getBytes();
    			fop.write(contentInBytes);
    			fop.flush();
    			fop.close();
    			System.out.println("Done erasing");
     
    		} catch (Exception e) {
    			e.printStackTrace();
    		} 
    	}
    }
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	start=System.currentTimeMillis();
    	resp.setStatus(204);
    	resp.setHeader("Content-Type", "application/xml; charset=utf-8");
    	resp.setHeader("Content-Encoding", "gzip");
    	
  	stop=System.currentTimeMillis();
  	System.out.println("Delete = "+ (stop-start));
    }

    public static void main(String[] args) throws Exception{
        int port = 8181;
       
        Server server = new Server(port);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        //context.setSecurityHandler(basicAuth("supuser*", "s3puser/", "Enter the special character usernameand password"));
        context.setContextPath("/manoj");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new jetty_all()),"/*");
        server.start();
        server.join();   
        
    }
}