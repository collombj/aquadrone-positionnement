package fr.onema.lib.network;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_imu;

import java.io.IOException;

import static org.junit.Assert.*;

public class ServerListenerTest {

    @Test
    public void testConstructorNotNull() {
        ServerListener serverListener = new ServerListener(1500);
        assertNotNull(serverListener);
    }

    @Test
    public void missingTests() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 2;
        assertEquals(true, serverListener.testValidityMavlinkMessage(msg));
        assertEquals(2, serverListener.getFirstTimestamp(msg));
        assertEquals(2, serverListener.getTimestamp(msg));
        msg_scaled_imu msg2 = new msg_scaled_imu();
        msg2.time_boot_ms = 3;
        assertEquals(3, serverListener.getBootTime(msg2));
        assertNotNull(serverListener.getMessageWorker());
        serverListener.stop();
    }

    @Test
    public void testStartThread() throws InterruptedException {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        Thread.sleep(100);
        assertNotNull(serverListener.getListener());
        serverListener.stop();
    }

    @Test
    public void testStartDatagramChannel() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        assertFalse(serverListener.getDatagramSocket().isClosed());
        serverListener.stop();
    }

    @Test
    public void testStopDatagramChannel() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        serverListener.stop();
        assertTrue(serverListener.getDatagramSocket().isClosed());
    }

    @Test
    public void testReceiveMessage() throws IOException, InterruptedException {
        NetworkSender sender = new NetworkSender(1500, "127.0.0.1");
        sender.openConnection();
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        VirtualizerEntry virtual = new VirtualizerEntry(System.currentTimeMillis(), 2, 3, 4, (short) 5000, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        sender.add(virtual);
        sender.closeConnection();
        serverListener.stop();
    }

    @Test
    public void getBootTimeTest() {
        ServerListener serverListener = new ServerListener(1500);
        assertEquals(serverListener.getBootTime(null), 0);
    }
}
