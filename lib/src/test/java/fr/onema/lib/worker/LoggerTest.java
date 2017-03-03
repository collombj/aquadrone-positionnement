package fr.onema.lib.worker;

import fr.onema.lib.drone.Position;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LoggerTest {

    private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/rawInput.csv";
    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/virtualizedOutput.csv";
    private final static String resultsFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/resultsOutput.csv";

    private final static FileManager fileManager = new FileManager(refFile, virtualizedFile, resultsFile);
    private static Logger logger;
    private static BlockingDeque<Position> positions = new LinkedBlockingDeque<>();

    @BeforeClass
    public static void prepare() throws Exception {
        File ref = new File(refFile);
        ref.delete();
        File v = new File(virtualizedFile);
        v.delete();
        File res = new File(resultsFile);
        res.delete();
        populatePositions();
        logger = new Logger(fileManager);
        logger.start();
    }

    private static void populatePositions() {
        for (int i = 0; i < 10000; i = i + 200)
            positions.add(new Position(
                    System.currentTimeMillis() + i,
                    new GPSCoordinate(i + 244, i - 543, i - 10),
                    i,
                    IMU.build(new CartesianVelocity(i, i, i), new CartesianVelocity(i + 50, i + 50, i + 50), System.currentTimeMillis() - i, System.currentTimeMillis()),
                    GPS.build(System.currentTimeMillis(), i, i, i, i))
            );
    }

/*    @AfterClass
    public static void delete() {
        file ref = new file(refFile);
        ref.delete();
        file v = new file(virtualizedFile);
        v.delete();
        file res = new file(resultsFile);
        res.delete();
        logger.stop();
    }*/


    @Test
    public void newMAVLinkMessage() throws Exception {

        new Thread(() -> {
            while (!positions.isEmpty()) {
                logger.addPosition(positions.pop());
            }
        }).start();
        Thread.sleep(1000);
        List<VirtualizerEntry> virtualizerEntryList = fileManager.readVirtualizedEntries();
        assertFalse(virtualizerEntryList.isEmpty());
        assertTrue(virtualizerEntryList.get(2).getTimestamp() != 0);
    }

    @Test(expected = NullPointerException.class)
    public void newMAVLinkMessageNull() {
        logger.addPosition(null);
    }

}