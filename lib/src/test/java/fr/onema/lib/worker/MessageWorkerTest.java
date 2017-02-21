package fr.onema.lib.worker;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.Assert.assertTrue;

public class MessageWorkerTest {

    private static MessageWorker messageWorker = new MessageWorker();
    private static Thread insertThread;

    private static Deque<MAVLinkMessage> mavLinkMessageList;

    private static void populateMavLinkMessageList() {
        mavLinkMessageList = new ArrayDeque<>(210);
        VirtualizerEntry simulatedValue;
        for (int i = 0; i < 50; i++) {
            simulatedValue = new VirtualizerEntry(
                    System.currentTimeMillis(),
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
            mavLinkMessageList.add(simulatedValue.getIMUMessage(i));
            mavLinkMessageList.add(simulatedValue.getPressureMessage(i));
            mavLinkMessageList.add(simulatedValue.getTemperatureMessage(i));
        }
    }

    @BeforeClass
    public static void createWorker() {
        populateMavLinkMessageList();

        insertThread = new Thread(() -> {
            while (!mavLinkMessageList.isEmpty()) {
                try {
                    messageWorker.newMessage(27091994, mavLinkMessageList.removeFirst());
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

    @AfterClass
    public static void stopThread() {
        messageWorker.stop();
        insertThread.interrupt();
    }
}