package fr.onema.lib.File;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_global_position_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import static org.junit.Assert.*;

/**
 * Created by you on 07/02/2017.
 */
public class FileManagerTest {
    @Test
    public void readReferenceEntries() throws Exception {
        String workingDir = System.getProperty("user.dir");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        ReferenceEntry r = fm.readReferenceEntries().get(0);
        assertEquals(r.getTimestamp(), 1);
        assertEquals(r.getLat(), 2);
        assertEquals(r.getLon(), 3);
        assertEquals(r.getAlt(), 4);
        assertEquals(r.getTemperature(), 6);
    }

    @Test
    public void readVirtualizedEntries() throws Exception {
        String workingDir = System.getProperty("user.dir");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        VirtualizerEntry v = fm.readVirtualizedEntries().get(0);
        assertEquals(v.getTimestamp(), 1);
        assertEquals(v.getGPSLon(), 2);
        assertEquals(v.getGPSLat(), 3);
        assertEquals(v.getGPSAlt(), 4);
        assertEquals(v.getXacc(),5);
        assertEquals(v.getYacc(),6);
        assertEquals(v.getZacc(),7);
        assertEquals(v.getXgyro(),8);
        assertEquals(v.getYgyro(),9);
        assertEquals(v.getZgyro(),10);
        assertEquals(v.getXmag(),11);
        assertEquals(v.getYmag(),12);
        assertEquals(v.getZmag(),13);
        assertEquals(v.getTemperature(),15);
    }

    @Test
    public void computeFilesIntoCSV() throws Exception {
        String workingDir = System.getProperty("user.dir");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        fm.computeFilesIntoCSV();
    }

    // TODO : uncomment test after toCSV() merge with Parser.class
    @Test
    public void appendRaw() throws Exception {
        String workingDir = System.getProperty("user.dir");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        msg_global_position_int msg = new msg_global_position_int();
        msg.time_boot_ms = 0;
        msg.lat = 1;
        msg.lon = 2;
        msg.alt = 3;
        msg.hdg = 4;
        msg_scaled_pressure msg2 = new msg_scaled_pressure();
        msg2.time_boot_ms = 0;
        msg2.temperature = 5;
        // fm.appendRaw(GPS.build(msg), Temperature.build(msg2));
    }

    // TODO : uncomment test after toCSV() merge with Parser.class
    @Test
    public void appendVirtualized() throws Exception {
        String workingDir = System.getProperty("user.dir");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        // fm.appendVirtualized(new VirtualizerEntry(0,1,2,3,(short)4,(short)5,(short)6,(short)7,(short)8,(short)9,(short)10,(short)11,(short)12,13,(short) 14));
    }
}