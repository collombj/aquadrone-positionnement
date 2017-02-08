package fr.onema.lib.database.repository;

import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.SQLException;
import java.util.Objects;

/**
 * Created by Acer on 08/02/2017.
 */
public class DatabaseRepository {
    private DatabaseDriver dbDriver;

    private DatabaseRepository(Configuration config){
        dbDriver = DatabaseDriver.DatabaseDriverFactory.getDatabaseDriver(config);
    }

    private void setWritable(){
        dbDriver.initAsWritable();
    }

    private void setReadable(){
        dbDriver.initAsWritable();
    }
    public static class DatabaseRepositoryBuilder {
        public DatabaseRepository createRepositoryWritable(Configuration config) {
            Objects.requireNonNull(config);
            return new DatabaseRepository(config);
        }
    }

    /**
     * @param dive
     * @return
     */
    public DiveEntity insertDive(DiveEntity dive) throws SQLException {
        int id = dbDriver.insertDive(dive);
        dive.setId(id);
        dbDriver.closeConnection();
        return dive;
    }

    /**
     * @return
     */
    public DiveEntity getLastDive() {
        dbDriver.initAsReadable();
        DiveEntity dive = dbDriver.getLastDive();
        dbDriver.closeConnection();
        return null;
    }

    /**
     * @param diveId
     * @param timestamp
     */
    public void updateStartTime(int diveId, long timestamp) {

    }

    /**
     * @param diveId
     * @param timestamp
     */
    public void updateEndTime(int diveId, long timestamp) {

    }

    /**
     * @param mesure
     * @return
     */
    public MeasureEntity insertMeasure(MeasureEntity mesure) {
        return null;
    }

    /**
     * @param measureId
     * @param positionCorrected
     * @param precisionCm
     */
    public void updateMeasure(int measureId, GPSCoordinate positionCorrected, int precisionCm) {

    }
}
