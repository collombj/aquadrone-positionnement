package fr.onema.lib.virtualizer.entry;

import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_attitude;

import java.io.IOException;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

/**
 * Created by Theo on 06/02/2017.
 */
public class VirtualizerEntryTest {
    @Test
    public void testConstructorNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,2,3,4, 5, 6,7,8,9,10,11,12,13, 14,15);
        assertNotNull(virtual);
    }

    @Test
    public void testConstructorGetter() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,2,3,4, 5, 6,7,8,9,10,11,12,13, 14,15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals(2, virtual.getGpsLat());
        assertEquals(3, virtual.getGpsLon());
        assertEquals(4, virtual.getGpsAlt());
        assertEquals(5, virtual.getXacc());
        assertEquals(6, virtual.getYacc());
        assertEquals(7, virtual.getZacc());
        assertEquals(8.0, virtual.getRoll(), 0);
        assertEquals(9.0, virtual.getPitch(), 0);
        assertEquals(10.0, virtual.getYaw(), 0);
        assertEquals(11, virtual.getXmag());
        assertEquals(12, virtual.getYmag());
        assertEquals(13, virtual.getZmag());
        assertEquals(14, virtual.getPressure(), 0);
        assertEquals(15, virtual.getTemperature());
        msg_attitude msg = virtual.getAttitudeMessage();
        assertEquals(8.0, msg.roll, 0);
        assertEquals(9.0, msg.pitch, 0);
        assertEquals(10.0, msg.yaw, 0);
    }

    @Test
    public void testConstructorGetter2() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 5, 6,7,8,9,10,11,12,13, 14,15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals(5, virtual.getXacc());
        assertEquals(6, virtual.getYacc());
        assertEquals(7, virtual.getZacc());
        assertEquals(8.0, virtual.getRoll(), 0);
        assertEquals(9.0, virtual.getPitch(), 0);
        assertEquals(10.0, virtual.getYaw(), 0);
        assertEquals(11, virtual.getXmag());
        assertEquals(12, virtual.getYmag());
        assertEquals(13, virtual.getZmag());
        assertEquals(14, virtual.getPressure(), 0);
        assertEquals(15, virtual.getTemperature());
    }

    @Test
    public void testMavLinkMessageNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,2,3,4, 5, 6,7,8,9,10,11,12,13, 14,15);
        assertNotNull(virtual.getGPSMessage());
        assertNotNull(virtual.getIMUMessage());
        assertNotNull(virtual.getPressureMessage());
        assertNotNull(virtual.getTemperatureMessage());
    }

    @Test
    public void constructorNotNull() {
        VirtualizerEntry ref = new VirtualizerEntry(1,1,1,1,1,1,1,1,1,1,1,1,1,1,1);
        assertNotNull(ref);
    }

    @Test
    public void toCSVTest() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1,1,1, 1, 1, 1,1,1,1,1,1,1,1,1);
        assertEquals("1,1,1,1,1,1,1,1.0,1.0,1.0,1,1,1,1.0,1", ref.toCSV());
    }

    @Test
    public void getCSVHeaderTest() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1,1,1, 1, 1, 1,1,1,1,1,1,1,1,1);
        assertEquals("timestamp,gpsLongitude,gpsLatitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,roll,pitch,yaw,capX,capY,capZ,pression,temperature", ref.getCSVHeader());
    }

    @Test
    public void hasGPSTrue() throws IOException {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2,3,4, 5000, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        assertTrue(virtual.getHasGPS());
    }

    @Test
    public void hasGPSFalse() throws IOException {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 5000, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        assertFalse(virtual.getHasGPS());
    }
}
