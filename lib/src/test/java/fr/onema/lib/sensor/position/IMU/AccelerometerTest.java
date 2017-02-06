package fr.onema.lib.sensor.position.IMU;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by strock on 06/02/2017.
 */
public class AccelerometerTest {


    @Test
    public void getxAcceleration() throws Exception {
        Accelerometer a= new Accelerometer(3,4,5);
       assertEquals(3,a.getxAcceleration());


    }

    @Test
    public void getyAcceleration() throws Exception {
        Accelerometer a= new Accelerometer(3,4,5);
        assertEquals(4,a.getyAcceleration());

    }

    @Test
    public void getzAcceleration() throws Exception {
        Accelerometer a= new Accelerometer(3,4,5);
        assertEquals(5,a.getzAcceleration());

    }

}