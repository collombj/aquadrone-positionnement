package fr.onema.lib.worker;


import fr.onema.lib.drone.Position;
import fr.onema.lib.file.FileManager;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Classe de logging, affiche les informations relatives à une plongée pour traitement ultérieur.
 *
 * @author loics
 * @since 15-02-2017
 */
public class Tracer implements Worker {

    private static final Logger LOG = LoggerFactory.getLogger(Tracer.class.getName());

    private final FileManager fileManager;
    private final BlockingDeque<Position> positions = new LinkedBlockingDeque<>();
    private final Thread loggerThread = new Thread(new LoggerWorker());

    /**
     * Créer un Logger.
     *
     * @param fileManager Le FileManager qui va servir à écrire les données issues du logger..
     */
    Tracer(FileManager fileManager) {
        this.fileManager = Objects.requireNonNull(fileManager);
    }

    @Override
    public void start() {
        this.loggerThread.start();
    }

    @Override
    public void stop() {
        this.loggerThread.interrupt();
    }

    /**
     * Ajoute une position à la file des positions.
     *
     * @param currentPos La position à tracer.
     * @return Vrai si la position à été ajoutée avec succès. Sinon faux.
     */
    boolean addPosition(Position currentPos) {
        return this.positions.offer(Objects.requireNonNull(currentPos));
    }

    private class LoggerWorker implements Runnable{

        private VirtualizerEntry createVirtualizedFromPosition(Position position) {
            return new VirtualizerEntry(position.getGps(), position.getImu(), position.getPressure(), position.getMeasures());
        }

        @Override
        public void run() {
            try {
                fileManager.openFileForResults();
            } catch (IOException e) {
                LOG.error(e.getMessage());
                LOG.debug(e.getMessage(), e);
            }
            while(!Thread.interrupted()) {
                try {
                    fileManager.appendVirtualized(createVirtualizedFromPosition(positions.take()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    LOG.error(e.getMessage());
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
    }
}
