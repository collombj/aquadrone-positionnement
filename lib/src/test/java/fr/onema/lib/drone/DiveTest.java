package fr.onema.lib.drone;

import fr.onema.lib.database.DatabaseTools;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.DatabaseWorker;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Francois Vanderperre on 14/02/2017.
 */
public class DiveTest {
    private final Configuration configuration;
    private Dive dive;
    private GPSCoordinate previous = new GPSCoordinate(1, 2, 3);
    private GPSCoordinate previous2 = new GPSCoordinate(3, 4, 5);


    public DiveTest() throws FileNotFoundException {
        this.configuration = Configuration.build("settingsTest.properties");
        DatabaseWorker.getInstance().init(configuration);
        DatabaseWorker.getInstance().start();
    }

    @Before
    public void setUp() throws Exception {
        Configuration.Database configuration = this.configuration.getDatabaseInformation();
        DatabaseTools.dropStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.createStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.insertFakeMeasureInformation(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        dive = new Dive(System.currentTimeMillis());
        Thread.sleep(200);
        Position position = new Position(System.currentTimeMillis());
        position.setImu(IMU.build(new CartesianVelocity(0, 0, 0), System.currentTimeMillis(), previous,
                System.currentTimeMillis() + 2000, previous2));
        position.add(Temperature.build(System.currentTimeMillis(), 250));
        position.setGps(GPS.build(System.currentTimeMillis(),4,1,2,250));
        dive.add(position);

        Thread.sleep(200);

        long debut = System.currentTimeMillis();
        long fin = System.currentTimeMillis() + 10000;
        dive.startRecording(debut);

        Position position2 = new Position(System.currentTimeMillis());
        position2.setImu(IMU.build(new CartesianVelocity(0, 0, 0), System.currentTimeMillis(), previous,
                System.currentTimeMillis() + 2000, previous2));
        position2.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position3 = new Position(System.currentTimeMillis());
        position3.setImu(IMU.build(new CartesianVelocity(0, 0, 0), System.currentTimeMillis(), previous,
                System.currentTimeMillis() + 2000, previous2));
        position3.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position4 = new Position(System.currentTimeMillis());
        position4.setImu(IMU.build(new CartesianVelocity(0, 0, 0), System.currentTimeMillis(), previous,
                System.currentTimeMillis() + 2000, previous2));
        position4.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position5 = new Position(System.currentTimeMillis());
        position5.setImu(IMU.build(new CartesianVelocity(0, 0, 0), System.currentTimeMillis(), previous,
                System.currentTimeMillis() + 2000, previous2));
        position5.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position6 = new Position(System.currentTimeMillis());
        position6.setImu(IMU.build(new CartesianVelocity(0, 0, 0), System.currentTimeMillis(), previous,
                System.currentTimeMillis() + 2000, previous2));
        position6.add(Temperature.build(System.currentTimeMillis(), 250));
        position6.setGps(GPS.build(System.currentTimeMillis(),3,5,6,250));

        dive.add(position2);
        dive.add(position3);
        dive.add(position4);
        dive.add(position5);
        dive.stopRecording(fin);
        dive.endDive(position6);
    }


    @Test
    public void simulTraitement() throws InterruptedException {
        assertTrue(true);//ne s'execute que si aucune exception n a été levée avant
    }

    @After
    public void postTraitement() throws InterruptedException {
        Thread.sleep(1000);//s assure que le worker a eu le temps de finir ses traitements
        DatabaseWorker.getInstance().stop();//arrete le worker
    }

}
