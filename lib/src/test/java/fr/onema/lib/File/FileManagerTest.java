package fr.onema.lib.File;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by you on 07/02/2017.
 */
public class FileManagerTest {
    @Test
    public void readReferenceEntries() throws Exception {
        String workingDir = System.getProperty("user.dir");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
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
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
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

    // TODO : complete
/*
    @Test
    public void appendRaw() throws Exception {

    }

    @Test
    public void appendComputed() throws Exception {

    }
*/
}