package fr.onema.lib.virtualizer.entry;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ReferenceEntryTest {
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

}