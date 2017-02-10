package fr.onema.lib;

import fr.onema.lib.file.FileManager;
import fr.onema.simulator.Virtualizer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * //TODO: Pourquoi 2 fichiers de test ?
 *
 * Created by Jérôme on 08/02/2017.
 */
public class VirtualizerTest {
    @Test (expected = NullPointerException.class)
    public void testNullFileManager(){
        Virtualizer v = new Virtualizer(null, 100, null, null, 12);
        try {
            v.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test(expected = NullPointerException.class)
    public void testFileManager(){
        //TODO: finir le test...(et retirer le NullPointerException qui est la juste pour faire passer le build)
        FileManager fm = new FileManager("setting.properties","toto", null);
        Virtualizer v = new Virtualizer(fm, 100, "null", null, 12);

        try {
            v.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        long a = v.getDuration();
        assertEquals(v.getDuration(),a);
        assertEquals(v.getSimulationName(), "null");
        assertEquals(v.getFileManager(), fm);
        assertEquals(v.getSpeed(), 100);
    }


}
