package fr.onema.lib.worker;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.util.HashMap;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.*;

public class MessageWorkerTest {

    private static MessageWorker messageWorker = new MessageWorker();
    private static Thread insertThread;

    private static BlockingDeque<HashMap.SimpleEntry<Long, MAVLinkMessage>> mavLinkMessageList;
    private static long lastTime;

    private static BlockingDeque<HashMap.SimpleEntry<Long, MAVLinkMessage>> populateMavLinkMessageList() {
        BlockingDeque<HashMap.SimpleEntry<Long, MAVLinkMessage>> mavLinkMessageList = new LinkedBlockingDeque<>(250);
        VirtualizerEntry simulatedValue;
        for (int i = 0; i < 10000; i = i+200) {
            lastTime = System.currentTimeMillis()+i;
            simulatedValue = new VirtualizerEntry(
                    lastTime,
                    i+3777,
                    i+467,
                    i+88,
                    (short) i+3778,
                    (short) i+23,
                    (short) i+33,
                    (short) i+234,
                    (short) i+555,
                    (short) i+7778,
                    (short) i+999,
                    (short) i+866,
                    (short) i+123,
                    i+1443,
                    (short) i
            );
            mavLinkMessageList.offer(new HashMap.SimpleEntry<>(lastTime, simulatedValue.getGPSMessage()));
            mavLinkMessageList.offer(new HashMap.SimpleEntry<>(lastTime, simulatedValue.getIMUMessage(lastTime)));
            mavLinkMessageList.offer(new HashMap.SimpleEntry<>(lastTime, simulatedValue.getAttitudeMessage(lastTime)));
            mavLinkMessageList.offer(new HashMap.SimpleEntry<>(lastTime, simulatedValue.getPressureMessage(lastTime)));
            mavLinkMessageList.offer(new HashMap.SimpleEntry<>(lastTime, simulatedValue.getTemperatureMessage(lastTime)));
        }
        return mavLinkMessageList;
    }

    @BeforeClass
    public static void createWorker() throws InterruptedException {
        mavLinkMessageList = populateMavLinkMessageList();

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
        Thread.sleep(1000);
    }

    @AfterClass
    public static void stopThread() {
        messageWorker.stop();
        insertThread.interrupt();
    }

    @Test
    public void newMessage() throws Exception {
        Thread.currentThread().sleep(2000);
        assertNotNull(messageWorker.getDive());
        assertEquals(messageWorker.getMeasuresStates().size(), 4);
        assertEquals(messageWorker.getMavLinkConnection(), lastTime);
    }
}