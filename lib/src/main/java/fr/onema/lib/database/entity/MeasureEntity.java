package fr.onema.lib.database.entity;

import fr.onema.lib.geo.GPSCoordinate;

import java.util.Objects;

/**
 * Cette classe représente une mesure réalisée au cours de la plongée pour la base de données
 */
public class MeasureEntity {
    private int id;
    private final long timestamp;
    private GPSCoordinate locationBrut;
    private GPSCoordinate locationCorrected;
    private final int accelerationX;
    private final int accelerationY;
    private final int accelerationZ;
    private final double roll;
    private final double pitch;
    private final double yaw;
    private final int precisionCm;
    private final String measureValue;

    /**
     * Constructeur
     *
     * @param timestamp         La timestamp de la mesure, en millisecondes
     * @param locationBrut      Les coordonnées GPS de la mesure calculées
     * @param locationCorrected Les coordonnées GPS de la mesure après correction
     * @param accelerationX     L'accélération selon l'axe X du drone
     * @param accelerationY     L'accélération selon l'axe Y du drone
     * @param accelerationZ     L'accélération selon l'axe Z du drone
     * @param roll              le roll du drone
     * @param pitch             Le pitch du drone
     * @param yaw               Le yaw du drone
     * @param precisionCm       La precision estimée de la mesure
     * @param measureValue      La valeur de la mesure.
     */
    public MeasureEntity(long timestamp, GPSCoordinate locationBrut, GPSCoordinate locationCorrected,
                         int accelerationX, int accelerationY, int accelerationZ, double roll, double pitch,
                         double yaw, int precisionCm, String measureValue) {
        //Objects.requireNonNull(locationBrut);
        //Objects.requireNonNull(locationCorrected);
        //Objects.requireNonNull(measureValue);
        this.timestamp = timestamp;
        this.locationBrut = locationBrut;
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
     * @param locationBrut      Les coordonnées GPS de la mesure calculées
     * @param locationCorrected Les coordonnées GPS de la mesure après correction
     * @param accelerationX     L'accélération selon l'axe X du drone
     * @param accelerationY     L'accélération selon l'axe Y du drone
     * @param accelerationZ     L'accélération selon l'axe Z du drone
     * @param roll              le roll du drone
     * @param pitch             Le pitch du drone
     * @param yaw               Le yaw du drone
     * @param precisionCm       La precision estimée de la mesure
     * @param measureValue      La valeur de la mesure.
     */
    public MeasureEntity(int id, long timestamp, GPSCoordinate locationBrut, GPSCoordinate locationCorrected,
                         int accelerationX, int accelerationY, int accelerationZ, double roll, double pitch,
                         double yaw, int precisionCm, String measureValue) {
        Objects.requireNonNull(locationBrut);
        Objects.requireNonNull(locationCorrected);
        Objects.requireNonNull(measureValue);
        if (id <= 0)
            throw new IllegalArgumentException("id must be positive but has value " + id);
        this.id = id;
        this.timestamp = timestamp;
        this.locationBrut = locationBrut;
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
    public GPSCoordinate getLocationBrut() {
        return locationBrut;
    }

    /**
     * @return Les coordonnées GPS de la mesure après correction
     */
    public GPSCoordinate getLocationCorrected() {
        return locationCorrected;
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
     * @return La valeur de la mesure.
     */
    public String getMeasureValue() {
        return measureValue;
    }


    /**
     * MEt a jour les coordonnées relevées
     *
     * @param locationBrut des coordonnées GPS
     */
    public void setLocationBrut(GPSCoordinate locationBrut) {
        this.locationBrut = locationBrut;
    }

    /**
     * Met a jour les coordonnées corrigées
     *
     * @param locationCorrected des coordonnées GPS
     */
    public void setLocationCorrected(GPSCoordinate locationCorrected) {
        this.locationCorrected = locationCorrected;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MeasureEntity that = (MeasureEntity) o;

        if (id != that.id)
            return false;
        if (timestamp != that.timestamp)
            return false;
        if (accelerationX != that.accelerationX)
            return false;
        if (accelerationY != that.accelerationY)
            return false;
        if (accelerationZ != that.accelerationZ)
            return false;
        if (Double.compare(roll, that.roll) != 0)
            return false;
        if (Double.compare(pitch, that.pitch) != 0)
            return false;
        if (Double.compare(yaw, that.yaw) != 0)
            return false;
        if (precisionCm != that.precisionCm)
            return false;
        if (locationBrut != null ? !locationBrut.equals(that.locationBrut) : that.locationBrut != null)
            return false;
        if (locationCorrected != null ? !locationCorrected.equals(that.locationCorrected) : that.locationCorrected != null)
            return false;
        return measureValue != null ? measureValue.equals(that.measureValue) : that.measureValue == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        result = 31 * result + (locationBrut != null ? locationBrut.hashCode() : 0);
        result = 31 * result + (locationCorrected != null ? locationCorrected.hashCode() : 0);
        result = 31 * result + accelerationX;
        result = 31 * result + accelerationY;
        result = 31 * result + accelerationZ;
        result = (int)(31 * result + roll);
        result = (int)(31 * result + pitch);
        result = (int)(31 * result + yaw);
        result = 31 * result + precisionCm;
        result = 31 * result + (measureValue != null ? measureValue.hashCode() : 0);
        return result;
    }
}
