package fr.onema.lib.sensor;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author loicszym
 * @since 06-02-2017
 */
public class TemperatureTest {
    private static Temperature temperature = Temperature.build(System.currentTimeMillis(), 20);

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

    @Test
    public void toCSV() throws Exception {
        assertEquals(temperature.toCSV(), String.valueOf(temperature.getValueTemperature()));
    }

    @Test
    public void getCSVHeader() throws Exception {
        assertEquals(temperature.getCSVHeader(), "temperature");
    }

    @Test
    public void getName() throws Exception {
        assertEquals(temperature.getName(), "Temperature");
    }

    @Test
    public void getUnit() throws Exception {
        assertEquals(temperature.getUnit(), "° Celcius");
    }

    @Test
    public void getType() throws Exception {
        assertEquals(temperature.getType(), "Integer");
    }

    @Test
    public void getDisplay() throws Exception {
        assertEquals(temperature.getDisplay(), "Timestamp=" + temperature.getTimestamp() + " valueTemperature=" + temperature.getValueTemperature());
    }
}
