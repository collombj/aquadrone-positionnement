package fr.onema.lib.network;

import fr.onema.lib.worker.Worker;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerListener implements Worker {
    private final int port;
    private long lastReceivedTimestamp = 0;
    private Thread listener;
    byte[] buf = new byte[1000];
    DatagramPacket dgp;
    DatagramSocket sk = null;

    /**
     * Constructeur de la classe ServerListener
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
            while(!Thread.interrupted()) {
                buf = new byte[1000];
                dgp = new DatagramPacket(buf, buf.length);
                openConnexion();
                try {
                    sk.receive(dgp);
                    byte[] b = dgp.getData();

                    // TODO timestamp
                    MAVLinkReader reader = new MAVLinkReader();
                    MAVLinkMessage mesg;
                    mesg = reader.getNextMessage(dgp.getData(), dgp.getLength());
                    while (mesg != null) {
                        System.out.println(mesg.toString());
                        if (reader.nbUnreadMessages() != 0) {
                            mesg = reader.getNextMessageWithoutBlocking();

                        } else {
                            break;
                        }
                    }
                } catch (IOException e) {
                    // TODO
                }
            }
        });
        listener.start();
    }

    /**
     * Permet de démarrer la thread
     */
    @Override
    public void start() {
        openConnexion();
        startThread();
    }

    /**
     * Permet d'arreter la thread
     */
    @Override
    public void stop() {
        listener.interrupt();
        sk.close();
    }

    /**
     * Initialise les variables nécessaires à la reception des données
     */
    public void openConnexion() {
        try {
            sk = new DatagramSocket(port);
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

    public DatagramSocket getSk() {
        return sk;
    }
}
