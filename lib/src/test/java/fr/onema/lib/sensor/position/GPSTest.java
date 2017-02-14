package fr.onema.lib.sensor.position;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by you on 11/02/2017.
 */
public class GPSTest {
    private final GPS gps = GPS.build(0, 1, 2, 3, (float)4);

    @Test
    public void build() throws Exception {
        assertNotNull(gps);
    }

    @Test
    public void getPosition() throws Exception {
        assertEquals(gps.getPosition().lat, 1, 0);
        assertEquals(gps.getPosition().lon, 2, 0);
        assertEquals(gps.getPosition().alt, 3, 0);
    }

    @Test
    public void toCSV() throws Exception {
        assertEquals(gps.toCSV(), gps.getTimestamp() + "," + gps.getPosition().lat + "," + gps.getPosition().lon + "," + gps.getPosition().alt + "," + gps.getDirection());
    }

    @Test
    public void getCSVHeader() throws Exception {
        assertEquals(gps.getCSVHeader(), "timestamp,latitude,longitude,altitude,direction");
    }

}