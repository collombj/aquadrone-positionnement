package fr.onema.simulator;

import org.junit.Test;

/**
 * Created by you on 15/02/2017.
 */
public class ComparisonExceptionTest {
    @Test(expected = ComparisonException.class)
    public void testException() throws ComparisonException {
        throw new ComparisonException(new Exception().getCause());
    }
}