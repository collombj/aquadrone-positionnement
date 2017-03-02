package fr.onema.lib.sensor.position;

import fr.onema.lib.sensor.Sensor;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure2;


/***
 * Classe permettant la représentation d'une mesure de pression
 */
public class Pressure extends Sensor {
    private static final String HEADER = "timestamp,asbolute,differential,temperature";
    private final float absolute;
    private final float differential;
    private final int temperature;

    private Pressure(long timestamp, float absolute, float differential, int temperature) {
        super(timestamp);
        this.absolute = absolute;
        this.differential = differential;
        this.temperature = temperature;
    }

    /***
     * Factory de la mesure de pression
     * @param msg Message mavlink correspondant à la pression
     * @return l'objet représentant la mesure gps
     */
    public static Pressure build(long timestamp, msg_scaled_pressure2 msg) {
        return new Pressure(timestamp, msg.press_abs, msg.press_diff, msg.temperature);
    }

    /***
     * Factory de la mesure de pression
     * @param timestamp L'heure de la mesure
     * @param temperature La valeur de température de la mesure
     * @return L'objet représentant la mesure gps
     */
     public static Pressure build(long timestamp, int temperature) {
        return new Pressure(timestamp, 0, 0, temperature);
    }

    /**
     * Permet de calculer la profondeur en fonction de la pression absolue et de la pression atmosphérique
     * @param atmosphericPressure
     * @return la profondeur du drone
     */
    public double getDepth(float atmosphericPressure) {
        return (this.absolute * atmosphericPressure) / (9.81);
    }

    /**
     * Retourne la valeur absolue de la pression
     *
     * @return absolute
     */
    public float getAbsolute() {
        return absolute;
    }

    /**
     * retourne la valeur differentielle
     *
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
        return getTimestamp() + "," + getAbsolute() + "," + getDifferential() + "," + getTemperature();
    }

    @Override
    public String getCSVHeader() {
        return HEADER;
    }
}