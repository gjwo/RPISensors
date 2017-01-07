package devices.driveAssembly;

import devices.motors.Motor;
import logging.SystemLog;
import subsystems.SubSystem;

/**
 * RPITank
 * Created by MAWood on 02/07/2016.
 */
public class TankDriveAssembly implements DriveAssembly
{
    private final Motor left;
    private final Motor right;

    private float angle; // this is 0-360* with 0 being forward, 90 right spin, 270 left spin and 180 reverse
    private float speed; // this is 0-1

    public TankDriveAssembly(Motor left, Motor right)
    {
        this.left = left;
        this.right = right;
        this.angle = 0;
        this.speed = 0;
    }

    @Override
    public void setSpeed(float speed)
    {
        SystemLog.log(SubSystem.SubSystemType.DRIVE_ASSEMBLY,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"TDA setSpeed");
        if (speed < 0) speed = 0;
        if (speed > 1) speed = 1;
        this.speed = speed;
        updateCourse();
        SystemLog.log(SubSystem.SubSystemType.DRIVE_ASSEMBLY,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End TDA setSpeed");

    }

    @Override
    public float getSpeed()
    {
        return speed;
    }

    @Override
    public void setDirection(float angle)
    {
        SystemLog.log(SubSystem.SubSystemType.DRIVE_ASSEMBLY,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"TDA setDirection");
        if (angle < 0) angle += 360;
        if (angle < 0) angle = 0;
        if (angle >= 360) angle = 0;
        this.angle = angle;
        updateCourse();
        SystemLog.log(SubSystem.SubSystemType.DRIVE_ASSEMBLY,SystemLog.LogLevel.TRACE_INTERFACE_METHODS,"End TDA setDirection");
    }

    @Override
    public float getDirection()
    {
        return angle;
    }

    @Override
    public void stop()
    {
        this.setSpeed(0);
        left.stop();
        right.stop();
    }

    private void updateCourse()
    {
        SystemLog.log(SubSystem.SubSystemType.DRIVE_ASSEMBLY,SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"TDA updateCourse");
        float leftCoefficient;
        float rightCoefficient;
        float adjustedDirection = this.getDirection();

        // Adjusted direction is a transformation of direction where forwards and backwards are ignored and
        // a -90 to 90 direction is formed for the amount of turning in whichever direction it is going
        // this makes straight forwards and straight backwards 0

        if (this.getDirection() > 90 && this.getDirection() < 270)
            adjustedDirection = (adjustedDirection * -1) + 540;
        if (adjustedDirection >= 270) adjustedDirection -= 360;


        if (adjustedDirection > 0)
        {
            leftCoefficient = 1;
            rightCoefficient = 1 - ((Math.abs(adjustedDirection) / 90) * 2);
        } else
        {
            leftCoefficient = 1 - ((Math.abs(adjustedDirection) / 90) * 2);
            rightCoefficient = 1;
        }


        if (this.getDirection() > 90 && this.getDirection() < 270)
        {
            leftCoefficient *= -1;
            rightCoefficient *= -1;
        }

        left.setOutput(leftCoefficient * this.getSpeed());
        right.setOutput(rightCoefficient * this.getSpeed());
        SystemLog.log(SubSystem.SubSystemType.DRIVE_ASSEMBLY,SystemLog.LogLevel.TRACE_INTERNAL_METHODS,"End TDA UpdateCourse");
    }
}
