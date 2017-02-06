package fr.onema.app.sensor.position.IMU;

/**
 * Created by strock on 06/02/2017.
 */
public class Gyroscope {

    private final int xRotation;
    private final int yRotation;
    private final int zRotation;


    public Gyroscope(int xRotation, int yRotation, int zRotation) {
        this.xRotation = xRotation;
        this.yRotation = yRotation;
        this.zRotation = zRotation;
    }

    public int getxRotation() {
        return xRotation;
    }

    public int getyRotation() {
        return yRotation;
    }

    public int getzRotation() {
        return zRotation;
    }
}
