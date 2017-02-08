package fr.onema.lib;

import fr.onema.simulator.Virtualizer;
import org.junit.Test;

/**
 * Created by Jérôme on 08/02/2017.
 */
public class VirtualizerTest {
    //
    @Test (expected = NullPointerException.class)
    public void testNullFileManager(){
        Virtualizer v = new Virtualizer(null, 100, "null");
        v.start();
    }


}
