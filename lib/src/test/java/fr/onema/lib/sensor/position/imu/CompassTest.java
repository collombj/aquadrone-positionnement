package fr.onema.lib.sensor.position.imu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by strock on 06/02/2017.
 */
public class CompassTest {


    @Test
    public void getxMagnetic() throws Exception {
        Compass a= new Compass(3,4,5);
        assertEquals(3,a.getxMagnetic());

    }

    @Test
    public void getyMagnetic() throws Exception {
        Compass a= new Compass(3,4,5);
        assertEquals(4,a.getyMagnetic());

    }

    @Test
    public void getzMagnetic() throws Exception {
        Compass a= new Compass(3,4,5);
        assertEquals(5,a.getzMagnetic());

    }

}