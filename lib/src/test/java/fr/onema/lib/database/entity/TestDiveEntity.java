package fr.onema.lib.database.entity;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Francois Vanderperre on 06/02/2017.
 *
 * Classe de test
 */
public class TestDiveEntity {

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
