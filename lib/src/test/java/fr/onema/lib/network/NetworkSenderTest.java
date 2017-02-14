package fr.onema.lib.network;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import static org.junit.Assert.*;

public class NetworkSenderTest {
    public static final String ASCII = "ASCII";

    @Test
    public void constructorNotNull() throws IOException {
        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        networkSender.openConnection();
        assertNotNull(networkSender);
        networkSender.closeConnection();
    }

    @Test
    public void openConnectionNotNull() throws IOException {
        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        ServerSocket server = new ServerSocket(1239);
        networkSender.openConnection();
        assertNotNull(networkSender.getDsocket());
        networkSender.closeConnection();
        server.close();
    }

    @Test
    public void closeConnectionNotNull() throws IOException {
        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        ServerSocket server = new ServerSocket(1239);
        networkSender.openConnection();
        networkSender.closeConnection();
        assertTrue(networkSender.getDsocket().isClosed());
    }

    @Test
    public void threadStarted() throws IOException {
        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        networkSender.openConnection();
        assertTrue(networkSender.getSender().isAlive());
        networkSender.closeConnection();
    }

    @Test
    public void addTest() throws IOException {
        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        VirtualizerEntry vir = new VirtualizerEntry(1, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        networkSender.openConnection();
        networkSender.add(vir);
        networkSender.getQueue().contains(vir);
        networkSender.closeConnection();
    }

    @Test
    public void sendMessage() throws IOException, InterruptedException {
        ArrayBlockingQueue<String> list = new ArrayBlockingQueue<String>(10);
        Thread sender = new Thread(() -> {
                DatagramChannel server = null;
                try {
                    server = DatagramChannel.open();
                    server.bind(new InetSocketAddress(1239));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteBuffer buff = ByteBuffer.allocate(1024);
                try {
                    server.receive(buff);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                buff.flip();
                try {
                    list.put(Charset.forName(ASCII).decode(buff).toString());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        });
        sender.start();

        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        VirtualizerEntry virtual = new VirtualizerEntry(1, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        MAVLinkMessage msg = virtual.getIMUMessage();
        networkSender.openConnection();
        networkSender.add(virtual);
        assertEquals(msg.toString(), list.take());
        networkSender.closeConnection();
    }
}