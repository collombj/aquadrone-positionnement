package fr.onema.lib.sensor.position;

import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PressureTest {
    @Test
    public void build() throws Exception {
        msg_scaled_pressure2 msg = new msg_scaled_pressure2();
        Pressure p = Pressure.build(27091994, msg);
        assertNotNull(p);
    }

    @Test
    public void getAbsolute() throws Exception {
        msg_scaled_pressure2 msg = new msg_scaled_pressure2();
        msg.press_abs = 3;
        Pressure p = Pressure.build(27091994, msg);
        assertEquals(3.0, p.getAbsolute(), 0);
    }

    @Test
    public void getDifferential() throws Exception {
        msg_scaled_pressure2 msg = new msg_scaled_pressure2();
        msg.press_diff = 3;
        Pressure p = Pressure.build(27091994, msg);
        assertEquals(3.0, p.getDifferential(), 0);
    }

    @Test
    public void getTemperature() throws Exception {
        msg_scaled_pressure2 msg = new msg_scaled_pressure2();
        msg.temperature = 3;
        Pressure p = Pressure.build(27091994, msg);
        assertEquals(3.0, p.getTemperature(), 0);
    }

    @Test
    public void toCSV() throws Exception {
        msg_scaled_pressure2 msg = new msg_scaled_pressure2();
        msg.time_boot_ms = 1;
        msg.temperature = 4;
        msg.press_diff = 3;
        msg.press_abs = 2;
        Pressure p = Pressure.build(27091994, msg);
        assertEquals(p.toCSV(), "27091994,2.0,3.0,4");
    }

    @Test
    public void getCSVHeader() throws Exception {
        msg_scaled_pressure2 msg = new msg_scaled_pressure2();
        Pressure p = Pressure.build(27091994, msg);
        assertEquals(p.getCSVHeader(), "timestamp,asbolute,differential,temperature");
    }
}