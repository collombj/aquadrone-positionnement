package fr.onema.simulator;

import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Classe qui crée les points de relevé manquants dans un fichier CSV
 */
class MissingPointsGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(MissingPointsGenerator.class.getName());
    private static final String CSV_HEADER = "timestamp,longitude,latitude,altitude,temperature";
    private static final int REQUIRED_LENGTH = 5;
    private static final double DISTANCE_BETWEEN_POINTS = 0.5;
    private final List<String> entries;
    private final List<Point> pointsInput;
    private final List<Point> pointsOutput;
    private String csvFilePath;

    private MissingPointsGenerator(String filePath) {
        this.csvFilePath = filePath;
        entries = new ArrayList<>();
        pointsInput = new ArrayList<>();
        pointsOutput = new ArrayList<>();
    }

    /**
     * Builder de la classe. Il s'occupe d'instancier un générateur de points manquants, de récupérer les lignes
     * du fichier passé en argument dans une liste et d'en extraire les informations présentes pour faire une
     * liste de points
     *
     * @param filePath chemin du fichier CSV d'entrée
     * @return un objet MissingPointsGenerator
     * @throws IOException Quand une erreur se produit
     */
    static MissingPointsGenerator build(String filePath) throws IOException {
        Objects.requireNonNull(filePath);
        if ("".equals(filePath)) {
            throw new IllegalArgumentException("Path was empty");
        }
        MissingPointsGenerator generator = new MissingPointsGenerator(filePath);
        generator.retrieveLines();
        generator.retrieveInformationsFromLines();
        return generator;
    }

    private void retrieveLines() throws IOException {
        try (Stream<String> s = Files.lines(Paths.get(csvFilePath))) {
            s.skip(1).forEach(entries::add);
        } catch (IOException ioe) {
            throw new IOException("An error occurred while adding file lines in the list :" + ioe);
        }
    }

    private void retrieveInformationsFromLines() {
        Objects.requireNonNull(entries);
        entries.forEach(entry -> {
            String[] members = entry.split(",");
            if (members.length == REQUIRED_LENGTH) {
                long timestamp = Long.parseLong(members[0]);
                long lat = (long) (Double.valueOf(members[1]) * 10_000_000);
                long lon = (long) (Double.valueOf(members[2]) * 10_000_000);
                long alt = (long) (Double.valueOf(members[3]) * 1_000);
                int measure = (int) (Float.parseFloat(members[4]) * 100);
                Point point = new Point(new GPSCoordinate(lat, lon, alt), measure, 0, timestamp);
                pointsInput.add(point);
            } else {
                LOGGER.error("The line '" + entry + "' doesn't fit the requirements " +
                        "(number of arguments = " + REQUIRED_LENGTH + ")");
            }
        });
    }

    /**
     * @param stringPath chemin du fichier CSV de sortie
     *                   Crée et remplit le fichier de sortie, qui contient tous les points du fichiers de départ,
     *                   plus les points générés dans le builder. On donne le path du fichier en argument
     * @throws IOException Quand il y a une erreur lors de l'écriture dans le fichier
     */
    void generateOutput(String stringPath) throws IOException {
        Objects.requireNonNull(stringPath);
        Objects.requireNonNull(csvFilePath);
        if ("".equals(stringPath)) {
            throw new IllegalArgumentException("file path is empty");
        }
        List<String> outputs;
        Path filePath = Paths.get(stringPath);
        for (int i = 0; i < pointsInput.size() - 1; i++) {
            createIntermediaryPoints(pointsInput.get(i), pointsInput.get(i + 1));
            if (i == pointsInput.size() - 2) {
                pointsOutput.add(pointsInput.get(i + 1));
            }
        }

        outputs = formatToCSV();
        try {
            Files.write(filePath, outputs, Charset.forName("UTF-8"));
        } catch (IOException ioe) {
            throw new IOException("Writing in file failed :", ioe);
        }
    }

    private List<String> formatToCSV() {
        List<String> list = new ArrayList<>();
        list.add(CSV_HEADER);
        pointsOutput.forEach(p -> list.add(p.toCSV()));
        return list;
    }

    private void createIntermediaryPoints(Point previousPoint, Point nextPoint) {

        GPSCoordinate previousCoordinates = previousPoint.getCoordinates();
        GPSCoordinate nextCoordinates = nextPoint.getCoordinates();
        double distance = GeoMaths.gpsDistance(previousCoordinates, nextCoordinates);

        long nbPoints = Math.round(distance / DISTANCE_BETWEEN_POINTS);
        int nbPointsInt = (int) nbPoints;
        long diffLat = nextCoordinates.lat - previousCoordinates.lat;
        long diffLon = nextCoordinates.lon - previousCoordinates.lon;
        long diffAlt = nextCoordinates.alt - previousCoordinates.alt;
        int diffMeasure = nextPoint.getMeasure() - previousPoint.getMeasure();
        int diffDir = nextPoint.getDirection() - previousPoint.getDirection();
        long diffTimeStamp = nextPoint.getTimestamp() - previousPoint.getTimestamp();

        for (int i = 0; i < nbPoints; i++) {
            long latitude = previousCoordinates.lat + i * (diffLat / nbPoints);
            long longitude = previousCoordinates.lon + i * (diffLon / nbPoints);
            long altitude = previousCoordinates.alt + i * (diffAlt / nbPoints);
            int measure = previousPoint.getMeasure() + i * (diffMeasure / nbPointsInt);
            int direction = previousPoint.getDirection() + i * (diffDir / nbPointsInt);
            long timestamp = previousPoint.getTimestamp() + i * (diffTimeStamp / nbPoints);
            Point point = new Point(new GPSCoordinate(latitude, longitude, altitude), measure, direction, timestamp);
            pointsOutput.add(point);
        }
    }

    /**
     * Classe interne représentant un point (des coordonnées, un timestamp et une valeur de température)
     */
    private class Point {
        private final GPSCoordinate coordinates;
        private final int measure;
        private final int direction;
        private final long timestamp;

        Point(GPSCoordinate coordinates, int value, int direction, long timestamp) {
            this.coordinates = coordinates;
            this.measure = value;
            this.direction = direction;
            this.timestamp = timestamp;
        }

        String toCSV() {
            return timestamp + "," + coordinates.lat + "," + coordinates.lon + "," + coordinates.alt + "," + direction + "," + measure;
        }

        GPSCoordinate getCoordinates() {
            return coordinates;
        }

        int getMeasure() {
            return measure;
        }

        int getDirection() {
            return direction;
        }

        long getTimestamp() {
            return timestamp;
        }
    }
}