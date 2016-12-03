package main;

import devices.driveAssembly.DriveAssembly;
import devices.driveAssembly.TankDriveAssembly;
import devices.motors.DebugMotor;

/**
 * RPISensors - main
 * Created by MAWood on 03/12/2016.
 */
public class MotorTest
{
    public static void main(String[] args)
    {
        DriveAssembly DA = new TankDriveAssembly(new DebugMotor("left"),new DebugMotor("right"));
        DA.setSpeed(1f);
        DA.setDirection(180);
        DA.setDirection(0);
        DA.setDirection(270);
        DA.setDirection(90);
    }
}
