package fr.onema.app.Worker;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;


/**
 * Created by Francois Vanderperre on 08/02/2017.
 */
public class DatabaseWorker implements Worker {

    /*
    *   Permet de communiquer les objets à traiter au thread
     */
    private class DatabaseAction {
        BiConsumer<MeasureRepository, Object[]> action;
        Object[] obj;

        DatabaseAction(BiConsumer<MeasureRepository, Object[]> action, Object... args) {
            this.action = action;
            this.obj = args;
        }

        BiConsumer<MeasureRepository, Object[]> getAction() {
            return action;
        }

        Object[] getObj() {
            return obj;
        }
    }

    private final Thread dbWorkerThread;
    private final LinkedBlockingQueue<DatabaseAction> actionQueue = new LinkedBlockingQueue<>(10000);

    public DatabaseWorker(Configuration configuration) {
        dbWorkerThread = new Thread(() -> {
            try {
                MeasureRepository repository =
                        MeasureRepository.MeasureRepositoryBuilder.getRepositoryWritable(configuration);
                while (!Thread.currentThread().isInterrupted()) {
                    DatabaseAction action = actionQueue.poll();
                    action.getAction().accept(repository, action.getObj());
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Démarre le thread de gestion de base de données
     */
    @Override
    public void start() {
        dbWorkerThread.start();
    }

    /**
     * Interrompt le thread de gestion de base de données
     */
    @Override
    public void stop() {
        dbWorkerThread.interrupt();
    }

    /**
     * @param dive
     */
    public void newDive(DiveEntity dive) {
        actionQueue.offer(new DatabaseAction(_newDive, dive));
    }

    /**
     *
     */
    private BiConsumer<MeasureRepository, Object[]> _newDive = (repository, args) -> {
        if (args.length != 1 || !(args[0] instanceof DiveEntity)) {
            System.out.println("Error DatabaseWorker.newDive : invalid args");
            return;
        }
        try {
            repository.insertDive((DiveEntity) args[0]);
        } catch (SQLException e) {
            System.out.println("Error DatabaseWorker.newDive: couldn t insert dive");
            e.printStackTrace();
        }
    };

    /**
     * @return
     */
    public void insertMeasure(MeasureEntity measureEntity, int diveID, int measureInfoID) {//TODO
        actionQueue.offer(
                new DatabaseAction(_insertMeasure, measureEntity, new Integer(diveID), new Integer(measureInfoID)));
    }

    /**
     *
     */
    private BiConsumer<MeasureRepository, Object[]> _insertMeasure = (repository, args) -> {
        if (args.length != 3 || !(args[0] instanceof MeasureEntity)
                || !(args[1] instanceof Integer) || !(args[2] instanceof Integer)) {
            System.out.println("Error DatabaseWorker.insertMeasure : invalid args");
            return;
        }
        try {
            repository.insertMeasure((MeasureEntity) args[0], (Integer) args[1], (Integer) args[2]);
        } catch (SQLException e) {
            System.out.println("Error DatabaseWorker.insertMeasure : could not insert measure");
            e.printStackTrace();
        }
    };

    /**
     *
     */
    public void updatePosition(int measureId, GPSCoordinate positionCorrected, int precisionCm) {//TODO
        actionQueue.offer(new DatabaseAction(
                _updatePosition, new Integer(measureId), positionCorrected, new Integer(precisionCm)));
    }

    private BiConsumer<MeasureRepository, Object[]> _updatePosition = (repository, args) -> {
        if (args.length != 3 || !(args[0] instanceof Integer)
                || !(args[1] instanceof GPSCoordinate) || !(args[2] instanceof Integer)) {
            System.out.println("Error DatabaseWorker.updatePosition : invalid args");
            return;
        }
        try {
            repository.updateMeasure((Integer) args[0], (GPSCoordinate) args[1], (Integer) args[2]);
        } catch (SQLException e) {
            System.out.println("Error DatabaseWorker.updatePosition : could not update position " + args[0]);
            e.printStackTrace();
        }
    };

    /**
     * @param timestamp
     * @param diveID
     */
    public void startRecording(long timestamp, int diveID) {
        actionQueue.offer(new DatabaseAction(_startRecording, new Long(timestamp), new Integer(diveID)));
    }

    /**
     *
     */
    private BiConsumer<MeasureRepository, Object[]> _startRecording = (repository, args) -> {
        if (args.length != 2 || !(args[0] instanceof Long) || !(args[1] instanceof Integer) ) {
            System.out.println("Error DatabaseWorker.startRecording : invalid args");
            return;
        }
        try {
            repository.updateStartTime((Integer) args[1], (Long) args[0]);
        } catch (SQLException e) {
            System.out.println("Error DatabaseWorker.startRecording : could not update dive " + args[0]);
            e.printStackTrace();
        }
    };

    /**
     * @param timestamp
     * @param diveID
     */
    public void stopRecording(long timestamp, int diveID) {
        actionQueue.offer(new DatabaseAction(_stopRecording, new Long(timestamp), new Integer(diveID)));
    }

    /**
     *
     */
    private BiConsumer<MeasureRepository, Object[]> _stopRecording = (repository, args) -> {
        if (args.length != 2 || !(args[0] instanceof Long) || !(args[1] instanceof Integer)) {
            System.out.println("Error DatabaseWorker.stopRecording : invalid args");
            return;
        }
        try {
            repository.updateEndTime((Integer) args[1], (Long) args[0]);
        } catch (SQLException e) {
            System.out.println("Error DatabaseWorker.stopRecording : could not update dive " + args[0]);
            e.printStackTrace();
        }
    };

    /**
     * @param message
     */
    public void sendNotification(String message) {
        actionQueue.offer(new DatabaseAction(_sendNotification, message));
    }

    /**
     *
     */
    private BiConsumer<MeasureRepository, Object[]> _sendNotification = (repository, args) -> {
        if (args.length != 1 || !(args[0] instanceof String)) {
            System.out.println("Error DatabaseWorker.sendNotification : invalid args");
            return;
        }
    };
}
