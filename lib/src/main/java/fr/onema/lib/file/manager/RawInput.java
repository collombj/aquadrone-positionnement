package fr.onema.lib.file.manager;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.file.Parser;
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


public class RawInput implements FileManager {

    private final String rawInputFilePath;

    public RawInput(String inputFilePath) {
        this.rawInputFilePath =
                Objects.requireNonNull(inputFilePath.replaceAll("/\\\\", String.valueOf(File.separatorChar)));
    }

    @Override
    public List<ReferenceEntry> readReferenceEntries() throws IOException {
        List<ReferenceEntry> refs = new ArrayList<>();
        try (Stream<String> s = Files.lines(Paths.get(rawInputFilePath))) {
            s.skip(1).forEach(e -> refs.add(Parser.parseReference(e)));
        }
        return refs;
    }

    @Override
    public void appendRaw(GPS gps, Temperature temp) throws IOException {
        File f = new File(rawInputFilePath);
        if (!f.exists()) {
            f.createNewFile();
            String out = "Écriture du fichier de référence : " + rawInputFilePath;
            LOGGER.log(Level.FINE, out);
        }
        try (FileWriter fw = new FileWriter(f, true)) {
            if (getLineNumber(f) == 0) {
                fw.write(ReferenceEntry.HEADER);
            }
            fw.write("\n" + gps.getTimestamp() + "," + gps.getPosition().lat + "," + gps.getPosition().lon + ","
                    + gps.getPosition().alt + "," + gps.getDirection() + "," + temp.getValue());
            fw.close();
        }
    }

    @Override
    public List<VirtualizerEntry> readVirtualizedEntries() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendVirtualized(VirtualizerEntry ve) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void openFileForResults() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendResults(ReferenceEntry re, MeasureEntity m, double margin) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getResults(String replacement) {
        throw new UnsupportedOperationException();
    }
}
