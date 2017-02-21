package fr.onema.lib.database.entity;

import fr.onema.lib.geo.GPSCoordinate;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Francois Vanderperre on 06/02/2017.
 *
 *  Classe de test
 */
public class TestMeasureEntity {
    private GPSCoordinate brut = new GPSCoordinate(1, 1, 1);
    private GPSCoordinate brut2 = new GPSCoordinate(3, 3, 3);
    private GPSCoordinate correct = new GPSCoordinate(2, 2, 2);
    private GPSCoordinate correct2 = new GPSCoordinate(4, 4, 4);

    @Test
    public void testCreateAndGet() {
        MeasureEntity e = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
        MeasureEntity e2 = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
        MeasureEntity e3 = new MeasureEntity(2, 300, brut2, correct2, 2, 4, 5, 6, 7 ,8, 9, "uneMesazda");
        MeasureEntity e4 = new MeasureEntity(1, 230, brut, correct, 2, 2, 3, 1, 2 ,3, 2, "uneMes");
        MeasureEntity e5 = new MeasureEntity(1, 230, brut, correct, 1, 3, 3, 1, 2 ,3, 2, "uneMes");
        MeasureEntity e6 = new MeasureEntity(1, 230, brut, correct, 1, 2, 4, 1, 2 ,3, 2, "uneMes");
        MeasureEntity e7 = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 5, 2 ,3, 2, "uneMes");
        MeasureEntity e8 = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 6 ,3, 2, "uneMes");
        MeasureEntity e9 = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,7, 2, "uneMes");
        MeasureEntity e10 = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 8, "uneMes");
        MeasureEntity e11 = new MeasureEntity(4, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
        assertNotNull(e);
        assertTrue(e.getId() == 1);
        assertTrue(e.getLocationBrute().equals(brut));
        assertTrue(e.getLocationCorrected().equals(correct));
        assertTrue(e.getAccelerationX() == 1);
        assertTrue(e.getAccelerationY() == 2);
        assertTrue(e.getAccelerationZ() == 3);
        assertTrue(e.getRoll() == 1.0);
        assertTrue(e.getPitch() == 2.0);
        assertTrue(e.getYaw() == 3.0);
        assertTrue(e.getPrecisionCm() == 2);
        assertTrue(e.getMeasureValue().equals("uneMes"));
        assertTrue(e.equals(e2));
        assertFalse(e.equals(null));
        assertFalse(e.hashCode() != e2.hashCode());
        e.setLocationBrute(brut);
        e.setLocationCorrected(correct);
        e.setPrecisionCm(5);
        assertEquals(e.getPrecisionCm(), 5);
        assertTrue(e.equals(e));
        assertFalse(e.equals(e3));
        assertFalse(e.equals(e4));
        assertFalse(e.equals(e5));
        assertFalse(e.equals(e6));
        assertFalse(e.equals(e7));
        assertFalse(e.equals(e8));
        assertFalse(e.equals(e9));
        assertFalse(e.equals(e10));
        assertFalse(e.equals(e11));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateNegativeId() {
        new MeasureEntity(-1, 230, brut, correct, 1, 2, 1, 2 ,3, 3, 2, "uneMes");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullGPSBrut() {
        new MeasureEntity(1, 230, null, correct, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullGPSCorrected() {
        new MeasureEntity(1, 230, brut, null, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNullMeasure() {
        new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 2, null);
    }

}
