package fr.onema.lib.database.entity;

import fr.onema.lib.geo.GPSCoordinate;

import java.util.Objects;

/**
 * Cette classe représente une mesure réalisée au cours de la plongée pour la base de données
 */
public class MeasureEntity {
    private final long timestamp;
    private final int accelerationX;
    private final int accelerationY;
    private final int accelerationZ;
    private final double roll;
    private final double pitch;
    private final double yaw;
    private final String measureValue;
    private GPSCoordinate locationBrute;
    private GPSCoordinate locationCorrected;
    private int precisionCm;
    private int id;

    /**
     * Constructeur
     *
     * @param timestamp         La timestamp de la mesure, en millisecondes
     * @param locationBrute      Les coordonnées GPS_SENSOR de la mesure calculées
     * @param locationCorrected Les coordonnées GPS_SENSOR de la mesure après correction
     * @param accelerationX     L'accélération selon l'axe X du drone
     * @param accelerationY     L'accélération selon l'axe Y du drone
     * @param accelerationZ     L'accélération selon l'axe Z du drone
     * @param roll              le roll du drone
     * @param pitch             Le pitch du drone
     * @param yaw               Le yaw du drone
     * @param precisionCm       La precision estimée de la mesure
     * @param measureValue      La valeur de la mesure.
     */
    public MeasureEntity(long timestamp, GPSCoordinate locationBrute, GPSCoordinate locationCorrected,
                         int accelerationX, int accelerationY, int accelerationZ, double roll, double pitch,
                         double yaw, int precisionCm, String measureValue) {
        Objects.requireNonNull(locationBrute);
        Objects.requireNonNull(locationCorrected);
        Objects.requireNonNull(measureValue);
        this.timestamp = timestamp;
        this.locationBrute = locationBrute;
        this.locationCorrected = locationCorrected;
        this.accelerationX = accelerationX;
        this.accelerationY = accelerationY;
        this.accelerationZ = accelerationZ;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
        this.precisionCm = precisionCm;
        this.measureValue = measureValue;
    }

    /**
     * Constructeur
     *
     * @param id                L'identifiant de la mesure en base
     * @param timestamp         La timestamp de la mesure, en millisecondes
     * @param locationBrute     Les coordonnées GPS_SENSOR de la mesure calculées
     * @param locationCorrected Les coordonnées GPS_SENSOR de la mesure après correction
     * @param accelerationX     L'accélération selon l'axe X du drone
     * @param accelerationY     L'accélération selon l'axe Y du drone
     * @param accelerationZ     L'accélération selon l'axe Z du drone
     * @param roll              le roll du drone
     * @param pitch             Le pitch du drone
     * @param yaw               Le yaw du drone
     * @param precisionCm       La precision estimée de la mesure
     * @param measureValue      La valeur de la mesure.
     */
    public MeasureEntity(int id, long timestamp, GPSCoordinate locationBrute, GPSCoordinate locationCorrected,
                         int accelerationX, int accelerationY, int accelerationZ, double roll, double pitch,
                         double yaw, int precisionCm, String measureValue) {
        this(timestamp, locationBrute, locationCorrected,
                accelerationX, accelerationY, accelerationZ, roll, pitch, yaw, precisionCm, measureValue);
        if (id <= 0)
            throw new IllegalArgumentException("id must be positive but has value " + id);
        this.id = id;
    }

    /**
     * @return L'identifiant en base de la mesure
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'ID de la mesure.
     *
     * @param id La valeur de l'ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return La timestamp de la mesure, en millisecondes
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @return Les coordonnées GPS de la mesure calculées
     */
    public GPSCoordinate getLocationBrute() {
        return locationBrute;
    }

    /**
     * MEt a jour les coordonnées relevées
     *
     * @param locationBrute des coordonnées GPS
     */
    public void setLocationBrute(GPSCoordinate locationBrute) {
        this.locationBrute = locationBrute;
    }

    /**
     * @return Les coordonnées GPS de la mesure après correction
     */
    public GPSCoordinate getLocationCorrected() {
        return locationCorrected;
    }

    /**
     * Met a jour les coordonnées corrigées
     *
     * @param locationCorrected des coordonnées GPS
     */
    public void setLocationCorrected(GPSCoordinate locationCorrected) {
        this.locationCorrected = locationCorrected;
    }

    /**
     * @return L'accélération selon l'axe X du drone
     */
    public int getAccelerationX() {
        return accelerationX;
    }

    /**
     * @return L'accélération selon l'axe Y du drone
     */
    public int getAccelerationY() {
        return accelerationY;
    }

    /**
     * @return L'accélération selon l'axe Z du drone
     */
    public int getAccelerationZ() {
        return accelerationZ;
    }

    /**
     * @return La rotation autour de l'axe X du drone
     */
    public double getRoll() {
        return roll;
    }

    /**
     * @return La rotation autour de l'axe Y du drone
     */
    public double getPitch() {
        return pitch;
    }

    /**
     * @return La rotation autour de l'axe Z du drone
     */
    public double getYaw() {
        return yaw;
    }

    /**
     * @return La precision estimée de la mesure
     */
    public int getPrecisionCm() {
        return precisionCm;
    }

    /**
     * Met à jour la précision de la mesure
     *
     * @param precisionCm La précision en cm
     */
    public void setPrecisionCm(int precisionCm) {
        this.precisionCm = precisionCm;
    }

    /**
     * @return La valeur de la mesure.
     */
    public String getMeasureValue() {
        return measureValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MeasureEntity that = (MeasureEntity) o;

        if (timestamp != that.timestamp)
            return false;
        if (accelerationX != that.accelerationX)
            return false;
        if (accelerationY != that.accelerationY)
            return false;
        if (accelerationZ != that.accelerationZ)
            return false;
        if (Double.compare(that.roll, roll) != 0)
            return false;
        if (Double.compare(that.pitch, pitch) != 0)
            return false;
        if (Double.compare(that.yaw, yaw) != 0)
            return false;
        if (precisionCm != that.precisionCm)
            return false;
        if (id != that.id)
            return false;
        if (locationBrute != null ? !locationBrute.equals(that.locationBrute) : that.locationBrute != null)
            return false;
        if (locationCorrected != null ? !locationCorrected.equals(that.locationCorrected) : that.locationCorrected != null)
            return false;
        return measureValue != null ? measureValue.equals(that.measureValue) : that.measureValue == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (locationBrute != null ? locationBrute.hashCode() : 0);
        result = 31 * result + (locationCorrected != null ? locationCorrected.hashCode() : 0);
        result = 31 * result + accelerationX;
        result = 31 * result + accelerationY;
        result = 31 * result + accelerationZ;
        result = (int) (31 * result + roll);
        result = (int) (31 * result + pitch);
        result = (int) (31 * result + yaw);
        result = 31 * result + precisionCm;
        result = 31 * result + (measureValue != null ? measureValue.hashCode() : 0);
        return result;
    }
}
