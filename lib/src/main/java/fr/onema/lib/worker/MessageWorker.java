package fr.onema.lib.worker;

import fr.onema.lib.drone.Dive;
import fr.onema.lib.drone.Measure;
import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Classe worker de messages. Récupère les messages depuis le ServerListerner.
 * Un fois les messages récupérés, ils sont traités et envoyés aux positions des plongées.
 *
 * @author loics
 * @since 09-02-2017
 */
public class MessageWorker implements Worker {

    private static final long INTERVAL = 250;
    private static final long FIX_THRESHOLD = 5;

    // The lists of measures fetched from the MAVLinkMessages
    private final BlockingQueue<Measure> measuresWaiting = new ArrayBlockingQueue<>(50);
    // Represents the current states of the sensors. This map is updated each time a sensor produces data
    private final Map<String, Long> measuresStates = new HashMap<>();
    // List that contains all the received MAVLinkMessages waiting to be treated by the worker
    private final BlockingQueue<MAVLinkMessage> messages = new ArrayBlockingQueue<>(200);
    // Worker thread that treats the MAVLinkMessages
    private final Thread mavLinkMessagesThread = new Thread(new MavLinkMessagesThreadWorker());
    // Represents the dive currently associated
    private Dive dive;
    private String lastMessageType;

    private Boolean inDive;
    private Position currentPos;
    private GPS lastPos;
    private Boolean mavLinkConnection;
    private long firstInfo = 0;

    private final AtomicReference<MAVLinkMessage> bufferMavLink = new AtomicReference<>();

    /**
     * Constructeur de MessageWorker
     * Attention, après l'instanciation la pluspars des champs seront encore null. Ils seront créés en cours de
     * communication avec avec le serveur.
     */
    public MessageWorker() {
        this.dive = null;
        this.inDive = false;
        this.currentPos = null;
        this.lastPos = null;
        this.mavLinkConnection = false;
    }

    /**
     * Ajoute un message MavLink à la liste des messages à traiter.
     *
     * @param message Le message MavLink à traiter.
     * @throws InterruptedException En cas d'intérruption du thread courant.
     */
    void newMessage(MAVLinkMessage message) throws InterruptedException {
        this.messages.put(Objects.requireNonNull(message));
    }

    /**
     * Vide la liste des mesures en attentes.
     */
    void clearWaitingList() {
        this.measuresWaiting.clear();
    }

    /**
     * Indique si la liste des mesures est vide.
     *
     * @return Vrai si la liste est vide. Sinon faux.
     */
    boolean isWaitingListEmpty() {
        return this.measuresWaiting.isEmpty();
    }

    public Map<String, Long> getMeasuresStates() {
        return measuresStates;
    }

    /**
     * Ajoute une objet Temperature à la liste des mesures.
     * Ces mesures seront associées à une position.
     *
     * @param temperature La Temperature à ajouter.
     * @throws InterruptedException En cas d'intérruption du thread courant.
     */
    public void add(Temperature temperature) throws InterruptedException {
        this.measuresWaiting.put(temperature);
    }

    /**
     * Démarre un enregistrement de plongée.
     */
    public void startRecording() {
        this.dive.startRecording(System.currentTimeMillis());
    }

    /**
     * Arrête un enregistrement de plongée.
     */
    public void stopRecording() {
        this.dive.stopRecording(System.currentTimeMillis());
    }

    /**
     * Donne l'information de connexion MavLink.
     *
     * @return Vrai si la connexion est établie. Faux sinon. (Experimental)
     */
    public Boolean getMavLinkConnection() {
        return mavLinkConnection;
    }

    /**
     * Donne la dernière position GPS connue.
     *
     * @return La dernière position GPS connue.
     */
    public GPS getLastPos() {
        return lastPos;
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
        this.mavLinkConnection = false;
        this.mavLinkMessagesThread.interrupt();
    }

    private class MavLinkMessagesThreadWorker implements Runnable {


