package devices.motors;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import java.util.concurrent.TimeUnit;

/**
 * RPITank
 * Created by MAWood on 02/07/2016.
 */
public class StepperMotor implements AngularPositioner
{
    private enum Direction
    {
        forward,
        backwards
    }

    private final byte[] stepSequence;
    private final PinState[] states;
    private final GpioPinDigitalOutput[] pins;
    private final int STEPS_PER_ROTATION;
    private final int STEP_DELAY_NANOS = 700000;
    private int phase;
    private float angularPosition; // current angularPosition in degrees relative to initial angularPosition

    public StepperMotor(GpioPinDigitalOutput[] pins, int steps_per_rotation)
    {
        this.pins = pins;
        this.phase = 0;
        this.STEPS_PER_ROTATION = steps_per_rotation;
        this.states = new PinState[]{PinState.LOW, PinState.HIGH};
        this.stepSequence = new byte[4];
        this.stepSequence[0] = (byte) 0b0011;
        this.stepSequence[1] = (byte) 0b0110;
        this.stepSequence[2] = (byte) 0b1100;
        this.stepSequence[3] = (byte) 0b1001;
        this.angularPosition = 0;
    }

    private void step(Direction direction)
    {
        float changeAngle;
        if(direction == Direction.forward)
        {
            phase = (phase + 1) % stepSequence.length;
            changeAngle = 1f;
        } else
        {
            phase = (phase + 3) % stepSequence.length;
            changeAngle = -1f;
        }

        for(int i = 0; i<4; i++)
        {
            //System.out.println(phase);
            pins[i].setState(states[(stepSequence[phase] >> 3-i) & 1]);
            long start = System.nanoTime();
            //TODO: make dynamic
            try
            {
                TimeUnit.NANOSECONDS.sleep(STEP_DELAY_NANOS);
            } catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
        updateAngle(changeAngle*360f/(float)STEPS_PER_ROTATION);
    }

    public void rotate(float degrees) {
        Direction dir = degrees > 0 ? Direction.forward : Direction.backwards;
        int steps = Math.round((Math.abs(degrees)/360) * STEPS_PER_ROTATION);
        //System.out.println("Doing " + steps + " steps");
        while(steps> 0)
        {
            step(dir);
            steps--;
        }
        updateAngle(degrees);
    }

    // AngularPositioner methods

    /**
     * getAngularPosition
     * @return              -   angular position in degrees
     */
    @Override
    public float getAngularPosition() {return angularPosition;}

    /**
     * setAngularPosition   -   Sets the position
     * @param angle         -   0-359.9999 degrees
     */
    @Override
    public void setAngularPosition(float angle)
    {
        if( angle > angularPosition)
        {
            rotate(angle - angularPosition);
        } else
        {
            rotate(angle + angularPosition);
        }
    }

    /**
     * angularPositionResolution    -   Positioning accuracy in degrees
     * @return                      -   0-359.9999
     */
    @Override
    public float angularPositionResolution()
    {
        return 360f/(float)STEPS_PER_ROTATION;
    }

    /**
     * calibrateAngularPosition   -   (Re)Calibrates the initial position of the
     *                                  stepper to a bearing (0-359.9999 degrees)
     *                                  without moving the motor
     * @param angle               -   Bearing (0-359.9999 degrees)
     */
    @Override
    public void calibrateAngularPosition(float angle){ this.angularPosition = angle;}

    /**
     * updateAngle          -   updates the angularPosition the stepper believes it is
     *                          pointing in following a motor action
     * @param degrees       -   external bearing in degrees 0-359 (but will cope with -ve etc)
     */
    private void updateAngle(float degrees)
    {
        angularPosition += degrees;
        while (angularPosition < 0) angularPosition +=360;
        while (angularPosition >=360 ) angularPosition -=360;
    }
}
