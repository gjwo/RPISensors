package devices.controller;

/**
 * RPISensors - devices
 * Created by MAWood on 04/12/2016.
 */
public class PIDController extends Thread
{
    enum OperatingMode
    {
        AUTOMATIC,
        MANUAL
    }

    private long lastTime;
    private double input, output, setpoint;
    private double ITerm, lastInput;
    private double kp, ki, kd;
    private double sampleRate;
    private double outMin, outMax;
    private OperatingMode mode;

    public PIDController(double setPoint, double sampleRate, double kp, double ki, double kd, double outMin, double outMax, OperatingMode mode)
    {
        this.setpoint = setPoint;
        this.sampleRate = sampleRate;
        this.kp = kp;
        this.ki = ki;
        this.kd = kd;
        this.outMin = outMin;
        this.outMax = outMax;
        this.mode = mode;
    }

    void initialise()
    {
        lastInput = input;
        ITerm = output;
        if(ITerm> outMax) ITerm= outMax;
        else if(ITerm< outMin) ITerm= outMin;
        this.start();
    }

    @Override
    public void run()
    {
        lastTime = System.currentTimeMillis();
        super.run();
        while(!Thread.interrupted())
        {
            if(System.currentTimeMillis() - lastTime > (1000*(1/sampleRate)))
            {
                compute();
            } else try
                {
                    Thread.sleep((long)Math.floor(100*(1/sampleRate)));
                } catch (InterruptedException ignored) {}
        }
    }

    void compute()
    {
        if(mode != OperatingMode.AUTOMATIC) return;

        long now = System.currentTimeMillis();
        double timeChange = (now - lastTime);
        if (timeChange<(1f/sampleRate)*1000) return; // escape if the sample is too quick

   /*Compute all the working error variables*/
        double error = setpoint - input;
        ITerm += ki * error;
        if(ITerm> outMax) ITerm= outMax;
        else if(ITerm< outMin) ITerm= outMin;
        double dInput = error - lastInput;

   /*Compute PID output*/
        output = kp * error + ITerm - kd * dInput;
        if(output > outMax) output = outMax;
        else if(output < outMin) output = outMin;

   /*Remember some variables for next time*/
        lastInput = input;
        lastTime = now;
    }

    void setTunings(double Kp, double Ki, double Kd)
    {
        double sampleTime = 1/sampleRate;
        kp = Kp;
        ki = Ki * sampleTime;
        kd = Kd / sampleTime;
    }
    void setSampleRate(double newSampleRate)
    {
        if (newSampleRate <= 0) return;
        double ratio = sampleRate/newSampleRate;
        ki *= ratio;
        kd /= ratio;
        sampleRate = newSampleRate;
    }

    void setOutputLimits(double Min, double Max)
    {
        if(Min > Max) return;
        outMin = Min;
        outMax = Max;

        if(output > outMax) output = outMax;
        else if(output < outMin) output = outMin;

        if(ITerm> outMax) ITerm= outMax;
        else if(ITerm< outMin) ITerm= outMin;
    }

    void setOperatingMode(OperatingMode mode)
    {
        if(mode == OperatingMode.AUTOMATIC && this.mode != OperatingMode.AUTOMATIC) initialise();
        if(mode == OperatingMode.MANUAL && this.mode != OperatingMode.MANUAL) Thread.interrupted();
        this.mode = mode;
    }

    public void setInput(double input)
    {
        this.input = input;
    }

    public double getOutput()
    {
        return output;
    }

    public double getSetpoint()
    {
        return setpoint;
    }

    public void setSetpoint(double setpoint)
    {
        this.setpoint = setpoint;
    }
}
