package fr.onema.lib.database.entity;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Francois Vanderperre on 06/02/2017.
 *
 *  Classe de test
 */
public class TestMeasureInformationEntity {

    @Test
    public void testCreateAndGet() {
        MeasureInformationEntity e = new MeasureInformationEntity(1, "unNom", "uneUnit", "unType", "unDisplay");
        assertNotNull(e);
        assertTrue(e.getId() == 1);
        assertTrue(e.getName().equals("unNom"));
        assertTrue(e.getUnit().equals("uneUnit"));
        assertTrue(e.getType().equals("unType"));
        assertTrue(e.getDisplay().equals("unDisplay"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativeValue() {
        new MeasureInformationEntity(-1, "unNom", "uneUnit", "unType", "unDisplay");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullName() {
        new MeasureInformationEntity(-1, null, "uneUnit", "unType", "unDisplay");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullUnit() {
        new MeasureInformationEntity(-1, "unNom", null, "unType", "unDisplay");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullType() {
        new MeasureInformationEntity(-1, "unNom", "uneUnit", null, "unDisplay");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullDisplay() {
        new MeasureInformationEntity(-1, "unNom", "uneUnit", "unType", null);
    }

}
