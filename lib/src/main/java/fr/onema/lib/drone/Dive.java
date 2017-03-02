package fr.onema.lib.drone;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.DatabaseWorker;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.onema.lib.drone.Dive.State.ON;
import static fr.onema.lib.drone.Dive.State.RECORD;


public class Dive {
    private static final Logger LOGGER = Logger.getLogger(Dive.class.getName());
    private final DatabaseWorker dbWorker = DatabaseWorker.getInstance();
    private GPSCoordinate reference;
    private List<Position> positions = new ArrayList<>();
    private DiveEntity diveEntity;
    private State state;
    private int numberOfmovement;
    private CartesianVelocity lastVitesse;
    private List<MeasureEntity> measures = new ArrayList<>();
    private List<MeasureEntity> measuresUpdated = new ArrayList<>();

    /**
     * Crée une nouvelle Dive
     */
    public Dive() throws SQLException, FileNotFoundException {
        diveEntity = new DiveEntity();

        MeasureRepository repos =
                MeasureRepository.MeasureRepositoryBuilder.getRepositoryWritable(Configuration.getInstance());
        repos.insertDive(diveEntity);
        numberOfmovement = 0;
        state = ON;
    }

    /**
     * Ajoute une position à la plongée
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
                LOGGER.log(Level.WARNING, "A packet has been throwed away");
                return;
            }

            Position lastPos = positions.get(positions.size() - 1);
            if (position.hasGPS()) {
                position.setPositionBrute(position.getGps().getPosition());
                position.setCartesianBrute(GeoMaths.computeCartesianPosition(reference, position.getPositionBrute()));
                /*lastVitesse = GeoMaths.computeVelocityFromCartesianCoordinate(
                        lastPos.getCartesianBrute(),
                        position.getCartesianBrute(),
                        position.getTimestamp() - lastPos.getTimestamp());*/
            } else if (position.hasIMU()) {
                lastVitesse = position.calculate(lastPos, lastVitesse);
                position.setPositionBrute(GeoMaths.computeGPSCoordinateFromCartesian(reference, position.getCartesianBrute()));
            }
        }

        updateMeasuresAndPosition(position);
    }

    private void updateMeasuresAndPosition(Position position) {
        for (Measure measure : position.getMeasures()) {
            // créer l'entité
            IMU imu = position.getImu();
            int xAccel = imu == null ? 0 : imu.getAccelerometer().getxAcceleration();
            int yAccel = imu == null ? 0 : imu.getAccelerometer().getyAcceleration();
            int zAccel = imu == null ? 0 : imu.getAccelerometer().getzAcceleration();
            double roll = imu == null ? 0 : imu.getGyroscope().getRoll();
            double pitch = imu == null ? 0 : imu.getGyroscope().getPitch();
            double yaw = imu == null ? 0 : imu.getGyroscope().getYaw();
            MeasureEntity entity = new MeasureEntity(
                    position.getTimestamp(),
                    position.getPositionBrute(),
                    position.getPositionRecalculated(),
                    xAccel,
                    yAccel,
                    zAccel,
                    roll,
                    pitch,
                    yaw,
                    -1,
                    measure.getValue());

            // l'insérer en base
            dbWorker.insertMeasure(entity, diveEntity.getId(), measure.getName());
            dbWorker.sendNotification();
            // l'insérer à la liste des mesures
            measures.add(entity);
        }
        positions.add(position);
    }


    /**
     * Termine la plongée
     */
    public void endDive(Position position) {
        Objects.requireNonNull(position);
        if (!position.hasGPS()) {
            throw new IllegalArgumentException("La dernière position d'une plongée doit être localisée en GPS_SENSOR");
        }

        if(positions.isEmpty()) {
            LOGGER.log(Level.INFO, "An empty dive has been ignored");
            return;
        }

        position.setImu(IMU.build(lastVitesse, new CartesianVelocity(0, 0, 0), positions.get(positions.size() - 1).getTimestamp(), position.getTimestamp()));
        position.setPositionBrute(position.getGps().getPosition());
        position.calculate(positions.get(positions.size() - 1), lastVitesse); //Calcul de la position cartésienne
        if (state == RECORD) {
            dbWorker.stopRecording(System.currentTimeMillis(), diveEntity.getId());
        }

        positions.get(positions.size()-1).getMeasures().forEach(position::add);
        position.setPositionRecalculated(position.getPositionBrute());
        // Update last one
        updateMeasuresAndPosition(position);
        // Creation de la liste des mesures recalculées
        GeoMaths.recalculatePosition(positions, reference, position.getPositionBrute());
        for (Position pos : positions) {
            createUpdatedMeasuresList(pos);
        }
        LOGGER.log(Level.INFO, "All positions have been updated");
        updateMeasuresInBase();
    }

    private void createUpdatedMeasuresList(Position position) {
        for (Measure measure : position.getMeasures()) {
            // créer l'entité
            IMU imu = position.getImu();
            int xAccel = imu == null ? 0 : imu.getAccelerometer().getxAcceleration();
            int yAccel = imu == null ? 0 : imu.getAccelerometer().getyAcceleration();
            int zAccel = imu == null ? 0 : imu.getAccelerometer().getzAcceleration();
            double roll = imu == null ? 0 : imu.getGyroscope().getRoll();
            double pitch = imu == null ? 0 : imu.getGyroscope().getPitch();
            double yaw =imu == null ? 0 : imu.getGyroscope().getYaw();
            MeasureEntity entity = new MeasureEntity(
                    position.getTimestamp(),
                    position.getPositionBrute(),
                    position.getPositionRecalculated() == null ? position.getPositionBrute() : position.getPositionRecalculated(),
                    xAccel,
                    yAccel,
                    zAccel,
                    roll,
                    pitch,
                    yaw,
                    -1,
                    measure.getValue());
            //l insérer à la liste des mesures corrigées
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
        dbWorker.sendNotification();
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
