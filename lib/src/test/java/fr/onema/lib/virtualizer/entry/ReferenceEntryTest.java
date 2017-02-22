package fr.onema.lib.virtualizer.entry;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReferenceEntryTest {

    @Test
    public void missingTests() {
        ReferenceEntry ref = new ReferenceEntry(1, 1, 1, 1, 1, (short)1);
        assertTrue(ref.equals(ref));
        assertFalse(ref.equals(null));
        ReferenceEntry ref2 = new ReferenceEntry(2, 1, 1, 1, 1, (short)1);
        ReferenceEntry ref3 = new ReferenceEntry(1, 2, 1, 1, 1, (short)1);
        ReferenceEntry ref4 = new ReferenceEntry(1, 1, 2, 1, 1, (short)1);
        ReferenceEntry ref5 = new ReferenceEntry(1, 1, 1, 2, 1, (short)1);
        ReferenceEntry ref6 = new ReferenceEntry(1, 1, 1, 1, 2, (short)1);
        ReferenceEntry ref7 = new ReferenceEntry(1, 1, 1, 1, 1, (short)2);
        assertFalse(ref.equals(ref2));
        assertFalse(ref.equals(ref3));
        assertFalse(ref.equals(ref4));
        assertFalse(ref.equals(ref5));
        assertFalse(ref.equals(ref6));
        assertFalse(ref.equals(ref7));
        assertEquals(ref.hashCode(), ref.hashCode());
        assertNotEquals(ref.hashCode(), ref2.hashCode());
    }

    @Test(expected=IllegalArgumentException.class)
    public void illegalArgument() {
        ReferenceEntry ref = new ReferenceEntry(-1, 1,1,1, 1, (short)1 );
    }

    @Test
    public void constructorNotNull() {
        ReferenceEntry ref = new ReferenceEntry(1, 1,1,1, 1, (short)1 );
        assertNotNull(ref);
    }

    @Test
    public void toCSVTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 1,1,1, 1, (short)1 );
        assertEquals("1,1,1,1,1.0,1", ref.toCSV());
    }

    @Test
    public void getCSVHeaderTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 1,1,1, 1, (short)1 );
        assertEquals("timestamp,latitude,longitude,altitude,direction,temperature", ref.getCSVHeader());
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructorTimestampNeg() {
        ReferenceEntry ref = new ReferenceEntry(-1, 1,1,1, 1, (short)1 );
    }

    @Test(expected=IllegalArgumentException.class)
    public void constructorDirectionNeg() {
        ReferenceEntry ref = new ReferenceEntry(1, 1,1,1, -4, (short)1 );
    }

    @Test
    public void toCSVForComputedFormatTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 1,2,3, 4, (short)1 );
        assertEquals("1,2,3,4.0", ref.toCSVforComputedFormat());
    }

    @Test
    public void getTimestampTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 2,3,4, 5, (short)6 );
        assertEquals(1,ref.getTimestamp());
    }

    @Test
    public void getLatTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 2,3,4, 5, (short)6 );
        assertEquals(2,ref.getLat());
    }

    @Test
    public void getLonTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 2,3,4, 5, (short)6 );
        assertEquals(3,ref.getLon());
    }

    @Test
    public void getAltTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 2,3,4, 5, (short)6 );
        assertEquals(4,ref.getAlt());
    }

    @Test
    public void getDirectionTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 2,3,4, 5, (short)6 );
        assertEquals(5.0,ref.getDirection(),0);
    }

    @Test
    public void getTemperatureTest() {
        ReferenceEntry ref = new ReferenceEntry(1, 2,3,4, 5, (short)6 );
        assertEquals(6,ref.getTemperature());
    }

}