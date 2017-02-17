package fr.onema.lib.network;

import fr.onema.lib.worker.MessageWorker;
import fr.onema.lib.worker.Worker;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_attitude;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerListener implements Worker {
    private final int port;
    private byte[] buf;
    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket = null;
    private long lastReceivedTimestamp = 0;
    private int lastTimeUsec = 0;
    private Thread listener;
    private MessageWorker messageWorker = new MessageWorker();

    /**
     * Constructeur de la classe ServerListener
     *
     * @param port le port d'écoute du server
     */
    public ServerListener(int port) {
        this.port = port;
    }

    /**
     * Permet de démarrer la Thread d'écoute qui réçoit et transmet les mavlink messages
     */
    public void startThread() {
        listener = new Thread(() -> {
            while (!Thread.interrupted()) {
                buf = new byte[1000];
                datagramPacket = new DatagramPacket(buf, buf.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    MAVLinkReader reader = new MAVLinkReader();
                    MAVLinkMessage mesg = reader.getNextMessage(datagramPacket.getData(), datagramPacket.getLength());
                    if (testValidityMavlinkMessage(mesg)) {
                        while (mesg != null) {
                            this.messageWorker.newMessage(lastReceivedTimestamp, mesg);
                            if (reader.nbUnreadMessages() != 0) {
                                mesg = reader.getNextMessageWithoutBlocking();
                            } else {
                                break;
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    //do nothing for now TODO Add logger
                }
            }
        });
        listener.start();
    }

    private boolean testValidityMavlinkMessage(MAVLinkMessage mesg) {
        long tmpTime;
        if (mesg instanceof msg_gps_raw_int) {
            tmpTime = ((msg_gps_raw_int) mesg).time_usec;
            if (lastReceivedTimestamp <= tmpTime) {
                lastReceivedTimestamp = tmpTime;
                return true;
            }
            return false;
        }

        if (mesg instanceof msg_scaled_imu) {
            tmpTime = ((msg_scaled_imu) mesg).time_boot_ms;
        } else if (mesg instanceof msg_scaled_pressure) {
            tmpTime = ((msg_scaled_pressure) mesg).time_boot_ms;
        } else if (mesg instanceof msg_attitude) {
            tmpTime = ((msg_attitude) mesg).time_boot_ms;
        } else {
            tmpTime = 0;
        }

        if (lastTimeUsec <= tmpTime) {
            lastTimeUsec = (int) tmpTime;
            return true;
        }
        return false;
    }


    /**
     * Permet de démarrer la thread
     */
    @Override
    public void start() {
        openConnexion();
        startThread();
        this.messageWorker.start();
    }

    /**
     * Permet d'arreter la thread
     */
    @Override
    public void stop() {
        listener.interrupt();
        datagramSocket.close();
        this.messageWorker.stop();
    }

    /**
     * Initialise les variables nécessaires à la reception des données
     */
    public void openConnexion() {
        try {
            datagramSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            // TODO
        }
    }

    public long getLastReceivedTimestamp() {
        return lastReceivedTimestamp;
    }

    public Thread getListener() {
        return listener;
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public MessageWorker getMessageWorker() {
        return messageWorker;
    }
}
