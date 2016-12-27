package devices.motors;

import devices.controller.PIDController;
import devices.encoder.Encoder;

/**
 * RPISensors - devices.motors
 * Created by MAWood on 23/12/2016.
 */
public class EncoderFeedbackMotor implements Motor
{
    private final Encoder encoder;

    private final Motor motor;

    private final PIDController PID;

    private final double kp;
    private final double ki;
    private final double kd;

    private final float sampleRate;

    private final boolean debug;

    public EncoderFeedbackMotor(Encoder encoder, Motor motor, double kp, double ki, double kd, float sampleRate, boolean reversed)
    {
        this(encoder, motor, kp, ki, kd, sampleRate, reversed, false);
    }

    public EncoderFeedbackMotor(Encoder encoder, Motor motor, double kp, double ki, double kd, float sampleRate, boolean reversed, boolean debug)
    {
        this.encoder = encoder;
        this.motor = motor;

        this.kp = kp;
        this.ki = ki;
        this.kd = kd;

        this.sampleRate = sampleRate;

        this.debug = debug;

        PID = new PIDController(reversed,0,sampleRate,kp,ki,kd,-1,1, PIDController.OperatingMode.AUTOMATIC, debug);//, true);
        PID.setInputProvider(encoder);
        PID.addOutputListener(motor);

        PID.initialise();
    }

    @Override
    public void setOutput(float speed)
    {
        if(debug) System.out.println("new setpoint: " + speed);
        PID.setOperatingMode(PIDController.OperatingMode.AUTOMATIC);
        PID.setSetpoint(speed);
    }

    @Override
    public float getSpeed()
    {
        return (float)PID.getSetpoint();
    }

    @Override
    public void stop()
    {
        PID.setSetpoint(0);
        PID.setOperatingMode(PIDController.OperatingMode.MANUAL);
        motor.stop();
    }
}
