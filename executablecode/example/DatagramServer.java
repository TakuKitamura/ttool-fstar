import java.io.*;
import java.net.*;
import java.util.*;
 
public class DatagramServer implements Runnable {
	private static final int PORT = 8374;
	
    protected DatagramSocket socket = null;
    protected BufferedReader in = null;
    protected DatagramPacket dgp;
    
    protected Feeder feed;
 
    protected int port;
    protected boolean go;
    
    private Thread t;
    
    
    public DatagramServer() {
    	port = PORT;
    }
    
    public void setPort(int _port) {
    	port = _port;
    }
    
    public void setFeeder(Feeder _feed) {
    	feed = _feed;
    }
    
    public void runServer() {
    	go = true;
    	t = new Thread(this);
    	t.start();
    }
    
    public void stopServer() {
    	go = false;
    	if (t != null) {
    		t.interrupt();
    	}
    }

    	
    public static void main(String[] args) {
    	DatagramServer ds = new DatagramServer();
    	ds.runServer();
	} 
	
	public void run() {
		byte[] buf = new byte[1000];
    	
		try {
			socket = new DatagramSocket(port);
			dgp = new DatagramPacket(buf, buf.length);
    		System.out.println("Server started on port:" + port);
    		while (go) {
    			socket.receive(dgp);
    			String rec = new String(dgp.getData(), 0, dgp.getLength());
    			String rcvd =  rec + ", length=" + dgp.getLength() + ", from address: "
    			+ dgp.getAddress() + ", port: " + dgp.getPort();
    			//System.out.println("Received:" + rcvd);
    			
    			if (feed != null) {
    				feed.setMessage(rec);
    			}
    			
    			//BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
    			//String outMessage = stdin.readLine();
    			//buf = ("Server say: " + outMessage).getBytes();
    			//DatagramPacket out = new DatagramPacket(buf, buf.length, dgp.getAddress(), dgp.getPort());
    			//sk.send(out);
    		}
		} catch (Exception e) {
			System.out.println("Exception e:" + e.getMessage());
		}
	}
	
	
	public boolean sendTo(String s) {
		if ((socket == null) || (dgp == null)) {
			return false;
		}
		
		try {
			byte[] buf = s.getBytes();
			DatagramPacket out = new DatagramPacket(buf, buf.length, dgp.getAddress(), dgp.getPort());
			socket.send(out);
		} catch (Exception e) {
			System.out.println("Exception e:" + e.getMessage());
			return false;
		}
		return true;
	}
	
	
    
}