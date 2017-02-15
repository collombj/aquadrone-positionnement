package fr.onema.simulator;

import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by Jérôme on 14/02/2017.
 */

//TODO TO BE CONTINUED
public class MissingPointsGenerator {
    private String csvFilePath;
    private final List<String> entries;
    private final List<Point> pointsInput;
    private final List<Point> pointsOutput;
    private static final Logger LOGGER = Logger.getLogger(MissingPointsGenerator.class.getName());
    private static final String CSV_HEADER = "timestamp,longitude,latitude,altitude,temperature";
    private static final int REQUIRED_LENGTH = 5;
    private static final double DISTANCE_BETWEEN_POINTS = 0.5;

    public static class Point {
        private final GPSCoordinate coordinates;
        private final float measure;
        private final long timestamp;

        Point(GPSCoordinate coordinates, float value, long timestamp){
            this.coordinates = coordinates;
            this.measure = value;
            this.timestamp = timestamp;
        }

        String toCSV(){
            return timestamp + "," + coordinates.lon + "," + coordinates.lat + "," + coordinates.alt + "," + measure;
        }

        GPSCoordinate getCoordinates() {
            return coordinates;
        }

        float getMeasure() {
            return measure;
        }

        long getTimestamp() {
            return timestamp;
        }
    }

    private MissingPointsGenerator(String filePath) {
        this.csvFilePath = filePath;
        entries = new ArrayList<>();
        pointsInput = new ArrayList<>();
        pointsOutput = new ArrayList<>();
    }

    public static MissingPointsGenerator build(String filePath){
        Objects.requireNonNull(filePath);
        MissingPointsGenerator generator = new MissingPointsGenerator(filePath);
        generator.retrieveLines();
        generator.retrieveInformationsFromLines();
        return generator;
    }

    private void retrieveInformationsFromLines(){
        Objects.requireNonNull(entries);
        entries.forEach(entry -> {
            String[] members = entry.split(",");
            if(members.length == REQUIRED_LENGTH) {
                long timestamp = Long.parseLong(members[0]);
                long lon = Long.parseLong(members[1]);//X
                long lat = Long.parseLong(members[2]);//Y
                long alt = Long.parseLong(members[3]);//Z
                float measure = Float.parseFloat(members[4]);
                Point point = new Point(new GPSCoordinate(lat, lon, alt), measure, timestamp);
                pointsInput.add(point);
            } else {
                LOGGER.log(Level.INFO,"The line '"+ entry + "' doesn't fit the requirements " +
                        "(number of arguments = " + REQUIRED_LENGTH + ")");
            }
        });
    }

    private void retrieveLines(){
        try (Stream<String> s = Files.lines(Paths.get(csvFilePath))) {
            s.skip(1).forEach(entries::add);
        }catch (IOException ioe){
            LOGGER.log(Level.SEVERE,"An error occurred while adding file lines in the list");
        }
    }

    /*public*/private void populate(){
        String stringPath = "temp.csv";
        List<String> outputs;
        Path filePath = Paths.get(stringPath);
        for(int i = 0 ; i < pointsInput.size() -1 ; i++){
            createIntermediaryPoints(pointsInput.get(i),pointsInput.get(i+1));
        }
        outputs = formatToCSV();

        try{
            Files.write(filePath,outputs, Charset.forName("UTF-8"));
        }catch(IOException ioe){
            LOGGER.log(Level.SEVERE, "Writing in file failed", ioe);
        }

    }

    private List<String> formatToCSV() {
        List<String> list = new ArrayList<>();
        list.add(CSV_HEADER);
        pointsOutput.forEach(p -> list.add(p.toCSV()));
        return list;
    }

    private void createIntermediaryPoints(Point previousPoint, Point nextPoint){

        GPSCoordinate previousCoordinates = previousPoint.getCoordinates();
        GPSCoordinate nextCoordinates = nextPoint.getCoordinates();
        double distance = GeoMaths.gpsDistance(previousCoordinates, nextCoordinates);

        long nbPoints = Math.round(distance/DISTANCE_BETWEEN_POINTS);
        long diffLat = nextCoordinates.lat - previousCoordinates.lat;
        long diffLon = nextCoordinates.lon - previousCoordinates.lon;
        long diffAlt = nextCoordinates.alt - previousCoordinates.alt;
        float diffMeasure = nextPoint.getMeasure() - previousPoint.getMeasure();
        long diffTimeStamp = nextPoint.getTimestamp() - previousPoint.getTimestamp();

        for(int i =0;i<nbPoints;i++){
            long latitude = previousCoordinates.lat + i*(diffLat/nbPoints);
            long longitude = previousCoordinates.lon + i*(diffLon/nbPoints);
            long altitude = previousCoordinates.alt + i*(diffAlt/nbPoints);
            float measure = previousPoint.getMeasure() + i*(diffMeasure/nbPoints);
            long timestamp = previousPoint.getTimestamp() + i*(diffTimeStamp/nbPoints);
            Point point = new Point(new GPSCoordinate(latitude,longitude,altitude),measure,timestamp);
            pointsOutput.add(point);
        }
    }
}