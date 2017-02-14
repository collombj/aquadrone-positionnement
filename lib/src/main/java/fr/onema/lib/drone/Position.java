package fr.onema.lib.drone;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;

import java.util.List;

/**
 * Created by strock on 07/02/2017.
 */

/***
 * Classe permettant de décrire la mesure d'une position
 */
// TODO : les commentaires des méthodes publiques
public class Position {
    private List<MeasureEntity> entities;
    private long timestamp;
    private GPSCoordinate positionBrute = null;
    private GPSCoordinate positionRecalculated = null;
    private int direction;
    private IMU imu;
    private Pressure pressure;
    private List<Measure> measures;

    /**
     * Constructeur de la mesure de position
     * @param timestamp Heure de la mesure
     * @param positionBrute Coordonnées gps (latitude, longitude, altitude)
     * @param direction Orientation du gps en degrés
     */
    public Position(long timestamp, GPSCoordinate positionBrute, int direction, IMU imu) {
        //// TODO: insert GPS 
        this.timestamp = timestamp;
        this.positionBrute = positionBrute;
        this.direction = direction;
        this.imu = imu;
    }

    public List<MeasureEntity> getEntities() {
        return entities;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public GPSCoordinate getPositionBrute() {
        return positionBrute;
    }

    public GPSCoordinate getPositionRecalculated() {
        return positionRecalculated;
    }

    public int getDirection() {
        return direction;
    }

    public IMU getImu() {
        return imu;
    }

    public void setImu(IMU imu) {
        this.imu = imu;
    }

    public Pressure getPressure() {
        return pressure;
    }

    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
    }

    public List<Measure> getMeasures() {
        return measures;
    }

    /**
     * Ajoute une mesure à cette position
     * @param newMeasure nouvelle mesure {@link Measure}
     */
    public void add(Measure newMeasure) {
        measures.add(newMeasure);
    }

    /**
     * Retourne la liste de mesures avec les positions pour l'insert ou l'update de données.
     * @return Liste des mesures
     */
    public List<MeasureEntity> save() {
        if (entities.isEmpty()) {
            if (positionBrute != null) {
                for (Measure measure : measures) {
                    //TODO attendre merge pour supprimer id et la position recalculé
                    //TODO lien vers le entity information
                    entities.add(new MeasureEntity(0, timestamp, positionBrute, positionRecalculated,
                            imu.getAccelerometer().getxAcceleration(), imu.getAccelerometer().getyAcceleration(), imu.getAccelerometer().getzAcceleration(),
                            imu.getGyroscope().getRoll(), imu.getGyroscope().getPitch(), imu.getGyroscope().getYaw(), -1, measure.getName()));

                }
            } else {
                throw new IllegalStateException("Pas de position brute !");
            }
        } else {
            if (positionRecalculated != null) {
                /*
                for (int i = 0; i < measures.size(); i++) {
                    //TODO getter sur la position reclaculé de lmeasure entity
                    //TODO calcul distance Geomaths
                }
                */
            } else {
                throw new IllegalStateException("Pas de position recalculée !");
            }
        }
        return entities;
    }


    public void calculate(Position position) {
        // TODO
    }

    public void recalculate() {
        // TODO
    }


}
