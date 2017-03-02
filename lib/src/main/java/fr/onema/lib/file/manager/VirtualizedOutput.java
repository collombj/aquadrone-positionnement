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

public class VirtualizedOutput implements FileManager {

    private final String virtualizedOutputFilePath;

    public VirtualizedOutput(String virtualizedOutputFilePath) {
        this.virtualizedOutputFilePath =
                Objects.requireNonNull(virtualizedOutputFilePath.replaceAll("/\\\\", String.valueOf(File.separatorChar)));
    }

    @Override
    public List<VirtualizerEntry> readVirtualizedEntries() throws IOException {
        List<VirtualizerEntry> virts = new ArrayList<>();
        try (Stream<String> s = Files.lines(Paths.get(virtualizedOutputFilePath))) {
            s.skip(1).filter(e -> !e.isEmpty()).forEach(e -> virts.add(Parser.parseVirtualizer(e)));
        }
        return virts;
    }

    @Override
    public void appendVirtualized(VirtualizerEntry ve) throws IOException {
        File f = new File(virtualizedOutputFilePath);
        if (!f.exists()) {
            f.createNewFile();
            String out = "Écriture du fichier de données virtualisées : " + virtualizedOutputFilePath;
            LOGGER.log(Level.FINE, out);
        }
        try (FileWriter fw = new FileWriter(f, true)) {
            if (getLineNumber(f) == 0) {
                fw.write(VirtualizerEntry.HEADER);
            }
            fw.write("\n" + ve.toCSV());
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public void openFileForResults() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendResults(ReferenceEntry re, MeasureEntity m, double margin) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<String> getResults(String replacement) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void appendRaw(GPS gps, Temperature temp) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ReferenceEntry> readReferenceEntries() throws IOException {
        throw new UnsupportedOperationException();
    }
}
