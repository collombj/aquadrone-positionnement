package fr.onema.lib.worker;

import fr.onema.lib.drone.Dive;
import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.*;

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

    private static final long FIX_THRESHOLD = 5;
    private static final String IMU_SENSOR = "IMU";
    private static final String GPS_SENSOR = "GPS";
    private static final String TEMPERATURE_SENSOR = "Temperature";
    private static final String PRESSURE_SENSOR = "Pressure";
    private static final Logger LOGGER = Logger.getLogger(MessageWorker.class.getName());

    // Represents the current states of the sensors. This map is updated each time a sensor produces data
    private final Map<String, Long> measuresStates = new HashMap<>();
    // List that contains all the received MAVLinkMessages waiting to be treated by the worker
    private final BlockingQueue<HashMap.SimpleEntry<Long, MAVLinkMessage>> messages = new ArrayBlockingQueue<>(200);
    // Worker thread that treats the MAVLinkMessages
    private final Thread mavLinkMessagesThread = new Thread(new MavLinkMessagesThreadWorker());
    // Utilisé pour la fusion Atitude + IMU = IMU DB
    private MAVLinkMessage imuBuffer;

    // Represents the dive currently associated
    private Dive dive;
    private Boolean inDive = false;
    private Position currentPos;
    private long mavLinkConnection;

    /**
     * Constructeur de MessageWorker
     * Attention, après l'instanciation la pluspars des champs seront encore null. Ils seront créés en cours de
     * communication avec avec le serveur.
     */
    public MessageWorker() {
        // Default constructor
    }

    /**
     * Ajoute un message MavLink à la liste des messages à traiter.
     *
     * @param message Le message MavLink à traiter.
     * @throws InterruptedException En cas d'intérruption du thread courant.
     */
    public void newMessage(long timestamp, MAVLinkMessage message) throws InterruptedException {
        Objects.requireNonNull(message);
        this.messages.put(new HashMap.SimpleEntry<>(timestamp, message));
    }

    public Dive getDive() {
        return dive;
    }

    public Map<String, Long> getMeasuresStates() {
        return measuresStates;
    }

    /**
     * Démarre un enregistrement de plongée.
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
     * Donne l'information de connexion MavLink.
     *
     * @return Timestamp du dernier message reçus
     */
    public long getMavLinkConnection() {
        return mavLinkConnection;
    }

    /**
     * Démarre le thread de lecture de flux MavLink.
     */
    @Override
    public void start() {
        this.mavLinkMessagesThread.start();
    }

    /**
     * Stop le thread de lecture de flux MavLink.
     */
    @Override
    public void stop() {
        this.mavLinkConnection = 0;
        this.mavLinkMessagesThread.interrupt();
    }

    private class MavLinkMessagesThreadWorker implements Runnable {


        private void computeMavLinkMessage(long timestamp, MAVLinkMessage mavLinkMessage) {
            // If Dive doesn't exist
            if (dive == null) {
                dive = new Dive();
                currentPos = new Position();
            }

            // If the MAVLinkMessage is an accurate GPS_SENSOR message
            // And if the drone was previously diving
            mavLinkConnection = timestamp;
            switch (mavLinkMessage.messageType) {
                case msg_gps_raw_int.MAVLINK_MSG_ID_GPS_RAW_INT: // ID -> msg_global_position_int -> GPS SIGNAL RECEIVED!
                    gpsReceived(timestamp, (msg_gps_raw_int) mavLinkMessage);
                    break; // GPS
                case msg_scaled_imu.MAVLINK_MSG_ID_SCALED_IMU:
                    imuReceived(timestamp, (msg_scaled_imu) mavLinkMessage);
                    break;
                case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
                    attitudeReceived(timestamp, (msg_attitude) mavLinkMessage);
                    break; //IMU
                case msg_scaled_pressure2.MAVLINK_MSG_ID_SCALED_PRESSURE2:
                    pressureReceived(timestamp, (msg_scaled_pressure2) mavLinkMessage);
                    break; // Pressure
                case msg_scaled_pressure3.MAVLINK_MSG_ID_SCALED_PRESSURE3:
                    temperatureReceived(timestamp, (msg_scaled_pressure3) mavLinkMessage);
                    break; //Temperature
                default:
                    break;
            }
        }

        private void pressureReceived(long timestamp, msg_scaled_pressure2 pressureMessage) {
            LOGGER.log(Level.INFO, "Attitude [timestamp: " + timestamp + ", time: " + pressureMessage.time_boot_ms + "]");
            Pressure pressure = Pressure.build(timestamp, pressureMessage);

            currentPos.setPressure(pressure);
            updateState(PRESSURE_SENSOR, pressure.getTimestamp());
        }

        private void temperatureReceived(long timestamp, msg_scaled_pressure3 temperatureMessage) {
            LOGGER.log(Level.INFO, "Attitude [timestamp: " + timestamp + ", time: " + temperatureMessage.time_boot_ms + "]");
            Temperature temperature = Temperature.build(timestamp, temperatureMessage);

            currentPos.add(temperature);
            updateState(TEMPERATURE_SENSOR, temperature.getTimestamp());
        }

        private void attitudeReceived(long timestamp, msg_attitude attitudeMessage) {
            LOGGER.log(Level.INFO, "Attitude [timestamp: " + timestamp + ", time: " + attitudeMessage.time_boot_ms + "]");
            if (imuBuffer == null) {
                imuBuffer = attitudeMessage;
                return;
            }

            if (imuBuffer instanceof msg_scaled_imu) {
                msg_scaled_imu imuMessage = (msg_scaled_imu) imuBuffer;
                imuBuffer = null;
                IMU imu = IMU.build(timestamp, imuMessage, attitudeMessage);
                processIMUData(imu);
                return;
            }

            imuBuffer = attitudeMessage; // Update msg_scale_imu value
        }

        private void imuReceived(long timestamp, msg_scaled_imu imuMessage) {
            LOGGER.log(Level.INFO, "Attitude [timestamp: " + timestamp + ", time: " + imuMessage.time_boot_ms + "]");
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

            imuBuffer = imuMessage; // Update msg_scale_imu value
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
            LOGGER.log(Level.INFO, "GPS [timestamp: " + timestamp + ", time: " + gpsMessage.time_usec + "]");
            if (gpsMessage.fix_type < FIX_THRESHOLD) {
                return; // IGNORE the value : lack of precision.
            }
            processGPSData(fr.onema.lib.sensor.position.GPS.build(timestamp, gpsMessage));
        }

        private void processGPSData(GPS gps) {
            long timestamp = gps.getTimestamp();

            if (inDive) {
                currentPos.setGps(gps);
                inDive = false;
                dive.endDive(currentPos);
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
                }
            }
        }

    }

}
