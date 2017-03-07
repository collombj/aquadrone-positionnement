package fr.onema.lib.drone;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.Pressure;
import fr.onema.lib.sensor.position.imu.IMU;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une position. Cette position doit avoir plusieurs mesures associées dont: IMU, GPS_SENSOR et Pressure.
 * Au sein de cette classe nous avons une liste de Measure et de MeasureEntity associées à la position courante.
 */
public class Position {
    private List<MeasureEntity> entities = new ArrayList<>();
    private List<Measure> measures = new ArrayList<>();
    private long timestamp;
    private GPSCoordinate positionBrute = null;
    private CartesianCoordinate cartesianBrute = null;
    private GPSCoordinate positionRecalculated = null;
    private int direction;
    private GPS gps;
    private IMU imu;
    private Pressure pressure;


    /**
     * Constructeur de la mesure de position
     *
     * @param timestamp     Heure de la mesure
     * @param positionBrute Coordonnées gps (latitude, longitude, altitude)
     * @param direction     Orientation du gps en degrés
     */
    public Position(long timestamp, GPSCoordinate positionBrute, int direction, IMU imu, GPS gps) {
        if (imu == null && gps == null)
            throw new InvalidParameterException("Position need either an IMU or a GPS_SENSOR value");
        this.gps = gps;
        this.timestamp = timestamp;
        this.positionBrute = positionBrute;
        this.direction = direction;
        this.imu = imu;
    }

    /**
     * Constructeur de Position seulement avec timestamp. Les autres valeurs des capteurs peuvent être associés plus
     * tard grâce aux setters.
     */
    public Position() {
        // Nothing instantiated
    }

    /**
     * Constructeur de Position seulement avec timestamp
     * Les autres valeurs des capteurs peuvent être associés plus tard grâce aux setters
     *
     * @param timestamp Le timestamp de la positione actuelle
     */
    public Position(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter de la position brute cartésienne. Utile pour le calcul de la position recalculé
     *
     * @return La position cartésienne brute
     */
    public CartesianCoordinate getCartesianBrute() {
        return cartesianBrute;
    }

    /**
     * Met a jour la position brute cartésienne
     *
     * @param cartesianBrute La position cartésienne brute
     */
    public void setCartesianBrute(CartesianCoordinate cartesianBrute) {
        this.cartesianBrute = cartesianBrute;
    }

    /**
     * Getter du timestamp de la position
     *
     * @return Le timestamp de la position
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Met à jour le timestamp
     *
     * @param timestamp l'heure de la mesure
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Getter de la position GPS_SENSOR brute de la position
     *
     * @return La position GPS_SENSOR brute de la position
     */
    public GPSCoordinate getPositionBrute() {
        return positionBrute;
    }

    /**
     * Met a jour la coordonnées brutes
     *
     * @param positionBrute des coordonnées GPS_SENSOR
     */
    void setPositionBrute(GPSCoordinate positionBrute) {
        this.positionBrute = positionBrute;
        this.entities.forEach(a -> a.setLocationBrute(positionBrute));
    }

    /**
     * Getter de la position GPS_SENSOR corrigée de la position
     *
     * @return La position GPS_SENSOR corrigée de la position
     */
    public GPSCoordinate getPositionRecalculated() {
        return positionRecalculated;
    }

    /**
     * Met à jour les coordonnées recalculées
     *
     * @param positionRecalculated des coordonées GPS_SENSOR
     */
    public void setPositionRecalculated(GPSCoordinate positionRecalculated) {
        this.positionRecalculated = positionRecalculated;
        this.entities.forEach(a -> a.setLocationCorrected(positionRecalculated));
    }

    /**
     * Getter de la direction de la position
     *
     * @return La direction de la position
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Getter de l'IMU
     *
     * @return L'IMU associé à la position
     */
    public IMU getImu() {
        return imu;
    }

    /**
     * Définit l'IMU de la position.
     *
     * @param imu L'IMU de la position.
     */
    public void setImu(IMU imu) {
        this.imu = imu;
    }

    public GPS getGps() {
        return gps;
    }

    /**
     * Définit le GPS_SENSOR de la position
     *
     * @param gps Le GPS_SENSOR de la position
     */
    public void setGps(GPS gps) {
        this.gps = gps;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    /**
     * Ajoute une mesure à cette position
     *
     * @param newMeasure Nouvelle mesure {@link Measure}
     */
    public void add(Measure newMeasure) {
        measures.add(newMeasure);
    }


    /**
     * Vérifie si un GPS_SENSOR est associé à la position
     *
     * @return Vrai si un GPS_SENSOR est associé. Sinon faux
     */
    public boolean hasGPS() {
        return this.gps != null;
    }

    /**
     * Vérifie si un IMU est associé à la position
     *
     * @return Vrai si un IMU est associé. Sinon faux
     */
    public boolean hasIMU() {
        return this.imu != null;
    }

    /**
     * Procède au calcule de la position actuelle grâce à la position précedente
     * Cette position précédente est enregistré en coordonnées cartésienes et en GPS
     * Afin de ce faire, il est nécessaire d'avoir un IMU, la position précédente et la vitesse précedente
     *
     * @param previousPosition La position précédente.
     * @param previousVelocity La vitesse de la position précédente.
     * @return la vitesse au cours du deplacement
     */
    CartesianVelocity calculate(Position previousPosition, CartesianVelocity previousVelocity) {
        if (this.hasGPS()) {
            this.positionRecalculated = this.positionBrute;
        }
        GeoMaths.MovementWrapper wrapper = GeoMaths.computeNewPosition(
                previousPosition.getCartesianBrute(),
                imu.getGyroscope().getYaw(),
                imu.getGyroscope().getPitch(),
                imu.getGyroscope().getRoll(),
                timestamp - previousPosition.getTimestamp(),
                imu.getAccelerometer());
        this.setCartesianBrute(wrapper.getCoordinate());
        return wrapper.getVelocity();
    }

    /**
     * Méthode permettant d'accèder à la pression relative à une position
     *
     * @return La pression courrante
     */
    public Pressure getPressure() {
        return pressure;
    }

    /**
     * Méthode permettant de mettre à jour la pression sur une position
     *
     * @param pressure La pression relative à la position mise à jour
     */
    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
    }
}
