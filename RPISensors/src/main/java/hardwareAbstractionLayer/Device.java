package hardwareAbstractionLayer;

import java.io.IOException;

/**
 * Device   -   provides basic operations for communicating with a device
 * Created by MAWood on 17/07/2016.
 */
public interface Device
{
    byte read(int registerAddress) throws IOException;
    byte[] read(int registerAddress, int count) throws IOException;
    void write(int registerAddress, byte data) throws IOException;
    void write(int registerAddress, byte[] buffer) throws IOException;
}