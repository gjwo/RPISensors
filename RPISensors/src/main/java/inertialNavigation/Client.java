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
    private final InetAddress address;
    private final int port;
    private final String name;
    private final DatagramSocket socket;
    private boolean dataReady;
    private TimestampedData3f newData;

    Client(InetAddress address, int port, String name, DatagramSocket socket)
    {
        this.address = address;
        this.port = port;
        this.name = name;
        this.socket = socket;
        dataReady = false;
    }

    @Override
    public void run()
    {
        while(!Thread.interrupted())
        {
            if(dataReady) sendData(newData);
        }
    }

    private void sendData(TimestampedData3f newData)
    {
        dataReady = false;
        String data = "Angles," + newData.toCSV();
        try
        {
            socket.send(new DatagramPacket(data.getBytes(), data.getBytes().length, this.getAddress(), this.getPort()));
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private InetAddress getAddress()
    {
        return address;
    }

    private int getPort()
    {
        return port;
    }

    String getName()
    {
        return name;
    }

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
}
