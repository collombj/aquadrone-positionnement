package fr.onema.lib.file.manager;

import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure3;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class RawInputTest {

    private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/file/rawInput.csv";
    private final static RawInput RAW_INPUT = new RawInput(refFile);
    private final static RawInput RAW_INPUT_BUG = new RawInput("notapath");

    @BeforeClass
    public static void prepare() throws Exception {
        File ref = new File(refFile);
        ref.delete();
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 1;
        msg.lat = 2;
        msg.lon = 3;
        msg.alt = 4;
        msg.cog = 5;
        msg_scaled_pressure3 msg2 = new msg_scaled_pressure3();
        msg2.time_boot_ms = 0;
        msg2.temperature = 6;
        long timestamp = 27091994;
        RAW_INPUT.appendRaw(GPS.build(timestamp, msg), Temperature.build(timestamp, msg2));
    }

    @AfterClass
    public static void delete() {
        File ref = new File(refFile);
        ref.delete();
    }

    @Test
    public void readReferenceEntries() throws Exception {
        assertNotNull(RAW_INPUT.readReferenceEntries());
        ReferenceEntry r = RAW_INPUT.readReferenceEntries().get(0);
        assertEquals(r.getTimestamp(), 27091994);
        assertEquals(r.getLat(), 2);
        assertEquals(r.getLon(), 3);
        assertEquals(r.getAlt(), 4);
        assertEquals(r.getDirection(), (float) 5, 0);
        assertEquals(r.getTemperature(), 6);
    }

    @Test(expected = NoSuchFileException.class)
    public void testException1() throws IOException {
        RAW_INPUT_BUG.readReferenceEntries();
    }

    @Test
    public void testException3() throws IOException {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg_scaled_pressure3 msg2 = new msg_scaled_pressure3();
        long timestamp = 27091994;
        RAW_INPUT_BUG.appendRaw(GPS.build(timestamp, msg), Temperature.build(timestamp, msg2));
    }

    @Test
    public void appendRaw() throws Exception {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 0;
        msg.lat = 1;
        msg.lon = 2;
        msg.alt = 3;
        msg.cog = 4;
        msg_scaled_pressure3 msg2 = new msg_scaled_pressure3();
        msg2.time_boot_ms = 0;
        msg2.temperature = 5;
        long timestamp = 27091994;
        RAW_INPUT.appendRaw(GPS.build(timestamp, msg), Temperature.build(timestamp, msg2));
    }

}