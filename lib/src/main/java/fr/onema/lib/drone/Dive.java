package fr.onema.lib.drone;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.worker.DatabaseWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static fr.onema.lib.drone.Dive.State.ON;
import static fr.onema.lib.drone.Dive.State.RECORD;

/**
 * Created by strock on 09/02/2017.
 */
public class Dive {
    private GPSCoordinate reference;
    private List<Position> positions = new ArrayList<>();
    private final DatabaseWorker dbWorker = DatabaseWorker.getInstance();
    private DiveEntity diveEntity;
    private State state;
    private int numberOfmovement;
    private CartesianVelocity lastVitesse;
    private List<MeasureEntity> measures = new ArrayList<>();
    private List<MeasureEntity> measuresUpdated = new ArrayList<>();

    /**
     * Crée une nouvelle Dive
     */
    public Dive() {
        diveEntity = new DiveEntity();
        dbWorker.newDive(diveEntity);
        numberOfmovement = 0;
        state = ON;
    }

    /**
     * Ajoute une position à la plongée
     *
     * @param position une position
     */
    public void add(Position position) {
        // si c'est le premier point
        if (positions.isEmpty()) {
            // si la premiere position n a pas de gps, on la rejette
            if (!position.hasGPS()) {
                return;
            }

            position.setPositionBrute(position.getGps().getPosition());
            position.setCartesianBrute(new CartesianCoordinate(0, 0, 0));
            lastVitesse = new CartesianVelocity(0, 0, 0);
            reference = position.getPositionBrute();
        } else {//si ce n'est pas le premier point on calcule
            if (!position.hasIMU() && !position.hasGPS()) { // on ignore les paquets sans imu
                return;
            }

            Position lastPos = positions.get(positions.size() - 1);
            if (position.hasGPS()) {
                position.setPositionBrute(position.getGps().getPosition());
                position.setCartesianBrute(GeoMaths.computeCartesianPosition(reference, position.getPositionBrute()));
                lastVitesse = GeoMaths.computeVelocityFromCartesianCoordinate(
                        lastPos.getCartesianBrute(),
                        position.getCartesianBrute(),
                        position.getTimestamp() - lastPos.getTimestamp());
            } else if (position.hasIMU()) {
                lastVitesse = position.calculate(lastPos, lastVitesse, reference);
            }
        }

        updateMeasuresAndPosition(position);
    }

    private void updateMeasuresAndPosition(Position position) {
        for (Measure measure : position.getMeasures()) {
            // créer l'entité
            // l'insérer en base
            IMU imu = position.getImu();
            MeasureEntity entity = new MeasureEntity(
                    position.getTimestamp(),
                    position.getPositionBrute(),
                    position.getPositionRecalculated(),
                    imu == null ? 0 : imu.getAccelerometer().getxAcceleration(),
                    imu == null ? 0 : imu.getAccelerometer().getyAcceleration(),
                    imu == null ? 0 : imu.getAccelerometer().getzAcceleration(),
                    imu == null ? 0 : imu.getGyroscope().getRoll(),
                    imu == null ? 0 : imu.getGyroscope().getPitch(),
                    imu == null ? 0 : imu.getGyroscope().getRoll(),
                    -1,
                    measure.getValue());
            dbWorker.insertMeasure(entity, diveEntity.getId(), measure.getName());
            measures.add(entity);
        }
        positions.add(position);
    }


    /**
     * Termine la plongée
     */
    public void endDive(Position position) {
        Objects.requireNonNull(position);
        if (!position.hasGPS())
            throw new IllegalArgumentException("La dernière position d'une plongée doit être localisée en GPS_SENSOR");
        position.setPositionBrute(position.getGps().getPosition());
        position.calculate(positions.get(positions.size() - 1), lastVitesse, reference);
        if (state == RECORD)
            dbWorker.stopRecording(System.currentTimeMillis(), diveEntity.getId());

        // Update last one
        updateMeasuresAndPosition(position);
        // Creation de la liste des mesures recalculées
        positions = GeoMaths.recalculatePosition(positions, reference, position.getPositionBrute());
        for (Position pos : positions) {
            createUpdatedMeasuresList(pos);
        }
        System.err.println("Update all");
        updateMeasuresInBase();
    }

    private void createUpdatedMeasuresList(Position pos) {
        for (Measure measure : pos.getMeasures()) {
            // créer l'entité
            // l'insérer en base
            MeasureEntity entity = new MeasureEntity(
                    pos.getTimestamp(),
                    pos.getPositionBrute(),
                    pos.getPositionRecalculated(),
                    pos.getImu().getAccelerometer().getxAcceleration(),
                    pos.getImu().getAccelerometer().getyAcceleration(),
                    pos.getImu().getAccelerometer().getzAcceleration(),
                    pos.getImu().getGyroscope().getRoll(),
                    pos.getImu().getGyroscope().getPitch(),
                    pos.getImu().getGyroscope().getRoll(),
                    -1,
                    measure.getValue());
            // dbWorker.insertMeasure(entity, diveEntity.getId(), measure.getName());
            measuresUpdated.add(entity);
        }
    }

    private void updateMeasuresInBase() {
        if (measures.size() != measuresUpdated.size())
            throw new IllegalStateException("Erreur algorithme : nombre de mesures differents apres recalcul");

        MeasureEntity e1;
        MeasureEntity e2;

        for (int i = 0; i < measures.size(); i++) {
            e1 = measures.get(i);
            e2 = measuresUpdated.get(i);

            if (!e1.diveEquals(e2)) {
                throw new IllegalStateException("Erreur algorithme : ordre des mesures non préservé");
            }
            dbWorker.updatePosition(e1.getId(), e2.getLocationCorrected(), e2.getPrecisionCm());
        }
    }


    /**
     * Termine l enregistrement de la plongée
     * * @param timestamp le debut de l enregistrement
     */
    public void startRecording(long timestamp) {
        state = RECORD;
        dbWorker.startRecording(timestamp, diveEntity.getId());
    }

    /**
     * Comment l'enregistrement de la plongée
     *
     * @param timestamp la fin de l'enregistrement
     */
    public void stopRecording(long timestamp) {
        state = ON;
        dbWorker.stopRecording(timestamp, diveEntity.getId());
    }

    /**
     * Incremente le nombre de mouvements effectués
     */
    public void newMovement() {
        numberOfmovement++;
    }


    /**
     * @return le nombre de mouvements
     */
    public int getNumberOfmovement() {
        return numberOfmovement;
    }

    /**
     * Les différents états de la plongée
     */
    enum State {
        OFF,
        ON,
        RECORD
    }
}
