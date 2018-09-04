package cameraNode;
import java.io.*;
import java.net.*;

public class Dispatcher {
	    private final DatagramSocket socket;
	    private final String nodeName;
	    final int localPort;
 
	    public Dispatcher(String name) throws IOException {
	        nodeName = name;
	        socket = new DatagramSocket();
	        localPort = socket.getLocalPort();
	    }
	    
	    public void broadcastSelf(String msg) {  // will be called by CameraNode.addNode whenever new nodes join the cluster
	    	 try {
					String message = msg+nodeName+" "+InetAddress.getLocalHost().toString()+" @port:"+localPort;
	                InetAddress group = InetAddress.getByName("239.5.5.5");
	                sendMessage (message, group, 4446);
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    }
	    
	    public void sendMessage (String message, InetAddress destination, int port) {
            try {
                byte[] buf = new byte[256];
                buf = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buf, buf.length, destination, port);
                socket.send(packet);

            } catch (IOException e) {
                e.printStackTrace();
            }
    	}

	    public DatagramSocket getSocket() {
	    	return socket;
	    }    
	}