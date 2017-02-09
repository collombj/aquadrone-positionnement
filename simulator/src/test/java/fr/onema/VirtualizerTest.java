package fr.onema;

import fr.onema.lib.file.FileManager;
import fr.onema.simulator.Virtualizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jérôme on 09/02/2017.
 */
public class VirtualizerTest {
    @Test
    void testNoFileManager(){
        Virtualizer v = new Virtualizer(null, 100, "aaa", "test", 5432);
    }

    @Test
    void testNoSimulationName(){
        //TODO Mettre une valeur
        FileManager fm = null;
        Virtualizer v = new Virtualizer(fm, 100, null, "test", 5432);
    }

    @Test
    void testNoHost(){
        //TODO Mettre une valeur
        FileManager fm = null;
        Virtualizer v = new Virtualizer(fm, 100, "aaa", null, 5432);
    }

    @Test
    void testNoSpeed(){
        //TODO Mettre une valeur
        FileManager fm = null;
        Virtualizer v = new Virtualizer(fm, 0, "aaa", "test", 5432);
    }

    @Test
    void testNo(){
        //TODO Mettre une valeur
        FileManager fm = null;
        Virtualizer v = new Virtualizer(fm, 100, "aaa", "test", 5432);
    }
}
