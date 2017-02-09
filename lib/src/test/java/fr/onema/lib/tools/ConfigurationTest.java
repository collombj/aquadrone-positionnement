package fr.onema.lib.tools;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Created by Jérôme on 06/02/2017.
 */
public class ConfigurationTest {
    private final static String content = "flow.lat=1\n" +
            "flow.lon=2\n" +
            "flow.alt=3\n" +
            "database.host=db.local\n" +
            "database.port=5432\n" +
            "database.base=base\n" +
            "database.user=user\n" +
            "database.password=pwd\n" +
            "database.notify-key=test_key\n" +
            "geo.srid=2154";
    private File filePath;

    @Before
    public void setUp() throws Exception {
        try {
            filePath = File.createTempFile("test", "properties");
            PrintWriter writer = new PrintWriter(filePath.getCanonicalFile(), "UTF-8");
            writer.println(content);
            writer.close();
        } catch (IOException e) {
            fail();
        }
    }

    @After
    public void tearDown() throws Exception {
        filePath.delete();
    }

    @Test(expected = NullPointerException.class)
    public void testNoPath() throws Exception {
        Configuration.build(null);
    }

    @Test(expected = FileNotFoundException.class)
    public void testBadPath() throws Exception {
        Configuration.build("/toto/test/abcd");
    }

    @Test
    public void testRead() throws Exception {
        Configuration config = Configuration.build(filePath.getAbsolutePath());

        assertEquals(1, config.getFlow().getLat());
        assertEquals(2, config.getFlow().getLon());
        assertEquals(3, config.getFlow().getAlt());

        assertEquals("db.local", config.getDatabaseInformation().getHostname());
        assertEquals(5432, config.getDatabaseInformation().getPort());
        assertEquals("base", config.getDatabaseInformation().getBase());
        assertEquals("user", config.getDatabaseInformation().getUsername());
        assertEquals("pwd", config.getDatabaseInformation().getPassword());
        assertEquals("test_key", config.getDatabaseInformation().getNotifyKey());

        assertEquals(2154, config.getGeo().getSrid());
    }

    @Test
    public void testEdit() throws Exception {
        Configuration config = Configuration.build(filePath.getAbsolutePath());
        Configuration configBis;

        int lat = config.getFlow().getLat();
        int lon = config.getFlow().getLon();
        int alt = config.getFlow().getAlt();

        config.setCorrection(lat, lon, alt);
        configBis = Configuration.build(filePath.getAbsolutePath());
        assertEquals(lat, config.getFlow().getLat());
        assertEquals(lon, config.getFlow().getLon());
        assertEquals(alt, config.getFlow().getAlt());
        assertEquals(configBis.getFlow().getLat(), config.getFlow().getLat());
        assertEquals(configBis.getFlow().getLon(), config.getFlow().getLon());
        assertEquals(configBis.getFlow().getAlt(), config.getFlow().getAlt());

        config.setCorrection(lat + 1, lon, alt);
        configBis = Configuration.build(filePath.getAbsolutePath());
        assertEquals(lat + 1, config.getFlow().getLat());
        assertEquals(lon, config.getFlow().getLon());
        assertEquals(alt, config.getFlow().getAlt());
        assertEquals(configBis.getFlow().getLat(), config.getFlow().getLat());
        assertEquals(configBis.getFlow().getLon(), config.getFlow().getLon());
        assertEquals(configBis.getFlow().getAlt(), config.getFlow().getAlt());

        config.setCorrection(lat, lon + 1, alt);
        configBis = Configuration.build(filePath.getAbsolutePath());
        assertEquals(lat, config.getFlow().getLat());
        assertEquals(lon + 1, config.getFlow().getLon());
        assertEquals(alt, config.getFlow().getAlt());
        assertEquals(configBis.getFlow().getLat(), config.getFlow().getLat());
        assertEquals(configBis.getFlow().getLon(), config.getFlow().getLon());
        assertEquals(configBis.getFlow().getAlt(), config.getFlow().getAlt());

        config.setCorrection(lat, lon, alt + 1);
        configBis = Configuration.build(filePath.getAbsolutePath());
        assertEquals(lat, config.getFlow().getLat());
        assertEquals(lon, config.getFlow().getLon());
        assertEquals(alt + 1, config.getFlow().getAlt());
        assertEquals(configBis.getFlow().getLat(), config.getFlow().getLat());
        assertEquals(configBis.getFlow().getLon(), config.getFlow().getLon());
        assertEquals(configBis.getFlow().getAlt(), config.getFlow().getAlt());
    }
}
