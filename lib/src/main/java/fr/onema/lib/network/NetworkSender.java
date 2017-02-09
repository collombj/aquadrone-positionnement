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
    private ArrayBlockingQueue queue;
    private DatagramChannel client;
    private final static byte buffer[] = new byte[8000];

    /**
     * Constructeur de la classe NetworkSender
     * @param port le port de l'hôte
     * @param host l'adresse de l'hôte
     */
    public NetworkSender(int port, String host) {
        this.port = port;
        this.host = host;
        queue = new ArrayBlockingQueue(100);
    }

    /**
     * Permet d'ajouter une virtualizerEntry
     * @param entry Un champ de type VirtualizerEntry
     */
    public void add(VirtualizerEntry entry) {
        this.entry = entry;
        Thread listener = new Thread(){
            public void run() {
                while (!Thread.interrupted()) {
                    if (entry.getHasGPS() == true) {
                        MAVLinkMessage msgGPS = entry.getGPSMessage();
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
                        e.printStackTrace();
                    }
                }
                closeConnection();
            }
        };
        Thread sender = new Thread(){
            public void run() {
                try {
                    openConnection();
                } catch (IOException e) {

                }
                while (!Thread.interrupted()) {
                    MAVLinkMessage msg;
                    try {
                        msg = (MAVLinkMessage) queue.take();
                        send(msg);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                closeConnection();
            }
        };
        new Thread(listener).start();
        new Thread(sender).start();
    }

    /**
     * Envoi un message MavLink au destinataire
     */
    public void send(MAVLinkMessage msg) {
        ByteBuffer buff = Charset.forName("utf8").encode(msg.toString());
        InetSocketAddress dest= new InetSocketAddress(host,port);
        try {
            client.send(buff,dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Permet d'ouvrir la connexion avec le destinataire
     */
    public void openConnection() throws IOException {
        client = DatagramChannel.open();
        client.bind(null);
    }

    /**
     * Permet de fermer la connexion avec le destinataire
     */
    public void closeConnection() {
        try {
            client.close();
        } catch (IOException e) {

        }
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
     * Permet de récupérer le socket
     * @return le socket
     */
    public DatagramChannel getChannel() {
        return client;
    }
}
