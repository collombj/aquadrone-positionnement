package fr.onema.lib.database.entity;

import fr.onema.lib.geo.GPSCoordinate;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by Francois Vanderperre on 06/02/2017.
 *
 *  Classe de test
 */
public class TestMeasureEntity {
    private GPSCoordinate brut = new GPSCoordinate(1, 1, 1);
    private GPSCoordinate correct = new GPSCoordinate(2, 2, 2);

    @Test
    public void testCreateAndGet() {
        MeasureEntity e = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
        MeasureEntity e2 = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2 ,3, 2, "uneMes");
        assertNotNull(e);
        assertTrue(e.getId() == 1);
        assertTrue(e.getLocationBrut().equals(brut));
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
