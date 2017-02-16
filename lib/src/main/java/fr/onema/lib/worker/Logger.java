package fr.onema.lib.worker;


import fr.onema.lib.file.FileManager;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;

/**
 * Classe de logging, affiche les informations relatives à une plongée pour traitement ultérieur.
 *
 * @author loics
 * @since 15-02-2017
 */
public class Logger implements Worker {

    private static final long RANGE = 250;

    private final FileManager fileManager;
    private final BlockingDeque<MAVLinkMessage> messages = new LinkedBlockingDeque<>();
    private final Thread loggerThread = new Thread(new LoggerWorker());

    // SENSORS
    private GPS gps;
    private IMU imu;
    private Pressure pressure;
    private msg_scaled_imu scaledImu;
    private msg_attitude attitude;
    private String lastInfoReceived;
    private long lastMessage = 0;

    /**
     * Créer un Logger.
     *
     * @param fileManager Le FileManager qui va servir à écrire les données issues du logger..
     */
    Logger(FileManager fileManager) {
        this.fileManager = fileManager;
        this.gps = null;
        this.imu = null;
        this.pressure = null;
    }

    /**
     * Ajoute un nouveau message MAVLink à la file des messsages du Logger.
     *
     * @param message Le message MAVLink qui doit être ajouté.
     * @return Vrai si le message à bien été ajouté. Faux sinon.
     */
    boolean newMAVLinkMessage(MAVLinkMessage message) {
        return this.messages.offer(Objects.requireNonNull(message));
    }

    @Override
    public void start() {
        this.loggerThread.start();
    }

    @Override
    public void stop() {
        this.loggerThread.interrupt();
    }

    private class LoggerWorker implements Runnable{

        private void sendCurrentInfos() {
            VirtualizerEntry virtualizerEntry =  new VirtualizerEntry(
                    gps,
                    imu,
                    pressure
            );
            try {
                fileManager.appendVirtualized(virtualizerEntry);
            } catch (IOException e) {
                FileManager.LOGGER.log(Level.SEVERE, e.getMessage());
            }
            gps = null;
            imu = null;
            pressure = null;
            scaledImu = null;
            attitude = null;
            lastMessage = 0;
            lastInfoReceived = "";
        }

        private void processData(long infoTimestamp, String sensorName, int sensorID, MAVLinkMessage message) {
            if((infoTimestamp - lastMessage > RANGE && infoTimestamp != lastMessage)
                    || lastInfoReceived.equals(sensorName)) {
                // create new virtualizer
                sendCurrentInfos();
            }
            switch (sensorID) {
                case msg_gps_raw_int.MAVLINK_MSG_ID_GPS_RAW_INT:
                    gps = GPS.build((msg_gps_raw_int) message);
                    lastMessage = gps.getTimestamp();
                    break;
                case msg_scaled_imu.MAVLINK_MSG_ID_SCALED_IMU:
                    checkIMUScaledBuild((msg_scaled_imu) message);
                    break;
                case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
                    checkIMUAttitudeBuild((msg_attitude) message);
                    break;
                case msg_scaled_pressure.MAVLINK_MSG_ID_SCALED_PRESSURE:
                    pressure = Pressure.build((msg_scaled_pressure) message);
                    lastMessage = pressure.getTimestamp();
                    break;
                default:
                    break;
            }
            lastInfoReceived = sensorName;
        }

        private void checkIMUAttitudeBuild(msg_attitude message) {
            if(scaledImu != null) {
                IMU.build(scaledImu, message);
            } else {
                attitude = message;
            }
        }

        private void checkIMUScaledBuild(msg_scaled_imu message) {
            if(attitude != null) {
                IMU.build(message, attitude);
            } else {
                scaledImu = message;
            }
        }

        @Override
        public void run() {
            try {
                fileManager.openFileForResults();
            } catch (IOException e) {
                FileManager.LOGGER.log(Level.SEVERE, e.getMessage());
            }
            while(!Thread.interrupted()) {
                try {
                    MAVLinkMessage mavLinkMessage = messages.take();
                    switch (mavLinkMessage.messageType) {
                        case msg_gps_raw_int.MAVLINK_MSG_ID_GPS_RAW_INT:
                            msg_gps_raw_int gpsData = (msg_gps_raw_int) mavLinkMessage;
                            processData(gpsData.time_usec, GPS.class.getCanonicalName(), gpsData.messageType, mavLinkMessage);
                            break;
                        case msg_scaled_imu.MAVLINK_MSG_ID_SCALED_IMU:
                            msg_scaled_imu imuData = (msg_scaled_imu)  mavLinkMessage;
                            processData(imuData.time_boot_ms, IMU.class.getCanonicalName(), imuData.messageType, mavLinkMessage);
                            break;
                        case msg_attitude.MAVLINK_MSG_ID_ATTITUDE:
                            msg_attitude attitudeData = (msg_attitude)  mavLinkMessage;
                            processData(attitudeData.time_boot_ms, IMU.class.getCanonicalName(), attitudeData.messageType, mavLinkMessage);
                            break;
                        case msg_scaled_pressure.MAVLINK_MSG_ID_SCALED_PRESSURE:
                            msg_scaled_pressure pressureData = (msg_scaled_pressure) mavLinkMessage;
                            processData(pressureData.time_boot_ms, Pressure.class.getCanonicalName(), pressureData.messageType, mavLinkMessage);
                            break;
                        default:
                            break;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}
