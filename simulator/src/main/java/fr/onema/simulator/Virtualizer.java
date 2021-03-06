package fr.onema.simulator;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.file.manager.RawInput;
import fr.onema.lib.file.manager.ResultsOutput;
import fr.onema.lib.file.manager.VirtualizedOutput;
import fr.onema.lib.network.NetworkSender;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Point d'entrée du simulateur
 */
public class Virtualizer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Virtualizer.class.getName());
    private VirtualizedOutput virtualizedOutput;
    private int speed;
    private String simulationName;
    private int port;
    private String host;
    private long start;
    private long stop;

    /**
     * Constructeur qui initialise un FileManager, un entier de vitesse et un nom de simulation
     *
     * @param virtualizedOutput FileManager
     * @param speed             valeur de la vitesse
     * @param simulationName    Nom de la simulation
     * @param host              Adresse de l'hôte
     * @param port              Port sur lequel on se connecte à l'hôte
     */
    public Virtualizer(VirtualizedOutput virtualizedOutput, int speed, String simulationName, String host, int port) {
        if (speed < 1 || port < 1) {
            throw new IllegalArgumentException("Speed and port cannot be negative");
        } else {
            this.port = port;
            this.speed = speed;
        }
        this.virtualizedOutput = Objects.requireNonNull(virtualizedOutput);
        this.simulationName = Objects.requireNonNull(simulationName);
        this.host = Objects.requireNonNull(host);
    }

    public static void compare(Configuration config, RawInput rawInput, ResultsOutput resultsOutput, double errorAllowed) throws ComparisonException {
        Objects.requireNonNull(config);

        try {
            List<ReferenceEntry> listRefEntry = rawInput.readReferenceEntries();
            MeasureRepository repository = MeasureRepository.MeasureRepositoryBuilder.getRepositoryReadable(config);
            DiveEntity dive = repository.getLastDive();
            List<MeasureEntity> listMeasures = repository.getMeasureFrom(dive);
            int minimum = Math.min(listMeasures.size(), listRefEntry.size());
            if (minimum != 0) {
                resultsOutput.openFileForResults();
                for (int i = 0; i < minimum; i++) {
                    writeIntoFile(listRefEntry.get(i), resultsOutput, listMeasures.get(i), errorAllowed);
                }
            }
        } catch (Exception e) {
            throw new ComparisonException(e);
        }
    }

    private static void writeIntoFile(ReferenceEntry ref, ResultsOutput resultsOutput, MeasureEntity measure, double errVal) throws IOException {
        try {
            resultsOutput.appendResults(ref, measure, errVal);
        } catch (IOException e) {
            LOGGER.error("Couldn't write error in the error file");
            LOGGER.debug("Couldn't write error in the error file", e);

            throw e;
        }
    }

    /**
     * Lance une simulation
     */
    public void start() throws IOException {
        start = System.currentTimeMillis(); //Pour avoir un start en millisecondes
        List<VirtualizerEntry> entries = virtualizedOutput.readVirtualizedEntries();
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
                LOGGER.error("Interrupted during sending");
                LOGGER.debug("Interrupted during sending", e);
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
     * Récupère la vitesse d'obtention de données
     *
     * @return La vitesse d'obtention de données
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * Récupère le nom de la simulation
     *
     * @return Le nom de la simulation
     */
    public String getSimulationName() {
        return simulationName;
    }

    /**
     * Récupère le temps de départ
     *
     * @return La valeur du temps de départ
     */
    private long getStart() {
        return start;
    }

    /**
     * Récupère le temps de fin
     *
     * @return La valeur du temps de fin
     */
    private long getStop() {
        return stop;
    }

    /**
     * Récupère le port vers la base de données
     *
     * @return Le numéro de port de la base de données
     */
    public int getPort() {
        return port;
    }

    /**
     * Récupère le host de la base de donnéesÒ
     *
     * @return Le hostname de la base de données
     */
    public String getHost() {
        return host;
    }
}
