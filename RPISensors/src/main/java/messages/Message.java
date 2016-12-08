package messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.Instant;

import dataTypes.TimestampedData3f;
import inertialNavigation.Quaternion;

/**
 * Message	-	This class implements a serializable message type for communication between the device and a client
 * @author GJWood
 * 
 * The message implements a simple request / response protocol between the device and the client normally the 
 * client will initiate the dialogue with a request, and the device will respond with a response. normally this
 * is one message in, one out. The exceptions are:
 * STREAM_REQ will result in a stream of message being sent until the client requests the stream to stop
 * MSG_ERROR will be sent as a response if the request is invalid in some way.
 * 
 * Messages are constructed as follows
 * Message Type		Command Type	Parameter Type	class variable	Error Type	Comments
 * PING_REQ			N/A				N/A				N/A				N/A
 * PING_RESP		N/A				N/A				time			SUCCESS
 * CLIENT_REG_REQ	N/A				N/A				N/A				N/A		Client address recorded elsewhere
 * CLIENT_REG_RESP	N/A				N/A				time			SUCCESS	or CANNOT_COMPLY
 * GET_PARAM_REQ	N/A				Any				N/A				N/A
 * GET_PARAM_RESP	N/A				Any				see below		SUCCESS or INVALID_DATA
 * STREAM_REQ		EXECUTE/STOP	Any Streamable	N/A				N/A
 * STREAM_RESP		N/A				Any Streamable	see below		SUCCESS or CANNOT_COMPLY or INVALID_DATA
 * CONTROL_REQ		EXECUTE			Any Command		See below		N/A
 * CONTROL_REQ		STOP			N/A				N/A				N/A			
 * CONTROL_RESP		as REQ			as REQ			as REQ			SUCCESS or any other			
 * MSG_ERROR		N/A				N/A				N/A				Any
 * A ping request may be made by clients in any state including unregistered.
 * A client must be registered before other requests are made
 * A client may make multiple requests including stream requests
 * Requests should always get the corresponding response or MSG_ERROR unless packets are dropped
 */
public class Message implements Serializable
{

	private static final long serialVersionUID = 7342722317166474653L;

	public enum MessageType {	PING,PING_RESP,
								CLIENT_REG_REQ,CLIENT_REG_RESP,
								GET_PARAM_REQ,GET_PARAM_RESP,
								SET_PARAM_REQ,SET_PARAM_RESP,
								STREAM_REQ, STREAM_RESP,
								CONTROL_REQ,CONTROL_RESP,
								MSG_ERROR}
	public enum NavRequestType {TAIT_BRYAN,		// timestamped yaw, pitch and roll
								QUATERNION,		// timestamped Quaternion
								RAW_9DOF,		// not sure this is needed
								MAGNETOMETER,	// timestamped Magnetometer reading x,y,z
								ACCELEROMETER,	// timestamped Accelerometer reading x,y,z
								GYROSCOPE}		// timestamped Gyroscope reading x,y,z
	public enum ErrorMsgType {SUCCESS,UNKNOWN,UNSUPPORTED,INVALID_DATA,CANNOT_COMPLY}
	public enum ParameterType {	TAIT_BRYAN,		// Streamable timestamped yaw, pitch and roll
								QUATERNION,		// Streamable timestamped Quaternion
								MAGNETOMETER,	// Streamable timestamped Magnetometer reading x,y,z
								ACCELEROMETER,	// Streamable timestamped Accelerometer reading x,y,z
								GYROSCOPE,		// Streamable timestamped Gyroscope reading x,y,z
								HEADING,		// Command - compass heading
								SPEED,			// Command - linear speed
								TURN_ANGLE		// Command - Rate of turn
								}
	public enum CommandType {EXECUTE,STOP}
	
	private MessageType msgType;
	private ParameterType parameterType;
	private CommandType commandType;
	private ErrorMsgType errorMsgType;
	private TimestampedData3f navAngles; // holds the angles of the type specified by NavRequestType
	private Quaternion quaternion;
	private float heading;
	private float speed;
	private float turnAngle;
	private Instant time;

	public Message() {
		msgType = MessageType.MSG_ERROR;
		errorMsgType = ErrorMsgType.UNKNOWN;
		navAngles = new TimestampedData3f(0,0,0);
		quaternion = new Quaternion();
		heading= 0f;
		speed = 0f;
		turnAngle = 0f;
		time = Instant.now();
	}

	public MessageType getMsgType() {return msgType;}

	public void setMsgType(MessageType msgType) {this.msgType = msgType;}

	public ErrorMsgType getErrorMsgType() {return errorMsgType;}

	public void setErrorMsgType(ErrorMsgType errorMsgType) {this.errorMsgType = errorMsgType;}

	public TimestampedData3f getNavAngles() {return navAngles;}

	public void setNavAngles(TimestampedData3f navAngles) {this.navAngles = navAngles;}

	public Quaternion getQuaternion() {return quaternion;}

	public void setQuaternion(Quaternion quaternion) {this.quaternion = quaternion;}

	public float getHeading() {return heading;}

	public void setHeading(float heading) {this.heading = heading;}

	public float getSpeed() {return speed;}

	public void setSpeed(float speed) {this.speed = speed;}

	public float getTurnAngle() {return turnAngle;}

	public void setTurnAngle(float turnAngle) {this.turnAngle = turnAngle;}
	
	public Instant getTime() {return time;}

	public void setTime(Instant time) {this.time = time;}
	

	public ParameterType getParameterType() {return parameterType;}

	public void setParameterType(ParameterType parameterType) {this.parameterType = parameterType;}

	public CommandType getCommandType() {return commandType;}

	public void setCommandType(CommandType commandType) {this.commandType = commandType;}

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
	
	public static Message deSerializeMsg(byte[] recBytes)
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