package fr.onema.lib;

import fr.onema.lib.file.FileManager;
import fr.onema.simulator.Virtualizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jérôme on 08/02/2017.
 */
public class VirtualizerTest {
    @Test (expected = NullPointerException.class)
    public void testNullFileManager(){
        Virtualizer v = new Virtualizer(null, 100, "null");
        v.start();

    }

    @Test
    public void testFileManager(){
        FileManager fm = new FileManager("setting.properties","toto");
        Virtualizer v = new Virtualizer(fm, 100, "null");
        v.start();
        long a = v.getDuration();
        assertEquals(v.getDuration(),a);
        assertEquals(v.getSimulationName(), "null");
        assertEquals(v.getFileManager(), fm);
        assertEquals(v.getSpeed(), 100);
    }


}
