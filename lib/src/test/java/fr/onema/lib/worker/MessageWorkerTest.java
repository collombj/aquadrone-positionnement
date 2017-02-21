package fr.onema.lib.worker;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;

import static org.junit.Assert.assertTrue;

public class MessageWorkerTest {

    private static MessageWorker messageWorker = new MessageWorker();
    private static Thread insertThread;

    private static Deque<HashMap.SimpleEntry<Long, MAVLinkMessage>> mavLinkMessageList;

    private static long lastTimestamp;

    private static void populateMavLinkMessageList() {
        mavLinkMessageList = new ArrayDeque<>(260);
        VirtualizerEntry simulatedValue;
        for (int i = 0; i < 50; i++) {
            lastTimestamp = System.currentTimeMillis()+250;
            simulatedValue = new VirtualizerEntry(
                    lastTimestamp,
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
            mavLinkMessageList.add(new HashMap.SimpleEntry<>(lastTimestamp, simulatedValue.getGPSMessage()));
            mavLinkMessageList.add(new HashMap.SimpleEntry<>(lastTimestamp, simulatedValue.getPressureMessage(i)));
            mavLinkMessageList.add(new HashMap.SimpleEntry<>(lastTimestamp, simulatedValue.getIMUMessage(i)));
            mavLinkMessageList.add(new HashMap.SimpleEntry<>(lastTimestamp, simulatedValue.getAttitudeMessage(i)));
            mavLinkMessageList.add(new HashMap.SimpleEntry<>(lastTimestamp, simulatedValue.getTemperatureMessage(i)));
        }
    }

    @BeforeClass
    public static void createWorker() throws InterruptedException {
        populateMavLinkMessageList();
        DatabaseWorker.getInstance().start();

        insertThread = new Thread(() -> {
            while (!mavLinkMessageList.isEmpty()) {
                try {
                    messageWorker.newMessage(mavLinkMessageList.getFirst().getKey(), mavLinkMessageList.removeFirst().getValue());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        messageWorker.start();
        insertThread.start();
        insertThread.join();
        Thread.currentThread().sleep(1000);
    }

    @Test
    public void newMessage() throws Exception {
        Thread.currentThread().sleep(1000);
        assertTrue(mavLinkMessageList.size() != 200);
    }

    @AfterClass
    public static void stopThread() {
        messageWorker.stop();
        DatabaseWorker.getInstance().stop();
        insertThread.interrupt();
    }
}