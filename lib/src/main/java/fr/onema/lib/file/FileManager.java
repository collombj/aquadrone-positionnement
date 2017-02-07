package fr.onema.lib.file;

import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by you on 07/02/2017.
 */

/***
 * Classe utilitaire permettant la gestion des CSV brut et modifiés de données MavLink
 */
public class FileManager {
    private final String rawInputFilePath;
    private final String computedOutputFilePath;

    /***
     * Constructeur du FileManager
     * @param inputFilePath Chemin d'accès vers le fichier brut d'entrée
     * @param outputFilePath Chemin d'accès vers le fichier brut de sortie
     */
    public FileManager(String inputFilePath, String outputFilePath) {
        this.rawInputFilePath = Objects.requireNonNull(inputFilePath);
        this.computedOutputFilePath = Objects.requireNonNull(outputFilePath);
    }

    /***
     * Transforme les valeurs du fichier brut CSV en Stream
     * @return Stream contenant les valeurs ReferenceEntry
     */
    public List<ReferenceEntry> readReferenceEntries() {
        try (Stream<String> s = Files.lines(Paths.get(rawInputFilePath))) {
            return s.skip(1).map(e -> Parser.parseReference(e)).collect(Collectors.toList());
        } catch (IOException e) {
            // TODO : exception handling
            e.printStackTrace();
        }
        return null;
    }

    /***
     * Transforme les valeurs du fichier modifié CSV en Stream
     * @return Stream contenant les valeurs VirtualizedEntry
     */
    public List<VirtualizerEntry> readVirtualizedEntries() {
        try (Stream<String> s = Files.lines(Paths.get(computedOutputFilePath))) {
            return s.skip(1).map(e -> Parser.parseVirtualizer(e)).collect(Collectors.toList());
        } catch (IOException e) {
            // TODO : exception handling
            e.printStackTrace();
        }
        return null;
    }

    // TODO : complete
/*
    // TODO : replace int p by Pressure p
    public void appendRaw(GPS gps, IMU imu, Temperature temp, int p) {

    }

    public void appendComputed(ReferenceEntry re, Measure m, int i) {

    }
*/
}
