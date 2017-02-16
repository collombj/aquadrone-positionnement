package fr.onema.lib.worker;

import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.*;

public class MessageWorkerTest {

    private static MessageWorker messageWorker = new MessageWorker();
    private static Thread insertThread;

    private static BlockingDeque<MAVLinkMessage> mavLinkMessageList;

    static BlockingDeque<MAVLinkMessage> populateMavLinkMessageList() {
        BlockingDeque<MAVLinkMessage> mavLinkMessageList = new LinkedBlockingDeque<>(210);
        VirtualizerEntry simulatedValue;
        for (int i = 0; i < 10000; i = i+200) {
            simulatedValue = new VirtualizerEntry(
                    System.currentTimeMillis()+i,
                    i,
                    i,
                    i,
                    (short) i,
                    (short) i,
                    (short) i,
                    (short) i,
                    (short) i,
                    (short) i,
                    (short) i,
                    (short) i,
                    (short) i,
                    i,
                    (short) i
            );
            mavLinkMessageList.add(simulatedValue.getGPSMessage());
            mavLinkMessageList.add(simulatedValue.getIMUMessage());
            mavLinkMessageList.add(simulatedValue.getPressureMessage());
            mavLinkMessageList.add(simulatedValue.getTemperatureMessage());
        }
        return mavLinkMessageList;
    }

    @BeforeClass
    public static void createWorker() {
        mavLinkMessageList = populateMavLinkMessageList();

        insertThread = new Thread(() -> {
            while (!mavLinkMessageList.isEmpty()) {
                try {
                    messageWorker.newMessage(mavLinkMessageList.removeFirst());
                    Thread.currentThread().sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        messageWorker.start();
        insertThread.start();
    }

    @Test
    public void newMessage() throws Exception {
        Thread.currentThread().sleep(2000);
        assertTrue(mavLinkMessageList.size() != 200);
    }

    @Test
    public void clearWaitingList() throws Exception {
        Temperature temperature = Temperature.build(System.currentTimeMillis(), 20);
        messageWorker.add(temperature);
        assertFalse(messageWorker.isWaitingListEmpty());
        messageWorker.clearWaitingList();
        assertTrue(messageWorker.isWaitingListEmpty());
    }

    @Test
    public void add() throws Exception {
        Temperature temperature = Temperature.build(System.currentTimeMillis(), 20);
        messageWorker.add(temperature);
        assertFalse(messageWorker.isWaitingListEmpty());
    }

    @AfterClass
    public static void stopThread() {
        messageWorker.stop();
        insertThread.interrupt();
    }
}