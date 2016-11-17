package comms;
import java.io.*;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NavClient {
	private static final int serverPortNbr = 9876;
    private static final Pattern dataTFFFsplit = Pattern.compile("(\\d+),([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*),([+-]?\\d*\\.?\\d*)");

	public NavClient() {
		// TODO Auto-generated constructor stub
	}
    public static void main(String[] args) throws IOException {
    	 
        if (args.length != 1) {
             System.out.println("Usage: java NavClient <hostname>");
             return;
        }
 
            // get a datagram socket
        DatagramSocket socket = new DatagramSocket();
 
            // send request
        byte[] buf = new byte[256];
        InetAddress address = InetAddress.getByName(args[0]);
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, serverPortNbr);
        socket.send(packet); //register interest with server
        boolean stop = false;
        Pattern msgSplit = Pattern.compile("(\\p{Alpha}+),(?<=sentence).*");
        Matcher msg;
        String msgType;
        while (!stop)
        {
            // get response
        	DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
            socket.receive(inPacket);
 
            // display response
            String received = new String(inPacket.getData(), 0, packet.getLength());
            msg = msgSplit.matcher(received);
            msgType = msg.group(1);
            switch (msgType)
            {
            case "STOP": 	stop = true;
            				break;
            case "Angles":	processAnglesMsg(msg.group(2));
            				break;
            default:		System.out.println("msg: " + received);
            }   
        }
        socket.close();
    }
    private static void processAnglesMsg(String s)
    {
    	Matcher data = dataTFFFsplit.matcher(s);
    	Long time = Long.parseLong(data.group(1));
    	float yaw = Float.parseFloat(data.group(2));
    	float pitch = Float.parseFloat(data.group(3));
    	float roll = Float.parseFloat(data.group(4));
    	System.out.format("Angles - [%8d] Yaw: %08.3f Pitch: %08.3f Roll: %08.3f%n",time,yaw, pitch,roll);
    }
}