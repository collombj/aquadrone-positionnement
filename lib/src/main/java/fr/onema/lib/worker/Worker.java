package fr.onema.lib.worker;

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
