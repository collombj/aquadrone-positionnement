package fr.onema.lib.geo;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by julien on 13/02/2017.
 */
public class MatrixTest {

    @Test(expected = IllegalArgumentException.class)
    public void testBadInstance() {
        Matrix.getInstance(-2, 1);
    }

    @Test
    public void testGetInstance() {
        Matrix m = Matrix.getInstance(3, 2);
        assertEquals(3, m.lines());
        assertEquals(2, m.columns());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadUsageGetter() {
        Matrix m = Matrix.getInstance(3, 2);
        m.get(3, 2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBadSet() {
        Matrix m = Matrix.getInstance(3, 3);
        m.set(3, 3, 1);
    }

    @Test(expected = NullPointerException.class)
    public void testMultNull() {
        Matrix m = Matrix.getInstance(3, 2);
        m.mult(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMultBadSize() {
        Matrix a = Matrix.getInstance(3, 3);
        Matrix b = Matrix.getInstance(4, 2);

        a.mult(b);
    }

    @Test
    public void testMult() {
        Matrix a = Matrix.getInstance(3, 3);
        Matrix b = Matrix.getInstance(3, 1);

        a.set(0, 0, 2);
        a.set(0, 1, 42);
        a.set(0, 2, -2);

        a.set(1, 0, 7);
        a.set(1, 1, -3);
        a.set(1, 2, 7);

        a.set(2, 0, 10);
        a.set(2, 1, 0);
        a.set(2, 2, 6);

        b.set(0, 0, 2);
        b.set(1, 0, -4);
        b.set(2, 0, -1);

        Matrix result = a.mult(b);

        assertEquals(-162.0, result.get(0, 0));
        assertEquals(19.0, result.get(1, 0));
        assertEquals(14.0, result.get(2, 0));
    }
}
