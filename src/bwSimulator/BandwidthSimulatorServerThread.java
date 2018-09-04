package bwSimulator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class BandwidthSimulatorServerThread extends Thread {
	private static final int DEBUG_LEVEL = 2;
    private Socket clientSock = null;
    private String nodeName = new String();
    private int cameraId;
	
    public BandwidthSimulatorServerThread(Socket socket) {

        super();
        this.clientSock = socket;

    }

	   public void run(){
		   boolean isRunning = true;
		    PrintWriter output = null;
			BufferedReader input = null;
				try {
						output = new PrintWriter(clientSock.getOutputStream(), true);
				
						input = new BufferedReader(new InputStreamReader(
			                                clientSock.getInputStream()));
				} catch (IOException e) {
					e.printStackTrace();
				}
		      
			      while (isRunning) {	
			      String inputLine;
			        try {
						if ((inputLine = input.readLine()) != null) { 
						  if (inputLine.contains("SetCameraBW")) {
						      String split[] = inputLine.split(" ");
						      int cameraId = Integer.parseInt(split[1]);
						      int bwIndex = Integer.parseInt(split[2]);
						      BandwidthSimulator.setCameraBandwidth(cameraId, bwIndex);
						 }
						 else if (inputLine.contains("RequestBW")) {
							 double avBW = BandwidthSimulator.getAvailableUploadBW();
							 int bw = (avBW > 0.0) ? 1 : -1;
						    output.println(bw);
						 }
						 else if (inputLine.contains("RequestRealBW")) {
							 double avBW = BandwidthSimulator.getAvailableUploadBW();
						    output.println(avBW);
						 }
						  
						 else if  (inputLine.contains("SetName")){
							 String split[] = inputLine.split(" ");
							 nodeName = split[1];
							 if (DEBUG_LEVEL > 1) System.out.println(nodeName+" has registered");
						 }
						 
						 else if  (inputLine.contains("SetCameraID")){
							 String split[] = inputLine.split(" ");
						     cameraId = Integer.parseInt(split[1]);
						 }
						}
					} catch (NumberFormatException | IOException e) {
						isRunning = false;
						BandwidthSimulator.removeCameraArrayElement(cameraId);  /// node is not needed. can be removed
						//e.printStackTrace();
					}
			        try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			      }
		   }	    
}
