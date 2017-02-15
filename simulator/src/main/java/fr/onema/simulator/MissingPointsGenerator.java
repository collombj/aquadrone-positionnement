package fr.onema.simulator;

import fr.onema.lib.geo.GPSCoordinate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
    private final List<GPSCoordinate> points;
    private final List<Integer> measures;
    private static final Logger LOGGER = Logger.getLogger(MissingPointsGenerator.class.getName());
    private static final int REQUIRED_LENGTH = 5;

    /*public String toCSV() {
        Objects.requireNonNull(entries);
        StringBuilder sb = new StringBuilder();
        entries.forEach(x -> sb.append(x));
        return sb.toString();
    }*/

    private MissingPointsGenerator(String filePath) {
        Objects.requireNonNull(filePath);
        this.csvFilePath = filePath;
        entries = new ArrayList<>();
        points = new ArrayList<>();
        measures = new ArrayList<>();
    }

    private void retrieveInformationsFromLines(){
        Objects.requireNonNull(entries);
        entries.forEach(entry -> {
            String[] tab = entry.split(",");
            if(tab.length == REQUIRED_LENGTH) {
                int timestamp = Integer.parseInt(tab[0]);
                long x = Long.parseLong(tab[1]);
                long y = Long.parseLong(tab[2]);
                long alt = Long.parseLong(tab[3]);
                int measure = Integer.parseInt(tab[4]);

                measures.add(measure);
                points.add(timestamp, new GPSCoordinate(x, y, alt));
            }
        });
    }

    public static MissingPointsGenerator build(String filePath){
        MissingPointsGenerator generator = new MissingPointsGenerator(filePath);
        generator.retrieveLines();
        generator.retrieveInformationsFromLines();
        //generator.
        return generator;
    }

    private void retrieveLines(){
        try (Stream<String> s = Files.lines(Paths.get(csvFilePath))) {
            s.skip(1).forEach(entries::add);
        }catch (IOException ioe){
            LOGGER.log(Level.SEVERE,"An error occured while adding file lines in the list");
        }
    }
}