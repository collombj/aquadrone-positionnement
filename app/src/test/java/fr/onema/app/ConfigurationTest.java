package fr.onema.app;

import fr.onema.app.siren.Configuration;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Jérôme on 06/02/2017.
 */
public class ConfigurationTest {
    @Test(expected = IllegalArgumentException.class)
    public void testNoPath(){
        Configuration c = new Configuration(null);
        fail("The test was expected to fail");
    }

    @Test
    public void testGetGivenPath(){
        Configuration c = new Configuration("test.properties");
        assertEquals(c.getPath(), "test.properties");
    }

    @Test
    public void testWithDefaultPath() {
        try {
            Configuration c = new Configuration();
            c.setCorrection(523,256 ,5545);
            assertEquals(c.getPath(), "settings.properties");
            assertEquals(c.getHost(), "1.2.3.4");
            assertEquals(c.getPort(), "12275");
            assertEquals(c.getUser(), "toto");
            assertEquals(c.getPasswd(), "cacahuete");
            assertEquals(c.getNotifyKey(), "32569");
        } catch(IOException ex){
            fail("Unexpected IOException");
        }
    }
}
