package fr.onema.lib.geo;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


/**
 * Created by julien on 07/02/2017.
 */
public class CartesianCoordinateTest {

    @Test
    public void testConstructor() {
        CartesianCoordinate test = new CartesianCoordinate(2,3,4);
        assertEquals(2.0, test.x);
        assertEquals(3.0, test.y);
        assertEquals(4.0, test.z);
    }
}
