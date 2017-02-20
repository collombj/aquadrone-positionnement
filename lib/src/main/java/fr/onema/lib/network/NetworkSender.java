package fr.onema.lib.network;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkSender {
    private static final Logger LOGGER = Logger.getLogger(NetworkSender.class.getName());
    private final int port;
    private final String host;
    private byte[] buffer;
    private InetAddress hostAddress;
    private final ArrayBlockingQueue<MAVLinkMessage> queue;
    private DatagramSocket dsocket;
    private Thread sender;
    private long firstTimestamp = -1;

    /**
     * Constructeur de la classe NetworkSender
     *
     * @param port le port de l'hôte
     * @param host l'adresse de l'hôte
     */
    public NetworkSender(int port, String host) throws IOException {
        this.port = port;
        this.host = host;
        queue = new ArrayBlockingQueue<>(100);
        startThread();
        openConnection();
    }

    /**
     * Permet d'ajouter une virtualizerEntry
     *
     * @param entry Un champ de type VirtualizerEntry
     */
    public void add(VirtualizerEntry entry) {
        if (firstTimestamp == -1) {
            firstTimestamp = entry.getTimestamp();
        }

        if (entry.getHasGPS()) {
            MAVLinkMessage msgGPS = entry.getGPSMessage();
            try {
                queue.put(msgGPS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        MAVLinkMessage msgIMU = entry.getIMUMessage(entry.getTimestamp() - firstTimestamp);
        MAVLinkMessage msgAttitude = entry.getAttitudeMessage(entry.getTimestamp() - firstTimestamp);
        MAVLinkMessage msgPressure = entry.getPressureMessage(entry.getTimestamp() - firstTimestamp);
        MAVLinkMessage msgTemperature = entry.getTemperatureMessage(entry.getTimestamp() - firstTimestamp);
        try {
            queue.put(msgIMU);
            queue.put(msgAttitude);
            queue.put(msgPressure);
            queue.put(msgTemperature);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Envoi un message MavLink au destinataire
     */
    private void send(MAVLinkMessage msg) throws IOException {
        String msgFormatted = msg.toString();
        LOGGER.log(Level.INFO, msgFormatted);
        buffer = msg.encode();
        DatagramPacket out = new DatagramPacket(buffer, buffer.length, hostAddress, port);
        dsocket.send(out);
    }

    /**
     * Permet d'ouvrir la connexion avec le destinataire
     */
    public void openConnection() throws IOException {
        dsocket = new DatagramSocket();
        buffer = new byte[1000];
        hostAddress = InetAddress.getByName(host);
    }

    /**
     * Permet de fermer la connexion avec le destinataire
     */
    public void closeConnection() {
        this.sender.interrupt();
    }

    /**
     * Permet de récupérer le port
     *
     * @return le port
     */
    public int getPort() {
        return port;
    }

    /**
     * permet de récupérer l'adresse hôte
     *
     * @return l'adresse hôte
     */
    public String getHost() {
        return host;
    }

    /**
     * Demarre la thread d'envoi de messages
     */
    private void startThread() {
        sender = new Thread(() -> {
            while (!Thread.interrupted() || (Thread.interrupted() && !queue.isEmpty())) {
                MAVLinkMessage msg;
                try {
                    msg = queue.take();
                    send(msg);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.SEVERE, e.getMessage(), e);
                }
            }
            dsocket.close();
        });
        sender.start();
    }

    /**
     * Getter pour la thread sender
     *
     * @return la thread
     */
    public Thread getSender() {
        return sender;
    }

    /**
     * getter de la blocking queue
     *
     * @return la blocking queue
     */
    public ArrayBlockingQueue getQueue() {
        return queue;
    }

    /**
     * Getter de la DatagramSocket
     *
     * @return la datagram socket
     */
    public DatagramSocket getDsocket() {
        return dsocket;
    }
}
