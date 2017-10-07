//project name is jetty_new

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@SuppressWarnings("serial")
public class jetty_all extends HttpServlet {
	//static byte[] buf = new byte[5000]; 
	 static String sorderid="-";
	 public long start,stop;
	 public StringBuffer strbuf;
	 static String data = "";
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	if(req.getPathInfo().contains("getorder"))
    	{
    		resp.addHeader("orderid", sorderid);
    		resp.setStatus(200);   		
    	}

    }
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	
  	
  	
    }

	@SuppressWarnings("resource")
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	if(req.getPathInfo().contains("neworder"))
    	{
    		/*HttpClient base = new DefaultHttpClient();
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
    		  */
    		String orderId = req.getHeader("productid");
    		
    		sorderid = orderId;
    		try{
    		String content = new Scanner(new File("/home/pi/Desktop/orderdetails.txt")).useDelimiter("\\Z").next();
			  JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
			  data = ((JSONObject)jsonObject.get(orderId)).toString();
			  new Thread(new Runnable() {
				    public void run() {
				    	HttpClient clientadd = new DefaultHttpClient();
			  			  HttpPost post = new HttpPost("http://localhost:1339/");
			  			  StringEntity input;
						try {
							input = new StringEntity(data,"UTF-8");
						
			  			  post.setHeader("Content-Type", "application/atom+xml");
			  			  post.setEntity(input);
			  			HttpResponse response2 = clientadd.execute(post);
			  			System.out.println(response2.getStatusLine().getStatusCode());
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				}).start();
    		  
  			  System.out.println("recieved the order");
    			}
  			catch (Exception e) {
  					e.printStackTrace();
  				}
    	}
    }
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
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