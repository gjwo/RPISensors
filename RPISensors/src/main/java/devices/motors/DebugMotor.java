package devices.motors;

/**
 * RPISensors - devices.motors
 * Created by MAWood on 03/12/2016.
 */
public class DebugMotor implements Motor
{
    private String name;
    private float speed;

    public DebugMotor(String name)
    {
        this.name = name;
        this.speed = 0;
        printState();
    }

    @Override
    public void setSpeed(float speed)
    {
        this.speed = speed;
        printState();
    }

    @Override
    public float getSpeed()
    {
        return 0;
    }

    @Override
    public void stop()
    {
        setSpeed(0f);
    }

    private void printState()
    {
        System.out.println("Motor '" + name + "' is running at speed: " + speed);
    }
}
