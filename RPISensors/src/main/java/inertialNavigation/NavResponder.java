package inertialNavigation;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import messages.Message;
import messages.Message.ErrorMsgType;
import messages.Message.MessageType;
public class NavResponder extends Thread
{
    private static final int serverPortNbr = 9876;
    private static final int bufferSize = 1024;
    private ArrayList<Client> clients;
    private DatagramSocket socket = null;
    private int debugLevel;
    private Navigate nav;

    public NavResponder(Navigate nav,String name,int debugLevel) throws IOException {
        super(name);
        clients = new ArrayList<>();
        if (debugLevel >=3) System.out.println("NavResponder Constructor");
        socket = new DatagramSocket(serverPortNbr);
        this.debugLevel = debugLevel;
        this.nav = nav;
    }

    public void run() {
        if (debugLevel >=2) System.out.println("NavResponder run");
        byte[] buf = new byte[bufferSize];
        try
        {
            socket.setSoTimeout(1000); //Milliseconds
        } catch (SocketException e)
        {
            e.printStackTrace();
        }
        DatagramPacket inPacket = new DatagramPacket(buf, buf.length);
        while (!Thread.interrupted()) {
        	try{
	            try {
	                socket.receive(inPacket); //wait for a client request
	                handleMessage(inPacket);
	            } catch (SocketTimeoutException  e)
	            {//do nothing
	            }
            } catch (IOException e){
            	e.printStackTrace();
            }
        }
        for(Client client:clients) client.stop();
        socket.close();
        if (debugLevel >=2) System.out.println("End NavResponder run");
    }

    private void handleMessage(DatagramPacket packet)
    {
        if (debugLevel >=3) System.out.println("handleMessage");
    	int receivedBytes = 0;
		receivedBytes = packet.getLength(); //actual length of data
		byte[] trimmedData = new byte[receivedBytes];
		for(int i = 0; i < receivedBytes; i++)
		{
			trimmedData[i] = packet.getData()[i];
		}
		//if (debugLevel >=4)System.out.println(receivedBytes+","+ trimmedData.length+"," + Arrays.toString(trimmedData));
    	Message reqMsg = Message.deSerializeMsg(trimmedData);
    	if(reqMsg == null)
    	{
    		System.err.println("null message recieved");
    		System.exit(5);
    	}

    	if (debugLevel >=4)System.out.println("Received msg: "+ reqMsg.toString());

    	Message respMsg = new Message();
    	respMsg.setErrorMsgType(ErrorMsgType.CANNOT_COMPLY); 	
    	boolean newClient = false;
    	
        Client client = new Client(packet.getAddress(),packet.getPort(),packet.getAddress().getHostName(), socket); //NB not recorded yet
        
        newClient = true;
        for(Client existingClient:clients)
        {
        	if(client.matches(existingClient))
        	{	
        		if (debugLevel >=4)System.out.println("Client Matched "+ client.toString() + " = "+existingClient.toString());
        		client = existingClient;  // may have earlier requests set
        		newClient = false;
        		break;
        	}
        }
        if (debugLevel >=4) System.out.println("Message type: "+reqMsg.getMsgType()+ " from "+client.toString());
        if (debugLevel >=4) System.out.println(reqMsg.toString());
        switch (reqMsg.getMsgType())
        {
        case PING: 
            respMsg.setMsgType(MessageType.PING_RESP);
            respMsg.setErrorMsgType(ErrorMsgType.SUCCESS);
            //time has been set by creating the message
            try
            {
            	byte[] ba = respMsg.serializeMsg();
                socket.send(new DatagramPacket(ba, ba.length, client.getAddress(), client.getPort()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        	break;
        case CLIENT_REG_REQ: 
            if (newClient)	
            { 	//register client and start thread
                new Thread(client).start();
                nav.registerInterest(client);
            	clients.add(client);
            	if (debugLevel >=1)System.out.println("Client registered: " + client.toString());
            }
            //send response, even if already registered
            respMsg.setMsgType(MessageType.CLIENT_REG_RESP);
            respMsg.setErrorMsgType(ErrorMsgType.SUCCESS);
            client.sendMsg(respMsg);
        	break;
        case GET_PARAM_REQ: 
        	respMsg.setMsgType(MessageType.GET_PARAM_RESP);
        	Client.buildParameterMsg(reqMsg.getParameterType(),respMsg);
            client.sendMsg(respMsg);
       	break;
        case SET_PARAM_REQ:
        	respMsg.setMsgType(MessageType.SET_PARAM_RESP);
        	Client.buildParameterMsg(reqMsg.getParameterType(),respMsg);
            client.sendMsg(respMsg);
        	break;
        case STREAM_REQ:
        	client.addParam(reqMsg.getParameterType()); //STREAM_RESP will be sent by client thread when data is available
        	if (debugLevel >=4)System.out.println("Added parameter "+ reqMsg.getParameterType().name() + " for: " + client.toString());
        	break;
        case CONTROL_REQ: 
        	respMsg.setMsgType(MessageType.CONTROL_RESP);
        	Client.buildParameterMsg(reqMsg.getParameterType(),respMsg);
            client.sendMsg(respMsg);
			break;
        case MSG_ERROR:
		default:	
        	respMsg.setMsgType(MessageType.MSG_ERROR);
            client.sendMsg(respMsg);
        }
        if (debugLevel >=3) System.out.println("End handleMessage");
    }
}