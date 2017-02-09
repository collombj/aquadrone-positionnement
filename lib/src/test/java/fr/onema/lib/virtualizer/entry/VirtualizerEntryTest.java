package fr.onema.lib.virtualizer.entry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Theo on 06/02/2017.
 */
public class VirtualizerEntryTest {
    @Test
    public void testConstructorNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,2,3,4,(short) 5, (short)6,(short)7,(short)8,(short)9,(short)10,(short)11,(short)12,(short)13, 14,(short)15);
        assertNotNull(virtual);
    }

    @Test
    public void testConstructorGetter() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,2,3,4,(short) 5, (short)6,(short)7,(short)8,(short)9,(short)10,(short)11,(short)12,(short)13, 14,(short)15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals(2, virtual.getGPSLat());
        assertEquals(3, virtual.getGPSLon());
        assertEquals(4, virtual.getGPSAlt());
        assertEquals((short)5, virtual.getXacc());
        assertEquals((short)6, virtual.getYacc());
        assertEquals((short)7, virtual.getZacc());
        assertEquals((short)8, virtual.getXgyro());
        assertEquals((short)9, virtual.getYgyro());
        assertEquals((short)10, virtual.getZgyro());
        assertEquals((short)11, virtual.getXmag());
        assertEquals((short)12, virtual.getYmag());
        assertEquals((short)13, virtual.getZmag());
        assertEquals(14, virtual.getPressure(), 0);
        assertEquals((short)15, virtual.getTemperature());
    }

    @Test
    public void testConstructorGetter2() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,(short) 5, (short)6,(short)7,(short)8,(short)9,(short)10,(short)11,(short)12,(short)13, 14,(short)15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals((short)5, virtual.getXacc());
        assertEquals((short)6, virtual.getYacc());
        assertEquals((short)7, virtual.getZacc());
        assertEquals((short)8, virtual.getXgyro());
        assertEquals((short)9, virtual.getYgyro());
        assertEquals((short)10, virtual.getZgyro());
        assertEquals((short)11, virtual.getXmag());
        assertEquals((short)12, virtual.getYmag());
        assertEquals((short)13, virtual.getZmag());
        assertEquals(14, virtual.getPressure(), 0);
        assertEquals((short)15, virtual.getTemperature());
    }

    @Test
    public void testMavLinkMessageNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1,2,3,4,(short) 5, (short)6,(short)7,(short)8,(short)9,(short)10,(short)11,(short)12,(short)13, 14,(short)15);
        assertNotNull(virtual.getGPSMessage());
        assertNotNull(virtual.getIMUMessage());
        assertNotNull(virtual.getPressureMessage());
        assertNotNull(virtual.getTemperatureMessage());
    }

    @Test
    public void constructorNotNull() {
        VirtualizerEntry ref = new VirtualizerEntry(1,1,1,1,(short)1,(short)1,(short)1,(short)1,(short)1,(short)1,(short)1,(short)1,(short)1,1,(short)1);
        assertNotNull(ref);
    }

    @Test
    public void toCSVTest() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1,1,1, (short)1, (short)1, (short)1,(short)1,(short)1,(short)1,(short)1,(short)1,(short)1,1,(short)1);
        assertEquals("1,1,1,1,1,1,1,1,1,1,1,1,1,1.0,1", ref.toCSV());
    }

    @Test
    public void getCSVHeaderTest() {
        VirtualizerEntry ref = new VirtualizerEntry(1, 1,1,1, (short)1, (short)1, (short)1,(short)1,(short)1,(short)1,(short)1,(short)1,(short)1,1,(short)1);
        assertEquals("timestamp,gpsLongitude,gpsLatitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,rotationX,rotationY,rotationZ,capX,capY,capZ,pression,temperature", ref.getCSVHeader());
    }

}
