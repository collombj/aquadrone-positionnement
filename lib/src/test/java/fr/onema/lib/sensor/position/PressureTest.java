package fr.onema.lib.sensor.position;

import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by you on 07/02/2017.
 */
public class PressureTest {
    @Test
    public void build() throws Exception {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        Pressure p = Pressure.build(msg);
        assertNotNull(p);
    }

    @Test
    public void build2() {
        Pressure p2 = Pressure.build(3, 1, 2);
        assertEquals(p2.getTimestamp(), 3);
        assertEquals(p2.getAbsolute(), 0, 0);
        assertEquals(p2.getDifferential(), 0, 0);
        assertEquals(p2.getTemperature(), 2);
    }

    @Test
    public void getAbsolute() throws Exception {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        msg.press_abs = 3;
        Pressure p = Pressure.build(msg);
        assertEquals(3.0, p.getAbsolute(), 0);
    }

    @Test
    public void getDifferential() throws Exception {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        msg.press_diff = 3;
        Pressure p = Pressure.build(msg);
        assertEquals(3.0, p.getDifferential(), 0);
    }

    @Test
    public void getTemperature() throws Exception {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        msg.temperature = 3;
        Pressure p = Pressure.build(msg);
        assertEquals(3.0, p.getTemperature(), 0);
    }

    @Test
    public void toCSV() throws Exception {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        msg.time_boot_ms = 1;
        msg.temperature = 4;
        msg.press_diff = 3;
        msg.press_abs = 2;
        Pressure p = Pressure.build(msg);
        assertEquals(p.toCSV(),"1,2.0,3.0,4");
    }

    @Test
    public void getCSVHeader() throws Exception {
        msg_scaled_pressure msg = new msg_scaled_pressure();
        Pressure p = Pressure.build(msg);
        assertEquals(p.getCSVHeader(), "timestamp,asbolute,differential,temperature");
    }

}