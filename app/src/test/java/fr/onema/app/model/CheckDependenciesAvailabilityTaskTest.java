package fr.onema.app.model;

import fr.onema.app.Main;
import org.junit.Test;

import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

/**
 * Created by you on 16/02/2017.
 */
// TODO : don't delete me cause build operation will crash
public class CheckDependenciesAvailabilityTaskTest {
    @Test
    public void testMethod() {
        Main m = new Main();
        try {
            m.initWorker();
            assertTrue(CheckDependenciesAvailabilityTask.checkPostgresAvailability(m.getConfiguration()));
        } catch (FileNotFoundException e) {
            // ignore
        }
    }
}