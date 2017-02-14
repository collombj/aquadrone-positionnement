package fr.onema.lib.sensor.position;

import fr.onema.lib.sensor.Sensor;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

/**
 * Created by Theo on 06/02/2017.
 */
// TODO : complete
public class Pressure extends Sensor {
    private final float absolute;
    private final float differential;
    private final int temperature;
    private static String HEADER = "timestamp,asbolute,differential,temperature";

    private Pressure(long timestamp, float absolute, float differential, int temperature) {
        super(timestamp);
        this.absolute = absolute;
        this.differential = differential;
        this.temperature = temperature;
    }

    // TODO : complete
    public static Pressure build(msg_scaled_pressure msg) {
        return new Pressure(msg.time_boot_ms, msg.press_abs, msg.press_diff, msg.temperature);
    }

    // TODO : complete
    public static Pressure build(long timestamp, int altitude, int temperature) {
        // TODO : complete --> calcul de la pression à partir de l'altitude (et température ?)
        int absolute = 0;
        return new Pressure(timestamp, absolute, 0, temperature);
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
        return HEADER;
    }
}