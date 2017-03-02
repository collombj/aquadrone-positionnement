package fr.onema.lib.file.manager;

import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by loics on 01/03/2017.
 */
public class ResultsOutputTest {

    private final static String resultsFile = System.getProperty("user.dir") + "/src/test/java/fr/onema/lib/file/resultsOutput.csv";
    private final static ResultsOutput RESULTS_OUTPUT_BUG = new ResultsOutput("");

    @AfterClass
    public static void delete() {
        File ref = new File(resultsFile);
        ref.delete();
    }

    @Test(expected = IOException.class)
    public void testException5() throws IOException {
        File ref = new File("notapath");
        ref.delete();
        RESULTS_OUTPUT_BUG.openFileForResults();
    }

    @Test(expected = IOException.class)
    public void testException6() throws IOException {
        ReferenceEntry re = new ReferenceEntry(0, 4, 5, 6, (float) 7, 8);
        MeasureEntity m = new MeasureEntity(
                0, new GPSCoordinate(4, 5, 6), new GPSCoordinate(1, 2, 3), 0, 0, 0, 0, 0, 0, 13, "test");
        RESULTS_OUTPUT_BUG.appendResults(re, m, 14);
    }

    @Test
    public void appendResults() throws Exception {
        ResultsOutput fm = new ResultsOutput(resultsFile);
        fm.openFileForResults();
        ReferenceEntry re = new ReferenceEntry(0, 4, 5, 6, (float) 7, 8);
        MeasureEntity m = new MeasureEntity(
                0, new GPSCoordinate(4, 5, 6), new GPSCoordinate(4, 5, 6), 0, 0, 0, 0, 0, 0, 13, "test");
        fm.appendResults(re, m, 14);
        fm.appendResults(re, m, 0);
    }

    @Test
    public void getResults() throws Exception {
        appendResults(); // TODO
        FileManager fm = new ResultsOutput(resultsFile);
        List<String> results = fm.getResults("||");
        assertEquals("timestamp||corrected.latitude||corrected.longitude||corrected.altitude||brut.latitude||brut.longitude||brut.altitude||ref.latitude||ref.longitude||ref.altitude||ref.direction||ref.temperature||difference.x||difference.y||difference.z||difference.absolute||precision||margin||margin.error", results.get(0));
//        assertEquals("0||4||5||6||5||6||4||5||6||7.0||8||0.0||0.0||0.0||0.0||13||14.0||false", results.get(1)); FIXME
    }

}