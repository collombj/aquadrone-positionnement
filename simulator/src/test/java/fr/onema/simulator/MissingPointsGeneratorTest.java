package fr.onema.simulator;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.Assert.fail;

/**
 * Created by Jérôme on 15/02/2017.
 */
public class MissingPointsGeneratorTest {
    private static String refContent = "timestamp,longitude,latitude,altitude,temperature\n" +
            "645451445,2.2834504,48.9019386,-1.02365,-0.3\n" +
            "1556484644,2.2835147,48.9019549,-1.13598,-0,7\n" +
            "2464874168,2.2835798,48.901976,-1.09863,-0.5\n" +
            "3344641686,2.2836515,48.9019791,-1.00135,-0.2\n" +
            "4416818686,2.2837119,48.9019566,-0.95468,0.0\n" +
            "5466418146,2.2837783,48.9019412,-0.89364,0.3\n" +
            "6418146816,2.2838514,48.9019483,-0.68729,0.9\n" +
            "7616668464,2.283913,48.9019491,-0.39658,1.3\n" +
            "8314841665,2.2839781,48.9019491,-0.19963,2.0\n" +
            "9686416861,2.2839761,48.9019064,0,3.1";
    private static File referencePath;
    private static File resultPath;

    @BeforeClass
    public static void init() throws Exception {
        referencePath = initFile("reference", refContent);
        resultPath = initFile("result", "");
    }

    private static File initFile(String fileName, String content) {
        try {
            File path = File.createTempFile(fileName, "csv");
            path.deleteOnExit();
            PrintWriter writer = new PrintWriter(path.getCanonicalFile(), "UTF-8");
            writer.println(content);
            writer.close();
            return path;
        } catch (IOException e) {
            fail();
        }
        return null;
    }

    @Test
    public void testBuildAndGenerateWithFile() throws Exception {
        MissingPointsGenerator generator = MissingPointsGenerator.build(referencePath);
        generator.generateOutput();
    }

    @Test
    public void testBuildAndGenerateWithPath() throws Exception {
        MissingPointsGenerator generator = MissingPointsGenerator.build(referencePath.getPath());
        generator.generateOutput(resultPath.getPath());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildCorrectlyButGenerateWithEmptyPath() throws Exception {
        MissingPointsGenerator generator = MissingPointsGenerator.build(referencePath.getPath());
        generator.generateOutput("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuildWithNeitherPathNorFile() throws Exception {
        MissingPointsGenerator generator = MissingPointsGenerator.build("");
    }

    @Test(expected = NullPointerException.class)
    public void testGenerateWithoutBuild() throws Exception {
        MissingPointsGenerator generator = null;
        generator.generateOutput();
    }
}
