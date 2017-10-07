import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


public class sensor {
	public static void main(String[] args) {
		try {
			String i="i";
			String prev="j";
			String url = "http://localhost:1310/";
			HttpClient client = new DefaultHttpClient();
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("/home/pi/Desktop/motion.txt", true))); 
		FileOutputStream fop = null;
		File file;
			file = new File("/home/pi/Desktop/motion" + System.currentTimeMillis() + ".txt");
			fop = new FileOutputStream(file);
			if (!file.exists()) {
				file.createNewFile();
			}
			
		for(;;)
		{			
			  HttpGet get = new HttpGet(url);
			  get.addHeader("Content-Type", "application/atom+xml");
			  get.addHeader("Authorization","Basic c21wQWRtaW46czNwQWRtaW4=");
			  HttpResponse response2 = client.execute(get);
			  i = EntityUtils.toString(response2.getEntity());
			  if(!prev.equalsIgnoreCase(i))
			  {
				  Calendar cal = Calendar.getInstance();
				  String data = dateFormat.format(cal.getTime()) + " " + System.currentTimeMillis() + " Status = " + i + "\n";
				  byte[] contentInBytes = data.getBytes();
					fop.write(contentInBytes);
					fop.flush();
				  System.out.println(data);
				  prev = i;
			  }
			  System.out.println(i);
			Thread.sleep(3000);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	

}