        private void computeMavLinkMessage(MAVLinkMessage mavLinkMessage) throws Exception {

            // If Dive doesn't exist
            if (dive == null) {
                dive = new Dive(System.currentTimeMillis());
                currentPos = new Position(System.currentTimeMillis());
            }

            // If the MAVLinkMessage is an accurate GPS message
            // And if the drone was previously diving
            mavLinkConnection = true;
            switch (mavLinkMessage.messageType) {
                case msg_gps_raw_int.MAVLINK_MSG_ID_GPS_RAW_INT: // ID -> msg_global_position_int -> GPS SIGNAL RECEIVED!
                    msg_gps_raw_int gpsData = (msg_gps_raw_int) mavLinkMessage;
                    if (gpsData.fix_type < FIX_THRESHOLD)
                        return; // IGNORE the value : lack of precision.
                    addOrCreateToPosition(gpsData.time_usec, gpsData.getClass().getCanonicalName());
                    processGPSData(GPS.build(gpsData), gpsData);
                    break; // GPS
                case msg_scaled_imu.MAVLINK_MSG_ID_SCALED_IMU:
                    if (bufferMavLink.get() == null) {
                        bufferMavLink.set(mavLinkMessage);
                        break;
                    } else if (bufferMavLink.get() instanceof msg_attitude) {

                        msg_scaled_imu imuData = (msg_scaled_imu) mavLinkMessage;
                        addOrCreateToPosition(imuData.time_boot_ms, imuData.getClass().getCanonicalName());
                        IMU imu = IMU.build(imuData, (msg_attitude) bufferMavLink.get());
                        bufferMavLink.set(null);
                        processIMUData(imu, imuData);
                        break; //IMU
                    }
                    bufferMavLink.set(mavLinkMessage); // Update msg_scale_imu value
                    break;
                case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
                    if (bufferMavLink.get() == null) {
                        bufferMavLink.set(mavLinkMessage);
                        break;
                    } else if (bufferMavLink.get() instanceof msg_scaled_imu) {

                        msg_attitude attitudeData = (msg_attitude) mavLinkMessage;
                        addOrCreateToPosition(attitudeData.time_boot_ms, attitudeData.getClass().getCanonicalName());
                        msg_scaled_imu message = (msg_scaled_imu) bufferMavLink.get();
                        IMU imu = IMU.build(message, attitudeData);
                        bufferMavLink.set(null);
                        processIMUData(imu, message);
                        break; //IMU
                    }
                    bufferMavLink.set(mavLinkMessage); // Update msg_scale_imu value
                    break;
                case msg_scaled_pressure.MAVLINK_MSG_ID_SCALED_PRESSURE:
                    msg_scaled_pressure pressureData = (msg_scaled_pressure) mavLinkMessage;
                    addOrCreateToPosition(pressureData.time_boot_ms, pressureData.getClass().getCanonicalName());
                    Pressure pressure = Pressure.build(pressureData);
                    processPressureData(pressure, pressureData);
                    break; //PRESSURE
                default:
                    // Got nothing valid.
                    break;
            }

        }

        // If the measure is within the right (250ms). We will add it to the current position.
        // If we get the same measure kind within the 250ms we will create another position to store it in.
        private void addOrCreateToPosition(long timestamp, String sensorName) {
            if (firstInfo == 0) {
                firstInfo = timestamp;
                lastMessageType = sensorName;
            }
            if ((timestamp - firstInfo < INTERVAL) && timestamp - firstInfo >= 0) {
                if (lastMessageType.equals(sensorName)) { // same info within the given interval
                    // Send the current position
                    dive.add(currentPos);
                    // Then create a new position
                    currentPos = new Position(timestamp);
                    firstInfo = timestamp;
                }
            } else {
                // We create a new position and add the information
                dive.add(currentPos);
                currentPos = new Position(timestamp);
                firstInfo = timestamp;
            }
        }

        private void processPressureData(Pressure pressure, msg_scaled_pressure pressureData) throws InterruptedException {
            if ((currentPos.hasGPS() || currentPos.hasIMU())) {
                dive.add(currentPos);
            }
            add(Temperature.build(pressureData));
            updateState(pressure.getClass().getCanonicalName(), pressureData.time_boot_ms);
        }

        private void processIMUData(IMU imu, msg_scaled_imu imuData) {
            if (currentPos.hasIMU()) {
                dive.add(currentPos);
            }
            currentPos.setImu(imu);
            updateState(imu.getClass().getCanonicalName(), imuData.time_boot_ms);
        }

        private void processGPSData(GPS gps, msg_gps_raw_int gpsData) throws Exception {
            if (inDive && !currentPos.hasGPS()) { // If no GPS signal prior the message
                currentPos.setGps(gps); // Possible duplicate (see UC4)
                measuresWaiting.clear();
                dive.endDive(currentPos);
                dive = new Dive(gpsData.time_usec);
            } else if (currentPos.hasGPS()) {
                dive.add(currentPos);
            }
            currentPos.setGps(gps); // Possible duplicate (see UC4)
            lastPos = gps;
            updateState(gps.getClass().getCanonicalName(), gpsData.time_usec);
        }

        private void updateState(String sensor, long timestamp) {
            measuresStates.put(sensor, timestamp);
        }

        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    mavLinkConnection = false;
                    computeMavLinkMessage(messages.take());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
