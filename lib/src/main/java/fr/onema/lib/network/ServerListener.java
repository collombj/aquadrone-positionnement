package fr.onema.lib.network;

import fr.onema.lib.worker.Worker;
import org.mavlink.MAVLinkReader;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class ServerListener implements Worker {
    private final int port;
    byte[] buf;
    DatagramPacket dgp;
    DatagramSocket sk = null;
    private long lastReceivedTimestamp = 0;
    private int timeUsec = 0;
    private Thread listener;
    private boolean gpsValid = false;
    private boolean pressureValid = false;

    /**
     * Constructeur de la classe ServerListener
     *
     * @param port le port d'écoute du server
     */
    public ServerListener(int port) {
        this.port = port;
    }

    /**
     * Permet de convertir un tableau de bytes en long
     *
     * @param b un tableau de bytes
     * @return un long
     */
    public static long bytesToLong(byte[] b) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result * -1;
    }

    /**
     * Permet de convertir un tableau de bytes en long
     *
     * @param b un tableau de bytes
     * @return un long
     */
    public static int bytesToInt(byte[] b) {
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (b[i] & 0xFF);
        }
        return result * -1;
    }

    /**
     * Permet de démarrer la Thread d'écoute qui réçoit et transmet les mavlink messages
     */
    public void startThread() {
        listener = new Thread(() -> {
            while (!Thread.interrupted()) {
                buf = new byte[1000];
                dgp = new DatagramPacket(buf, buf.length);
                try {
                    sk.receive(dgp);
                    byte[] b = dgp.getData();
                    MAVLinkReader reader = new MAVLinkReader();
                    MAVLinkMessage mesg;
                    mesg = reader.getNextMessage(dgp.getData(), dgp.getLength());
                    if (mesg.getClass() == msg_gps_raw_int.class) {
                        long tmpTime = bytesToLong(b);
                        if (lastReceivedTimestamp <= tmpTime) {
                            lastReceivedTimestamp = tmpTime;
                            pressureValid = true;
                        } else {
                            pressureValid = false;
                            break;
                        }
                    }
                    if (mesg.getClass() == msg_scaled_imu.class || mesg.getClass() == msg_scaled_pressure.class) {
                        int tmpTime = bytesToInt(b);
                        if (timeUsec <= tmpTime) {
                            timeUsec = tmpTime;
                            pressureValid = true;
                        } else {
                            pressureValid = false;
                            break;
                        }
                    }
                    if (gpsValid || pressureValid) {
                        while (mesg != null) {
                            // TODO Implémenter MessageWorker
                            if (reader.nbUnreadMessages() != 0) {
                                mesg = reader.getNextMessageWithoutBlocking();
                            } else {
                                break;
                            }
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
