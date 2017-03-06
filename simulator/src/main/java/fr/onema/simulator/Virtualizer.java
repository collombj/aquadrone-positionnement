package fr.onema.simulator;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.network.NetworkSender;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Point d'entrée du simulateur
 */
public class Virtualizer {
    private static final Logger LOGGER = Logger.getLogger(Virtualizer.class.getName());
    private FileManager fileManager;
    private int speed;
    private String simulationName;
    private int port;
    private String host;
    private long start;
    private long stop;

    /**
     * Constructeur qui initialise un FileManager, un entier de vitesse et un nom de simulation
     *
     * @param filePathInput  FileManager
     * @param speed          valeur de la vitesse
     * @param simulationName Nom de la simulation
     * @param host           Adresse de l'hôte
     * @param port           Port sur lequel on se connecte à l'hôte
     */
    public Virtualizer(FileManager filePathInput, int speed, String simulationName, String host, int port) {
        if (speed < 1 || port < 1) {
            throw new IllegalArgumentException("Speed and port cannot be negative");
        } else {
            this.port = port;
            this.speed = speed;
        }
        this.fileManager = Objects.requireNonNull(filePathInput);
        this.simulationName = Objects.requireNonNull(simulationName);
        this.host = Objects.requireNonNull(host);
    }

    /**
     * Lance une simulation
     */
    public void start() throws IOException {
        start = System.currentTimeMillis(); //Pour avoir un start en millisecondes
        List<VirtualizerEntry> entries = fileManager.readVirtualizedEntries();
        NetworkSender sender = new NetworkSender(port, host);
        sender.start();
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);

        long previousTimestamp = entries.get(0).getTimestamp();

        for (int i = 0; i < entries.size(); i++) {
            final int count = i;
            ScheduledFuture<?> scheduled = executor.schedule(() -> sender.add(entries.get(count)), entries.get(count).getTimestamp() - previousTimestamp, TimeUnit.MILLISECONDS);
            try {
                scheduled.get();
            } catch (InterruptedException | ExecutionException e) {
                LOGGER.log(Level.SEVERE, "Interrupted during sending", e);
            }
            previousTimestamp = entries.get(count).getTimestamp();
        }

        executor.shutdown();
        sender.closeConnection();
        stop = System.currentTimeMillis(); //Pour avoir un stop en millisecondes
    }

    /**
     * Récupère la durée de la simulation en millisecondes
     *
     * @return la durée de la simulation
     */
    public long getDuration() {
        return getStop() - getStart();
    }

    /**
     * Fonction permettant la comparaison des entrées
     * @param config La configuration associée
     * @param errorAllowed Le taux d'erreur acceptable
     * @throws ComparisonException En cas d'échec de comparaison
     */
    public void compare(Configuration config, double errorAllowed) throws ComparisonException {
        Objects.requireNonNull(config);

        try {
            List<ReferenceEntry> listRefEntry = fileManager.readReferenceEntries();
            MeasureRepository repository = MeasureRepository.MeasureRepositoryBuilder.getRepositoryReadable(config);
            DiveEntity dive = repository.getLastDive();
            List<MeasureEntity> listMeasures = repository.getMeasureFrom(dive);
            int minimum = Math.min(listMeasures.size(), listRefEntry.size());
            fileManager.openFileForResults();
            for (int i = 0; i < minimum; i++) {
                writeIntoFile(listRefEntry.get(i), listMeasures.get(i), errorAllowed);
            }
        } catch (Exception e) {
            throw new ComparisonException(e);
        }
    }

    private void writeIntoFile(ReferenceEntry ref, MeasureEntity measure, double errVal) throws IOException {
        try {
            fileManager.appendResults(ref, measure, errVal);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Couldn't write error in the error file", e);
            throw e;
        }
    }

    /**
     * Récupère la vitesse d'obtention de données
     * @return La vitesse d'obtention de données
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Récupère le nom de la simulation
     * @return Le nom de la simulation
     */
    public String getSimulationName() {
        return simulationName;
    }

    /**
     * Récupère le temps de départ
     * @return La valeur du temps de départ
     */
    private long getStart() {
        return start;
    }

    /**
     * Récupère le temps de fin
     * @return La valeur du temps de fin
     */
    private long getStop() {
        return stop;
    }

    /**
     * Récupère le port vers la base de données
     * @return Le numéro de port de la base de données
     */
    public int getPort() {
        return port;
    }

    /**
     * Récupère le host de la base de donnéesÒ
     * @return Le hostname de la base de données
     */
    public String getHost() {
        return host;
    }
}
