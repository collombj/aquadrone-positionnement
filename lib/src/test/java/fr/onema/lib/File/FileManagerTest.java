package fr.onema.lib.File;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by you on 07/02/2017.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class FileManagerTest {

    private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/rawInput.csv";
    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/virtualizedOutput.csv";
    private final static String resultsFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/resultsOutput.csv";
    private final static FileManager fm = new FileManager(refFile, virtualizedFile, resultsFile);
    private final static FileManager fm_bugged = new FileManager("notapath", "notapath");

    @BeforeClass
    public static void prepare() throws Exception {
        File ref = new File(refFile);
        ref.delete();
        File v = new File(virtualizedFile);
        v.delete();
        File res = new File(resultsFile);
        res.delete();
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 1;
        msg.lat = 2;
        msg.lon = 3;
        msg.alt = 4;
        msg.cog = 5;
        msg_scaled_pressure msg2 = new msg_scaled_pressure();
        msg2.time_boot_ms = 0;
        msg2.temperature = 6;
        fm.appendRaw(GPS.build(msg), Temperature.build(msg2));
        fm.appendVirtualized(new VirtualizerEntry(1, 2, 3, 4, (short)5, (short)6, (short)7, (short)8, (short)9,
                (short)10, (short)11, (short)12, (short)13, 14, (short)15));
    }

    @AfterClass
    public static void delete() {
        File ref = new File(refFile);
        ref.delete();
        File v = new File(virtualizedFile);
        v.delete();
        File res = new File(resultsFile);
        res.delete();
    }

    @Test (expected = IOException.class)
    public void testExceptions() throws IOException {
        fm_bugged.readReferenceEntries();
        fm_bugged.readVirtualizedEntries();
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 1;
        msg.lat = 2;
        msg.lon = 3;
        msg.alt = 4;
        msg.cog = 5;
        msg_scaled_pressure msg2 = new msg_scaled_pressure();
        msg2.time_boot_ms = 0;
        msg2.temperature = 6;
        fm_bugged.appendRaw(GPS.build(msg), Temperature.build(msg2));
        fm_bugged.appendVirtualized(new VirtualizerEntry(1, 2, 3, 4, (short)5, (short)6, (short)7, (short)8, (short)9,
                (short)10, (short)11, (short)12, (short)13, 14, (short)15));
        fm_bugged.openFileForResults();
        ReferenceEntry re = new ReferenceEntry(0,4,5,6,(float)7,(short)8);
        MeasureEntity m = new MeasureEntity(
                0, new GPSCoordinate(4,5,6), new GPSCoordinate(1,2,3), 0, 0, 0, 0, 0, 0, 13, "test");
        fm_bugged.appendResults(re, m, 14);
    }

    @Test
    public void readReferenceEntries() throws Exception {
        assertNotNull(fm.readReferenceEntries());
        ReferenceEntry r = fm.readReferenceEntries().get(0);
        assertEquals(r.getTimestamp(), 1);
        assertEquals(r.getLat(), 2);
        assertEquals(r.getLon(), 3);
        assertEquals(r.getAlt(), 4);
        assertEquals(r.getDirection(), (float) 5, 0);
        assertEquals(r.getTemperature(), 6);
    }

    @Test
    public void readVirtualizedEntries() throws Exception {
        VirtualizerEntry v = fm.readVirtualizedEntries().get(0);
        assertEquals(v.getTimestamp(), 1);
        assertEquals(v.getGpsLon(), 3);
        assertEquals(v.getGpsLat(), 2);
        assertEquals(v.getGpsAlt(), 4);
        assertEquals(v.getXacc(),5);
        assertEquals(v.getYacc(),6);
        assertEquals(v.getZacc(),7);
        assertEquals(v.getRoll(),8.0,0.1);
        assertEquals(v.getPitch(),9.0,0.1);
        assertEquals(v.getYaw(),10.0,0.1);
        assertEquals(v.getXmag(),11);
        assertEquals(v.getYmag(),12);
        assertEquals(v.getZmag(),13);
        assertEquals(v.getTemperature(),15);
    }

    @Test
    public void appendRaw() throws Exception {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 0;
        msg.lat = 1;
        msg.lon = 2;
        msg.alt = 3;
        msg.cog = 4;
        msg_scaled_pressure msg2 = new msg_scaled_pressure();
        msg2.time_boot_ms = 0;
        msg2.temperature = 5;
        fm.appendRaw(GPS.build(msg), Temperature.build(msg2));
    }

    @Test
    public void appendVirtualized() throws Exception {
        fm.appendVirtualized(new VirtualizerEntry(0, 1, 2, 3, (short) 4, (short) 5, (short) 6,
                (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, 13, (short) 14));
    }

    @Test
    public void appendResults() throws Exception {
        FileManager fm = new FileManager(refFile, virtualizedFile, resultsFile);
        fm.openFileForResults();
        ReferenceEntry re = new ReferenceEntry(0, 4, 5, 6, (float) 7, (short) 8);
        MeasureEntity m = new MeasureEntity(
                0, new GPSCoordinate(4, 5, 6), new GPSCoordinate(1, 2, 3), 0, 0, 0, 0, 0, 0, 13, "test");
        fm.appendResults(re, m, 14);
        fm.appendResults(re, m, 0);
    }

    @Test
    public void getResults() throws Exception {
        appendResults();
        FileManager fm = new FileManager(refFile, virtualizedFile, resultsFile);
        List<String> results = fm.getResults("||");
        assertEquals("timestamp||corrected.latitude||corrected.longitude||corrected.altitude||ref.latitude||ref.longitude||ref.altitude||ref.direction||ref.temperature||difference.x||difference.y||difference.z||difference.absolute||precision||margin||margin.error", results.get(0));
        assertEquals("0||1||2||3||4||5||6||7.0||8||252633.94405114||864468.0688498556||-1.0147670209231338E7||1.0187558079690589E7||13||14.0||true", results.get(1));
    }
}