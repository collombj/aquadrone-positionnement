package fr.onema.lib.sensor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author loicszym
 * @since 06-02-2017
 */
public class TemperatureTest {

    @Test
    public void testGetter() {
        long timeStamp = System.currentTimeMillis();
        Temperature temperature = Temperature.build(timeStamp, 20);
        assertEquals(20, temperature.getValueTemperature());
        assertEquals(timeStamp, temperature.getTimestamp());
    }

    @Test
    public void testObjectTemperature() {
        Temperature temperature = Temperature.build(System.currentTimeMillis(), 20);
        assertNotNull(temperature);

        Temperature temperature1 = Temperature.build(System.currentTimeMillis(), -1);
        assertNotNull(temperature1);
    }

}
