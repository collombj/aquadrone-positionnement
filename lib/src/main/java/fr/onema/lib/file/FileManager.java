package fr.onema.lib.file;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.drone.Measure;
import fr.onema.lib.geo.CartesianCoordinate;
import fr.onema.lib.geo.GeoMaths;
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
    private final String resultsOutputFilePath;
    private final String resultsCSVHeader = "timestamp,corrected.latitute,corrected.longitude,corrected.altitude," +
            "ref.latitude,ref.longitude,ref.altitude,ref.direction,ref.temperature,difference.x,difference.y,difference.z," +
            "difference.absolute,precision,margin,margin.error";

    private static final Logger LOGGER = Logger.getLogger(FileManager.class.getName());

    /***
     * Constructeur du FileManager
     * @param inputFilePath Chemin d'accès vers le fichier brut d'entrée
     * @param virtualizedOutputFilePath Chemin d'accès vers le fichier de sorties des données virtualisées
     * @param resultsOutputFilePath Chemin d'accès vers le fichier de sorties des données computées
     */
    public FileManager(String inputFilePath, String virtualizedOutputFilePath, String resultsOutputFilePath) {
        this.rawInputFilePath = Objects.requireNonNull(inputFilePath);
        this.virtualizedOutputFilePath = Objects.requireNonNull(virtualizedOutputFilePath);
        this.resultsOutputFilePath = Objects.requireNonNull(resultsOutputFilePath);
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
                fw.write(ReferenceEntry.HEADER);
            }
            fw.write("\n" + gps.getTimestamp() + "," + gps.getPosition().lat + "," + gps.getPosition().lon + ","
                    + gps.getPosition().alt + "," + gps.getDirection() + "," + temp.getValue());
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
                fw.write(VirtualizerEntry.HEADER);
            }
            fw.write("\n" + ve.toCSV());
            fw.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /**
     * Open results.csv to write header
     */
    public void openFile() throws IOException {
        File f = new File(resultsOutputFilePath);
        f.delete();
        try (FileWriter fw = new FileWriter(f, false)) {
            fw.write(resultsCSVHeader);
            fw.close();
        } catch (IOException e) {
            throw e;
        }
    }

    /***
     * Permet d'ajouter une ligne au fichier CSV de comparaison results.csv
     * @param re Mesure de référence du fichier brut (raw/ref)
     * @param m Mesure récupérée depuis la base de donnée
     * @param margin Marge d'erreur acceptable entrée par l'utilisateur au début de la génération
     */
    public void appendResults(ReferenceEntry re, MeasureEntity m, double margin) throws IOException {
        File f = new File(resultsOutputFilePath);
        try (FileWriter fw = new FileWriter(f, true)) {
            CartesianCoordinate ref = GeoMaths.computeXYZfromLatLonAlt(re.getLat(),re.getLon(),re.getAlt());
            CartesianCoordinate adjusted = GeoMaths.computeXYZfromLatLonAlt(m.getLocationCorrected().lat, m.getLocationCorrected().lon, m.getLocationCorrected().alt);
            double diffX = ref.x - adjusted.x;
            double diffY = ref.y - adjusted.y;
            double diffZ = ref.z - adjusted.z;
            double diffAbsolute = GeoMaths.cartesianDistance(ref, adjusted);
            boolean error = diffAbsolute > margin ? true : false;
            fw.write("\n" + re.getTimestamp() + "," + m.getLocationCorrected().lat + "," + m.getLocationCorrected().lon
                    + "," + m.getLocationCorrected().alt + "," + re.getLat() + "," + re.getLon() + "," + re.getAlt()
                    + "," + re.getDirection() + "," + re.getTemperature() + "," + diffX + "," + diffY + "," + diffZ
                    + "," + diffAbsolute + "," + m.getPrecisionCm() + "," + margin + "," + error);
            fw.close();
        } catch (IOException e) {
            throw e;
        }
    }
}
