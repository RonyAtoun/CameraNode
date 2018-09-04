package vmsSim;
import java.net.*;
import java.util.regex.Pattern;
import java.io.*;

public class VmsServerThread extends Thread {
	private Socket clientSock = null;
	private boolean active = true;
	
	   public VmsServerThread(Socket socket) throws IOException {
		   super();
		   this.clientSock = socket;
		      System.out.println("Connection successful.");
		      System.out.println("Listening for input....");
	   }
	   
	   public void run() {
	      while (active == true) {	
		      
			try {
				BufferedReader input = null;
			
		      input = new BufferedReader(new InputStreamReader(
		                                clientSock.getInputStream()));
		      String inputLine;
		        if ((inputLine = input.readLine()) != null) { 
		         System.out.println("Server: " + inputLine);
		         if (inputLine.contains("shutdown")) 
		         {
		        	 active = false;
		         }
		         else {
		        	 int siteSize = countSubstring ("node", inputLine);
		        	 System.out.println("number of nodes: "+siteSize);
		        	 VmsSim.addSite(siteSize);
		         }
		         
		      }
			} catch (IOException e) {
				active = false;
				//e.printStackTrace();
			}
			try {
				Thread.sleep(400);
			} catch (InterruptedException e) {
				//e.printStackTrace();
			}
	      }
	   }
	   
		
		synchronized int countSubstring(String subStr, String str){
				// the result of split() will contain one more element than the delimiter
				// the "-1" second argument makes it not discard trailing empty strings
				return str.split(Pattern.quote(subStr), -1).length - 1;
		}
		
	        
}
