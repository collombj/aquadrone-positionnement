package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.entity.MeasureInformationEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertTrue;


public class TestDatabaseDriver {
    private final Configuration configuration = Configuration.getInstance();
    private final DatabaseDriver driver;

    private GPSCoordinate brut = new GPSCoordinate(1000000, 1000000, 1000000);
    private GPSCoordinate correct = new GPSCoordinate(2000000, 2000000, 2000);

    public TestDatabaseDriver() throws FileNotFoundException {
        driver = DatabaseDriver.build(configuration);
    }

    @Before
    public void setUp() throws Exception {
        Configuration.Database configuration = this.configuration.getDatabaseInformation();

        DatabaseTools.dropStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.createStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.insertFakeMeasureInformation(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
    }

    @Test
    public void testConnection() throws Exception {
        driver.initAsReadable();
        driver.closeConnection();
        driver.initAsWritable();
        driver.closeConnection();
    }

    @Test
    public void insertAndRetrieveDive() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        driver.initAsWritable();
        driver.insertDive(dive);
        DiveEntity dive2 = driver.getLastDive();
        assertTrue(dive.equals(dive2));
        driver.closeConnection();
    }

    @Test(expected = SQLException.class)
    public void testCannotWrite() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        driver.initAsReadable();
        driver.insertDive(dive);
        DiveEntity dive2 = driver.getLastDive();
        assertTrue(dive.equals(dive2));
        driver.closeConnection();
    }

    @Test
    public void insertAndRetrieveMeasure() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        driver.initAsWritable();
        int idDive = driver.insertDive(dive);

        MeasureEntity mesure = new MeasureEntity(System.currentTimeMillis(), brut, correct, 1, 2, 3, 1, 2, 3, 2, "uneMes");
        driver.insertMeasure(mesure, idDive, 1);
        List<MeasureEntity> mesures = driver.getMeasureFrom(dive);
        MeasureEntity mes2 = mesures.get(0);
        if (mes2 != null) {
            assertTrue(mesure.equals(mes2));
        } else {
            throw new Exception("Aucune valeur trouvée en base");
        }
        driver.closeConnection();
    }


    @Test
    public void updatePosition() throws Exception {
        driver.initAsWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        int diveID = driver.insertDive(dive);
        MeasureEntity mesure = new MeasureEntity(System.currentTimeMillis(), brut, correct, 1, 2, 3, 1, 2, 3, 2, "uneMes");
        driver.insertMeasure(mesure, diveID, 1);


        driver.updatePosition(mesure.getId(), 350000000, 350000000, 350000, 2);
        List<MeasureEntity> mesures = driver.getMeasureFrom(dive);
        if (mesures != null && mesures.size() > 0) {
            MeasureEntity mes2 = mesures.get(0);
            if (mes2 != null) {
                assertTrue(mes2.getLocationCorrected().lon == 350000000);
                assertTrue(mes2.getLocationCorrected().lat == 350000000);
                assertTrue(mes2.getLocationCorrected().alt == 350000);
            } else {
                throw new Exception("Aucune valeur trouvée en base");
            }
        } else throw new Exception("Liste de valeurs nulle");
        driver.closeConnection();
    }

    @Test
    public void startRecording() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        driver.initAsWritable();
        int diveID = driver.insertDive(dive);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        driver.startRecording(timestamp.getTime(), diveID);
        DiveEntity d2 = driver.getLastDive();
        assertTrue(d2.getStartTime() == timestamp.getTime());
        driver.closeConnection();
    }

    @Test
    public void stopRecording() throws Exception {
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        driver.initAsWritable();
        int diveID = driver.insertDive(dive);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        driver.stopRecording(timestamp.getTime(), diveID);
        DiveEntity d2 = driver.getLastDive();
        assertTrue(d2.getEndTime() == timestamp.getTime());
        driver.closeConnection();
    }

    @Test
    public void getMeasureInfo() throws Exception {
        driver.initAsWritable();
        MeasureInformationEntity measure = driver.getMeasureInfo(1);
        MeasureInformationEntity measure1 = driver.getMeasureInfoFromName("temperature");
        assertTrue(measure.equals(measure1));
        driver.closeConnection();
    }


}
