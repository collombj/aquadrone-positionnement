package fr.onema.lib.sensor;

import fr.onema.lib.drone.Measure;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure;

/**
 * Classe représentant l'état d'un capteur à un instant T.
 *
 * @author loicszym
 * @since 06-02-2017
 */
public class Temperature extends Sensor implements Measure {

    private int valueTemperature;

    private Temperature(long timestamp, int temperature) {
        super(timestamp);
        this.valueTemperature = temperature;
    }

    /**
     * Factory methode pour créer une Temperature
     *
     * @param timestamp Timestamp de la Temperature.
     * @param temperature Valeur de la température à l'instant donné.
     * @return Un objet Temperature représentant une température à l'instant donné.
     */
    public static Temperature construct(long timestamp, int temperature) {
        return new Temperature(timestamp, temperature);
    }


    /**
     * Méthode permettant de créer une Temperature à partir d'un message PressureMAVLink
     *
     * @param pressureMAVLinkMessage Message MAVLink provenant d'une pression.
     * @return Un objet Temperature représentant une température à l'instant donné.
     */
    public static Temperature build(msg_scaled_pressure pressureMAVLinkMessage) {
        return Temperature.construct(System.currentTimeMillis(), pressureMAVLinkMessage.temperature);
    }

    /**
     * Getter de Temperature
     *
     * @return La valeur de la Temperature.
     */
    public int getValueTemperature() {
        return valueTemperature;
    }

    /**
     * Getter de Temperature
     *
     * @return Le timestamp associé à la valeur de Temperature.
     */
    public long getTimestamp() {
        return super.timestamp;
    }

    @Override
    public String toCSV() {
        return String.valueOf(getValueTemperature());
    }

    @Override
    public String getCSVHeader() {
        return "temperature";
    }

    @Override
    public String getName() {
        return "Temperature";
    }

    @Override
    public String getUnit() {
        return "° Celcius";
    }

    @Override
    public String getType() {
        return "Integer";
    }

    @Override
    public String getValue() {
        return String.valueOf(getValueTemperature());
    }

    @Override
    public String getDisplay() {
        return "Timestamp=" + super.timestamp + " valueTemperature=" + getValueTemperature();
    }
}
