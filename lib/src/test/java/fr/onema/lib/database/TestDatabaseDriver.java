package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Acer on 07/02/2017.
 */


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
        DiveEntity dive = new DiveEntity(1, 120, 250);
        dbDriver.initAsWritable();
        dbDriver.insertDive(dive);
        DiveEntity dive2 = dbDriver.getLastDive();
        assertTrue(dive.equals(dive2));
        dbDriver.closeConnection();
    }

    @Test(expected = SQLException.class)
    public void testCannotWrite() throws Exception {
        DiveEntity dive = new DiveEntity(1, 120, 250);
        dbDriver.initAsReadable();
        dbDriver.insertDive(dive);
        DiveEntity dive2 = dbDriver.getLastDive();
        assertTrue(dive.equals(dive2));
        dbDriver.closeConnection();
    }

    @Test
    public void insertAndRetrieveMeasure() throws Exception {
        DiveEntity dive = new DiveEntity(2, 120, 250);
        dbDriver.initAsWritable();
        dbDriver.insertDive(dive);

        MeasureEntity mesure = new MeasureEntity(1, 230, brut, correct, 1, 2, 3, 1, 2, 3, 2, "uneMes");
        dbDriver.insertMeasure(mesure, 2, 0);
        List<MeasureEntity> mesures = dbDriver.getMeasureFrom(dive);
        MeasureEntity mes2 = mesures.get(0);
        if (mes2 != null) {
            assertTrue(mesure.equals(mes2));
        } else {
            throw new Exception("Aucune valeur trouvée en base");
        }
    }


    @Test
    public void getLastDive() throws Exception {
        DiveEntity dive = new DiveEntity(5, 120, 250);
        DiveEntity dive2 = new DiveEntity(6, 135, 500);
        DiveEntity dive3 = new DiveEntity(4, 430, 250);
        DiveEntity dive4 = new DiveEntity(3, 170, 750);
        dbDriver.initAsWritable();
        dbDriver.insertDive(dive2);
        dbDriver.insertDive(dive3);
        dbDriver.insertDive(dive4);

        DiveEntity dive5 = dbDriver.getLastDive();
        assertTrue(dive2.equals(dive5));
    }


    @Test
    public void updatePosition() throws Exception {

        dbDriver.initAsWritable();
        dbDriver.updatePosition(1, 350, 350, 350, 2);
        DiveEntity dive = new DiveEntity(2, 120, 250);
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

    }

    @Test
    public void startRecording() throws Exception {
        DiveEntity dive = new DiveEntity(8, 1, 2);
        dbDriver.initAsWritable();
        dbDriver.insertDive(dive);

        dbDriver.startRecording(1500, 8);
        DiveEntity d2 = dbDriver.getLastDive();
        assertTrue(d2.getStartTime() == 1500);
    }

    @Test
    public void stopRecording() throws Exception {
        dbDriver.initAsWritable();
        dbDriver.stopRecording(1500, 8);
        DiveEntity d2 = dbDriver.getLastDive();
        assertTrue(d2.getEndTime() == 1500);

    }


}
