package fr.onema.lib.sensor.position.imu;

/***
 * Classe permettant la description de l'accélération sur les axes x, y, z
 */
public class Accelerometer {
    private final int xAcceleration;
    private final int yAcceleration;
    private final int zAcceleration;

    /**
     * Constructeur par défaut
     * @param xAcceleration coordonnée acceleration x
     * @param yAcceleration coordonnée acceleration y
     * @param zAcceleration coordonnée acceleration z
     */
    public Accelerometer(int xAcceleration, int yAcceleration, int zAcceleration) {
        this.xAcceleration = xAcceleration;
        this.yAcceleration = yAcceleration;
        this.zAcceleration = zAcceleration;
    }

    /**
     * Getter de la valeur de l'accélération sur l'axe x
     * @return L'accélération sur l'axe x
     */
    public int getxAcceleration() {
        return xAcceleration;
    }

    /**
     * Getter de la valeur de l'accélération sur l'axe y
     * @return L'accélération sur l'axe y
     */
    public int getyAcceleration() {
        return yAcceleration;
    }

    /**
     * Getter de la valeur de l'accélération sur l'axe z
     * @return L'accélération sur l'axe z
     */
    public int getzAcceleration() {
        return zAcceleration;
    }

    @Override
    public String toString() {
        return "[accX: " + xAcceleration + ", accY: " + yAcceleration + ", accZ: " + zAcceleration + "]";
    }
}
