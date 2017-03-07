package fr.onema.lib.worker;

/***
 * Interface pour partager des méthodes entre Workers
 */
public interface Worker {
    /**
     * Démarre le worker
     */
    void start();

    /**
     * Arrête le worker
     */
    void stop();
}
