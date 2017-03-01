package fr.onema.lib.file.manager;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * Classe utilitaire permettant la gestion des CSV brut et modifiés de données MavLink
 */
public interface FileManager {

    Logger LOGGER = Logger.getLogger(FileManager.class.getName());
    String RESULTS_CSV_HEADER = "timestamp,corrected.latitude,corrected.longitude,corrected.altitude,brut.latitude,brut.longitude,brut.altitude," +
            "ref.latitude,ref.longitude,ref.altitude,ref.direction,ref.temperature,difference.x,difference.y,difference.z," +
            "difference.absolute,precision,margin,margin.error";

    /***
     * Transforme les valeurs du fichier brut CSV en Liste
     * @return Liste contenant les valeurs ReferenceEntry
     */
    List<ReferenceEntry> readReferenceEntries() throws IOException;

    /***
     * Transforme les valeurs du fichier modifié CSV en Liste
     * @return Liste contenant les valeurs VirtualizedEntry
     */
    List<VirtualizerEntry> readVirtualizedEntries() throws IOException;

    /***
     * Permet d'ajouter une ligne au fichier CSV brut d'entrée (ref)
     * @param gps Coordonnées gps
     * @param temp Valeur de la température
     */
    void appendRaw(GPS gps, Temperature temp) throws IOException;

    /***
     * Permet d'ajouter une ligne au fichier CSV virtualisé de sortie (virtualized)
     * @param ve VirtualizedEntry contenant les valeurs à écrire
     */
    void appendVirtualized(VirtualizerEntry ve) throws IOException;

    /**
     * Open results.csv to write header
     */
    void openFileForResults() throws IOException;

    /***
     * Permet d'ajouter une ligne au fichier CSV de comparaison results.csv
     * @param re Mesure de référence du fichier brut (raw/ref)
     * @param m Mesure récupérée depuis la base de donnée
     * @param margin Marge d'erreur acceptable entrée par l'utilisateur au début de la génération
     */
    void appendResults(ReferenceEntry re, MeasureEntity m, double margin) throws IOException;

    /**
     * Méthode permettant d'obtenir une lecture reformattée du fichier de comparaison
     *
     * @param replacement Caractère remplacant "," [virgule] dans le csv d'entrée
     * @return Le fichier de résultat dans une liste de ligne. Chaque élément est séparé par le caractère de remplacement spécifié.
     * @throws IOException En cas d'erreur du à la manipulation du fichier
     */
    List<String> getResults(String replacement) throws IOException;

    default int getLineNumber(File f) throws IOException {
        return (int) Files.lines(Paths.get(f.getPath())).count();
    }
}
