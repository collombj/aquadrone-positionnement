package fr.onema.app.Worker;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Francois Vanderperre on 08/02/2017.
 */
public class DatabaseWorker implements Worker {
    private Queue<Runnable> actionQueue = new LinkedBlockingDeque<>();//TODO a fixer

    /**
     *
     */
    @Override
    public void start() {

    }

    /**
     *
     */
    @Override
    public void stop() {

    }

    /**
     * @return
     */
    public int insertMeasure() {//TODO
        return 0;
    }

    /**
     *
     */
    public void updatePosition() {//TODO

    }

    /**
     * @param timestamp
     */
    public void startRecording(long timestamp){//TODO

    }

    /**
     * @param timestamp
     */
    public void stopRecording(long timestamp){//TODO

    }

}
