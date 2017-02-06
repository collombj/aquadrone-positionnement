package fr.onema.lib.sensor;

import fr.onema.lib.sensor.Temperature;
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
        Temperature temperature = Temperature.construct(timeStamp, 20);
        assertEquals(20, temperature.getValueTemperature());
        assertEquals(timeStamp, temperature.getTimestamp());
    }

    @Test
    public void testObjectTemperature() {
        Temperature temperature = Temperature.construct(System.currentTimeMillis(), 20);
        assertNotNull(temperature);

        Temperature temperature1 = Temperature.construct(System.currentTimeMillis(), -1);
        assertNotNull(temperature1);
    }

}
