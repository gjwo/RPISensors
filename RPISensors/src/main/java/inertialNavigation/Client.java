package inertialNavigation;

import dataTypes.TimestampedData3f;
import sensors.interfaces.UpdateListener;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * NavigationDisplay - inertialNavigation
 * Created by MAWood on 25/11/2016.
 */
public class Client implements Runnable, UpdateListener
{
    public enum NavRequestType {TAIT_BRYAN,QUATERNION,RAW_9DOF,MAGNETOMETER,ACCELEROMETER,GYROSCOPE}

    private final InetAddress address;
    private final int port;
    private final String name;
    private final DatagramSocket socket;
    private NavRequestType reqType;
    private boolean dataReady;
    private TimestampedData3f newData;
    private boolean stopped;

    Client(InetAddress address, int port, String name, DatagramSocket socket,NavRequestType reqType)
    {
        this.address = address;
        this.port = port;
        this.name = name;
        this.socket = socket;
        this.reqType = reqType;
        this.dataReady = false;
        this.stopped = false;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted() && !stopped)
        {
            if(dataReady) sendData(newData);
        }
    }

    private void sendData(TimestampedData3f newData)
    {
        dataReady = false;
        switch (reqType)
        {
            case TAIT_BRYAN:
            case QUATERNION:
            case RAW_9DOF:
            case MAGNETOMETER:
            case ACCELEROMETER:
            case GYROSCOPE:
            default:
                newData = Instruments.getAngles();
                String data = "Angles," + newData.toCSV();
                try
                {
                    socket.send(new DatagramPacket(data.getBytes(), data.getBytes().length, this.getAddress(), this.getPort()));
                } catch (IOException e)
                {
                    e.printStackTrace();
                }
        }
    }

    // getters use in package only
    InetAddress getAddress() {return address;}
    NavRequestType getReqType() {return reqType;}
    int getPort() {return port;}
    String getName() {return name;}

    public String toString()
    {
        return "Name:" + name + " Address: " + address.getHostAddress() + " Port: "+ port;
    }

    @Override
    public void dataUpdated()
    {
        newData = Instruments.getAngles();
        dataReady = true;
    }

    public void stop()
    {
        this.stopped = true;
    }

}
