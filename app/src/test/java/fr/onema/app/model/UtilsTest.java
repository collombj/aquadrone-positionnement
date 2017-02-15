package fr.onema.app.model;

import fr.onema.lib.tools.Configuration;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by you on 14/02/2017.
 */
public class UtilsTest {
    @Test
    public void checkPostgresAvailability() throws Exception {
        assertEquals(Utils.checkPostgresAvailability(Configuration.build("settings.properties")), true);
    }

    @Test
    public void checkMavlinkAvailability() throws Exception {
        assertEquals(Utils.checkMavlinkAvailability(), true);
    }

}