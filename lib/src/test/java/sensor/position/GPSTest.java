package sensor.position;

import fr.onema.lib.sensor.position.GPS;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_gps_raw_int;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
/**
 * Created by you on 07/02/2017.
 */
public class GPSTest {
    @Test
    public void build() throws Exception {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        GPS gps = GPS.build(msg);
        assertNotNull(gps);
    }

    @Test
    public void getPosition() throws Exception {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.lat = 3;
        msg.lon = 4;
        msg.alt = 5;
        GPS gps = GPS.build(msg);
        assertNotNull(gps.getPosition());
        assertEquals(3, gps.getPosition().lat);
        assertEquals(4, gps.getPosition().lon);
        assertEquals(5, gps.getPosition().alt);
    }

    @Test
    public void getDirection() throws Exception {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.cog = 10;
        GPS gps = GPS.build(msg);
        assertNotNull(gps.getDirection());
        assertEquals(10, gps.getDirection());
    }

    @Test
    public void toCSV() {
        msg_gps_raw_int msg = new msg_gps_raw_int();
        msg.time_usec = 1;
        msg.lat = 3;
        msg.lon = 4;
        msg.alt = 5;
        msg.cog = 10;
        GPS gps = GPS.build(msg);
        assertEquals("1,3,4,5,10", gps.toCSV());
    }
}
