package fr.onema.lib.geo;

import fr.onema.lib.sensor.position.IMU.Accelerometer;
import junit.framework.TestCase;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by julien on 07/02/2017.
 */
public class GeoMathsTest {

    @Test(expected = NullPointerException.class)
    public void testCartesianDistanceNonNull1() {
        CartesianCoordinate pos1 = null;
        CartesianCoordinate pos2 = new CartesianCoordinate(2,3,4);

        GeoMaths.cartesianDistance(pos1, pos2);
    }

    @Test(expected = NullPointerException.class)
    public void testCartesianDistanceNonNull2() {
        CartesianCoordinate pos1 = null;
        CartesianCoordinate pos2 = new CartesianCoordinate(2,3,4);

        GeoMaths.cartesianDistance(pos2, pos1);
    }

    @Test
    public void testCartesianDistance() {
        CartesianCoordinate pos1 = new CartesianCoordinate(2,3,5);
        CartesianCoordinate pos2 = new CartesianCoordinate(2,3,4);

        assertEquals(1.0, GeoMaths.cartesianDistance(pos1, pos2));
    }

    @Test(expected = NullPointerException.class)
    public void testGPSDistanceNonNull1() {
        GPSCoordinate pos1 = new GPSCoordinate(1,2,3);
        GPSCoordinate pos2 = null;
        GeoMaths.gpsDistance(pos1, pos2);
    }

    @Test(expected = NullPointerException.class)
    public void testGPSDistanceNonNull2() {
        GPSCoordinate pos1 = null;
        GPSCoordinate pos2 = new GPSCoordinate(1,2,3) ;
        GeoMaths.gpsDistance(pos1, pos2);

    }

    @Test
    public void testDeg2Rad() {
        assertEquals(0.0174533, GeoMaths.deg2rad(1), 0.001);
        assertEquals(0.785398, GeoMaths.deg2rad(45), 0.001);
        assertEquals(2.87979, GeoMaths.deg2rad(165), 0.001);
    }

    @Test
    public void testRad2Deg() {
        assertEquals(1.0, GeoMaths.rad2deg(0.0174533), 0.01);
        assertEquals(45.0, GeoMaths.rad2deg(0.785398), 0.01);
        assertEquals(165.0, GeoMaths.rad2deg(2.87979), 0.01);
    }
/*
    @Test
    public void testComputeXYZFromLatLonAlt() {
        CartesianCoordinate cartesianCoordinate = GeoMaths.computeXYZfromLatLonAlt(GeoMaths.deg2rad(45),GeoMaths.deg2rad(45), 130);
        assertEquals(3194484.14506057, cartesianCoordinate.x, 0.5);
        assertEquals(3194484.14506057, cartesianCoordinate.y, 0.5);
        assertEquals(4487440.33268369, cartesianCoordinate.z, 0.5);
    }
    */

    @Test(expected = NullPointerException.class)
    public void testComputeCartesianPositionNonNull1() {
        GPSCoordinate gps1 = null;
        GPSCoordinate gps2 = new GPSCoordinate(450_000_000, 450_000_000, 130_000);

        GeoMaths.computeCartesianPosition(gps1, gps2);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeCartesianPositionNonNull2() {
        GPSCoordinate gps1 = null;
        GPSCoordinate gps2 = new GPSCoordinate(450_000_000, 450_000_000, 130_000);

        GeoMaths.computeCartesianPosition(gps2, gps1);
    }

    @Test
    public void testComputeCartesianPosition() {
        GPSCoordinate gps1 = new GPSCoordinate(450_000_500, 450_000_000, 130_000);
        GPSCoordinate gps2 = new GPSCoordinate(450_000_000, 450_000_000, 130_000);

        CartesianCoordinate cartesianCoordinate = GeoMaths.computeCartesianPosition(gps1, gps2);
        assertEquals(2.77, cartesianCoordinate.x, 0.01);
        assertEquals(2.77, cartesianCoordinate.y, 0.01);
        assertEquals(-3.92, cartesianCoordinate.z, 0.01);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeCartesianVelocityNonNull() {
        CartesianCoordinate pos1 = null;
        GeoMaths.computeVelocityFromCartesianCoordinate(pos1, 100);
    }

    @Test
    public void testComputeCartesianVelocity() {
        CartesianCoordinate pos1 = new CartesianCoordinate(2, 1, 1);
        CartesianVelocity velocity = GeoMaths.computeVelocityFromCartesianCoordinate(pos1, 100);

        assertEquals(20.0, velocity.vx);
        assertEquals(10.0, velocity.vy);
        assertEquals(10.0, velocity.vz);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeAccelerometerDataNonNull1() {
        CartesianVelocity velocity1 = null;
        CartesianVelocity velocity2 = new CartesianVelocity(2, 1, -1);

        GeoMaths.computeAccelerometerData(velocity1, velocity2, 1000);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeAccelerometerDataNonNull2() {
        CartesianVelocity velocity1 = null;
        CartesianVelocity velocity2 = new CartesianVelocity(2, 1, -1);

        GeoMaths.computeAccelerometerData(velocity2, velocity1, 1000);
    }

    @Test
    public void testComputeAccelerometerData() {
        CartesianVelocity velocity1 = new CartesianVelocity(0, 0, 0);
        CartesianVelocity velocity2 = new CartesianVelocity(2, 1, -1);

        Accelerometer accelerometer = GeoMaths.computeAccelerometerData(velocity1, velocity2, 1000);

        assertEquals(2.0*0.101972*1000, accelerometer.getxAcceleration(), 1);
        assertEquals(1.0*0.1019*1000, accelerometer.getyAcceleration(), 1);
        assertEquals(-1.0*0.1019*1000, accelerometer.getzAcceleration(), 1);


        velocity1 = new CartesianVelocity(3, 1, -1);
        velocity2 = new CartesianVelocity(4, 0, -4);

        accelerometer = GeoMaths.computeAccelerometerData(velocity1, velocity2, 1000);

        assertEquals(1.0*0.101972*1000, accelerometer.getxAcceleration(), 1);
        assertEquals(-1*0.1019*1000, accelerometer.getyAcceleration(), 1);
        assertEquals(-3.0*0.1019*1000, accelerometer.getzAcceleration(), 1);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeGPSCoordinateNonNull1() {
        GPSCoordinate gps = null;
        CartesianCoordinate cc = new CartesianCoordinate(1,2,3);

        GeoMaths.computeGPSCoordinateFromCartesian(gps, cc);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeGPSCoordinateNonNull2() {
        GPSCoordinate gps = new GPSCoordinate(1,2,3);
        CartesianCoordinate cc = null;

        GeoMaths.computeGPSCoordinateFromCartesian(gps, cc);
    }

    @Test
    public void testComputeGPSCoordinate() {

        GPSCoordinate ref = new GPSCoordinate(450_000_000, 450_000_000, 130_000);
        GPSCoordinate test = new GPSCoordinate(450_000_500, 450_000_500, 130_000);


        CartesianCoordinate cartesianCoordinate = GeoMaths.computeCartesianPosition(ref, test);

        GPSCoordinate gpsCoordinate = GeoMaths.computeGPSCoordinateFromCartesian(ref, cartesianCoordinate);

        assertEquals(test.lat, gpsCoordinate.lat);
        assertEquals(test.lon, gpsCoordinate.lon);
        assertEquals(test.alt, gpsCoordinate.alt);
    }
}
