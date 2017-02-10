package fr.onema.lib.worker;

import fr.onema.lib.drone.Dive;
import fr.onema.lib.drone.Measure;
import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_global_position_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author loics
 * @since 09-02-2017
 */
public class MessageWorker implements Worker {

    private final Dive dive;
    private final BlockingQueue<Measure> measuresWaiting = new ArrayBlockingQueue<>(Integer.MAX_VALUE);
    private final Map<String, Long> measuresStates = new HashMap<>();
    private final BlockingQueue<MAVLinkMessage> messages = new ArrayBlockingQueue<>(Integer.MAX_VALUE);
    private final Thread mavLinkMessagesThread = new Thread(new MavLinkMessagesThreadWorker());
    private Boolean inDive;
    private Position currentPos;
    private GPS lastPos;
    private Boolean mavLinkConnection;

    public MessageWorker() {
        this.dive = new Dive();
        this.inDive = false;
        this.currentPos = new Position();
        this.lastPos = null;
        this.mavLinkConnection = false;
    }

    public void computeMavLinkMessage(MAVLinkMessage mavLinkMessage) throws InterruptedException {
        this.mavLinkConnection = true;

        // If Dive doesn't exist
        if (!this.dive.isInitiated()) {
            this.dive.construct(System.currentTimeMillis());
            this.currentPos.init(); //TODO Default constructor
        }

        // If the MAVLinkMessage is an accurate GPS message
        // And if the drone was previously diving
        switch (mavLinkMessage.messageType) { // TODO replace this ugly SWITCH-CASE block
            case msg_global_position_int.MAVLINK_MSG_ID_GLOBAL_POSITION_INT: // ID -> msg_global_position_int -> GPS SIGNAL RECEIVED!
                msg_global_position_int gpsData = (msg_global_position_int) mavLinkMessage;
                GPS gps = GPS.build(gpsData); //TODO check if msg.GPS is accurate
                if (this.inDive && !this.currentPos.hasGPS()) { // If no GPS signal prior the message
                    this.currentPos.setGps(gps); // Possible duplicate (see UC4)
                    this.dive.add(currentPos, measuresWaiting);
                    measuresWaiting.clear();
                    this.currentPos.init();
                    this.dive.endDive();
                    this.dive.construct(System.currentTimeMillis());
                }
                if (this.currentPos.hasGPS()) {
                    this.dive.add(currentPos, measuresWaiting);
                }
                this.currentPos.setGps(gps); // Possible duplicate (see UC4)
                this.lastPos = gps;
                updateState(gps.getClass().getCanonicalName(), gpsData.time_boot_ms);
                break;
            case msg_scaled_imu.MAVLINK_MSG_ID_SCALED_IMU:
                msg_scaled_imu imuData = (msg_scaled_imu) mavLinkMessage;
                IMU imu = IMU.build(imuData);
                if (this.currentPos.hasIMU()) {
                    this.dive.add(currentPos, measuresWaiting);
                    this.currentPos.init();
                }
                this.currentPos.setImu(imu);
                updateState(imu.getClass().getCanonicalName(), imuData.time_boot_ms);
                break; //IMU
            case msg_scaled_pressure.MAVLINK_MSG_ID_SCALED_PRESSURE:
                msg_scaled_pressure pressureData = (msg_scaled_pressure) mavLinkMessage;
                Pressure pressure = Pressure.build(pressureData);
                if (this.currentPos.hasPressure() && (this.currentPos.hasGPS() || this.currentPos.hasIMU())) {
                    this.dive.add(currentPos, measuresWaiting);
                    this.currentPos.init();
                }
                this.currentPos.setPressure(pressure);
                updateState(Pressure.class.getCanonicalName(), pressureData.time_boot_ms);
                add(Temperature.build(pressureData));
                break; //PRESSURE
            default:
                // Got nothing valid.
                break;
        }

    }

    public void clearWaitingList() {
        synchronized (this.dive) {
            this.measuresWaiting.clear();
        }
    }

    public void add(Temperature temperature) throws InterruptedException {
        try {
            this.measuresWaiting.put(temperature);
        } catch (InterruptedException e) {
            throw e;
        }

    }

    public void startRecording() {
    }

    public void stopRecording() {
    }

    private void updateState(String sensor, long timestamp) {
        this.measuresStates.put(sensor, timestamp);
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
        this.mavLinkConnection = false;
        this.mavLinkMessagesThread.interrupt();
    }

    private class MavLinkMessagesThreadWorker implements Runnable {

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
