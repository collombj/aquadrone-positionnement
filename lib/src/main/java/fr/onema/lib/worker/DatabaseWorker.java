package fr.onema.lib.worker;

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
 * <p>
 * Cette classe permet de créer un thread qui va gérer les accès à la base de données
 */
public class DatabaseWorker implements Worker {

    private final Thread dbWorkerThread;
    private final LinkedBlockingQueue<DatabaseAction> actionQueue = new LinkedBlockingQueue<>(10000);
    /**
     * La methode d'insertion en base utilisée par le thread
     */
    private BiConsumer<MeasureRepository, Object[]> newDiveAux = (repository, args) -> {
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
     * La methode d'insertion des mesures
     */
    private BiConsumer<MeasureRepository, Object[]> insertMeasureAux = (repository, args) -> {
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
     * La methode utilisée par le thread pour mettre à jour la base
     */
    private BiConsumer<MeasureRepository, Object[]> updatePositionAux = (repository, args) -> {
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
     * Cette méthode est utlisée par le thread pour mettre à jour la base
     */
    private BiConsumer<MeasureRepository, Object[]> startRecordingAux = (repository, args) -> {
        if (args.length != 2 || !(args[0] instanceof Long) || !(args[1] instanceof Integer)) {
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
     * Cette méthode est utlisée par le thread pour mettre à jour la base
     */
    private BiConsumer<MeasureRepository, Object[]> stopRecordingAux = (repository, args) -> {
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
     * Cette méthode est utlisée par le thread pour notifier la base
     */
    private BiConsumer<MeasureRepository, Object[]> sendNotificationAux = (repository, args) -> {
        if (args.length != 1 || !(args[0] instanceof String)) {
            System.out.println("Error DatabaseWorker.sendNotification : invalid args");
            return;
        }
        try {
            repository.sendNotification((String) args[0]);
        } catch (SQLException e) {
            System.out.println("Error DatabaseWorker.sendNotification : could send notification : " + args[0]);
            e.printStackTrace();
        }
    };

    /**
     * Constructeur
     *
     * @param configuration un object Configuration avec les paramètres de connexion à la base de données
     */
    public DatabaseWorker(Configuration configuration) {
        dbWorkerThread = new Thread(() -> {
            try {
                MeasureRepository repository =
                        MeasureRepository.MeasureRepositoryBuilder.getRepositoryWritable(configuration);
                while (!Thread.currentThread().isInterrupted()) {
                    DatabaseAction action = actionQueue.take();
                    if (action != null) {
                        action.getAction().accept(repository, action.getObj());
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("database thread interrupted");
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
     * Permet d'inserer une nouvelle Dive en base de données
     *
     * @param dive une DiveEntity
     */
    public void newDive(DiveEntity dive) {
        actionQueue.offer(new DatabaseAction(newDiveAux, dive));
    }

    /**
     * Permet d'inserer une MeasureEntity
     *
     * @param measureEntity la MeasureEntity à insérer en base
     * @param diveID        l'identifiant de la plongée de la mesure
     * @param measureInfoID l'identifiant du type de mesure
     */
    public void insertMeasure(MeasureEntity measureEntity, int diveID, int measureInfoID) {
        actionQueue.offer(
                new DatabaseAction(insertMeasureAux, measureEntity, new Integer(diveID), new Integer(measureInfoID)));
    }

    /**
     * Permet de mettre à jour une MeasureEntity dans la base de données
     *
     * @param measureId         L'identifiant de la MeasureId à modifier
     * @param positionCorrected Les nouvelles coordonnées
     * @param precisionCm       La precision estimée de la mesure en cm
     */
    public void updatePosition(int measureId, GPSCoordinate positionCorrected, int precisionCm) {
        actionQueue.offer(new DatabaseAction(
                updatePositionAux, new Integer(measureId), positionCorrected, new Integer(precisionCm)));
    }

    /**
     * Cette méthode permet de mettre à jour l'heure de début d'une DiveEntity
     *
     * @param timestamp L'heure de début de plongée
     * @param diveID    L'identifiant de la DiveEntity
     */
    public void startRecording(long timestamp, int diveID) {
        actionQueue.offer(new DatabaseAction(startRecordingAux, new Long(timestamp), new Integer(diveID)));
    }

    /**
     * Cette méthode permet de mettre à jour la date de fin d'une DiveEntity
     *
     * @param timestamp L'heure de fin de plongée
     * @param diveID    L'identifiant de la diveEntity
     */
    public void stopRecording(long timestamp, int diveID) {
        actionQueue.offer(new DatabaseAction(stopRecordingAux, new Long(timestamp), new Integer(diveID)));
    }

    /**
     * Cette méthode permet d'envoyer des notifications à la base de données
     *
     * @param message le message a notifier
     */
    public void sendNotification(String message) {
        actionQueue.offer(new DatabaseAction(sendNotificationAux, message));
    }

    /**
     * Permet de communiquer les objets à traiter au thread sous une forme normalisée
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
}
