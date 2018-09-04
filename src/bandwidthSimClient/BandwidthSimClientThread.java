package bandwidthSimClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import cameraNode.CameraNode;

public class BandwidthSimClientThread extends Thread{
	   Socket sock=null;
	   PrintWriter output=null;
	   BufferedReader input=null;
	   String hostName;
	   String nodeName;

	   public BandwidthSimClientThread(String host, String node) throws IOException {
	  // public BandwidthSimClientThread(String host) throws IOException {
		   super();
		   hostName = host;
		   nodeName = node;
	   }
	   public void run() {
		      try {
		    	  //sock = new Socket("127.0.0.1", 10008);
		    	  sock = new Socket(InetAddress.getByName(hostName), 10008);
		    	  output = new PrintWriter(sock.getOutputStream(), true);
		          input = new BufferedReader(new InputStreamReader(
		                                        sock.getInputStream()));
		          output.println("SetName "+nodeName);
		       }
		       catch (UnknownHostException e) {
		          System.out.println("Unknown host");
		          System.exit(1);
		       }
		       catch (IOException ie) {
		          System.out.println("Cannot connect to host");
		          System.exit(1);
		       }
		      for (;;) {
		        String inputLine;
		        try {
					if ((inputLine = input.readLine()) != null) {
						int avBW = Integer.parseInt(inputLine);						
						CameraNode.receiveUploadBandwidth(avBW);
					}
				} catch (IOException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    
		        try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		        
		      }

		    }
		    
		    public void sendMessage(String msg) {
		          output.println(msg);
	       }
		    

}
