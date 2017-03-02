package fr.onema.lib.file.manager;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.GeoMaths;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

import static fr.onema.lib.geo.GeoMaths.deg2rad;

public class ResultsOutput implements FileManager {

    private final String resultsOutputFilePath;

    public ResultsOutput(String resultsOutputFilePath) {
        this.resultsOutputFilePath =
                Objects.requireNonNull(resultsOutputFilePath.replaceAll("/\\\\", String.valueOf(File.separatorChar)));
    }

    @Override
    public void openFileForResults() throws IOException {
        File f = new File(resultsOutputFilePath);
        if (f.delete()) {
            String out = "Fichier précédent de résultats écrasé : " + resultsOutputFilePath;
            LOGGER.log(Level.FINE, out);
        }
        try (FileWriter fw = new FileWriter(f, false)) {
            fw.write(RESULTS_CSV_HEADER);
            fw.close();
        }
    }

    @Override
    public void appendResults(ReferenceEntry re, MeasureEntity m, double margin) throws IOException {
        File f = new File(resultsOutputFilePath);
        try (FileWriter fw = new FileWriter(f, true)) {
            CartesianCoordinate ref = GeoMaths.computeXYZfromLatLonAlt(deg2rad(re.getLat() / 10_000_000.), deg2rad(re.getLon() / 10_000_000.), re.getAlt() / 1_000.);
            CartesianCoordinate adjusted = GeoMaths.computeXYZfromLatLonAlt(deg2rad(m.getLocationCorrected().lat / 10_000_000.), deg2rad(m.getLocationCorrected().lon / 10_000_000.), m.getLocationCorrected().alt / 1_000.);
            double diffX = ref.x - adjusted.x;
            double diffY = ref.y - adjusted.y;
            double diffZ = ref.z - adjusted.z;
            double diffAbsolute = GeoMaths.cartesianDistance(ref, adjusted);
            boolean error = diffAbsolute > margin;
            fw.write("\n" + re.getTimestamp() + ","
                    + m.getLocationCorrected().lat + "," + m.getLocationCorrected().lon + "," + m.getLocationCorrected().alt
                    + m.getLocationBrute().lat + "," + m.getLocationBrute().lon + "," + m.getLocationBrute().alt
                    + "," + re.getLat() + "," + re.getLon() + "," + re.getAlt() + "," + re.getDirection()
                    + "," + re.getTemperature() + ","
                    + diffX + "," + diffY + "," + diffZ + "," + diffAbsolute +
                    "," + m.getPrecisionCm() + "," + margin + "," + error);
            fw.close();
        }
    }

    @Override
    public List<String> getResults(String replacement) throws IOException {
        List<String> results = new ArrayList<>();
        try (Stream<String> s = Files.lines(Paths.get(resultsOutputFilePath))) {
            s.filter(e -> !e.isEmpty()).forEach(e -> results.add(e.replaceAll(",", replacement)));
        }

        return results;
    }

    @Override
    public List<ReferenceEntry> readReferenceEntries() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<VirtualizerEntry> readVirtualizedEntries() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendRaw(GPS gps, Temperature temp) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendVirtualized(VirtualizerEntry ve) throws IOException {
        throw new UnsupportedOperationException();
    }
}
