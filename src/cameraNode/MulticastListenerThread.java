package cameraNode;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;



import java.util.ArrayList;

//import cameraNode.CameraNode;
import cameraNode.NodeId;

public class MulticastListenerThread extends Thread {

	protected MulticastSocket socket;
	protected InetAddress address;
	protected NodeId nodeId;
	protected int unicastLocalPort = -1;
	ArrayList<Integer> cameraFlags = new ArrayList<Integer>();
		
	public MulticastListenerThread() throws IOException { 
		super();
		socket = new MulticastSocket(4446);
		address = InetAddress.getByName("239.5.5.5");
		socket.joinGroup(address);
	}

	public void run() {
		for (;;){   
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);

			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			String received = new String(packet.getData(), 0, packet.getLength());
			
			if (received.contains("registration")) {			
				String split[] = received.split("registration:| |/|@port:");
				String nodeName = split[2];
				String hostName = split[3];
				String IP = split[4];
				InetAddress receivedAddress = null;

				try {
					receivedAddress = InetAddress.getByName(split[4]);
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
				
				unicastLocalPort = Integer.parseInt(split[6]);
				nodeId = new NodeId(receivedAddress, unicastLocalPort, hostName, IP);
							
					try {
						CameraNode.addNode(nodeName, nodeId);
					} catch (InterruptedException | IOException e) {
						e.printStackTrace();
					}
			}

		}
	}

}
