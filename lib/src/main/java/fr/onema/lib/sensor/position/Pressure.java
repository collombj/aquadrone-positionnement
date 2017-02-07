package fr.onema.lib.sensor.position;

import fr.onema.lib.sensor.Sensor;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

/**
 * Created by Theo on 06/02/2017.
 */
public class Pressure extends Sensor {
    private float absolute;
    private float differential;
    private int temperature;

    private Pressure(long timestamp, float absolute, float differential, int temperature) {
        super(timestamp);
        this.absolute = absolute;
        this.differential = differential;
        this.temperature = temperature;
    }

    public static Pressure build(msg_scaled_pressure msg) {
        return new Pressure(msg.time_boot_ms, msg.press_abs, msg.press_diff, msg.temperature);
    }

    /**
     * Retourne la valeur absolute
     * @return absolute
     */
    public float getAbsolute() {
        return absolute;
    }

    /**
     * retourne la valeur differentielle
     * @return differential
     */
    public float getDifferential() {
        return differential;
    }

    /***
     * Retourne la valeur de température
     * @return temperature
     */
    public int getTemperature() {
        return temperature;
    }

    @Override
    public String toCSV() {
        return getTimestamp() + "," + getAbsolute() + "," + getDifferential() + "," + getTemperature() ;
    }

    @Override
    public String getCSVHeader() {
        return "timestamp,asbolute,differential,temperature";
    }
}