package fr.onema.lib.network;

import fr.onema.lib.worker.MessageWorker;
import fr.onema.lib.worker.Worker;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Classe permettant de lancer la thread d'écoute
 * Appeler la méthode start pour lancer la connexion et la thread
 */
public class ServerListener implements Worker {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerListener.class.getName());
    private final int port;
    private DatagramSocket datagramSocket = null;
    private Thread listener;
    private MessageWorker messageWorker = new MessageWorker();
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
        listener = new Thread(this::worker);
        listener.start();
    }

    private void worker() {
        byte[] buf = new byte[265];
        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
        while (!Thread.interrupted()) {
            try {
                datagramSocket.receive(datagramPacket);
                MAVLinkReader reader = new MAVLinkReader();
                MAVLinkMessage mesg = reader.getNextMessage(datagramPacket.getData(), datagramPacket.getLength());
                if (mesg == null) {
                    continue;
                }

                if (testValidityMavlinkMessage(mesg)) {
                    this.messageWorker.newMessage(System.currentTimeMillis(), mesg);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                Thread.currentThread().interrupt();
                if (!datagramSocket.isClosed()) {
                    LOGGER.error(e.getMessage());
                    LOGGER.debug(e.getMessage(), e);
                }
            }
        }
        datagramSocket.close();
    }

    /**
     * Compare les timestamps et déduit l'état du flux Mavlink
     *
     * @param msg Un message Mavlink
     * @return Le résultat du test
     */
    boolean testValidityMavlinkMessage(MAVLinkMessage msg) {
        long timestamp = getTimestamp(msg);
        if (timestamp >= messageTimestamp) {
            messageTimestamp = timestamp;
            return true;
        }
        return false;
    }

    /**
     * Retourne le timestamp à associé à un message
     *
     * @param msg Un message Mavlink
     * @return Le timestamp du message Mavlink
     */
    public long getTimestamp(MAVLinkMessage msg) {
        if (msg instanceof msg_gps_raw_int) {
            if (((msg_gps_raw_int) msg).time_usec != 0 && messageTimestamp != -1) {
                return messageTimestamp;
            } else {
                return 0;
            }
        }
        return getBootTime(msg);
    }

    long getBootTime(MAVLinkMessage msg) {
        if (msg instanceof msg_raw_imu) {
            return ((msg_raw_imu) msg).time_usec / 1_000;
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
    private void openConnexion() throws SocketException {
        datagramSocket = new DatagramSocket(port);
    }

    Thread getListener() {
        return listener;
    }

    DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    /**
     * Getter du MessageWorker
     *
     * @return Le MessageWorker associé à l'instance du serveur d'écoute
     */
    public MessageWorker getMessageWorker() {
        return messageWorker;
    }
}
