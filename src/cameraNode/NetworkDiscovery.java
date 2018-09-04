package cameraNode;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.Timer;

public class NetworkDiscovery {

	private final int DEBUG_LEVEL = 2;
	private final String nodeName;
	static NodeId nodeSuccessor=null;
	static boolean discoveryLoopCompleted = false;
	static final int SHORT_TIMEOUT = 2000;
	static final int LONG_TIMEOUT = 5000;

	
	public NetworkDiscovery (String node) {
		nodeName = node;
		setupRediscoveryTimer();
	}
	
    public void detectSuccessor (TreeMap<String, NodeId> nodeArray) {
        final Set<String> keySet = nodeArray.keySet();
        Iterator <String> iterator  = keySet.iterator();
        String firstKey = iterator.next();
        nodeSuccessor = null;
         
        if (firstKey.equals(nodeName)) nodeSuccessor = nodeArray.get(iterator.next());
        else {
            while (iterator.hasNext()) {
            	if (iterator.next().equals(nodeName) && iterator.hasNext()) nodeSuccessor = nodeArray.get(iterator.next());
            }
            if (nodeSuccessor == null) nodeSuccessor = nodeArray.get(firstKey);
        }
      }
    
    public NodeId getSuccessor() {
    	return nodeSuccessor;
    }

    private void setupRediscoveryTimer() {       /// KeepAlive message is sent to successor in network ring. This triggers a message loop which will return to
    											 /// initiator if all nodes are present. This is done by a second timer that checks if keepAlive from 
    										     /// initiating node was received
    	Timer sendKeepAlive = new Timer(LONG_TIMEOUT, e -> {
    		if (nodeSuccessor != null) {
				sendKeepAliveMessage();	
				setupAuxTimer();
    		}});
    		sendKeepAlive.setInitialDelay(2000);
    		sendKeepAlive.start();   	 	
    }
   
    private void sendKeepAliveMessage() {
    	String message = "keepAlive "+nodeName;
		InetAddress destination = nodeSuccessor.address;
		int port = nodeSuccessor.port;
		CameraNode.dispatcher.sendMessage(message, destination, port);		
    }
    private void setupAuxTimer() {
		Timer checkKeepAliveResponse = new Timer (SHORT_TIMEOUT, ee -> testKeepAlive());
 		checkKeepAliveResponse.setRepeats(false);
 		checkKeepAliveResponse.start();  
    }
    private void testKeepAlive() {
    	if (!discoveryLoopCompleted) {
	    	   if (DEBUG_LEVEL > 0) System.out.println("Rediscovery Loop did not complete - Restarting network discovery");
	    	   try {
	    		   CameraNode.rediscoverNetwork();
	    	   } catch (InterruptedException | IOException e1) {
				e1.printStackTrace();
	    	   }
			}
    	discoveryLoopCompleted = false; 
    }

    void testDiscoveryLoopComplete (String node) {
    	if (node.equals(nodeName)) discoveryLoopCompleted = true;   // full loop completed
    	else if (nodeSuccessor != null){							// send to successor
    		String message = "keepAlive "+node;
	    	InetAddress destination = nodeSuccessor.address;
	    	int port = nodeSuccessor.port;
			CameraNode.dispatcher.sendMessage(message, destination, port);
    	}
    }

    
}
