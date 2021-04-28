import java.io.*;
import java.net.*;
import java.util.*;

public class DatagramServer implements Runnable {
    private static final int PORT = 8374;
    private static final int SENDING_PORT = 8373;
    private static final int SENDING_PORT_1 = 8475;

    protected DatagramSocket socket;
    protected BufferedReader in = null;
    protected DatagramPacket dgp;

    protected Feeder feed;

    protected int port, sendingPort;
    protected boolean go;

    protected boolean notReceived = true;

    private Thread t;


    public DatagramServer() {
        port = PORT;
        sendingPort = SENDING_PORT;
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
            //sendingSocket = new DatagramSocket(sendingPort);
            dgp = new DatagramPacket(buf, buf.length);
            System.out.println("Server started on port:" + port);
            while (go) {
                socket.receive(dgp);
                notReceived = false;
                String rec = new String(dgp.getData(), 0, dgp.getLength());
                String rcvd =  rec + ", length=" + dgp.getLength() + ", from address: "
                    + dgp.getAddress() + ", port: " + dgp.getPort();
                //System.out.println("Received:" + rcvd);
		sendingPort = dgp.getPort();
		
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

    public boolean sendDatagramTo(String s) {
	port = sendingPort;
	
        if (socket == null) {
            return false;
        }

        System.out.println("Datagram sending 1");

        try {
            //DatagramSocket sendingSocket = new DatagramSocket(port);
            InetAddress addr = InetAddress.getByName("localhost");;
            /*if (notReceived) {
            // We assume "localhost" for the address
            System.out.println("Datagram sending 1.1");
            addr = InetAddress.getByName("localhost");
            } else {
            System.out.println("Datagram sending 1.2");
            addr = dgp.getAddress();
            }*/

            //System.out.println("Datagram sending 2");
            byte[] buf = s.getBytes();
            System.out.println("Datagram sending 3 on port=" + port);
            DatagramPacket out = new DatagramPacket(buf, buf.length, addr, sendingPort);
            System.out.println("Datagram sending 4");
            socket.send(out);
            System.out.println("Datagram sending 5");
            //sendingSocket.close();
            System.out.println("Datagram sending 5.1");
        } catch (Exception e) {
            System.out.println("Exception e:" + e.getMessage());
            return false;
        }
        System.out.println("Datagram sending 6");
        return true;
    }



}
