package fr.onema.lib.database.repository;

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

    @Test
    public void testCreation() throws IOException {
        System.out.println("testCreation");
        assertTrue(configuration != null);
        assertTrue(configuration.getHost() != null);
    }


    @Test
    public void testConnection() throws Exception {
        System.out.println("testConnection");
        repository.closeConnection();
        repository.setReadable();
        repository.closeConnection();
        repository.setWritable();
    }

    @Test
    public void insertAndRetrieveDive() throws Exception {
        System.out.println("insertAndRetrieveDive");
        repository.setWritable();
        DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 1000);
        repository.insertDive(dive);
        DiveEntity dive2 = repository.getLastDive();
        assertTrue(dive.equals(dive2));
        repository.closeConnection();
    }

    @Test
    public void insertAndRetrieveMeasure() throws Exception {
        System.out.println("insertAndRetrieveMeasure");
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
        System.out.println("updatePosition");
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
        System.out.println("startRecording");
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
        System.out.println("stopRecording");
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
