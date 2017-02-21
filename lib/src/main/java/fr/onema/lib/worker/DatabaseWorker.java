package fr.onema.lib.worker;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Francois Vanderperre on 08/02/2017.
 * <p>
 * Cette classe permet de créer un thread qui va gérer les accès à la base de données
 */
public class DatabaseWorker implements Worker {

    private static final Logger LOGGER = Logger.getLogger(DatabaseWorker.class.getName());
    private static DatabaseWorker INSTANCE ;
    private static Thread dbWorkerThread;
    private static LinkedBlockingQueue<DatabaseAction> actionQueue = new LinkedBlockingQueue<>(10000);
    private static String notificationKey;
    /**
     * La methode d'insertion en base utilisée par le thread
     */
    private BiConsumer<MeasureRepository, Object[]> newDiveAux = (repository, args) -> {
        if (args.length != 1 || !(args[0] instanceof DiveEntity)) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.newDive : invalid args");
            return;
        }
        try {
            repository.insertDive((DiveEntity) args[0]);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.newDive: couldn't insert dive", e);
        }
    };
    /**
     * La methode d'insertion des mesures
     */
    private BiConsumer<MeasureRepository, Object[]> insertMeasureAux = (repository, args) -> {
        if (args.length != 3 || !(args[0] instanceof MeasureEntity)
                || !(args[1] instanceof Integer) || !(args[2] instanceof String)) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.insertMeasure : invalid args");
            return;
        }
        try {
            repository.insertMeasure((MeasureEntity) args[0], (Integer) args[1], (String) args[2]);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.insertMeasure : could not insert measure", e);
        }
    };
    /**
     * La methode utilisée par le thread pour mettre à jour la base
     */
    private BiConsumer<MeasureRepository, Object[]> updatePositionAux = (repository, args) -> {
        if (args.length != 3 || !(args[0] instanceof Integer)
                || !(args[1] instanceof GPSCoordinate) || !(args[2] instanceof Integer)) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.updatePosition : invalid args");
            return;
        }
        try {
            repository.updateMeasure((Integer) args[0], (GPSCoordinate) args[1], (Integer) args[2]);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.updatePosition : could not update position " + args[0], e);
        }
    };
    /**
     * Cette méthode est utlisée par le thread pour mettre à jour la base
     */
    private BiConsumer<MeasureRepository, Object[]> startRecordingAux = (repository, args) -> {
        if (args.length != 2 || !(args[0] instanceof Long) || !(args[1] instanceof Integer)) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.startRecording : invalid args");
            return;
        }
        try {
            repository.updateStartTime((Integer) args[1], (Long) args[0]);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.startRecording : could not update dive " + args[0], e);
        }
    };
    /**
     * Cette méthode est utlisée par le thread pour mettre à jour la base
     */
    private BiConsumer<MeasureRepository, Object[]> stopRecordingAux = (repository, args) -> {
        if (args.length != 2 || !(args[0] instanceof Long) || !(args[1] instanceof Integer)) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.stopRecording : invalid args");
            return;
        }
        try {
            repository.updateEndTime((Integer) args[1], (Long) args[0]);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.stopRecording : could not update dive " + args[0], e);
        }
    };
    /**
     * Cette méthode est utilisée par le thread pour notifier la base
     */
    private BiConsumer<MeasureRepository, Object[]> sendNotificationAux = (repository, args) -> {
        if (args.length != 1 || !(args[0] instanceof String)) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.sendNotification : invalid args");
            return;
        }
        try {
            repository.sendNotification((String) args[0]);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error DatabaseWorker.sendNotification : could send notification : " + args[0], e);
        }
    };

    /**
     * Le constructeur est privé pour garantir l'unicité du singleton
     */
    private DatabaseWorker() {
    }

    /**
     * Permet d'obtenir la seule instance de databaseworker
     *
     * @return l'instance de databaseworker
     */
    public static DatabaseWorker getInstance() {
        if (INSTANCE == null){
            init();
        }
        return INSTANCE;
    }

    /**
     * Initialise le singlet
     * Doit etre appelée avant toute utilisation du databaseworker
     */
    private static void init() {
        INSTANCE = new DatabaseWorker();
        Configuration configuration = Configuration.getInstance();
        notificationKey = configuration.getDatabaseInformation().getNotifyKey();
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
                Thread.currentThread().interrupt();
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
        if (!actionQueue.offer(new DatabaseAction(newDiveAux, dive))) {
            LOGGER.log(Level.SEVERE, "DatabaseWorker.newDive : Dive insertion failed");
        }
    }

    /**
     * Permet d'inserer une MeasureEntity
     *
     * @param measureEntity   la MeasureEntity à insérer en base
     * @param diveID          l'identifiant de la plongée de la mesure
     * @param measureInfoName l'identifiant du type de mesure
     */
    public void insertMeasure(MeasureEntity measureEntity, int diveID, String measureInfoName) {
        if (!actionQueue.offer(new DatabaseAction(insertMeasureAux, measureEntity, diveID, measureInfoName))) {
            LOGGER.log(Level.SEVERE, "DatabaseWorker.insertMeasure : Measure insertion failed");
        }
    }

    /**
     * Permet de mettre à jour une MeasureEntity dans la base de données
     *
     * @param measureId         L'identifiant de la MeasureId à modifier
     * @param positionCorrected Les nouvelles coordonnées
     * @param precisionCm       La precision estimée de la mesure en cm
     */
    public void updatePosition(int measureId, GPSCoordinate positionCorrected, int precisionCm) {
        if (!actionQueue.offer(new DatabaseAction(updatePositionAux, measureId, positionCorrected, precisionCm))) {
            LOGGER.log(Level.SEVERE, "DatabaseWorker.updatePosition : Measure update failed");
        }
    }

    /**
     * Cette méthode permet de mettre à jour l'heure de début d'une DiveEntity
     *
     * @param timestamp L'heure de début de plongée
     * @param diveID    L'identifiant de la DiveEntity
     */
    public void startRecording(long timestamp, int diveID) {
        if (!actionQueue.offer(new DatabaseAction(startRecordingAux, timestamp, diveID))) {
            LOGGER.log(Level.SEVERE, "DatabaseWorker.startRecording : Dive timestamp update failed");
        }
    }

    /**
     * Cette méthode permet de mettre à jour la date de fin d'une DiveEntity
     *
     * @param timestamp L'heure de fin de plongée
     * @param diveID    L'identifiant de la diveEntity
     */
    public void stopRecording(long timestamp, int diveID) {
        if (!actionQueue.offer(new DatabaseAction(stopRecordingAux, timestamp, diveID))) {
            LOGGER.log(Level.SEVERE, "DatabaseWorker.stopRecording : Dive timestamp update failed");
        }
    }

    /**
     * Cette méthode permet d'envoyer des notifications à la base de données
     */
    public void sendNotification() {
        if (!actionQueue.offer(new DatabaseAction(sendNotificationAux, notificationKey))) {
            LOGGER.log(Level.SEVERE, "DatabaseWorker.sendNotification : Database notification failed");
        }
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
