package fr.onema.lib.drone;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;

import java.util.List;

/**
 * Created by strock on 07/02/2017.
 */
public class Position {
    private List<MeasureEntity> entities;
    private long timestamp;
    private GPSCoordinate positionBrut = null;
    private GPSCoordinate positionRecalculated = null;
    private int direction;
    private IMU imu;
    private Pressure pressure;
    private List<Measure> measures;


    /**
     *constructeur permet d'insérer les valeurs de bases
     * @param timestamp
     * @param positionBrut
     * @param direction
     */
    public Position(long timestamp, GPSCoordinate positionBrut, int direction,IMU imu) {
        //// TODO: insert GPS 
        this.timestamp = timestamp;
        this.positionBrut = positionBrut;
        this.direction = direction;
        this.imu=imu;
    }

    public List<MeasureEntity> getEntities() {
        return entities;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public GPSCoordinate getPositionBrut() {
        return positionBrut;
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

    public Pressure getPressure() {
        return pressure;
    }

    public List<Measure> getMeasures() {
        return measures;
    }


    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setImu(IMU imu) {
        this.imu = imu;
    }

    public void setPressure(Pressure pressure) {
        this.pressure = pressure;
    }

    /**
     * ajjoutte une mesure à cette position
     * @param newMeasure nouvelle mesure {@link Measure}
     */
    public void add(Measure newMeasure) {
        measures.add(newMeasure);

    }

    /**
     * retourne la liste de mesure avec les positions pour l'insert ou l'update de données.
     * @return liste de mesure
     */
    public List<MeasureEntity> save() {
        if (entities.isEmpty()) {


            if (positionBrut != null) {


                for (Measure measure : measures) {
                    //TODO attendre merge pour supprimer id et la position recalculé
                    //TODO lien vers le entity information
                    entities.add(new MeasureEntity(0, timestamp, positionBrut, positionRecalculated,
                            imu.getAccelerometer().getxAcceleration(), imu.getAccelerometer().getyAcceleration(), imu.getAccelerometer().getzAcceleration(),
                            imu.getGyroscope().getRoll(), imu.getGyroscope().getPitch(), imu.getGyroscope().getYaw(), -1, measure.getName()));

                }

            } else {
                throw new IllegalStateException("pas de position brut !");
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


    public void calculate(Position position) {
        // TODO
    }

    public void recalculate() {
        // TODO
    }


}
