package inertialNavigation;

import dataTypes.TimestampedData3f;
import messages.Message;
import messages.Message.ErrorMsgType;
import messages.Message.MessageType;
import messages.Message.ParameterType;
import sensors.interfaces.UpdateListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.EnumSet;

/**
 * NavigationDisplay - inertialNavigation
 * Created by MAWood on 25/11/2016.
 */
public class Client implements Runnable, UpdateListener
{
    private final InetAddress address;
    private final int port;
    private final String name;
    private final DatagramSocket socket;
    //private Message.NavRequestType reqType;
    private boolean dataReady;
    private TimestampedData3f newData;
    private EnumSet<ParameterType> params = EnumSet.noneOf(ParameterType.class);
    private boolean stopped;

    Client(InetAddress address, int port, String name, DatagramSocket socket,ParameterType ParamType)
    {
        this(address,port,name,socket);
        this.params.add(ParamType);
    }
    Client(InetAddress address, int port, String name, DatagramSocket socket)
    {
        this.address = address;
        this.port = port;
        this.name = name;
        this.socket = socket;
        this.dataReady = false;
        this.stopped = false;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted() && !stopped)
        {
            if(dataReady) sendData();
        }
    }

    public boolean matches(Client newClient)
    {
    	return (this.address.toString().equals(newClient.address.toString()) && (this.port == newClient.getPort()));
    }
    private void sendData()
    {
        dataReady = false;
        for(ParameterType param :params)
        {
        	Message msg = new Message();
        	msg.setMsgType(MessageType.STREAM_RESP);
        	buildParameterMsg(param,msg);
            try
            {
            	byte[] ba = msg.serializeMsg();
                socket.send(new DatagramPacket(ba, ba.length, this.getAddress(), this.getPort()));
            } catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void buildParameterMsg(ParameterType param ,Message msg )
    {
    	msg.setParameterType(param);
    	msg.setErrorMsgType(ErrorMsgType.SUCCESS);
    	switch (param)
        {
            case TAIT_BRYAN:
                msg.setNavAngles(Instruments.getAngles());
            case QUATERNION:
            	msg.setQuaternion(Instruments.getQuaternion());
            case MAGNETOMETER:
            	msg.setNavAngles(Instruments.getMagnetometer());
            case ACCELEROMETER:
                msg.setNavAngles(Instruments.getAccelerometer());
            case GYROSCOPE:
                msg.setNavAngles(Instruments.getGyroscope());
            default:
            	msg.setErrorMsgType(ErrorMsgType.UNSUPPORTED);
        }
    }
    public void sendMsg(Message msg)
    {
        try
        {
        	byte[] ba = msg.serializeMsg();
            socket.send(new DatagramPacket(ba, ba.length, this.getAddress(), this.getPort()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    // getters use in package only
    InetAddress getAddress() {return address;}
    int getPort() {return port;}
    String getName() {return name;}
    EnumSet<ParameterType> getParams(){return params;}
    
    void addParam(ParameterType p){params.add(p);}
    
    void removeParam(ParameterType p){params.remove(p);}
    

    public String toString()
    {
        return "Name:" + name + " Address: " + address.getHostAddress() + " Port: "+ port;
    }

    @Override
    public void dataUpdated() {dataReady = true;}

    public void stop() {this.stopped = true;}
}