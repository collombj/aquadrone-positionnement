package fr.onema.lib;

import fr.onema.lib.Drone.Measure;

/**
 * Created by loics on 06/02/2017.
 */
public class Temperature implements Measure {

    private int temperature;

    public Temperature(int temperature) {
        this.temperature = temperature;
    }

    public static Temperature construct(long timestamp, int temperature) {

    }

    @Override
    public String toCSV() {
        return null;
    }

    @Override
    public String getCSVHeader() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getUnit() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getDisplay() {
        return null;
    }
}
