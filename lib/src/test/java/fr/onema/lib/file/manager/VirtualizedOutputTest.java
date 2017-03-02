package fr.onema.lib.file.manager;

import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure3;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class VirtualizedOutputTest {

    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/file/virtualizedOutput.csv";
    private final static VirtualizedOutput VIRTUALIZED_OUTPUT = new VirtualizedOutput(virtualizedFile);
    private final static VirtualizedOutput VIRTUALIZED_OUTPUT_BUG = new VirtualizedOutput("notapath");

    @BeforeClass
    public static void prepare() throws Exception {
        File ref = new File(virtualizedFile);
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
        VIRTUALIZED_OUTPUT.appendVirtualized(new VirtualizerEntry(1, 2, 3, 4, 5, 6, 7, 8, 9,
                10, 11, 12, 13, 14, 15));
    }

    @AfterClass
    public static void delete() {
        File ref = new File(virtualizedFile);
        ref.delete();
        File notapath = new File("notapath");
        notapath.delete();
    }

    @Test
    public void readVirtualizedEntries() throws Exception {
        VirtualizerEntry v = VIRTUALIZED_OUTPUT.readVirtualizedEntries().get(0);
        assertEquals(v.getTimestamp(), 1);
        assertEquals(v.getGpsLon(), 3);
        assertEquals(v.getGpsLat(), 2);
        assertEquals(v.getGpsAlt(), 4);
        assertEquals(v.getXacc(), 5);
        assertEquals(v.getYacc(), 6);
        assertEquals(v.getZacc(), 7);
        assertEquals(v.getRoll(), 8.0, 0.1);
        assertEquals(v.getPitch(), 9.0, 0.1);
        assertEquals(v.getYaw(), 10.0, 0.1);
        assertEquals(v.getXmag(), 11);
        assertEquals(v.getYmag(), 12);
        assertEquals(v.getZmag(), 13);
        assertEquals(v.getTemperature(), 15);
    }

    @Test
    public void appendVirtualized() throws Exception {
        VIRTUALIZED_OUTPUT.appendVirtualized(new VirtualizerEntry(0, 1, 2, 3, 4, 5, 6,
                7, 8, 9, 10, 11, 12, 13, 14));
    }


    @Test(expected = IOException.class)
    public void testException2() throws IOException {
        VIRTUALIZED_OUTPUT_BUG.readVirtualizedEntries();
    }

    @Test(expected = IOException.class)
    public void testException4() throws IOException {
        VIRTUALIZED_OUTPUT_BUG.appendVirtualized(new VirtualizerEntry(1, 2, 3, 4, 5, 6, 7, 8, 9,
                10, 11, 12, 13, 14, 15));
    }
}