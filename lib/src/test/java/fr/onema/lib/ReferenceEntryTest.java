package fr.onema.lib;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ReferenceEntryTest {
    private ReferenceEntry entry = new ReferenceEntry(1,2,3,4,5,6);

    @Test
    public void testGetTemperature() {
        assertEquals(entry.getTemperature(), 6);
    }

    @Test
    public void testGetDirection() {
        assertEquals(entry.getDirection(), 5);
    }

    @Test
    public void testGetAlt() {
        assertEquals(entry.getAlt(), 4);
    }

    @Test
    public void testGetLon() {
        assertEquals(entry.getLon(), 3);
    }

    @Test
    public void testGetLat() {
        assertEquals(entry.getLat(), 2);
    }

    @Test
    public void testGetTimestamp() {
        assertEquals(entry.getTimestamp(), 1);
    }
}
