package fr.onema.lib.geo;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by julien on 07/02/2017.
 */
public class GPSCoordinateTest {

    @Test
    public void testConstructor() {
        GPSCoordinate test = new GPSCoordinate(2,3,4);
        assertEquals(2, test.lat);
        assertEquals(3, test.lon);
        assertEquals(4, test.alt);
    }
}
