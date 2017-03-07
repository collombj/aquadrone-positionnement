package fr.onema.lib.virtualizer.entry;

import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_attitude;

import java.io.IOException;
import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.*;

public class VirtualizerEntryTest {
    @Test
    public void testConstructorNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertNotNull(virtual);
    }

    @Test
    public void missingTests() {
        VirtualizerEntry v = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertTrue(v.equals(v));
        assertFalse(v.equals(null));
        VirtualizerEntry v2 = new VirtualizerEntry(0, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v3 = new VirtualizerEntry(1, 0, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v4 = new VirtualizerEntry(1, 2, 0, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v5 = new VirtualizerEntry(1, 2, 3, 0, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v6 = new VirtualizerEntry(1, 2, 3, 4, (short) 0, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v7 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 0, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v8 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 0, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v9 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 0, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v10 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 0, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v11 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 0, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v12 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 0, (short) 12, (short) 13, 14, (short) 15);
        VirtualizerEntry v16 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 0, (short) 13, 14, (short) 15);
        VirtualizerEntry v13 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 0, 14, (short) 15);
        VirtualizerEntry v14 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 0, (short) 15);
        VirtualizerEntry v15 = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 0);
        assertFalse(v.equals(v2));
        assertFalse(v.equals(v3));
        assertFalse(v.equals(v4));
        assertFalse(v.equals(v5));
        assertFalse(v.equals(v6));
        assertFalse(v.equals(v7));
        assertFalse(v.equals(v8));
        assertFalse(v.equals(v9));
        assertFalse(v.equals(v10));
        assertFalse(v.equals(v11));
        assertFalse(v.equals(v12));
        assertFalse(v.equals(v13));
        assertFalse(v.equals(v14));
        assertFalse(v.equals(v15));
        assertFalse(v.equals(v16));
        assertEquals(v.hashCode(), v.hashCode());
        assertNotEquals(v.hashCode(), v2.hashCode());
    }

    @Test
    public void testConstructorGetter() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, 8, 9, 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals(2, virtual.getGpsLat());
        assertEquals(3, virtual.getGpsLon());
        assertEquals(4, virtual.getGpsAlt());
        assertEquals((short) 5, virtual.getXacc());
        assertEquals((short) 6, virtual.getYacc());
        assertEquals((short) 7, virtual.getZacc());
        assertEquals(8.0, virtual.getRoll(), 0);
        assertEquals(9.0, virtual.getPitch(), 0);
        assertEquals(10.0, virtual.getYaw(), 0);
        assertEquals((short) 11, virtual.getXmag());
        assertEquals((short) 12, virtual.getYmag());
        assertEquals((short) 13, virtual.getZmag());
        assertEquals(14, virtual.getPressure(), 0);
        assertEquals((short) 15, virtual.getTemperature());
        msg_attitude msg = virtual.getAttitudeMessage(0);
        assertEquals(8.0, msg.roll, 0);
        assertEquals(9.0, msg.pitch, 0);
        assertEquals(10.0, msg.yaw, 0);
    }

    @Test
    public void testConstructorGetter2() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals((short) 5, virtual.getXacc());
        assertEquals((short) 6, virtual.getYacc());
        assertEquals((short) 7, virtual.getZacc());
        assertEquals(8.0, virtual.getRoll(), 0);
        assertEquals(9.0, virtual.getPitch(), 0);
        assertEquals(10.0, virtual.getYaw(), 0);
        assertEquals((short) 11, virtual.getXmag());
        assertEquals((short) 12, virtual.getYmag());
        assertEquals((short) 13, virtual.getZmag());
        assertEquals(14, virtual.getPressure(), 0);
        assertEquals((short) 15, virtual.getTemperature());
    }

    @Test
    public void testMavLinkMessageNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertNotNull(virtual.getGPSMessage());
        assertNotNull(virtual.getIMUMessage(0));
        assertNotNull(virtual.getPressureMessage(0));
        assertNotNull(virtual.getTemperatureMessage(0));
    }

    @Test
    public void constructorNotNull() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1, 1, 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, 1, (short) 1);
        assertNotNull(ref);
    }

    @Test
    public void toCSVTest() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1, 1, 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, 1, (short) 1);
        assertEquals("1,1,1,1,1,1,1,1.0,1.0,1.0,1,1,1,1.0,1", ref.toCSV());
    }

    @Test
    public void getCSVHeaderTest() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1, 1, 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, (short) 1, 1, (short) 1);
        assertEquals("timestamp,gpsLatitude,gpsLongitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,roll,pitch,yaw,capX,capY,capZ,pression,temperature", ref.getCSVHeader());
    }

    @Test
    public void hasGPSTrue() throws IOException {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5000, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertTrue(virtual.getHasGPS());
    }

    @Test
    public void hasGPSFalse() throws IOException {
        VirtualizerEntry virtual = new VirtualizerEntry(1, (short) 5000, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertFalse(virtual.getHasGPS());
    }

    @Test
    public void messagesNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(null, null, null, new ArrayList<>());
        assertEquals(0, virtual.getGpsLat());
        assertEquals(0, virtual.getGpsLon());
        assertEquals(0, virtual.getGpsAlt());
        assertEquals(0, virtual.getXacc());
        assertEquals(0, virtual.getYacc());
        assertEquals(0, virtual.getZacc());
        assertEquals(0, virtual.getRoll(), 0);
        assertEquals(0, virtual.getPitch(), 0);
        assertEquals(0, virtual.getYaw(), 0);
        assertEquals(0, virtual.getXmag());
        assertEquals(0, virtual.getYmag());
        assertEquals(0, virtual.getZmag());
        assertEquals(0, virtual.getPressure(), 0);
    }
}