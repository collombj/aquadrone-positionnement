package fr.onema.lib;

import fr.onema.DatabaseTools;
import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.network.ServerListener;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import fr.onema.simulator.Virtualizer;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by Jérôme on 09/02/2017.
 */
public class VirtualizerTest {
    private static final String configPath = System.getProperty("user.dir").replace("simulator", "lib") + "/settingsTest.properties";
    /*private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/rawInput.csv";
    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/virtualizedOutput.csv";
    private final static String virtualizedErrorFile = System.getProperty("user.dir") +  "/src/test/java/fr/onema/lib/virtualizedErrorOutput.csv";

    private final static FileManager sfm =new FileManager(refFile, virtualizedFile);
    private final static FileManager efm =new FileManager(refFile, virtualizedErrorFile);*/
    private static File referencePath;
    private static String refContent = "timestamp,latitude,longitude,altitude,direction,temperature\n" +
            "1487061053,0,0,0,0.0,0\n" +
            "1487061063,1,1,1,1.0,1\n" +
            "1487061073,2,2,2,2.0,2\n" +
            "1487061083,3,3,3,3.0,3\n" +
            "1487061093,4,4,4,4.0,4\n" +
            "1487061103,5,5,5,5.0,5\n" +
            "1487061113,6,6,6,6.0,6\n" +
            "1487061123,7,7,7,7.0,7\n" +
            "1487061133,8,8,8,8.0,8\n" +
            "1487061143,9,9,9,9.0,9";
    private static String virtContent = "timestamp,gpsLongitude,gpsLatitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,rotationX,rotationY,rotationZ,capX,capY,capZ,pression,temperature\n" +
            "1487061053,0,0,0,0,0,0,0,0,0,0,0,0,0.0,0\n" +
            "1487061063,1,1,1,-18183,-3770,7795,0,0,0,0,0,0,0.0,1\n" +
            "1487061073,2,2,2,19200,17458,13499,0,0,0,0,0,0,0.0,2\n" +
            "1487061083,3,3,3,-17412,22394,15888,0,0,0,0,0,0,0.0,3\n" +
            "1487061093,4,4,4,-12590,11842,18487,0,0,0,0,0,0,0.0,4\n" +
            "1487061103,5,5,5,8374,-3313,15128,0,0,0,0,0,0,0.0,5\n" +
            "1487061113,6,6,6,15168,30142,2861,0,0,0,0,0,0,0.0,6\n" +
            "1487061123,7,7,7,-18380,-11842,-32436,0,0,0,0,0,0,0.0,7\n" +
            "1487061133,8,8,8,20607,5385,5616,0,0,0,0,0,0,0.0,8\n" +
            "1487061143,9,9,9,15433,28903,-10733,0,0,0,0,0,0,0.0,9";
    private static File virtualizedPath;
    private static FileManager fm;
    private static File resultPath;
    private static ServerListener srv;

    @BeforeClass
    public static void init() throws Exception {
        referencePath = initFile("reference", refContent);
        virtualizedPath = initFile("virtualized", virtContent);
        resultPath = initFile("result", "");
        fm = new FileManager(referencePath.getCanonicalPath(), virtualizedPath.getCanonicalPath(), resultPath.getCanonicalPath());
        initDB(fm);
        initServer();
    }

    @AfterClass
    public static void stop() {
        srv.stop();
    }

    private static void initServer() {
        srv = new ServerListener(1234);
        srv.start();
    }

    private static void initDB(FileManager fm) throws Exception {
        List<VirtualizerEntry> list = fm.readVirtualizedEntries();
        Configuration config = Configuration.build(configPath);
        Configuration.Database db = config.getDatabaseInformation();

        DatabaseTools.dropStructure(db.getHostname(), db.getPort(), db.getBase(), db.getUsername(), db.getPassword());
        DatabaseTools.createStructure(db.getHostname(), db.getPort(), db.getBase(), db.getUsername(), db.getPassword());
        DatabaseTools.insertFakeMeasureInformation(db.getHostname(), db.getPort(), db.getBase(), db.getUsername(), db.getPassword());

        MeasureRepository repository = MeasureRepository.MeasureRepositoryBuilder.getRepositoryWritable(config);
        DiveEntity dive = new DiveEntity(1487061053, 1487061143);
        dive = repository.insertDive(dive);
        DiveEntity finalDive = dive;
        list.forEach(v -> {
            MeasureEntity measure = new MeasureEntity(v.getTimestamp(),
                    new GPSCoordinate(
                            v.getGpsLat(),
                            v.getGpsLon(),
                            v.getGpsAlt()),
                    new GPSCoordinate(
                            v.getGpsLat(),
                            v.getGpsLon(),
                            v.getGpsAlt()),
                    v.getXacc(),
                    v.getYacc(),
                    v.getZacc(),
                    v.getRoll(),
                    v.getPitch(),
                    v.getYaw(),
                    0,
                    Integer.toString(v.getTemperature()));
            try {
                repository.insertMeasure(measure, finalDive.getId(), 1);
            } catch (SQLException e) {
                fail(e.getMessage());
            }
        });
    }

    private static File initFile(String fileName, String content) {
        try {
            File path = File.createTempFile(fileName, "csv");
            path.deleteOnExit();
            PrintWriter writer = new PrintWriter(path.getCanonicalFile(), "UTF-8");
            writer.println(content);
            writer.close();
            return path;
        } catch (IOException e) {
            fail();
        }
        return null;
    }

    @Test(expected = NullPointerException.class)
    public void testNoFileManager() {
        Virtualizer v = new Virtualizer(null, 100, "aaa", "test", 5432);
    }

    @Test(expected = NullPointerException.class)
    public void testNoSimulationName() {
        FileManager fm = new FileManager("rawInput.csv", "virtualizedOutput.csv", "computedOutput.csv");
        Virtualizer v = new Virtualizer(fm, 100, null, "test", 5432);
    }

    @Test(expected = NullPointerException.class)
    public void testNoHost() {
        FileManager fm = new FileManager("rawInput.csv", "virtualizedOutput.csv", "computedOutput.csv");
        Virtualizer v = new Virtualizer(fm, 100, "aaa", null, 5432);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSpeed() {
        FileManager fm = new FileManager("rawInput.csv", "virtualizedOutput.csv", "computedOutput.csv");
        Virtualizer v = new Virtualizer(fm, 0, "aaa", "test", 5432);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPort() {
        FileManager fm = new FileManager("rawInput.csv", "virtualizedOutput.csv", "computedOutput.csv");
        Virtualizer v = new Virtualizer(fm, 100, "aaa", "test", 0);
    }

    @Test
    public void testGetters() throws IOException {
        Virtualizer v = new Virtualizer(fm, 100, "aaa", "localhost", 1234);
        v.start();
        assertEquals(v.getSimulationName(), "aaa");
        assertEquals(v.getSpeed(), 100);
        assertEquals(v.getHost(), "localhost");
        assertEquals(v.getPort(), 1234);
    }

    @Test
    public void testCompare() throws Exception {

        String workingSourceDir = System.getProperty("user.dir").replace("simulator", "lib");
        Virtualizer v = new Virtualizer(fm, 100, "aaa", "localhost", 1234);
        Configuration config = Configuration.build(workingSourceDir + "/settingsTest.properties");
        v.start();
        assertNotEquals(v.getDuration(), 0);
        v.compare(config, 0);
    }
}
