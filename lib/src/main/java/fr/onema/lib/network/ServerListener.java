package fr.onema.lib.network;

import fr.onema.lib.worker.MessageWorker;
import fr.onema.lib.worker.Worker;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.*;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerListener implements Worker {
    private static final Logger LOGGER = Logger.getLogger(ServerListener.class.getName());
    private final int port;
    private byte[] buf;
    private DatagramPacket datagramPacket;
    private DatagramSocket datagramSocket = null;
    private Thread listener;
    private MessageWorker messageWorker = new MessageWorker();
    private long firstTimestamp = -1;
    private long messageTimestamp = -1;

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
    private void startThread() {
        listener = new Thread(() -> {
            while (!Thread.interrupted()) {
                buf = new byte[265];
                datagramPacket = new DatagramPacket(buf, buf.length);
                try {
                    datagramSocket.receive(datagramPacket);
                    MAVLinkReader reader = new MAVLinkReader();
                    MAVLinkMessage mesg = reader.getNextMessage(datagramPacket.getData(), datagramPacket.getLength());
                    if (!testValidityMavlinkMessage(mesg)) {
                        LOGGER.log(Level.INFO, "Message Dropped [timestamp: " + getTimestamp(mesg) + " < " + messageTimestamp + "]");
                        continue;
                    }
                    while (mesg != null && reader.nbUnreadMessages() != 0) {
                        this.messageWorker.newMessage(messageTimestamp, mesg);
                        mesg = reader.getNextMessageWithoutBlocking();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e){
                    Thread.currentThread().interrupt();
                    if(!datagramSocket.isClosed()) {
                        LOGGER.log(Level.SEVERE, e.getMessage(), e);
                    }
                }
            }
            datagramSocket.close();
        });
        listener.start();
    }

    private boolean testValidityMavlinkMessage(MAVLinkMessage msg) {
        if (firstTimestamp == -1) {
            firstTimestamp = getFirstTimestamp(msg);
            messageTimestamp = firstTimestamp;
            return true;
        }

        long timestamp = getTimestamp(msg);
        if (timestamp >= messageTimestamp) {
            messageTimestamp = timestamp;
            return true;
        }
        return false;
    }

    private long getFirstTimestamp(MAVLinkMessage msg) {
        if (msg instanceof msg_gps_raw_int) {
            return ((msg_gps_raw_int) msg).time_usec;
        }

        return System.currentTimeMillis() - getBootTime(msg);
    }

    private long getTimestamp(MAVLinkMessage msg) {
        if (msg instanceof msg_gps_raw_int) {
            return ((msg_gps_raw_int) msg).time_usec;
        }

        return firstTimestamp + getBootTime(msg);
    }

    private long getBootTime(MAVLinkMessage msg) {
        if (msg instanceof msg_scaled_imu) {
            return ((msg_scaled_imu) msg).time_boot_ms;
        } else if (msg instanceof msg_scaled_pressure2) {
            return ((msg_scaled_pressure2) msg).time_boot_ms;
        } else if (msg instanceof msg_scaled_pressure3) {
            return ((msg_scaled_pressure3) msg).time_boot_ms;
        } else if (msg instanceof msg_attitude) {
            return ((msg_attitude) msg).time_boot_ms;
        }
        return 0;
    }


    /**
     * Permet de démarrer la thread
     */
    @Override
    public void start() {
        try {
            openConnexion();
            startThread();
            this.messageWorker.start();
        } catch (SocketException e) {
            throw new IllegalStateException(e);
        }
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
     *
     * @throws SocketException en cas de problème lors de l'écoute du port spécifié
     */
    public void openConnexion() throws SocketException {
        datagramSocket = new DatagramSocket(port);
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
