package fr.onema.lib.worker;


import fr.onema.lib.drone.Position;
import fr.onema.lib.file.manager.VirtualizedOutput;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe de logging, affiche les informations relatives à une plongée pour traitement ultérieur.
 *
 * @author loics
 * @since 15-02-2017
 */
public class Tracer implements Worker {

    private static final Logger LOG = Logger.getLogger(Logger.class.getName());

    private final VirtualizedOutput virtualizedOutput;
    private final BlockingDeque<Position> positions = new LinkedBlockingDeque<>();
    private final Thread tracerThread = new Thread(new TracerWorker());

    /**
     * Créer un Tracer.
     *
     * @param virtualizedOutput Le FileManager qui va servir à écrire les données issues du tracer..
     */
    Tracer(VirtualizedOutput virtualizedOutput) {
        this.virtualizedOutput = Objects.requireNonNull(virtualizedOutput);
    }

    @Override
    public void start() {
        this.tracerThread.start();
    }

    @Override
    public void stop() {
        this.tracerThread.interrupt();
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

    private class TracerWorker implements Runnable {

        private VirtualizerEntry createVirtualizedFromPosition(Position position) {
            return new VirtualizerEntry(position.getGps(), position.getImu(), position.getPressure(), position.getMeasures());
        }

        @Override
        public void run() {
            while(!Thread.interrupted()) {
                try {
                    virtualizedOutput.appendVirtualized(createVirtualizedFromPosition(positions.take()));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    LOG.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
    }
}
