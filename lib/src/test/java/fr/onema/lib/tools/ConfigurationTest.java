package fr.onema.lib.tools;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Jérôme on 06/02/2017.
 */
public class ConfigurationTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNoPath() {
        Configuration c = new Configuration(null);
        fail("The test was expected to fail");
    }

    @Test
    public void testGetGivenPath() {
        Configuration c = new Configuration("settingsTest.properties");
        assertEquals(c.getPath(), "settingsTest.properties");
    }

    @Test
    public void testWithDefaultPath() throws IOException {
        Configuration c = new Configuration();
        c.setCorrection(523, 256, 5545);
        assertEquals(c.getPath(), "settingsTest.properties");
        assertEquals(c.getHost(), "aquadrone.local");
        assertEquals(c.getPort(), "5432");
        assertEquals(c.getUser(), "test");
        assertEquals(c.getPasswd(), "test");
        assertEquals(c.getNotifyKey(), "32569");
    }
}
