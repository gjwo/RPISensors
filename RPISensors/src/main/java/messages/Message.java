package messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Message implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8142925110403962930L;

	public enum MessageType {PING,PING_RESP,CLIENT_REG_REQ,CLIENT_REG_RESP,NAV_DATA_REQ,NAV_DATA_RESP,CONTROL_COMMAND,CONTROL_RESP,MSG_ERROR}
	public enum NavRequestType {TAIT_BRYAN,QUATERNION,RAW_9DOF,MAGNETOMETER,ACCELEROMETER,GYROSCOPE}
	public enum ErrorMsgType {UNKNOWN,UNSUPPORTED,INVALID_DATA,CANNOT_COMPLY}
	
	private MessageType msgType;
	private NavRequestType navReqType;
	private ErrorMsgType errorMsgType;

	public Message() {
		msgType = MessageType.PING;
	}

	public byte[] serializeMsg()
	{
		try {
			ByteArrayOutputStream bStream = new ByteArrayOutputStream();
			ObjectOutput oo = new ObjectOutputStream(bStream); 
			oo.writeObject(this);
			oo.close();
			return bStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Message deSerializeMsg(byte[] recBytes)
	{
		ObjectInputStream iStream = null;
		Message msg = null;
		try {
			iStream = new ObjectInputStream(new ByteArrayInputStream(recBytes));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			msg = (Message) iStream.readObject();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			iStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}
}