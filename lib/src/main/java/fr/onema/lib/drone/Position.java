package fr.onema.lib.drone;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Représente une position. Cette position doit avoir plusieurs mesures associées dont: IMU, GPS et Pressure.
 * Au sein de cette classe nous avons une liste de Measure et de MeasureEntity associées à la position courante.
 *
 * @author phil
 * @since 10-02-2017
 */
public class Position {
    private List<MeasureEntity> entities = new ArrayList<>();
    private List<Measure> measures = new ArrayList<>();
    private long timestamp;
    private GPSCoordinate positionBrute = null;
    private GPSCoordinate positionRecalculated = null;
    private int direction;
    private GPS gps;
    private IMU imu;


    /**
     * Constructeur de la mesure de position
     * @param timestamp Heure de la mesure
     * @param positionBrute Coordonnées gps (latitude, longitude, altitude)
     * @param direction Orientation du gps en degrés
     */
    public Position(long timestamp, GPSCoordinate positionBrute, int direction, IMU imu, GPS gps) {
        if (imu == null && gps == null)
            throw new InvalidParameterException("Position need either an IMU or a GPS value");
        this.gps = gps;
        this.timestamp = timestamp;
        this.positionBrute = positionBrute;
        this.direction = direction;
        this.imu = imu;
    }

    /**
     * Constructeur de Position seulement avec timestamp. Les autres valeurs des capteurs peuvent être associés plus
     * tard grâce aux setters.
     *
     * @param timestamp Le timestamp de la positione actuelle.
     */
    public Position(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Récupère la liste des MeasureEntity associés à cette position.
     *
     * @return La liste des MeasureEntity associés à cette position.
     */
    public List<MeasureEntity> getEntities() {
        return entities;
    }

    /**
     * Retourne le timestamp de la position.
     *
     * @return Le timestamp de la position.
     */
    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Obtient la position GPS brute de la position.
     *
     * @return La position GPS brute de la position.
     */
    public GPSCoordinate getPositionBrute() {
        return positionBrute;
    }

    /**
     * Met a jour la coordonnées brutes
     *
     * @param positionBrute des coordonnées GPS
     */
    public void setPositionBrute(GPSCoordinate positionBrute) {
        this.positionBrute = positionBrute;
        this.entities.forEach(a -> a.setLocationBrut(positionBrute));
    }

    /**
     * Obtient la position GPS corrigée de la position.
     *
     * @return La position GPS corrigée de la position.
     */
    public GPSCoordinate getPositionRecalculated() {
        return positionRecalculated;
    }

    /**
     * Met à jour les coordonnées recalculées
     *
     * @param positionRecalculated des coordonées GPS
     */
    public void setPositionRecalculated(GPSCoordinate positionRecalculated) {
        this.positionRecalculated = positionRecalculated;
        this.entities.forEach(a -> a.setLocationCorrected(positionRecalculated));
    }

    /**
     * Obtient la direction de la position.
     *
     * @return La direction de la position.
     */
    public int getDirection() {
        return direction;
    }

    public IMU getImu() {
        return imu;
    }

    public GPS getGps() {
        return gps;
    }

    /**
     * Définit l'IMU de la position.
     *
     * @param imu L'IMU de la position.
     */
    public void setImu(IMU imu) {
        this.imu = imu;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    /**
     * Retourne la liste de mesure avec les positions pour l'insert ou l'update de données.
     *
     * @return liste de mesure
     */
    public List<MeasureEntity> getMeasureEntities() {
        if (entities.isEmpty()) {
            for (Measure measure : measures) {
                // TODO attendre merge pour supprimer id et la position recalculé
                // TODO lien vers le entity information
                entities.add(new MeasureEntity(timestamp, positionBrute, positionRecalculated,
                        imu.getAccelerometer().getxAcceleration(), imu.getAccelerometer().getyAcceleration(), imu.getAccelerometer().getzAcceleration(),
                        imu.getGyroscope().getRoll(), imu.getGyroscope().getPitch(), imu.getGyroscope().getYaw(), -1, measure.getName()));
            }
        } else {
            for (int i = 0; i < measures.size(); i++) {
                // TODO getter sur la position reclaculé de lmeasure entity
                // TODO calcul distance Geomaths
            }
        }
        return entities;
    }

    /**
     * Définit le GPS de la position.
     *
     * @param gps Le GPS de la position.
     */
    public void setGps(GPS gps) {
        this.gps = gps;
    }

    /**
     * Ajoute une mesure à cette position.
     *
     * @param newMeasure Nouvelle mesure {@link Measure}
     */
    public void add(Measure newMeasure) {
        measures.add(newMeasure);

    }

    /**
     * Procède au calcule de la position actuelle grâce à la position précedente.
     * Afin de ce faire, il est nécessaire d'avoir un GPS, IMU, Pressure associé à la position précédente.
     *
     * @param previousPosition La position précédente.
     * @param velocity         La vitesse de la position précédente.
     */
    public void calculate(Position previousPosition, CartesianVelocity velocity, GPSCoordinate reference) {
        // TODO
        Random rand = new Random();
        int x = rand.nextInt(25000 - 1) + 1;
        int y = rand.nextInt(25000 - 1) + 1;
        int z = rand.nextInt(25000 - 1) + 1;

        this.setPositionBrute(new GPSCoordinate(x, y, z));
    }

    /**
     * Vérifie si un GPS est associé à la position.
     *
     * @return Vrai si un GPS est associé. Sinon faux.
     */
    public boolean hasGPS() {
        return this.gps != null;
    }

    /**
     * Vérifie si un IMU est associé à la position.
     *
     * @return Vrai si un IMU est associé. Sinon faux.
     */
    public boolean hasIMU() {
        return this.imu != null;
    }
}
