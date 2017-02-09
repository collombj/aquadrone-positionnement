package fr.onema.lib.database.repository;

import fr.onema.lib.database.DatabaseTools;
import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by Acer on 08/02/2017.
 */
public class TestMeasureRepository {
    Configuration configuration = new Configuration("settingsTest.properties");
    MeasureRepository repository = MeasureRepository.MeasureRepositoryBuilder.getRepositoryWritable(configuration);

    private GPSCoordinate brut = new GPSCoordinate(1, 1, 1);
    private GPSCoordinate correct = new GPSCoordinate(2, 2, 2);

    public TestMeasureRepository() throws SQLException, ClassNotFoundException {
    }

    @Before
    public void setUp() throws Exception {
        DatabaseTools.dropStructure(configuration.getHost(), Integer.parseInt(configuration.getPort()), configuration.getDb(), configuration.getUser(), configuration.getPasswd());
        DatabaseTools.createStructure(configuration.getHost(), Integer.parseInt(configuration.getPort()), configuration.getDb(), configuration.getUser(), configuration.getPasswd());
        DatabaseTools.insertFakeMeasureInformation(configuration.getHost(), Integer.parseInt(configuration.getPort()), configuration.getDb(), configuration.getUser(), configuration.getPasswd());
    }

    @Test
    public void testCreation() throws IOException {
        assertTrue(configuration != null);
        assertTrue(configuration.getHost() != null);
    }


    @Test
    public void testConnection() throws Exception {
        repository.closeConnection();
        repository.setReadable();
        repository.closeConnection();
        repository.setWritable();
    }

    @Test
    public void insertAndRetrieveDive() throws Exception {
        repository.setWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        repository.insertDive(dive);
        DiveEntity dive2 = repository.getLastDive();
        assertTrue(dive.equals(dive2));
        repository.closeConnection();
    }

    @Test
    public void insertAndRetrieveMeasure() throws Exception {
        repository.setWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        repository.insertDive(dive);

        MeasureEntity mesure = new MeasureEntity(System.currentTimeMillis(), brut, correct, 1, 2, 3, 1, 2, 3, 2, "uneMes");
        repository.insertMeasure(mesure, dive.getId(), 1);
        List<MeasureEntity> mesures = repository.getMeasureFrom(dive);
        MeasureEntity mes2 = mesures.get(0);
        if (mes2 != null) {
            assertTrue(mesure.equals(mes2));
        } else {
            throw new Exception("Aucune valeur trouvée en base");
        }
        repository.closeConnection();
    }


    @Test
    public void updatePosition() throws Exception {
        repository.setWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        repository.insertDive(dive);

        MeasureEntity mesure = new MeasureEntity(System.currentTimeMillis(), brut, correct, 1, 2, 3, 1, 2, 3, 2, "uneMes");
        repository.insertMeasure(mesure, dive.getId(), 1);

        repository.updateMeasure(mesure.getId(),new GPSCoordinate(3230,3230,3230),25);
        List<MeasureEntity> mesures = repository.getMeasureFrom(dive);
        if (mesures != null && mesures.size() > 0) {
            MeasureEntity mes2 = mesures.get(0);
            if (mes2 != null) {
                assertTrue(mes2.getLocationCorrected().lon == 3230);
                assertTrue(mes2.getLocationCorrected().lat == 3230);
                assertTrue(mes2.getLocationCorrected().alt == 3230);
            } else {
                throw new Exception("Aucune valeur trouvée en base");
            }
        } else throw new Exception("Liste de valeurs nulle");
        repository.closeConnection();
    }

    @Test
    public void startRecording() throws Exception {
        repository.setWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        repository.insertDive(dive);
        repository.updateStartTime(dive.getId(),timestamp.getTime());
        DiveEntity d2 = repository.getLastDive();
        assertTrue(d2.getStartTime() == timestamp.getTime());
        repository.closeConnection();
    }

    @Test
    public void stopRecording() throws Exception {
        repository.setWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis()+25000);
        repository.insertDive(dive);
        repository.updateEndTime(dive.getId(),timestamp.getTime());
        DiveEntity d2 = repository.getLastDive();
        assertTrue(d2.getEndTime() == timestamp.getTime());
        repository.closeConnection();
    }
}
