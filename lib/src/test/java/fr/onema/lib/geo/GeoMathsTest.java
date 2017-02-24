package fr.onema.lib.geo;


import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.position.IMU.Accelerometer;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static fr.onema.lib.geo.GeoMaths.recalculatePosition;
import static junit.framework.TestCase.assertEquals;


public class GeoMathsTest {

    @Test(expected = NullPointerException.class)
    public void testCartesianDistanceNonNull1() {
        CartesianCoordinate pos1 = null;
        CartesianCoordinate pos2 = new CartesianCoordinate(2, 3, 4);

        GeoMaths.cartesianDistance(pos1, pos2);
    }

    @Test(expected = NullPointerException.class)
    public void testCartesianDistanceNonNull2() {
        CartesianCoordinate pos1 = null;
        CartesianCoordinate pos2 = new CartesianCoordinate(2, 3, 4);

        GeoMaths.cartesianDistance(pos2, pos1);
    }

    @Test
    public void testCartesianDistance() {
        CartesianCoordinate pos1 = new CartesianCoordinate(2, 3, 5);
        CartesianCoordinate pos2 = new CartesianCoordinate(2, 3, 4);

        assertEquals(1.0, GeoMaths.cartesianDistance(pos1, pos2));
    }

    @Test(expected = NullPointerException.class)
    public void testGPSDistanceNonNull1() {
        GPSCoordinate pos1 = new GPSCoordinate(1, 2, 3);
        GPSCoordinate pos2 = null;
        GeoMaths.gpsDistance(pos1, pos2);
    }

    @Test(expected = NullPointerException.class)
    public void testGPSDistanceNonNull2() {
        GPSCoordinate pos1 = null;
        GPSCoordinate pos2 = new GPSCoordinate(1, 2, 3);
        GeoMaths.gpsDistance(pos1, pos2);

    }

