package comms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPClient
{
	private static UDPClient client; 
	private DatagramSocket socket;
	private byte[] sendData;
	private byte[] receiveData;
	private InetAddress destinationAddress;
	private InetAddress myAddress;
	private static final int portNbr = 9876;
	private DatagramPacket sendPacket;
	private DatagramPacket receivePacket;
	
	
	public UDPClient()
	{
	    sendData = new byte[1024];								//Array for holding data to be sent
	    receiveData = new byte[1024];							//Array for holding data received
	 	try
	 	{
			socket = new DatagramSocket();						//create socket
			myAddress = InetAddress.getLocalHost();				//This machine's address

		} catch (SocketException | UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	public void printMyAddress()
	{
		System.out.println(myAddress.toString());
	}
	public void setDestination(InetAddress address)
	{
		destinationAddress = address;
	}
	public void sendMsg(byte[] msg)

	{
		sendData = msg;
	    DatagramPacket sendPacket = 
	    		new DatagramPacket(sendData, sendData.length, destinationAddress, portNbr);	//make packet with data, address and port
	    try {
			socket.send(sendPacket); 											//send the packet
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String receiveMsg()
	{
		receivePacket = new DatagramPacket(receiveData, receiveData.length);	//make a packet to receive data
		try {
			client.socket.receive(receivePacket);								//wait for and receive a response in the packet
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String (receivePacket.getData());
	}
	
    public static void main(String args[]) throws Exception
    {
    	BufferedReader inFromUser = 
    			new BufferedReader(new InputStreamReader(System.in));		//set up user input
    	client = new UDPClient();
	    client.setDestination(InetAddress.getByName("localhost"));			//get the target machine's address
	    
	    String sentence = inFromUser.readLine();							//get user input
	    client.sendMsg(sentence.getBytes());								//put input into the send array
	    
	    String modifiedSentence = client.receiveMsg();						//extract the pay load
	    System.out.println("FROM SERVER:" + modifiedSentence);				// print the pay load
	    client.socket.close();												//close the socket;
	 }
}