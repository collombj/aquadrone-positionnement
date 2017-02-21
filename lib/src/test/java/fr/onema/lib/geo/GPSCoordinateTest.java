package fr.onema.lib.geo;

import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by julien on 07/02/2017.
 */
public class GPSCoordinateTest {

    @Test
    public void testConstructor() {
        GPSCoordinate g = new GPSCoordinate(2,3,4);
        GPSCoordinate g2 = new GPSCoordinate(0,3,4);
        GPSCoordinate g3 = new GPSCoordinate(2,0,4);
        GPSCoordinate g4 = new GPSCoordinate(2,3,0);
        assertEquals(2, g.lat);
        assertEquals(3, g.lon);
        assertEquals(4, g.alt);
        assertTrue(g.equals(g));
        assertFalse(g.equals(null));
        assertFalse(g.equals(1));
        assertFalse(g.equals(g2));
        assertFalse(g.equals(g3));
        assertFalse(g.equals(g4));
    }
}
