package devices.driveAssembly;

import devices.controller.PIDControlled;
import devices.controller.PIDController;
import devices.encoder.Encoder;
import devices.motors.Motor;

import java.util.concurrent.TimeUnit;

/**
 * RPISensors - devices.driveAssembly
 * Created by MAWood on 23/12/2016.
 */
public class EncoderFeedbackPIDControlledDriveAssembly extends TankDriveAssembly implements DriveAssembly
{
    private final Encoder leftEn;
    private final Encoder rightEn;

    private PIDController leftPID;
    private PIDController rightPID;

    private static final float SAMPLE_RATE = 20;

    private static final double KP = 0.1;
    private static final double KI = 0.25;
    private static final double KD = 0.3;

    public EncoderFeedbackPIDControlledDriveAssembly(Motor left, Motor right, Encoder leftEn, Encoder rightEn)
    {
        super(left,right);
        this.leftEn = leftEn;
        this.rightEn = rightEn;

        leftPID = new PIDController(0d,SAMPLE_RATE,KP,KI,KD,-1,1, PIDController.OperatingMode.AUTOMATIC, true);
        leftPID.setInputProvider(leftEn);
        leftPID.addOutputListener(left);
        rightPID = new PIDController(0d,SAMPLE_RATE,KP,KI,KD,-1,1, PIDController.OperatingMode.AUTOMATIC);
        rightPID.setInputProvider(rightEn);
        rightPID.addOutputListener(right);

        leftPID.initialise();
        rightPID.initialise();
    }

    @Override
    protected void updateCourse()
    {
        float leftCoefficient;
        float rightCoefficient;
        float adjustedDirection = this.getDirection();

        // Adjusted direction is a transformation of direction where forwards and backwards are ignored and
        // a -90 to 90 direction is formed for the amount of turning in whichever direction it is going
        // this makes straight forwards and straight backwards 0

        if(this.getDirection() > 90 && this.getDirection() < 270)
            adjustedDirection = (adjustedDirection * -1) + 540;
        if(adjustedDirection>=270) adjustedDirection -= 360;



        if(adjustedDirection > 0)
        {
            leftCoefficient = 1;
            rightCoefficient = 1 - ((Math.abs(adjustedDirection) / 90) * 2);
        } else
        {
            leftCoefficient = 1 - ((Math.abs(adjustedDirection) / 90) * 2);
            rightCoefficient = 1;
        }


        if(this.getDirection() > 90 && this.getDirection() < 270)
        {
            leftCoefficient *= -1;
            rightCoefficient *= -1;
        }

        leftPID.setSetpoint(leftCoefficient * this.getSpeed());
        rightPID.setSetpoint(rightCoefficient * this.getSpeed());
    }

    @Override
    public void stop()
    {
        super.stop();
        leftPID.setOperatingMode(PIDController.OperatingMode.MANUAL);
        rightPID.setOperatingMode(PIDController.OperatingMode.MANUAL);
        try
        {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException ignored) {}
        left.stop();
        right.stop();
    }

    public void shutdown()
    {
        leftPID.interrupt();
        rightPID.interrupt();
    }
}
