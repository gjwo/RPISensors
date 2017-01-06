package devices.device;

import java.io.IOException;

/**
 * Created by MAWood on 17/07/2016.
 */
public interface Device
{
    byte read(int registerAddress) throws IOException;
    byte[] read(int registerAddress, int count) throws IOException;
    void write(int registerAddress, byte data) throws IOException;
    void write(int registerAddress, byte[] buffer) throws IOException;
}