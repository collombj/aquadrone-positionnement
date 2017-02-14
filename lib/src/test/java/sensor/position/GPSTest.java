package sensor.position;

import fr.onema.lib.sensor.position.GPS;
import org.junit.Test;

import static org.junit.Assert.*;
import org.mavlink.messages.ardupilotmega.*;
/**
 * Created by you on 07/02/2017.
 */
public class GPSTest {
    @Test
    public void build() throws Exception {
        msg_global_position_int msg = new msg_global_position_int();
        GPS gps = GPS.build(msg);
        assertNotNull(gps);
    }

    @Test
    public void getPosition() throws Exception {
        msg_global_position_int msg = new msg_global_position_int();
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
        msg_global_position_int msg = new msg_global_position_int();
        msg.hdg = 10;
        GPS gps = GPS.build(msg);
        assertNotNull(gps.getDirection());
        assertEquals(10, gps.getDirection());
    }

    @Test
    public void toCSV() {
        msg_global_position_int msg = new msg_global_position_int();
        msg.time_boot_ms = 1;
        msg.lat = 3;
        msg.lon = 4;
        msg.alt = 5;
        msg.hdg = 10;
        GPS gps = GPS.build(msg);
        assertEquals("1,3,4,5,10", gps.toCSV());
    }
}
