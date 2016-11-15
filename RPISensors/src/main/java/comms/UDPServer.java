package comms;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPServer {

	public UDPServer() {
		// TODO Auto-generated constructor stub
	}
	public static void main(String args[]) throws Exception
	{
   		DatagramSocket serverSocket = new DatagramSocket(9876);
        byte[] receiveData = new byte[1024];											//Array for holding data received
        byte[] sendData = new byte[1024];												//Array for holding data to be sent
        while(true)
        {
              DatagramPacket receivePacket = 
            		  new DatagramPacket(receiveData, receiveData.length); 				//packet for receiving data
              serverSocket.receive(receivePacket); 										//receive the data packet
              String sentence = new String( receivePacket.getData()); 					//extract the data
              System.out.println("RECEIVED: " + sentence); 								//output the data
              InetAddress IPAddress = receivePacket.getAddress();						//record address it came from
              int port = receivePacket.getPort();										//record port  it came from
              
              String capitalizedSentence = sentence.toUpperCase(); 						//modify the data
              
              sendData = capitalizedSentence.getBytes(); 								// add data to packet
              DatagramPacket sendPacket = 
            		  new DatagramPacket(sendData, sendData.length, IPAddress, port); 	//address packet
              serverSocket.send(sendPacket); 											//reply by sending packet containing modified data
        }
	}
}