package fr.onema.lib.worker;

/**
 * Created by Jérôme on 06/02/2017.
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
