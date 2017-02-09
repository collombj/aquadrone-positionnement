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

/**
 * Created by Theo on 08/02/2017.
 */
public class NetworkSenderTest {
    @Test
    public void constructorNotNull() {
        NetworkSender networkSender = new NetworkSender(5, "test");
        assertNotNull(networkSender);
    }

    @Test
    public void openConnectionNotNull() throws IOException {
        NetworkSender networkSender = new NetworkSender(1239, "127.0.0.1");
        ServerSocket server = new ServerSocket(1239);
        networkSender.openConnection();
        assertNotNull(networkSender.getChannel());
        networkSender.closeConnection();
    }

    @Test
    public void closeConnectionNotNull() throws IOException {
        NetworkSender networkSender = new NetworkSender(1240, "127.0.0.1");
        ServerSocket server = new ServerSocket(1240);
        networkSender.openConnection();
        networkSender.closeConnection();
        assertFalse(networkSender.getChannel().isConnected());
    }

    @Test
    public void sendMessage() throws IOException, InterruptedException {
        ArrayBlockingQueue<String> list = new ArrayBlockingQueue<String>(10);
        Thread thread1 = new Thread(() -> {

            NetworkSender sender = new NetworkSender(1241, "127.0.0.1");
            try {
                sender.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }

            VirtualizerEntry virtual = new VirtualizerEntry(1, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
            MAVLinkMessage msg = virtual.getIMUMessage();
            try {
                list.put(msg.toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            sender.send(msg);
            sender.closeConnection();
        });

        Thread thread2 = new Thread(() -> {
            DatagramChannel server = null;
            try {
                server = DatagramChannel.open();
                server.bind(new InetSocketAddress(1241));
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteBuffer buff = ByteBuffer.allocate(265);
            InetSocketAddress exp = null;
            try {
                server.receive(buff);
            } catch (IOException e) {
                e.printStackTrace();
            }
            buff.flip();
            try {
                list.put(Charset.forName("utf-8").decode(buff).toString());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        thread1.start();
        thread2.start();
        String s1 = list.take();
        String s2 = list.take();
        thread1.interrupt();
        thread2.interrupt();
        assertEquals(s1,s2);
    }
}
