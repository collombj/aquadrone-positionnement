package fr.onema.simulator.Virtualizer.Entry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by Theo on 06/02/2017.
 */
public class VirtualizerEntryTest {
    @Test
    public void testConstructorNotNull() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertNotNull(virtual);
    }

    @Test
    public void testConstructorGetter() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertEquals(1, virtual.getTimestamp());
        assertEquals(2, virtual.getGpsLat());
        assertEquals(3, virtual.getGpsLon());
        assertEquals(4, virtual.getGpsAlt());
        assertEquals((short) 5, virtual.getXacc());
        assertEquals((short) 6, virtual.getYacc());
        assertEquals((short) 7, virtual.getZacc());
        assertEquals((short) 8, virtual.getXgyro());
        assertEquals((short) 9, virtual.getYgyro());
        assertEquals((short) 10, virtual.getZgyro());
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
        assertNotNull(virtual.getIMUMessage());
        assertNotNull(virtual.getPressureMessage());
        assertNotNull(virtual.getTemperatureMessage());
    }

    @Test
    public void testCSV() {
        VirtualizerEntry virtual = new VirtualizerEntry(1, 2, 3, 4, (short) 5, (short) 6, (short) 7, (short) 8, (short) 9, (short) 10, (short) 11, (short) 12, (short) 13, 14, (short) 15);
        assertEquals(virtual.toCSV(), "1,2,3,4,5,6,7,8,9,10,11,12,13,14.0,15");
        assertEquals(virtual.getCSVHeader(), "timestamp, gps_lat, xacc, yacc, zacc, xgyro, ygyro, zgyro, xmag, ymag, zmag, pressure, temperature");
    }


}
