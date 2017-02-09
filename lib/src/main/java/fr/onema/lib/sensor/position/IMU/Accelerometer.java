package fr.onema.lib.sensor.position.IMU;

/**
 * created by strock on 06/02/2017.
 */
public class Accelerometer {
    private final int xAcceleration;
    private final int yAcceleration;
    private final int zAcceleration;

    /**
     *  @param xAcceleration coordonnée acceleration x
     * @param yAcceleration coordonnée acceleration y
     * @param zAcceleration coordonnée acceleration z
     */
    public Accelerometer(int xAcceleration, int yAcceleration, int zAcceleration) {
        this.xAcceleration = xAcceleration;
        this.yAcceleration = yAcceleration;
        this.zAcceleration = zAcceleration;
    }

    // TODO : complete
    public int getxAcceleration() {
        return xAcceleration;
    }

    // TODO : complete
    public int getyAcceleration() {
        return yAcceleration;
    }

    // TODO : complete
    public int getzAcceleration() {
        return zAcceleration;
    }
}
