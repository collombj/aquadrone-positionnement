package fr.onema.lib.drone;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;

import java.util.ArrayList;
import java.util.List;

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
    private GPSCoordinate positionBrut = null;
    private GPSCoordinate positionRecalculated = null;
    private int direction;

    private GPS gps;
    private IMU imu;
    private Pressure pressure;


    /**
     * Constructeur de Position.
     *
     * @param timestamp    Le timestamp de la position actuelle.
     * @param positionBrut Les coordonnées brut de la position.
     * @param direction    La direction de la position actuelle.
     */
    public Position(long timestamp, GPSCoordinate positionBrut, int direction, IMU imu, GPS gps, Pressure pressure) {
        this.pressure = pressure;
        this.gps = gps;
        this.timestamp = timestamp;
        this.positionBrut = positionBrut;
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

    /**
     * Obtient la position GPS brut de la position.
     *
     * @return La position GPS brut de la position.
     */
    public GPSCoordinate getPositionBrut() {
        return positionBrut;
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
     * Obtient la direction de la position.
     *
     * @return La direction de la position.
     */
    public int getDirection() {
        return direction;
    }

    private int getxRotationp() {
        return imu.getGyroscope().getxRotation();
    }

    private int getyRotation() {
        return imu.getGyroscope().getyRotation();
    }

    private int getzRotation() {
        return imu.getGyroscope().getzRotation();
    }

    /**
     * Obtient l'objet Pressure associé à la position.
     *
     * @return L'objet Pressure associé à la position.
     */
    public Pressure getPressure() {
        return pressure;
    }

    public IMU getImu() {
        return imu;
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
                //TODO attendre merge pour supprimer id et la position recalculé
                //TODO lien vers le entity information
                entities.add(new MeasureEntity(timestamp, positionBrut, positionRecalculated,
                        imu.getAccelerometer().getxAcceleration(), imu.getAccelerometer().getyAcceleration(), imu.getAccelerometer().getzAcceleration(),
                        getxRotationp(), getyRotation(), getzRotation(), -1, measure.getName()));

            }


        } else {
            if (positionRecalculated != null) {
                for (int i = 0; i < measures.size(); i++) {

                    //TODO getter sur la position reclaculé de lmeasure entity
                    //TODO calcul distance Geomaths

                }

            } else {
                throw new IllegalStateException("pas de position recalculée !");
            }

        }


        return entities;
    }

    /**
     * Définit l'IMU de la position.
     *
     * @param imu L'IMU de la position.
     */
    public void setImu(IMU imu) {
        this.imu = imu;
    }

    /**
     * Définit la Pressure de la position.
     *
     * @param pressure La pressure de la position.
     */
    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
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
    public void calculate(Position previousPosition, GPSCoordinate velocity) {
        // TODO
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
     * Vérifie si un Pressure est associé à la position.
     *
     * @return Vrai si un Pressure est associé. Sinon faux.
     */
    public boolean hasPressure() {
        return this.pressure != null;
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
