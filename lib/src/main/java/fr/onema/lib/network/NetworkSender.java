package fr.onema.lib.network;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.concurrent.ArrayBlockingQueue;

public class NetworkSender {
    private final int port;
    private final String host;
    private VirtualizerEntry entry;
    private ArrayBlockingQueue<MAVLinkMessage> queue;
    private DatagramSocket dsocket;
    private Thread sender;
    byte[] buffer;
    DatagramPacket packet;
    InetAddress hostAddress;

    /**
     * Constructeur de la classe NetworkSender
     * @param port le port de l'hôte
     * @param host l'adresse de l'hôte
     */
    public NetworkSender(int port, String host) {
        this.port = port;
        this.host = host;
        queue = new ArrayBlockingQueue<>(100);
        startThread();
    }

    /**
     * Permet d'ajouter une virtualizerEntry
     * @param entry Un champ de type VirtualizerEntry
     */
    public void add(VirtualizerEntry entry) {
        this.entry = entry;
            if (entry.getHasGPS() == true) {
               MAVLinkMessage msgGPS = entry.getGPSMessage();
               System.out.println("gps");
                try {
                    queue.put(msgGPS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        MAVLinkMessage msgIMU = entry.getIMUMessage();
        MAVLinkMessage msgPressure = entry.getPressureMessage();
        MAVLinkMessage msgTemperature = entry.getTemperatureMessage();
        try {
            queue.put(msgIMU);
            queue.put(msgPressure);
            queue.put(msgTemperature);
        } catch (InterruptedException e) {
            // TODO
        }
    }

    /**
     * Envoi un message MavLink au destinataire
     */
    public void send(MAVLinkMessage msg) throws IOException {
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
        packet = new DatagramPacket(buffer, buffer.length);
        hostAddress = InetAddress.getByName("127.0.0.1");
    }

    /**
     * Permet de fermer la connexion avec le destinataire
     */
    public void closeConnection() {
        dsocket.close();
    }

    /**
     * Permet de récupérer le port
     * @return le port
     */
    public int getPort() {
        return port;
    }

    /**
     * permet de récupérer l'adresse hôte
     * @return l'adresse hôte
     */
    public String getHost() {
        return host;
    }

    /**
     * Demarre la thread d'envoi de messages

     */
    public void startThread() {
        sender = new Thread(() -> {
            try {
                openConnection();
            } catch (IOException e) {
                // TODO
            }
            while (!Thread.interrupted()) {
                MAVLinkMessage msg;
                try {
                    msg = queue.take();
                    send(msg);
                } catch (InterruptedException e) {
                    // TODO
                } catch (IOException e) {
                    // TODO
                }
            }
            closeConnection();
        });
        sender.start();
    }

    /**
     * Getter pour la thread sender
     * @return la thread
     */
    public Thread getSender() {
        return sender;
    }

    /**
     * getter de la blocking queue
     * @return la blocking queue
     */
    public ArrayBlockingQueue getQueue() {
        return queue;
    }

    /**
     * Getter de la DatagramSocket
     * @return la datagram socket
     */
    public DatagramSocket getDsocket() {
        return dsocket;
    }


    public void interruptThread() {
        this.sender.interrupt();
    }
}
