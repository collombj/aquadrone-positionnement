package fr.onema.lib.worker;

import fr.onema.lib.File.FileManagerTest;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.MAVLinkMessage;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.io.File;
import java.io.IOException;
import java.util.Deque;

import static org.junit.Assert.*;

public class LoggerTest {

    private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/rawInput.csv";
    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/virtualizedOutput.csv";
    private final static String resultsFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/resultsOutput.csv";

    private static Logger logger;
    private final static FileManager fileManager = new FileManager(refFile, virtualizedFile, resultsFile);

    private static Deque<MAVLinkMessage> mavLinkMessageList;

    @BeforeClass
    public static void prepare() throws Exception {
        FileManagerTest.delete();
        mavLinkMessageList = MessageWorkerTest.populateMavLinkMessageList();
        logger = new Logger(fileManager);
        logger.start();
    }

    @AfterClass
    public static void delete() {
        FileManagerTest.delete();
        logger.stop();
    }


    @Test
    public void newMAVLinkMessage() throws Exception {

        while(!mavLinkMessageList.isEmpty()) {
            logger.newMAVLinkMessage(mavLinkMessageList.getFirst());
        }
    }

    @Test (expected = NullPointerException.class)
    public void newMAVLinkMessageNull() {
        logger.newMAVLinkMessage(null);
    }
}