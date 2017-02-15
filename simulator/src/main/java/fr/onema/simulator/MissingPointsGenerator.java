package fr.onema.simulator;

import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.geo.GeoMaths;

import java.io.File;
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

public class MissingPointsGenerator {
    private String csvFilePath; //Don't supposed to be accessed remotely
    private final List<String> entries; //Don't supposed to be accessed remotely
    private final List<Point> pointsInput; //Don't supposed to be accessed remotely
    private final List<Point> pointsOutput; //Don't supposed to be accessed remotely
    private static final Logger LOGGER = Logger.getLogger(MissingPointsGenerator.class.getName());
    private static final String CSV_HEADER = "timestamp,longitude,latitude,altitude,temperature";
    private static final int REQUIRED_LENGTH = 5;
    private static final double DISTANCE_BETWEEN_POINTS = 0.5;

    /**
     * Classe interne représentant un point (des coordonnées, un timestamp et une valeur de température)
     */
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

    /**
     * Builder de la classe. Il s'occupe d'instancier un générateur de points manquants, de récupérer les lignes
     * du fichier passé en argument dans une liste et d'en extraire les informations présentes pour faire une
     * liste de points
     * @param filePath chemin du fichier CSV d'entrée
     * @return un objet MissingPointsGenerator
     * @throws IOException Quand une erreur se produit
     */
    public static MissingPointsGenerator build(String filePath)throws IOException{
        Objects.requireNonNull(filePath);
        if ("".equals(filePath)){
            throw new IllegalArgumentException("Path was empty");
        }
        MissingPointsGenerator generator = new MissingPointsGenerator(filePath);
        generator.retrieveLines();
        generator.retrieveInformationsFromLines();
        return generator;
    }

    /**
     * Builder de la classe. Il s'occupe d'instancier un générateur de points manquants, de récupérer les lignes
     * du fichier passé en argument dans une liste et d'en extraire les informations présentes pour faire une
     * liste de points
     * @param file fichier CSV d'entrée
     * @return un objet MissingPointsGenerator
     * @throws IOException Problème lors de la lecture du fichier (Pas de fichier, problème lors de la lecture
     * d'une ligne...)
     */
    public static MissingPointsGenerator build(File file)throws IOException{
        Objects.requireNonNull(file);
        if ("".equals(file.getPath())){
            throw new IllegalArgumentException("File path was empty");
        }
        MissingPointsGenerator generator = new MissingPointsGenerator(file.getPath());
        generator.retrieveLines();
        generator.retrieveInformationsFromLines();
        return generator;
    }

    private void retrieveLines() throws IOException {
        try (Stream<String> s = Files.lines(Paths.get(csvFilePath))) {
            s.skip(1).forEach(entries::add);
        }catch (IOException ioe){
            throw new IOException("An error occurred while adding file lines in the list");
        }
    }

    private void retrieveInformationsFromLines(){
        Objects.requireNonNull(entries);
        entries.forEach(entry -> {
            String[] members = entry.split(",");
            if(members.length == REQUIRED_LENGTH) {
                long timestamp = Long.parseLong(members[0]);
                long lon = Long.parseLong(members[1].replace(".",","));//X
                long lat = Long.parseLong(members[2].replace(".",","));//Y
                long alt = Long.parseLong(members[3].replace(".",","));//Z
                float measure = Float.parseFloat(members[4].replace(".",","));
                Point point = new Point(new GPSCoordinate(lat, lon, alt), measure, timestamp);
                pointsInput.add(point);
            } else {
                LOGGER.log(Level.INFO,"The line '"+ entry + "' doesn't fit the requirements " +
                        "(number of arguments = " + REQUIRED_LENGTH + ")");
            }
        });
    }

    /**
     * Crée et remplit le fichier de sortie, qui contient tous les points du fichiers de départ,
     * plus les points générés dans le builder. Le path du fichier est par défaut
     * @throws IOException Quand il y a une erreur lors de l'écriture dans le fichier
     */
    public void generateOutput() throws IOException {
        Objects.requireNonNull(csvFilePath);
        String stringPath = "temp.csv";
        List<String> outputs;
        Path filePath = Paths.get(stringPath);
        for(int i = 0 ; i < pointsInput.size() -1 ; i++){
            createIntermediaryPoints(pointsInput.get(i),pointsInput.get(i+1));
        }
        outputs = formatToCSV();

        try{
            Files.write(filePath, outputs, Charset.forName("UTF-8"));
        }catch(IOException ioe){
            throw new IOException("Writing in file failed");
        }
    }

    /**
     * @param stringPath chemin du fichier CSV de sortie
     * Crée et remplit le fichier de sortie, qui contient tous les points du fichiers de départ,
     * plus les points générés dans le builder. On donne le path du fichier en argument
     * @throws IOException Quand il y a une erreur lors de l'écriture dans le fichier
     */
    public void generateOutput(String stringPath) throws IOException {
        Objects.requireNonNull(stringPath);
        Objects.requireNonNull(csvFilePath);
        if ("".equals(stringPath)) {
            throw new IllegalArgumentException("File path is empty");
        }
        List<String> outputs;
        Path filePath = Paths.get(stringPath);
        for(int i = 0 ; i < pointsInput.size() -1 ; i++){
            createIntermediaryPoints(pointsInput.get(i),pointsInput.get(i+1));
        }
        outputs = formatToCSV();

        try{
            Files.write(filePath, outputs, Charset.forName("UTF-8"));
        }catch(IOException ioe){
            throw new IOException("Writing in file failed");
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