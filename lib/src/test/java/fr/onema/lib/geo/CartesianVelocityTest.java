package fr.onema.lib.geo;

import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by julien on 07/02/2017.
 */
public class CartesianVelocityTest {

    @Test
    public void testContructor() {
        CartesianVelocity velocity = new CartesianVelocity(2, 3, 4);
        assertNotNull(velocity);
        assertEquals(2.0, velocity.vx);
        assertEquals(3.0, velocity.vy);
        assertEquals(4.0, velocity.vz);
    }
}
