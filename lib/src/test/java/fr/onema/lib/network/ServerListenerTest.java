package fr.onema.lib.network;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;

import static org.junit.Assert.*;

public class ServerListenerTest {
    @Test
    public void testConstructorNotNull() {
        ServerListener serverListener = new ServerListener(1500);
        assertNotNull(serverListener);
    }

    @Test
    public void testStartThread() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        assertNotNull(serverListener.getListener());
        serverListener.stop();
    }

    @Test
    public void testStopThread() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        serverListener.stop();
        assertTrue(serverListener.getListener().isInterrupted());
    }

    @Test
    public void testStartDatagramChannel() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        assertFalse(serverListener.getSk().isClosed());
        serverListener.stop();
    }

    @Test
    public void testStopDatagramChannel() {
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        serverListener.stop();
        assertTrue(serverListener.getSk().isClosed());
    }

    @Test
    public void testReceiveMessage() throws IOException, InterruptedException {
        NetworkSender sender = new NetworkSender(1501, "127.0.0.1");
        sender.openConnection();

        ServerListener serverListener = new ServerListener(1501);
        serverListener.start();

        VirtualizerEntry virtual = new VirtualizerEntry(1, 2,3,4, (short) 5000, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        MAVLinkMessage msg = virtual.getIMUMessage();

        sender.add(virtual);

        Thread.sleep(1000);
        sender.closeConnection();
        serverListener.stop();
    }

}
