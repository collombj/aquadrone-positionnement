package fr.onema.lib.database.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Francois Vanderperre on 06/02/2017.
 *
 * Classe de test
 */
public class TestDiveEntity {

    @Test
    public void missingTests() {
        DiveEntity e = new DiveEntity(1, 2, 3);
        assertTrue(e.equals(e));
        assertFalse(e.equals(null));
        DiveEntity e2 = new DiveEntity(2, 3, 4);
        DiveEntity e3 = new DiveEntity(3, 4, 5);
        DiveEntity e4 = new DiveEntity(4, 5, 6);
        assertFalse(e.equals(e2));
        assertFalse(e.equals(e3));
        assertFalse(e.equals(e4));
        assertEquals(e.hashCode(), e.hashCode());
        assertNotEquals(e.hashCode(), e2.hashCode());
    }

    @Test
    public void testCreateAndGet() {
        DiveEntity e = new DiveEntity(1, 120, 130);
        assertNotNull(e);
        assertTrue(e.getId() == 1);
        assertTrue(e.getStartTime() == 120);
        assertTrue(e.getEndTime() == 130);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativeValue() {
        new DiveEntity(1, -120, 120);
    }
}
