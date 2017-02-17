package fr.onema.lib.network;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

public class ServerListenerTest {


    @Test
    public void testConstructorNotNull() {
        ServerListener serverListener = new ServerListener(1500);
        assertNotNull(serverListener);
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
    public void bytesToNumberTest() throws UnsupportedEncodingException {
        ServerListener serverListener = new ServerListener(1500);
        long res = serverListener.bytesToNumber("MAVLINK =12345678 ");
        assertEquals(12345678,res);
    }

    @Test
    public void testReceiveMessage() throws IOException, InterruptedException {
        NetworkSender sender = new NetworkSender(1500, "127.0.0.1");
        sender.openConnection();
        ServerListener serverListener = new ServerListener(1500);
        serverListener.start();
        VirtualizerEntry virtual = new VirtualizerEntry(System.currentTimeMillis(), 2,3,4, (short) 5000, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        sender.add(virtual);
        sender.closeConnection();
        serverListener.stop();
    }
}
