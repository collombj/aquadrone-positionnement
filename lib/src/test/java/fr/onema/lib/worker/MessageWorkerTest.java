package fr.onema.lib.worker;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.assertTrue;

public class MessageWorkerTest {

    private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/rawInput.csv";
    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/virtualizedOutput.csv";
    private final static String resultsFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/resultsOutput.csv";

    private final static FileManager fileManager = new FileManager(refFile, virtualizedFile, resultsFile);

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
            mavLinkMessageList.add(simulatedValue.getIMUMessage(i));
            mavLinkMessageList.add(simulatedValue.getPressureMessage(i));
            mavLinkMessageList.add(simulatedValue.getTemperatureMessage(i));
        }
        return mavLinkMessageList;
    }

    @BeforeClass
    public static void createWorker() {
        mavLinkMessageList = populateMavLinkMessageList();

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