    @Test
    public void testGPSDistance() {
        GPSCoordinate gps1 = new GPSCoordinate(450_000_500, 450_000_000, 130_000);
        GPSCoordinate gps2 = new GPSCoordinate(450_000_000, 450_000_000, 130_000);


        assertEquals(GeoMaths.cartesianDistance(new CartesianCoordinate(0, 0, 0), GeoMaths.computeCartesianPosition(gps1, gps2)),
                GeoMaths.gpsDistance(gps1, gps2), 0.001);
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
        GeoMaths.computeVelocityFromCartesianCoordinate(pos1, pos1, 100);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeCartesianVelocityNonNull2() {
        CartesianCoordinate refPos = new CartesianCoordinate(0, 0, 0);
        CartesianCoordinate pos1 = null;
        GeoMaths.computeVelocityFromCartesianCoordinate(refPos, pos1, 100);
    }

    @Test
    public void testComputeCartesianVelocity() {
        CartesianCoordinate refPos = new CartesianCoordinate(0, 0, 0);
        CartesianCoordinate pos1 = new CartesianCoordinate(2, 1, 1);
        CartesianVelocity velocity = GeoMaths.computeVelocityFromCartesianCoordinate(refPos, pos1, 100);

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

        assertEquals(2.0 * 0.101972 * 1000, accelerometer.getxAcceleration(), 1);
        assertEquals(1.0 * 0.1019 * 1000, accelerometer.getyAcceleration(), 1);
        assertEquals(-1.0 * 0.1019 * 1000, accelerometer.getzAcceleration(), 1);


        velocity1 = new CartesianVelocity(3, 1, -1);
        velocity2 = new CartesianVelocity(4, 0, -4);

        accelerometer = GeoMaths.computeAccelerometerData(velocity1, velocity2, 1000);

        assertEquals(1.0 * 0.101972 * 1000, accelerometer.getxAcceleration(), 1);
        assertEquals(-1 * 0.1019 * 1000, accelerometer.getyAcceleration(), 1);
        assertEquals(-3.0 * 0.1019 * 1000, accelerometer.getzAcceleration(), 1);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeGPSCoordinateNonNull1() {
        GPSCoordinate gps = null;
        CartesianCoordinate cc = new CartesianCoordinate(1, 2, 3);

        GeoMaths.computeGPSCoordinateFromCartesian(gps, cc);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeGPSCoordinateNonNull2() {
        GPSCoordinate gps = new GPSCoordinate(1, 2, 3);
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

    @Test(expected = NullPointerException.class)
    public void testComputeNewPositionNPE1() {
        GeoMaths.computeNewPosition(null, 0,0,0, null, 12, null);


    }

    @Test(expected = NullPointerException.class)
    public void testComputeNewPositionNPE2() {
        CartesianCoordinate coordinate = new CartesianCoordinate(0,0,0);
        GeoMaths.computeNewPosition(coordinate, 0,0,0, null, 12, null);
    }

    @Test(expected = NullPointerException.class)
    public void testComputeNewPositionNPE3() {
        CartesianCoordinate coordinate = new CartesianCoordinate(0,0,0);
        CartesianVelocity velocity = new CartesianVelocity(0,0,0);
        GeoMaths.computeNewPosition(coordinate, 0,0,0, velocity, 12, null);


    }

    @Test
    public void testComputeNewPosition() {
        GPSCoordinate ref = new GPSCoordinate(450_000_000, 450_000_000, 130_000);
        GPSCoordinate test = new GPSCoordinate(450_000_500, 450_000_500, 130_000);

        CartesianCoordinate cartesianCoordinate = GeoMaths.computeCartesianPosition(ref, test);

        CartesianCoordinate newPo = GeoMaths.computeNewPosition(cartesianCoordinate, 0, -Math.PI / 2, 0, new CartesianVelocity(3, 0, 0), 500, new Accelerometer(0, 0, 0)).getCoordinate();

        assertEquals(cartesianCoordinate.x, newPo.x, 0.0001);
        assertEquals(cartesianCoordinate.y, newPo.y, 0.0001);
        assertEquals(cartesianCoordinate.z - 1.5, newPo.z, 0.0001);
    }

    @Test
    public void testComputeNewPosition2() {
        GPSCoordinate ref = new GPSCoordinate(450_000_000, 450_000_000, 130_000);
        GPSCoordinate test = new GPSCoordinate(450_000_500, 450_000_500, 130_000);

        CartesianCoordinate cartesianCoordinate = GeoMaths.computeCartesianPosition(ref, test);

        CartesianCoordinate newPo = GeoMaths.computeNewPosition(cartesianCoordinate, 0, 0, -Math.PI / 2, new CartesianVelocity(3, 0, 0), 500, new Accelerometer(0, 0, 0)).getCoordinate();

        assertEquals(cartesianCoordinate.x + 1.5, newPo.x, 0.0001);
        assertEquals(cartesianCoordinate.y, newPo.y, 0.0001);
        assertEquals(cartesianCoordinate.z, newPo.z, 0.0001);
    }


    @Test(expected = NullPointerException.class)
    public void testRecalculaterawpositionNull() {

        recalculatePosition(null, new GPSCoordinate(3,3,1), new GPSCoordinate(3,3,3)) ;

    }
    @Test(expected = NullPointerException.class)
    public void testRecalculaterawpositionNull1() {

        Position p1 = new Position(5);
        Position p2 = new Position(5);

        List<Position> list= new ArrayList<>();
        list.add(p1);
        list.add(p2);

        recalculatePosition(list, null, new GPSCoordinate(3,3,3)) ;

    }
    @Test(expected = NullPointerException.class)
    public void testRecalculaterawpositionNull2() {
        Position p1 = new Position(5);
        Position p2 = new Position(5);

        List<Position> list = new ArrayList<>();
        list.add(p1);
        list.add(p2);

        recalculatePosition(list, new GPSCoordinate(3,3,1), null) ;

    }
}
