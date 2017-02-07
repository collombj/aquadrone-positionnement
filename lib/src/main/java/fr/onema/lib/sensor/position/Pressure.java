package fr.onema.lib.sensor.position;

import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

public class Pressure /*extends Sensor*/ {
    private float absolute;
    private float differential;

    public void build(Long timestamp, int altitude, int temperature) {
        // tod
    }

    /**
     * Creer un objet Pressure grace à un message de pression
     * @param msg is a msg_scaled_pressure
     * @return an object Pressure
     */
    public Pressure build(msg_scaled_pressure msg) {
        Pressure press = new Pressure();
        press.absolute = msg.press_abs;
        press.differential = msg.press_diff;
        return press;
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
}
