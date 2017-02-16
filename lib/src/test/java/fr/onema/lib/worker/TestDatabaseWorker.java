package fr.onema.lib.worker;

import fr.onema.lib.database.DatabaseTools;
import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.repository.MeasureRepository;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Acer on 10/02/2017.
 */
public class TestDatabaseWorker {
    private final Configuration configuration;
    private final DatabaseWorker dbWorker;
    private final MeasureRepository repository;
    private DiveEntity dive = new DiveEntity(System.currentTimeMillis(), System.currentTimeMillis() + 2000);
    private long start = System.currentTimeMillis() + 5631;
    private long end = System.currentTimeMillis() + 15211;
    private GPSCoordinate brut = new GPSCoordinate(1, 1, 1);
    private GPSCoordinate correct = new GPSCoordinate(2, 2, 2);
    private MeasureEntity entity = new MeasureEntity(start, brut, brut, 0, 0, 0, 0, 0, 0, 0, "QQ");

    public TestDatabaseWorker() throws Exception {
        this.configuration = Configuration.build("settingsTest.properties");

        DatabaseWorker.getInstance().init(configuration);
        dbWorker = DatabaseWorker.getInstance();
        repository = MeasureRepository.MeasureRepositoryBuilder.getRepositoryReadable(configuration);
    }

    @Before
    public void setUp() throws Exception {
        Configuration.Database configuration = this.configuration.getDatabaseInformation();
        DatabaseTools.dropStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.createStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.insertFakeMeasureInformation(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        dbWorker.start();
        dbWorker.newDive(dive);
        Thread.sleep(1000);
        dbWorker.insertMeasure(entity, dive.getId(), "temperature");
        Thread.sleep(1000);
        dbWorker.updatePosition(entity.getId(), correct, 0);
        dbWorker.startRecording(start, dive.getId());
        dbWorker.stopRecording(end, dive.getId());
        dbWorker.sendNotification("notification");
    }

    @Test
    public void simulTraitement() throws Exception {
        Thread.sleep(1500);
        DiveEntity dive2 = repository.getLastDive();
        assertFalse(dive.equals(dive2));
        assertTrue(dive.getId() == dive2.getId());
        assertTrue(dive2.getStartTime() == start);
        assertTrue(dive2.getEndTime() == end);
        MeasureEntity entity2 = repository.getMeasureFrom(dive).get(0);
        assertFalse(entity.equals(entity2));
        assertTrue(entity.getId() == entity2.getId());
        assertTrue(entity2.getLocationCorrected().equals(correct));
    }

    @After
    public void afterEffect() throws Exception {
        dbWorker.stop();
    }
}
