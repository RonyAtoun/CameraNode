////////////////////////////////////////////////////////
//          BANDWIDTH SIMULATOR FOR VIRTUAL GATEWAY   //
//                                                    //
////////////////////////////////////////////////////////
package bwSimulator;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BandwidthSimulator {
	static final int DEBUG_LEVEL = 1; // DEBUG_LEVEL 1 will work with command line VMS sim. DEBUG_LEVEL 0 with application version of VMS simulatore
    static double availableBandwidth = 6.0;
    static double consumedBandwidth = 0.0;
    static ArrayList<Double> cameraBandwidth = new ArrayList<Double>();
    static double bwValues[] = {1.0, 0.45, 0.3, 0.1};
    static int numCameras = 0;
	
	public static void main(String[] args) throws IOException {
		EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    runSimulation();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
	       ServerSocket serverSocket = null;

	        boolean listeningSocket = true;
	        boolean vmsNotRegistered = (DEBUG_LEVEL == 1) ? false : true;
	        try {
	            serverSocket = new ServerSocket(10008);
	        } catch (IOException e) {
	            System.err.println("Could not listen on port: 10008");
	        }
	        while(listeningSocket){
	        	Socket clientSocket = serverSocket.accept();
	        	if (vmsNotRegistered == true) {
	        		String inputLine;
	                BufferedReader input = new BufferedReader(new InputStreamReader(
                        clientSocket.getInputStream()));
	                if ((inputLine = input.readLine()) != null) { 
	                	if  (inputLine.contains("SetName")){
	                		String split[] = inputLine.split(" ");
	                		String nodeName = split[1];
	                		if (nodeName.equals("vms")) System.out.println("vms has registered");
	                		BandwidthSimulatorServerThread serverThread = new BandwidthSimulatorServerThread(clientSocket);
	                		serverThread.start();
	                	}
	                }
	                vmsNotRegistered = false;
	        	}
	            else {
	            	cameraBandwidth.add(0.0);
	            	numCameras++;
	            	BandwidthSimulatorServerThread serverThread = new BandwidthSimulatorServerThread(clientSocket);
	            	serverThread.start();
	            }
	        }
	        serverSocket.close();       
	    }

    static synchronized void setAvailableBW (double bw) {
            availableBandwidth = bw;
            //availableBWDisplay = String.format("%.2f", bw);
            //consumedBWDisplay = String.format("%.2f", consumedBandwidth);
        }
    static synchronized double getAvailableUploadBW(){
        return availableBandwidth-consumedBandwidth;
    }
    static synchronized void setCameraBandwidth (int cameraId, int bwIndex) {
    	double bandwidth = bwValues[bwIndex];
    	if (numCameras > cameraId) {  // protect simulator from dropped nodes
    	    cameraBandwidth.set(cameraId, bandwidth);
	        double tmp = 0;
	        for (int i=0; i < numCameras; i++) tmp += cameraBandwidth.get(i);
	        consumedBandwidth = tmp;
	        System.out.println("Available bw = "+String.format("%.2f", availableBandwidth)+" Consumed bw = "+ String.format("%.2f", consumedBandwidth));
    	}
    }
    
    static synchronized void removeCameraArrayElement(int cameraId){  /// node is not needed. can be removed
    	consumedBandwidth -= cameraBandwidth.get(cameraId);
    	cameraBandwidth.remove(cameraId);
    	numCameras--;
    }

    static void runSimulation()  {
        final double VARIABLE_BANDWIDTH = 4.6;
            for (;;){
            	availableBandwidth = Math.random()*VARIABLE_BANDWIDTH + 0.4;
    	        //System.out.println("Available bw = "+availableBandwidth);
            	System.out.println (String.format("%.2f", availableBandwidth));
                  try {
                    Thread.sleep(30000);
                  } catch (InterruptedException e) {
                    e.printStackTrace();
                  } 
            }
    }
}


