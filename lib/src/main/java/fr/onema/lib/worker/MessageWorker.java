package fr.onema.lib.worker;

import fr.onema.lib.drone.Dive;
import fr.onema.lib.drone.Position;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.Pressure;
import fr.onema.lib.sensor.position.imu.IMU;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.*;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe worker de messages. Récupère les messages depuis le ServerListerner.
 * Un fois les messages récupérés, ils sont traités et envoyés aux positions des plongées.
 */
public class MessageWorker implements Worker {
    private static final long FIX_THRESHOLD = 1; // FIXME
    private static final String IMU_SENSOR = "IMU";
    private static final String GPS_SENSOR = "GPS";
    private static final String TEMPERATURE_SENSOR = "Temperature";
    private static final String PRESSURE_SENSOR = "Pressure";
    private static final Logger LOGGER = Logger.getLogger(MessageWorker.class.getName());
    private static final String TIMESTAMP = "Attitude [timestamp: ";
    private static final String TIME = ", time: ";
    private final Map<String, Long> measuresStates = new HashMap<>();
    private final BlockingQueue<HashMap.SimpleEntry<Long, MAVLinkMessage>> messages = new ArrayBlockingQueue<>(200);
    private final Thread mavLinkMessagesThread = new Thread(new MavLinkMessagesThreadWorker());
    private MAVLinkMessage imuBuffer;
    private fr.onema.lib.worker.Logger tracer;
    private Dive dive;
    private Boolean inDive = false;
    private Position currentPos;
    private long mavLinkConnection;

    /**
     * Constructeur de MessageWorker
     * Attention, après l'instanciation la pluspars des champs seront encore null. Ils seront créés en cours de
     * communication avec avec le serveur
     */
    public MessageWorker() {
        // default constructor
    }

    /**
     * Permet de démarrer le logger
     */
    public void startLogger() {
        this.tracer.start();
    }

    /**
     * Paramètre le FileManager qui doit êtra associé au MessageWorker. Ce FileManager servira uniquement à remplir le
     * fichier de trace
     * @param fileManager Le FileManager enregistrant dans le traceur
     */
    public void setTracer(FileManager fileManager) {
        this.tracer = new fr.onema.lib.worker.Logger(fileManager);
    }

    /**
     * Ajoute un message MavLink à la liste des messages à traiter
     * @param message Le message MavLink à traiter
     * @throws InterruptedException En cas d'intérruption du thread courant
     */
    public void newMessage(long timestamp, MAVLinkMessage message) throws InterruptedException {
        Objects.requireNonNull(message);
        this.messages.put(new HashMap.SimpleEntry<>(timestamp, message));
    }

    /***
     * Getter de la plongée
     * @return La Dive en cours
     */
    public Dive getDive() {
        return dive;
    }

    /***
     * Retourne l'état des capteurs Mavlink
     * @return Une map contenant les capteurs Mavlink
     */
    public Map<String, Long> getMeasuresStates() {
        return measuresStates;
    }

    /**
     * Démarre un enregistrement de plongée
     */
    public void startRecording() {
        if (dive != null) {
            this.dive.startRecording(System.currentTimeMillis());
        }
    }

    /**
     * Arrête un enregistrement de plongée.
     */
    public void stopRecording() {
        if (dive != null) {
            dive.stopRecording(System.currentTimeMillis());
        }
    }

    /**
     * Donne l'information de connexion MavLink
     * @return Timestamp du dernier message reçus
     */
    public long getMavLinkConnection() {
        return mavLinkConnection;
    }

    /**
     * Démarre le thread de lecture de flux MavLink
     */
    @Override
    public void start() {
        this.mavLinkMessagesThread.start();
    }

    /**
     * Stop le thread de lecture de flux MavLink
     */
    @Override
    public void stop() {
        this.mavLinkConnection = 0;
        this.mavLinkMessagesThread.interrupt();
        if (tracer != null) {
            this.tracer.stop();
        }
    }

    /**
     * Worker chargé du traitement des messages Mavlink
     */
    public class MavLinkMessagesThreadWorker implements Runnable {

        public void computeMavLinkMessage(long timestamp, MAVLinkMessage mavLinkMessage) throws SQLException, FileNotFoundException {
            if (dive == null) {
                dive = new Dive();
                currentPos = new Position();
            }

            mavLinkConnection = timestamp;
            switch (mavLinkMessage.messageType) {
                case msg_gps_raw_int.MAVLINK_MSG_ID_GPS_RAW_INT:
                    gpsReceived(timestamp, (msg_gps_raw_int) mavLinkMessage);
                    break; // GPS
                case msg_raw_imu.MAVLINK_MSG_ID_RAW_IMU:
                    imuReceived(timestamp, (msg_raw_imu) mavLinkMessage);
                    break;
                case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
                    attitudeReceived(timestamp, (msg_attitude) mavLinkMessage);
                    break; //IMU
                case msg_scaled_pressure2.MAVLINK_MSG_ID_SCALED_PRESSURE2:
                    pressureReceived(timestamp, (msg_scaled_pressure2) mavLinkMessage);
                    temperatureReceived(timestamp, (msg_scaled_pressure2) mavLinkMessage);
                    break; // Pressure
                case msg_scaled_pressure3.MAVLINK_MSG_ID_SCALED_PRESSURE3:
                    temperatureReceived(timestamp, (msg_scaled_pressure3) mavLinkMessage);
                    break; // Temperature
                default:
                    break;
            }
        }

