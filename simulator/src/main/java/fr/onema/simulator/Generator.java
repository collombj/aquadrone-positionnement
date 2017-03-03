package fr.onema.simulator;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.Pressure;
import fr.onema.lib.sensor.position.imu.IMU;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

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
    public void convert() throws IOException {
        GPSCoordinate refCoordinate = null;
        CartesianVelocity previousVelocity = null;
        CartesianCoordinate previousCartesian = null;
        for (ReferenceEntry reference : inputReferencies) {
            IMU imu;
            if (previous == null) {
                refCoordinate = new GPSCoordinate(reference.getLat(), reference.getLon(), reference.getAlt());
                previousCartesian = GeoMaths.computeCartesianPosition(refCoordinate, refCoordinate);
                previousVelocity = GeoMaths.computeVelocityFromCartesianCoordinate(new CartesianCoordinate(0, 0, 0), previousCartesian,
                        0);
                imu = IMU.build(previousVelocity,
                        previousVelocity,
                        reference.getTimestamp(),
                        reference.getTimestamp());
            } else {
                CartesianCoordinate cartCoordinate = GeoMaths.computeCartesianPosition(refCoordinate, new GPSCoordinate(reference.getLat(),
                        reference.getLon(), reference.getAlt()));
                CartesianVelocity velocity = GeoMaths.computeVelocityFromCartesianCoordinate(previousCartesian, cartCoordinate, reference.getTimestamp() - previous.getTimestamp());
                imu = IMU.build(previousVelocity,
                        velocity,
                        previous.getTimestamp(),
                        reference.getTimestamp());
                previousVelocity = velocity;
                previousCartesian = cartCoordinate;
            }
            previous = reference;
            Temperature t = Temperature.build(reference.getTimestamp(), reference.getTemperature());
            Pressure p = Pressure.build(reference.getTimestamp(), reference.getTemperature());
            fileManager.appendVirtualized(new VirtualizerEntry(reference.getTimestamp(), reference.getLat(), reference.getLon(), reference.getAlt(),
                    imu.getAccelerometer().getxAcceleration(),
                    imu.getAccelerometer().getyAcceleration(),
                    imu.getAccelerometer().getzAcceleration(),
                    imu.getGyroscope().getRoll(),
                    imu.getGyroscope().getPitch(),
                    imu.getGyroscope().getYaw(),
                    imu.getCompass().getxMagnetic(),
                    imu.getCompass().getyMagnetic(),
                    imu.getCompass().getzMagnetic(),
                    p.getAbsolute(),
                    t.getValueTemperature()));
        }
    }
}