package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertTrue;



public class TestDatabaseDriver {
    Configuration configuration = new Configuration("settingsTest.properties");
    DatabaseDriver dbDriver = DatabaseDriver.DatabaseDriverFactory.getDatabaseDriver(configuration);

    private GPSCoordinate brut = new GPSCoordinate(1, 1, 1);
    private GPSCoordinate correct = new GPSCoordinate(2, 2, 2);

    @Test
    public void testCreation() throws IOException {
        assertTrue(configuration != null);
        configuration.setCorrection(1, 1, 1);
        assertTrue(configuration.getHost() != null);
    }


    @Test
    public void testConnection() throws Exception {
        dbDriver.initAsReadable();
        dbDriver.closeConnection();
        dbDriver.initAsWritable();
        dbDriver.closeConnection();
    }

    @Test
    public void insertAndRetrieveDive() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        dbDriver.initAsWritable();
        dbDriver.insertDive(dive);
        DiveEntity dive2 = dbDriver.getLastDive();
        assertTrue(dive.equals(dive2));
        dbDriver.closeConnection();
    }

    @Test(expected = SQLException.class)
    public void testCannotWrite() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        dbDriver.initAsReadable();
        dbDriver.insertDive(dive);
        DiveEntity dive2 = dbDriver.getLastDive();
        assertTrue(dive.equals(dive2));
        dbDriver.closeConnection();
    }

    @Test
    public void insertAndRetrieveMeasure() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        dbDriver.initAsWritable();
        int idDive = dbDriver.insertDive(dive);

        MeasureEntity mesure = new MeasureEntity(System.currentTimeMillis(), brut, correct, 1, 2, 3, 1, 2, 3, 2, "uneMes");
        dbDriver.insertMeasure(mesure, idDive, 1);
        List<MeasureEntity> mesures = dbDriver.getMeasureFrom(dive);
        MeasureEntity mes2 = mesures.get(0);
        if (mes2 != null) {
            assertTrue(mesure.equals(mes2));
        } else {
            throw new Exception("Aucune valeur trouvée en base");
        }
        dbDriver.closeConnection();
    }


    @Test
    public void updatePosition() throws Exception {

        dbDriver.initAsWritable();
        dbDriver.updatePosition(9, 350, 350, 350, 2);
        DiveEntity dive = new DiveEntity(75, System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        List<MeasureEntity> mesures = dbDriver.getMeasureFrom(dive);
        if (mesures != null && mesures.size() > 0) {
            MeasureEntity mes2 = mesures.get(0);
            if (mes2 != null) {
                assertTrue(mes2.getLocationCorrected().lon == 350);
                assertTrue(mes2.getLocationCorrected().lat == 350);
                assertTrue(mes2.getLocationCorrected().alt == 350);
            } else {
                throw new Exception("Aucune valeur trouvée en base");
            }
        } else throw new Exception("Liste de valeurs nulle");
        dbDriver.closeConnection();
    }

    @Test
    public void startRecording() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        dbDriver.initAsWritable();
        int diveID = dbDriver.insertDive(dive);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dbDriver.startRecording(timestamp.getTime(), diveID);
        DiveEntity d2 = dbDriver.getLastDive();
        assertTrue(d2.getStartTime() == timestamp.getTime());
        dbDriver.closeConnection();
    }

    @Test
    public void stopRecording() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        dbDriver.initAsWritable();
        int diveID = dbDriver.insertDive(dive);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        dbDriver.stopRecording(timestamp.getTime(), diveID);
        DiveEntity d2 = dbDriver.getLastDive();
        assertTrue(d2.getEndTime() == timestamp.getTime());
        dbDriver.closeConnection();
    }


}
