package cameraNode;

import java.net.InetAddress;

public class NodeId {
	InetAddress address;
	int port;
	String hostName;
	String IP;
	long hashCode;
	int cameraId;

	public NodeId (InetAddress address, int port, String hostname, String IP){
		this.address = address;
		this.port = port;
		this.hostName = hostname;
		this.IP = IP;
		hashCode = (long) (IP+"/"+port).hashCode();
	}
}
