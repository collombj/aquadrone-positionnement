package fr.onema.lib.sensor;

import fr.onema.lib.drone.Measure;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure2;
import org.mavlink.messages.ardupilotmega.msg_scaled_pressure3;

/**
 * Classe représentant l'état d'un capteur à un instant T.
 */
public class Temperature extends Sensor implements Measure {
    private int valueTemperature;

    /**
     * Constructeur par défaut
     * @param timestamp   Heure de la mesure
     * @param temperature Valeur de la température
     */
    public Temperature(long timestamp, int temperature) {
        super(timestamp);
        this.valueTemperature = temperature;
    }

    /**
     * Factory method pour créer une Temperature
     * @param timestamp   Timestamp de la Temperature.
     * @param temperature Valeur de la température à l'instant donné.
     * @return Un objet Temperature représentant une température à l'instant donné.
     */
    public static Temperature build(long timestamp, int temperature) {
        return new Temperature(timestamp, temperature);
    }

    /**
     * Méthode permettant de créer une Temperature à partir d'un message PressureMAVLink
     * @param timestamp              Heure de la mesure
     * @param pressureMAVLinkMessage Message MAVLink provenant d'une pression.
     * @return Un objet Temperature représentant une température à l'instant donné.
     */
    public static Temperature build(long timestamp, msg_scaled_pressure3 pressureMAVLinkMessage) {
        return Temperature.build(timestamp, pressureMAVLinkMessage.temperature);
    }

    /**
     * Méthode permettant de créer une Temperature à partir d'un message PressureMAVLink
     * @param timestamp              Heure de la mesure
     * @param pressureMAVLinkMessage Message MAVLink provenant d'une pression.
     * @return Un objet Temperature représentant une température à l'instant donné.
     */
    public static Temperature build(long timestamp, msg_scaled_pressure2 pressureMAVLinkMessage) {
        return Temperature.build(timestamp, pressureMAVLinkMessage.temperature);
    }

    /**
     * Getter de la temperature
     * @return La valeur de la temperature.
     */
    public int getValueTemperature() {
        return valueTemperature;
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
        return "temperature";
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
