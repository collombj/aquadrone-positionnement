package fr.onema.lib.sensor.position.IMU;

/**
 * Created by strock on 06/02/2017.
 * Edit jroussel on 14/02/2017.
 * <p>
 * Classe représentant les éléments d'attitude du drone
 */
public class Gyroscope {
    private final double roll;
    private final double pitch;
    private final double yaw;

    /**
     * @param roll  le roll du drone
     * @param pitch le pitch du drone
     * @param yaw   le yaw du drone
     */
    public Gyroscope(double roll, double pitch, double yaw) {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }

    /**
     * Retourne le roll
     *
     * @return le roll
     */
    public double getRoll() {
        return roll;
    }

    /**
     * Retourne le pitch
     *
     * @return le ptich
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Retourne le yaw
     *
     * @return le yaw
     */
    public double getYaw() {
        return yaw;
    }
}
