package fr.onema.lib.worker;

import fr.onema.lib.drone.Dive;
import fr.onema.lib.drone.Measure;
import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 * @author loics
 * @since 09-02-2017
 */
public class MessageWorker implements Worker {

    private static final long INTERVAL = 250;
    private static final long FIX_THRESHOLD = 5;
    // The lists of measures fetched from the MAVLinkMessages
    private final BlockingQueue<Measure> measuresWaiting = new ArrayBlockingQueue<>(Integer.MAX_VALUE);
    // Represents the current states of the sensors. This map is updated each time a sensor produces data
    private final Map<String, Long> measuresStates = new HashMap<>();
    // List that contains all the received MAVLinkMessages waiting to be treated by the worker
    private final BlockingQueue<MAVLinkMessage> messages = new ArrayBlockingQueue<>(Integer.MAX_VALUE);
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

    public MessageWorker() {
        this.dive = null;
        this.inDive = false;
        this.currentPos = null;
        this.lastPos = null;
        this.mavLinkConnection = false;
    }

    public void newMessage(MAVLinkMessage message) throws InterruptedException {
        this.messages.put(Objects.requireNonNull(message));
    }

    public void clearWaitingList() {
        this.measuresWaiting.clear();
    }

    public void add(Temperature temperature) throws InterruptedException {
        this.measuresWaiting.put(temperature);
    }

    public void startRecording() {
        this.dive.startRecording(System.currentTimeMillis());
    }

    public void stopRecording() {
        this.dive.stopRecording(System.currentTimeMillis());
    }

    public Boolean getMavLinkConnection() {
        return mavLinkConnection;
    }

    public GPS getLastPos() {
        return lastPos;
    }

    @Override
    public void start() {
        this.mavLinkMessagesThread.start();
    }

    @Override
    public void stop() {
        this.mavLinkMessagesThread.interrupt();
    }

    private class MavLinkMessagesThreadWorker implements Runnable {


        private void computeMavLinkMessage(MAVLinkMessage mavLinkMessage) throws InterruptedException {

            // If Dive doesn't exist
            if (dive == null) {
                dive = new Dive(System.currentTimeMillis());
                currentPos = new Position(System.currentTimeMillis());
            }

            // If the MAVLinkMessage is an accurate GPS message
            // And if the drone was previously diving
            switch (mavLinkMessage.messageType) {
                case msg_gps_raw_int.MAVLINK_MSG_ID_GPS_RAW_INT: // ID -> msg_global_position_int -> GPS SIGNAL RECEIVED!
                    msg_gps_raw_int gpsData = (msg_gps_raw_int) mavLinkMessage;
                    if (gpsData.fix_type < FIX_THRESHOLD)
                        return; // IGNORE the value coz not precise enough.
                    addOrCreateToPosition(gpsData.time_usec, gpsData.getClass().getCanonicalName());
                    processGPSData(GPS.build(gpsData), gpsData);
                    break; // GPS
                case msg_scaled_imu.MAVLINK_MSG_ID_SCALED_IMU:
                    msg_scaled_imu imuData = (msg_scaled_imu) mavLinkMessage;
                    addOrCreateToPosition(imuData.time_boot_ms, imuData.getClass().getCanonicalName());
                    IMU imu = IMU.build(imuData);
                    processIMUData(imu, imuData);
                    break; //IMU
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
            if (currentPos.hasPressure() && (currentPos.hasGPS() || currentPos.hasIMU())) {
                dive.add(currentPos);
            }
            currentPos.setPressure(pressure);
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

        private void processGPSData(GPS gps, msg_gps_raw_int gpsData) {
            if (inDive && !currentPos.hasGPS()) { // If no GPS signal prior the message
                currentPos.setGps(gps); // Possible duplicate (see UC4)
                dive.add(currentPos);
                measuresWaiting.clear();
                dive.endDive();
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
                }
            }
        }

    }

}