        private void pressureReceived(long timestamp, msg_scaled_pressure2 pressureMessage) {
            LOGGER.log(Level.INFO, () -> TIMESTAMP + timestamp + TIME + pressureMessage.time_boot_ms + "]");
            Pressure pressure = Pressure.build(timestamp, pressureMessage);
            currentPos.setPressure(pressure);
            updateState(PRESSURE_SENSOR, pressure.getTimestamp());
        }

        private void temperatureReceived(long timestamp, msg_scaled_pressure3 temperatureMessage) {
            LOGGER.log(Level.INFO, () -> TIMESTAMP + timestamp + TIME + temperatureMessage.time_boot_ms + "]");
            Temperature temperature = Temperature.build(timestamp, temperatureMessage);
            currentPos.add(temperature);
            updateState(TEMPERATURE_SENSOR, temperature.getTimestamp());
        }

        private void temperatureReceived(long timestamp, msg_scaled_pressure2 temperatureMessage) {
            LOGGER.log(Level.INFO, () -> TIMESTAMP + timestamp + TIME + temperatureMessage.time_boot_ms + "]");
            Temperature temperature = Temperature.build(timestamp, temperatureMessage);
            currentPos.add(temperature);
            updateState(TEMPERATURE_SENSOR, temperature.getTimestamp());
        }

        private void attitudeReceived(long timestamp, msg_attitude attitudeMessage) throws FileNotFoundException {
            LOGGER.log(Level.INFO, () -> TIMESTAMP + timestamp + TIME + attitudeMessage.time_boot_ms + "]");
            if (imuBuffer == null) {
                imuBuffer = attitudeMessage;
                return;
            }

            if (imuBuffer instanceof msg_raw_imu) {
                msg_raw_imu imuMessage = (msg_raw_imu) imuBuffer;
                imuBuffer = null;
                IMU imu = IMU.build(timestamp, imuMessage, attitudeMessage);
                processIMUData(imu);
                return;
            }
            imuBuffer = attitudeMessage;
        }

        private void imuReceived(long timestamp, msg_raw_imu imuMessage) throws FileNotFoundException {
            LOGGER.log(Level.INFO, () -> TIMESTAMP + timestamp + TIME + imuMessage.time_usec + "]");
            if (imuBuffer == null) {
                imuBuffer = imuMessage;
                return;
            }

            if (imuBuffer instanceof msg_attitude) {
                msg_attitude attitudeMessage = (msg_attitude) MessageWorker.this.imuBuffer;
                imuBuffer = null;
                IMU imu = IMU.build(timestamp, imuMessage, attitudeMessage);
                processIMUData(imu);
                return;
            }

            imuBuffer = imuMessage;
        }

        private void processIMUData(IMU imu) {
            long timestamp = imu.getTimestamp();
            if (currentPos.hasIMU()) {
                savePosition();
            }

            currentPos.setTimestamp(timestamp);
            currentPos.setImu(imu);
            updateState(IMU_SENSOR, timestamp);
        }

        private void gpsReceived(long timestamp, msg_gps_raw_int gpsMessage) {
            LOGGER.log(Level.INFO, () -> "GPS [timestamp: " + timestamp + TIME + gpsMessage.time_usec + "]" +
                    " / FIX TYPE : " + gpsMessage.fix_type);
            if (gpsMessage.fix_type < FIX_THRESHOLD) {
                return;
            }
            processGPSData(GPS.build(timestamp, gpsMessage));
        }

        private void processGPSData(GPS gps) {
            long timestamp = gps.getTimestamp();

            if (inDive) {
                savePosition();
                currentPos = new Position();
                currentPos.setGps(gps);
                inDive = false;
                currentPos.setTimestamp(timestamp);
                try {
                    dive.endDive(currentPos);
                } catch (ArrayIndexOutOfBoundsException e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
                dive = null;
                currentPos = null;
                return;
            }

            if (currentPos.hasGPS()) {
                savePosition();
            }

            currentPos.setTimestamp(timestamp);
            currentPos.setGps(gps);
            updateState(GPS_SENSOR, timestamp);
        }

        private void savePosition() {
            if (!currentPos.hasGPS() && !inDive) {
                inDive = true;
            }

            dive.add(currentPos);

            if (tracer != null) {
                try {
                    tracer.addPosition(currentPos);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            currentPos = new Position();
        }

        private void updateState(String sensor, long timestamp) {
            measuresStates.put(sensor, timestamp);
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    AbstractMap.SimpleEntry<Long, MAVLinkMessage> element = messages.take();
                    computeMavLinkMessage(element.getKey(), element.getValue());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
