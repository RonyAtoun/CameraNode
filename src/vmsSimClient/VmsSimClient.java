package vmsSimClient;
import java.io.*;
import java.net.*;

public class VmsSimClient {
	   Socket sock=null;
	   PrintWriter output=null;
	   BufferedReader input=null;

	   public VmsSimClient(String hostName) {
		      try {
		    	  sock = new Socket(InetAddress.getByName(hostName), 8080);
		          output = new PrintWriter(sock.getOutputStream(), true);
		          input = new BufferedReader(new InputStreamReader(
		                                        sock.getInputStream()));
		       }
		       catch (UnknownHostException e) {
		          System.out.println("Unknown host");
		          System.exit(1);
		       }
		       catch (IOException ie) {
		          System.out.println("Cannot connect to host");
		          System.exit(1);
		       }

	   }
		    
      public void sendMessage(String msg) throws IOException {
		          output.println(msg);
	  }
		    
}
