package fr.onema.lib.sensor.position.IMU;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by strock on 06/02/2017.
 */
public class GyroscopeTest {


    @Test
    public void getxRotation() throws Exception {
        Gyroscope a= new Gyroscope(3,4,5);
        assertEquals(3,a.getxRotation());

    }

    @Test
    public void getyRotation() throws Exception {
        Gyroscope a= new Gyroscope(3,4,5);
        assertEquals(4,a.getyRotation());

    }

    @Test
    public void getzRotation() throws Exception {
        Gyroscope a= new Gyroscope(3,4,5);
        assertEquals(5,a.getzRotation());

    }

}