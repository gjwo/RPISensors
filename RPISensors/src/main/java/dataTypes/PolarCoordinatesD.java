package dataTypes;

/**
 * PolarCoordinatesD    -   Polar Coordinates theta, r with double values
 * Created by GJWood on 02/02/2017.
 */
public class PolarCoordinatesD
{
    private double theta;
    private double r;


    /**
     * PolarCoordinatesD    -   Constructor with user values
     * @param theta         -   angle in radians
     * @param r             -   radius
     */
    public PolarCoordinatesD(double theta, double r){
        this.theta = theta;
        this.r = r;
    }

    /**
     * PolarCoordinatesD    -   Constructor with 0 values
     */
    public PolarCoordinatesD(){
        this(0,0);
    }

    // getters
    public double getTheta() {return theta;}
    public double getR() {return r;}
    //setters
    public void setR(double r) {this.r = r;}
    public void setTheta(double theta) {this.theta = theta;}

    public String toString(){
        return String.format("theta: %1.3f r: %1.3f",theta,r);
    }

}
