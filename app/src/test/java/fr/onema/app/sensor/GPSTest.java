package fr.onema.app.sensor;

import fr.onema.app.sensor.position.GPS;
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
        assertEquals(4, gps.getPosition().lat);
        assertEquals(5, gps.getPosition().lat);
    }

    @Test
    public void getDirection() throws Exception {
        msg_global_position_int msg = new msg_global_position_int();
        msg.hdg = 10;
        GPS gps = GPS.build(msg);
        assertNotNull(gps.getDirection());
        assertEquals(10, gps.getDirection());
    }
}
