package vmsSim;

import java.awt.EventQueue;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class VmsSim {
	static ServerSocket serverSock = null;
	static boolean siteDetected = false;
    static String siteName = "FLIR 1";
    static int siteSize = 0;
    static ArrayList <Integer> cameraPriorities = new ArrayList<Integer>();



	public static void main(String[] args) throws java.io.IOException {
	      try {
		         serverSock = new ServerSocket(8080);
		      }
		      catch (IOException ie) {
		         System.out.println("Can't listen on 8080");
		         System.exit(1);
		      }
	   
	       System.out.println("Listening for connection....");
	       
	       for (;;) {
	    	      VmsServerThread serverThread;
	    	   	  Socket clientSock = null;
		    	  siteDetected = false;
			      try {
			         clientSock = serverSock.accept();
			      }
			      catch (IOException ie) {
			         System.out.println("Accept failed.");
			         System.exit(1);
			      }
				  
			      serverThread = new VmsServerThread(clientSock);
				  serverThread.start();
	       }
	}

	synchronized public static void addSite (int size) {//(final TreeMap<String, NodeId> cameraArray) {
		siteSize = size;
        if (siteDetected == false) {
              //siteSize = cameraArray.size();
               EventQueue.invokeLater(new Runnable() {
                   public void run() {
                         try {
                               JOptionPane.showMessageDialog(null, "New Site uploaded to Cloud\n"
                                         + "Containing "+siteSize+" Cameras\n"
                                                 + "Assigned Site Identification is "+siteName);
                         } catch (Exception e) {
                             e.printStackTrace();
                         }
                     }
                }); 
              siteDetected = true;
              for (int i = 0; i < siteSize; i++) {
                 cameraPriorities.add(0);
              }
        }
     }
     
    synchronized String getSiteId() {
        return siteName;
 }
 synchronized void setCameraPriority(int cameraId) {
        cameraPriorities.set(cameraId, 1);
 }
 synchronized void resetCameraPriority(int cameraId) {
        cameraPriorities.set(cameraId, 0);
 }
 synchronized ArrayList<Integer> getCameraPriorities() {
        return cameraPriorities;
 }
}

