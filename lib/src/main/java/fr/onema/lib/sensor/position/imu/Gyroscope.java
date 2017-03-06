package fr.onema.lib.sensor.position.imu;

/**
 * Classe représentant les éléments d'attitude du drone
 */
public class Gyroscope {
    private final double roll;
    private final double pitch;
    private final double yaw;

    /**
     * Constructeur par défaut
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
     * Getter de la valeur roll
     * @return La valeur roll
     */
    public double getRoll() {
        return roll;
    }

    /**
     * Getter de la valeur pitch
     * @return La valeur du ptich
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * Getter de la valeur du yaw
     * @return La valeur du yaw
     */
    public double getYaw() {
        return yaw;
    }
}
