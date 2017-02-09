package fr.onema.lib.file;

import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * Created by you on 07/02/2017.
 */

/***
 * Classe utilitaire permettant la gestion des CSV brut et modifiés de données MavLink
 */
public class FileManager {
    private final String rawInputFilePath;
    private final String virtualizedOutputFilePath;
    private final String computedOutputFilePath;

    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    /***
     * Constructeur du FileManager
     * @param inputFilePath Chemin d'accès vers le fichier brut d'entrée
     * @param virtualizedOutputFilePath Chemin d'accès vers le fichier de sorties des données virtualisées
     * @param computedOutputFilePath Chemin d'accès vers le fichier de sorties des données computées
     */
    public FileManager(String inputFilePath, String virtualizedOutputFilePath, String computedOutputFilePath) {
        this.rawInputFilePath = Objects.requireNonNull(inputFilePath);
        this.virtualizedOutputFilePath = Objects.requireNonNull(virtualizedOutputFilePath);
        this.computedOutputFilePath = Objects.requireNonNull(computedOutputFilePath);
    }

    /***
     * Transforme les valeurs du fichier brut CSV en Liste
     * @return Liste contenant les valeurs ReferenceEntry
     */
    public List<ReferenceEntry> readReferenceEntries() throws IOException {
        List<ReferenceEntry> refs = new ArrayList<>();
        try (Stream<String> s = Files.lines(Paths.get(rawInputFilePath))) {
            s.skip(1).forEach(e -> refs.add(Parser.parseReference(e)));
        } catch (IOException e) {
            throw e;
        }
        return refs;
    }

    /***
     * Transforme les valeurs du fichier modifié CSV en Liste
     * @return Liste contenant les valeurs VirtualizedEntry
     */
    public List<VirtualizerEntry> readVirtualizedEntries() throws IOException {
        List<VirtualizerEntry> virts = new ArrayList<>();
        try (Stream<String> s = Files.lines(Paths.get(virtualizedOutputFilePath))) {
            s.skip(1).forEach(e -> virts.add(Parser.parseVirtualizer(e)));
        } catch (IOException e) {
            throw e;
        }
        return virts;
    }

    /***
     * Permet d'écrire le fichier de données computés (virtualized + ref) en CSV
     */
    public void computeFilesIntoCSV() throws IOException {
        List<ReferenceEntry> refs = readReferenceEntries();
        List<VirtualizerEntry> virts = readVirtualizedEntries();
        if (refs.size() != virts.size()) {
            throw new IllegalArgumentException("Files (virtualized + ref don't have the same length");
        }
        File f = new File(computedOutputFilePath);
        try (FileWriter fw = new FileWriter(f, false)) {
            fw.write("timestamp,gpsLongitude,gpsLatitude,gpsAltitude,accelerationX,accelerationY,accelerationZ,rotationX,rotationY,rotationZ,capX,capY,capZ,pression,temperature,latitude,longitude,altitude,direction");
            for (int i = 0; i < refs.size(); i++) {
                fw.write("\n" + virts.get(i).toCSV() + "," + refs.get(i).toCSVforComputedFormat());
            }
            fw.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to write in the computed file");
            throw e;
        }
    }

    private int getLineNumber(File f) throws IOException {
        return (int)Files.lines(Paths.get(f.getPath())).count();
    }

    /***
     * Permet d'ajouter une ligne au fichier CSV brut d'entrée (ref)
     * @param gps Coordonnées gps
     * @param temp Valeur de la température
     */
    public void appendRaw(GPS gps, Temperature temp) throws IOException {
        File f = new File(rawInputFilePath);
        try (FileWriter fw = new FileWriter(f, true)) {
            int lineNumber = getLineNumber(f);
            if (!f.exists() || lineNumber == 0) {
                fw.write(ReferenceEntry.header);
            }
            fw.write("\n" + gps.getTimestamp() + "," + gps.getPosition().lat + "," + gps.getPosition().lon + "," + gps.getPosition().alt + "," + gps.getDirection() + "," + temp.getValue());
            fw.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /***
     * Permet d'ajouter une ligne au fichier CSV virtualisé de sortie (virtualized)
     * @param ve VirtualizedEntry contenant les valeurs à écrire
     */
    public void appendVirtualized(VirtualizerEntry ve) throws IOException {
        File f = new File(virtualizedOutputFilePath);
        try (FileWriter fw = new FileWriter(f, true)) {
            int lineNumber = getLineNumber(f);
            if (!f.exists() || lineNumber == 0) {
                fw.write(VirtualizerEntry.header);
            }
            fw.write("\n" + ve.toCSV());
            fw.close();
        } catch (IOException e) {
            throw e;
        }
    }
}
