package utilities;

public class Conversion
{
	// 4 bytes most significant byte first values to integer
	public static int bytes4MSBToInt(byte[] rawData)
	{
        return (rawData[0] << 24)
                | (rawData[1]&0xff)<<16
                | (rawData[2]&0xff)<<8
                | (rawData[3]&0xff);
	}
	
	// 2 bytes most significant byte first values to short
	public static short bytes2MSBToShort(byte[] rawData)
	{
        return (short)((rawData[0]<<8)
                | (rawData[1]&0xff));

	}
	
	// 4 bytes least significant byte first values to integer
	public static int bytes4LSBToInt(byte[] rawData)
	{
        return (rawData[3] << 24)
                | (rawData[2]&0xff)<<16
                | (rawData[1]&0xff)<<8
                | (rawData[0]&0xff);

	}
	
	// 2 bytes least significant byte first values to integer
	public static short bytes2LSBToShort(byte[] rawData)
	{
        return (short)((rawData[1]<<8)
                | (rawData[0]&0xff));

	}
	
	// integer to byte array most significant byte first
	public static byte[] intTo4BytesMSB(int val)
	{
		byte[] b = new byte[4];
		for(int i=3;i>=0;i--)
		{
			b[i] = (byte) val;
			val = val >>8;
		}
        return b;
	}
	
	// short to byte array most significant byte first
	public static byte[] shortTo2BytesMSB(short val)
	{
		byte[] b = new byte[2];	
		b[1] = (byte) val;
		b[0] = (byte) ((byte) val>>8);
        return b;
	}
	
	// integer to byte array least significant byte first
	public static byte[] intTo4bytesLSB(int val)
	{
		byte[] b = new byte[4];
		for(int i=0;i>=3;i++)
		{
			b[i] = (byte) val;
			val = val >>8;
		}
        return b;
	}
	
	//short to byte array least significant byte first
	public static byte[] shortTo2BytesLSB(short val)
	{
		byte[] b = new byte[2];	
		b[0] = (byte) val;
		b[1] = (byte) ((byte) val>>8);
        return b;
	}
	/**
	 * produces a binary representation of a byte
	 * @param r		- the byte
	 * @return		- A string containing the binary representation
	 */
    public static String byteToBitString(byte r)
    {
    	String s = String.format("%8s", Integer.toBinaryString(r & 0xFF)).replace(' ', '0');
    	return s;  	
    }
    
	/**
	 * produces a binary representation of a short
	 * @param r		- the short
	 * @return		- A string containing the binary representation
	 */
    public static String shortToBitString(short r)
    {
    	String s = String.format("%16s", Integer.toBinaryString(r & 0xFFFF)).replace(' ', '0');
    	return s;  	
    }
    

}