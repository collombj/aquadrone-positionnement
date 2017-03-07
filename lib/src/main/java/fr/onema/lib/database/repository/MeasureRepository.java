package fr.onema.lib.database.repository;

import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.entity.MeasureInformationEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

/**
 * Cette classe pour but de permettre à l'application d'envoyer des objets vers la base de données et d'en recevoir
 */
public class MeasureRepository {
    private DatabaseDriver dbDriver;

    private MeasureRepository(Configuration config) {
        dbDriver = DatabaseDriver.build(config);
    }

    /**
     * Cette méthode rend possible l'utilisation de la base pour des opérations de lecture et d'écriture.
     * A n'utiliser que si la connexion a été fermée préalablement
     */
    void setWritable() {
        dbDriver.initAsWritable();
    }

    /**
     * Cette méthode rend possible l'utilisation de la base pour des opérations de lecture uniquement
     * A n'utiliser que si la connexion a été fermée préalablement
     */
    void setReadable() {
        dbDriver.initAsReadable();
    }

    /**
     * Cette méthode insère une Dive dans la base de données
     *
     * @param dive La Dive à insérer
     * @return La Dive dont l'identifiant a été mis à jour avec son index en base de données
     */
    public DiveEntity insertDive(DiveEntity dive) throws SQLException {
        dive.setId(dbDriver.insertDive(dive));
        return dive;
    }

    /**
     * Cette méthode renvoie la dernière Dive qui a été insérée en base de données
     *
     * @return La dernière Dive qui a été insérée dans la base de données
     */
    public DiveEntity getLastDive() throws SQLException {
        return dbDriver.getLastDive();
    }

    /**
     * Cette méthode permet de mettre a jour l'instant auquel a démarré une Dive
     *
     * @param diveId    l'indentifiant de la Dive à modifier
     * @param timestamp la nouvelle heure de début
     */
    public void updateStartTime(int diveId, long timestamp) throws SQLException {
        dbDriver.startRecording(timestamp, diveId);
    }

    /**
     * Cette méthode permet de mettre à jour l'instant auquel s'est terminée une Dive
     *
     * @param diveId    l'identifiant de la dive
     * @param timestamp la nouvelle heure de fin
     */
    public void updateEndTime(int diveId, long timestamp) throws SQLException {
        dbDriver.stopRecording(timestamp, diveId);
    }

    /**
     * Permet d'insérer en base une Measure
     *
     * @param mesure La Measure a mettre en base
     * @return La Measure dont l'identifiant a été mis à jour avec son index d'insertion en base
     */
    public MeasureEntity insertMeasure(MeasureEntity mesure, int diveID, String measureInfoName) throws SQLException {
        mesure.setId(dbDriver.insertMeasure(mesure, diveID, measureInfoName));
        return mesure;
    }

    /**
     * Permet d'insérer en base une Measure
     *
     * @param mesure La Measure a mettre en base
     * @return La Measure dont l'identifiant a été mis à jour avec son index d'insertion en base
     */
    public MeasureEntity insertMeasure(MeasureEntity mesure, int diveID, int measureInfoId) throws SQLException {
        mesure.setId(dbDriver.insertMeasure(mesure, diveID, measureInfoId));
        return mesure;
    }

    /**
     * Permet de modifier le champ correspondant à la position recalculée d'une Measure en base de données
     *
     * @param measureId         L'identifiant de la Measure à modifier
     * @param positionCorrected Les nouvelles coordonnées
     * @param precisionCm       La précision de la nouvelle mesure
     */
    public void updateMeasure(int measureId, GPSCoordinate positionCorrected, int precisionCm) throws SQLException {
        Objects.requireNonNull(positionCorrected);
        dbDriver.updatePosition(
                measureId, positionCorrected.lat, positionCorrected.lon, positionCorrected.alt, precisionCm);
    }

    /**
     * Récupère les mesures d'une plongée donnée.
     *
     * @param dive La plongée.
     * @return La liste de mesures.
     * @throws SQLException En cas d'erreur de connexion à la base.
     */
    public List<MeasureEntity> getMeasureFrom(DiveEntity dive) throws SQLException {
        return dbDriver.getMeasureFrom(dive);
    }

    /**
     * Envoie une notification sous forme de chaine de caractère.
     *
     * @param message Le message à envoyer via la notification.
     * @throws SQLException En cas d'erreur de connexion à la base.
     */
    public void sendNotification(String message) throws SQLException {
        dbDriver.sendNotification(message);
    }

    /**
     * Permet de fermer la connection avec la base de données.
     * Utiliser setReadable ou setWrittable pour la réouvrir.
     */
    void closeConnection() throws SQLException {
        dbDriver.closeConnection();
    }


    /**
     * Retourne les informations relatives à un type de mesures dans la base de données
     *
     * @param measureInfosId l'identifiant de la mesure en base de données
     * @return La MeasureInformationEntity representant l'entité en base
     * @throws SQLException En cas d'erreur d'accès à la base.
     */
    public MeasureInformationEntity getMeasureInfo(int measureInfosId) throws SQLException {
        return dbDriver.getMeasureInfo(measureInfosId);
    }

    /**
     * Retourne les informations relatives à un type de mesures dans la base de données
     *
     * @param name le nom de l'entité en base
     * @return La MeasureInformationEntity representant l'entité en base
     * @throws SQLException En cas d'erreur d'accès à la base.
     */
    public MeasureInformationEntity getMeasureInfoFromName(String name) throws SQLException {
        return dbDriver.getMeasureInfoFromName(name);
    }


    /**
     * Cette classe sert de factory a MeasureRepository
     */
    public static class MeasureRepositoryBuilder {

        private MeasureRepositoryBuilder() {
            // avoid instantiation
        }

        /**
         * @param config un object de configuration contenant les informations de connexion à la base
         * @return un MeasureRepository utilisable en lecture et en ecriture
         */
        public static MeasureRepository getRepositoryWritable(Configuration config) {
            Objects.requireNonNull(config);
            MeasureRepository db = new MeasureRepository(config);
            db.setWritable();
            return db;
        }

        /**
         * @param config un object de configuration contenant les informations de connexion à la base
         * @return un MeasureRepository utilisable en lecture uniquement
         */
        public static MeasureRepository getRepositoryReadable(Configuration config) {
            Objects.requireNonNull(config);
            MeasureRepository db = new MeasureRepository(config);
            db.setReadable();
            return db;
        }
    }
}
