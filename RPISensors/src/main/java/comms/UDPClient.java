package comms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPClient
{

	public UDPClient() {
		// TODO Auto-generated constructor stub
	}
    public static void main(String args[]) throws Exception
    {
	      BufferedReader inFromUser =
	         new BufferedReader(new InputStreamReader(System.in));					//set up user input
	      DatagramSocket clientSocket = new DatagramSocket();						//create socket
	      //InetAddress IPAddress = InetAddress.getByName("localhost");
	      InetAddress IPAddress = InetAddress.getLocalHost();						//This machine's address - should be target?
	      byte[] sendData = new byte[1024];											//Array for holding data to be sent
	      byte[] receiveData = new byte[1024];										//Array for holding data received
	      
	      String sentence = inFromUser.readLine();									//get user input
	      sendData = sentence.getBytes();											//put input into the send array
	      DatagramPacket sendPacket = 
	    		  new DatagramPacket(sendData, sendData.length, IPAddress, 9876);	//make packet with data, address and port
	      clientSocket.send(sendPacket);											//send the packet
	      DatagramPacket receivePacket = 
	    		  new DatagramPacket(receiveData, receiveData.length);				//make a packet to receive data
	      clientSocket.receive(receivePacket);										//wait for and receive a response in the packet
	      String modifiedSentence = new String(receivePacket.getData());			//extract the pay load
	      System.out.println("FROM SERVER:" + modifiedSentence);					// print the pay load
	      clientSocket.close();														//close the socket;
	 }
}