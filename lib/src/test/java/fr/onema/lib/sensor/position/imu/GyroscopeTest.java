package fr.onema.lib.sensor.position.imu;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by strock on 06/02/2017.
 */
public class GyroscopeTest {


    @Test
    public void getRoll() throws Exception {
        Gyroscope a = new Gyroscope(3, 4, 5);
        assertEquals(3.0, a.getRoll(), 0);

    }

    @Test
    public void getPitch() throws Exception {
        Gyroscope a = new Gyroscope(3, 4, 5);
        assertEquals(4.0, a.getPitch(), 0);

    }

    @Test
    public void getYaw() throws Exception {
        Gyroscope a = new Gyroscope(3, 4, 5);
        assertEquals(5.0, a.getYaw(), 0);

    }

}