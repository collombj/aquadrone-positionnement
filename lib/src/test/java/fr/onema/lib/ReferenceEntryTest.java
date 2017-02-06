package fr.onema.lib;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class ReferenceEntryTest {
    /**Positive Parameter Values**/
    private static ReferenceEntry entry = new ReferenceEntry(1,2,3,4,5,6);

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

    @Test
    public void testToCSV() {
        assertEquals(entry.toCSV(), "1,2,3,4,5,6");
    }

    /**Negative Parameter Values**/
    private static ReferenceEntry entryNegative = new ReferenceEntry(1,-2,-3,-4,5,-6);

    @Test
    public void testGetNegativeTemperature() {
        assertEquals(entryNegative.getTemperature(), -6);
    }

    @Test
    public void testGetNegativeAlt() {
        assertEquals(entryNegative.getAlt(), -4);
    }

    @Test
    public void testGetNegativeLon() {
        assertEquals(entryNegative.getLon(), -3);
    }

    @Test
    public void testGetNegativeLat() {
        assertEquals(entryNegative.getLat(), -2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeclarationNegativeTimestamp(){
        ReferenceEntry entryNegative = new ReferenceEntry(-1,-2,-3,-4,5,-6);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDeclarationNegativeDirection(){
        ReferenceEntry entryNegative = new ReferenceEntry(1,-2,-3,-4,-5,-6);
    }
}
