package fr.onema.lib.database.entity;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Francois Vanderperre on 06/02/2017.
 * <p>
 * Classe de test
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
        MeasureInformationEntity e2 = new MeasureInformationEntity(2, "unNom", "uneUnit", "unType", "unDisplay");
        MeasureInformationEntity e3 = new MeasureInformationEntity(1, "nope", "uneUnit", "unType", "unDisplay");
        MeasureInformationEntity e4 = new MeasureInformationEntity(1, "unNom", "nope", "unType", "unDisplay");
        MeasureInformationEntity e5 = new MeasureInformationEntity(1, "unNom", "uneUnit", "nope", "unDisplay");
        MeasureInformationEntity e6 = new MeasureInformationEntity(1, "unNom", "uneUnit", "unType", "nope");
        assertTrue(e.equals(e));
        assertFalse(e.equals(null));
        assertFalse(e.equals(e2));
        assertFalse(e.equals(e3));
        assertFalse(e.equals(e4));
        assertFalse(e.equals(e5));
        assertFalse(e.equals(e6));
        assertNotEquals(e.hashCode(), e2.hashCode());
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
