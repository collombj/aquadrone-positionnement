package fr.onema.simulator;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mavlink.messages.ardupilotmega.msg_global_position_int;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

import java.io.File;

/**
 * Created by you on 13/02/2017.
 */
public class GeneratorTest {
    private final static String refFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/simulator/rawInput.csv";
    private final static String virtualizedFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/simulator/virtualizedOutput.csv";
    private final static FileManager fm = new FileManager(refFile, virtualizedFile);

    @BeforeClass
    public static void prepare() throws Exception {
        File ref = new File(refFile);
        ref.delete();
        File v = new File(virtualizedFile);
        v.delete();
        msg_global_position_int msg = new msg_global_position_int();
        msg.time_boot_ms = System.currentTimeMillis();
        msg.lat = 2;
        msg.lon = 3;
        msg.alt = 4;
        msg.hdg = 5;
        msg_scaled_pressure msg2 = new msg_scaled_pressure();
        msg2.time_boot_ms = msg.time_boot_ms;
        msg2.temperature = 6;
        fm.appendRaw(GPS.build(msg), Temperature.build(msg2));
        msg.time_boot_ms = System.currentTimeMillis();
        msg2.time_boot_ms = msg.time_boot_ms;
        fm.appendRaw(GPS.build(msg), Temperature.build(msg2));
        msg.time_boot_ms = System.currentTimeMillis();
        msg2.time_boot_ms = msg.time_boot_ms;
        fm.appendRaw(GPS.build(msg), Temperature.build(msg2));
    }

    @AfterClass
    public static void delete() {
        File ref = new File(refFile);
        ref.delete();
        File v = new File(virtualizedFile);
        v.delete();
    }

    @Test
    public void convert() throws Exception {
        Generator g = new Generator(refFile, virtualizedFile);
        g.convert();
    }
}