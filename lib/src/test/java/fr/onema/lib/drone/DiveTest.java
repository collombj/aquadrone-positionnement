package fr.onema.lib.drone;

import fr.onema.lib.database.DatabaseTools;
import fr.onema.lib.geo.CartesianVelocity;
import fr.onema.lib.sensor.Temperature;
import fr.onema.lib.sensor.position.GPS;
import fr.onema.lib.sensor.position.IMU.IMU;
import fr.onema.lib.tools.Configuration;
import fr.onema.lib.worker.DatabaseWorker;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

/**
 * Created by Francois Vanderperre on 14/02/2017.
 */
public class DiveTest {
    private final Configuration configuration = Configuration.getInstance();
    //private GPSCoordinate previous = new GPSCoordinate(1, 2, 3);
    //private GPSCoordinate previous2 = new GPSCoordinate(3, 4, 5);


    public DiveTest() throws FileNotFoundException {

    }

    @Before
    public void setUp() throws Exception {
        Dive dive;
        DatabaseWorker.getInstance().start();
        Configuration.Database configuration = this.configuration.getDatabaseInformation();
        DatabaseTools.dropStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.createStructure(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        DatabaseTools.insertFakeMeasureInformation(configuration.getHostname(), configuration.getPort(), configuration.getBase(), configuration.getUsername(), configuration.getPassword());
        dive = new Dive();
        Thread.sleep(200);
        Position position = new Position(System.currentTimeMillis());
        position.setImu(IMU.build(new CartesianVelocity(0, 0, 0), new CartesianVelocity(3, 0, 0), System.currentTimeMillis(),
                System.currentTimeMillis() + 2000));
        position.add(Temperature.build(System.currentTimeMillis(), 250));
        position.setGps(GPS.build(System.currentTimeMillis(), 4, 1, 2, 250));
        dive.add(position);

        Thread.sleep(200);

        long debut = System.currentTimeMillis();
        long fin = System.currentTimeMillis() + 10000;
        dive.startRecording(debut);

        Position position2 = new Position(System.currentTimeMillis() + 1000);
        position2.setImu(IMU.build(new CartesianVelocity(0, 2, 0), new CartesianVelocity(3, 0, 0), System.currentTimeMillis(),
                System.currentTimeMillis() + 2000));
        position2.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position3 = new Position(System.currentTimeMillis() + 2000);
        position3.setImu(IMU.build(new CartesianVelocity(3, 0, 0), new CartesianVelocity(3, 3, 3), System.currentTimeMillis(),
                System.currentTimeMillis() + 2000));
        position3.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position4 = new Position(System.currentTimeMillis() + 3000);
        position4.setImu(IMU.build(new CartesianVelocity(3, 3, 3), new CartesianVelocity(3, 2, 1), System.currentTimeMillis(),
                System.currentTimeMillis() + 2000));
        position4.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position5 = new Position(System.currentTimeMillis() + 4000);
        position5.setImu(IMU.build(new CartesianVelocity(3, 2, 1), new CartesianVelocity(5, 4, 2), System.currentTimeMillis(),
                System.currentTimeMillis() + 2000));
        position5.add(Temperature.build(System.currentTimeMillis(), 250));

        Position position6 = new Position(System.currentTimeMillis() + 5000);
        position6.setImu(IMU.build(new CartesianVelocity(5, 4, 2), new CartesianVelocity(2, 5, 2), System.currentTimeMillis(),
                System.currentTimeMillis() + 2000));
        position6.add(Temperature.build(System.currentTimeMillis(), 250));
        position6.setGps(GPS.build(System.currentTimeMillis(), 3, 5, 6, 250));

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

}
