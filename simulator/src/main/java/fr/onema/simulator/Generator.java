package fr.onema.simulator;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.sensor.position.Pressure;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Created by you on 11/02/2017.
 */

/***
 * Classe permettant la gestion des conversions entre données brutes et virtualisées du générateur CSV
 */
public class Generator {
    private final FileManager fileManager;
    private final List<ReferenceEntry> inputReferencies;
    private ReferenceEntry previous = null;

    public Generator(String inputFilePath, String virtualizedFilePath) throws IOException {
        this.fileManager = new FileManager(Objects.requireNonNull(inputFilePath), Objects.requireNonNull(virtualizedFilePath));
        this.inputReferencies = fileManager.readReferenceEntries();
    }

    /***
     * Permet de convertir les données du fichier d'entrée en données virtualisées et les écrire dans le fichier spécifié
     */
    public void convert() {
        inputReferencies.forEach(e -> {
            GPS g = GPS.build(e.getTimestamp(), e.getLat(), e.getLon(), e.getAlt(), e.getDirection());
            // TODO : gestion CartesianVelocity, faire évoluer ReferenceEntry ?
            // TODO : faire évoluer CSV ? (gestion plus poussée des GPS msg_global_position_int ?
            IMU i = previous == null
                    ? IMU.build(new CartesianVelocity(0,0,0), 0,
                    new GPSCoordinate(e.getLat(), e.getLon(), e.getAlt()), e.getTimestamp(),
                    new GPSCoordinate(e.getLat(), e.getLon(), e.getAlt()))
                    : IMU.build(new CartesianVelocity(0,0,0), previous.getTimestamp(),
                    new GPSCoordinate(previous.getLat(),previous.getLon(),previous.getAlt()), e.getTimestamp(),
                    new GPSCoordinate(e.getLat(), e.getLon(), e.getAlt()));
            previous = e;
            Temperature t = Temperature.build(e.getTimestamp(), e.getTemperature());
            Pressure p = Pressure.build(e.getTimestamp(), e.getAlt(), e.getTemperature());
            try {
                fileManager.appendVirtualized(new VirtualizerEntry(g.getTimestamp(), e.getLat(), e.getLon(), e.getAlt(),
                        (short) i.getAccelerometer().getxAcceleration(),
                        (short) i.getAccelerometer().getyAcceleration(),
                        (short) i.getAccelerometer().getzAcceleration(),
                        (short) i.getGyroscope().getxRotation(),
                        (short) i.getGyroscope().getyRotation(),
                        (short) i.getGyroscope().getzRotation(),
                        (short) i.getCompass().getxMagnetic(),
                        (short) i.getCompass().getyMagnetic(),
                        (short) i.getCompass().getzMagnetic(),
                        p.getAbsolute(),
                        (short) t.getValueTemperature()));
            } catch (IOException e1) {
                // TODO : exception handling
            }
        });
    }
}