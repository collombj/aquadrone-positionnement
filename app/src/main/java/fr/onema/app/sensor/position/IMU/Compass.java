package fr.onema.app.sensor.position.IMU;

/**
 * Created by strock on 06/02/2017.
 */
public class Compass {

    private final int xMagnetic;
    private final int yMagnetic;
    private final int zMagnetic;

    public Compass(int xMagnetic, int yMagnetic, int zMagnetic) {
        this.xMagnetic = xMagnetic;
        this.yMagnetic = yMagnetic;
        this.zMagnetic = zMagnetic;
    }

    public int getxMagnetic() {
        return xMagnetic;
    }

    public int getyMagnetic() {
        return yMagnetic;
    }

    public int getzMagnetic() {
        return zMagnetic;
    }
}
