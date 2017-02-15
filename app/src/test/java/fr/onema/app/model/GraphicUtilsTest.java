package fr.onema.app.model;

import fr.onema.lib.tools.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by you on 14/02/2017.
 */
public class GraphicUtilsTest {
    @Test
    public void checkPostgresAvailability() throws Exception {
        assertEquals(GraphicUtils.checkPostgresAvailability(Configuration.build("settings.properties")), true);
    }

    @Test
    public void checkMavlinkAvailability() throws Exception {
        assertEquals(GraphicUtils.checkMavlinkAvailability(), true);
    }

}