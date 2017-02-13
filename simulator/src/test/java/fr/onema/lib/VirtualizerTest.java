package fr.onema.lib;

import fr.onema.lib.file.FileManager;
import fr.onema.lib.tools.Configuration;
import fr.onema.simulator.Virtualizer;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by Jérôme on 09/02/2017.
 */
public class VirtualizerTest {
    @Test(expected = NullPointerException.class)
    public void testNoFileManager(){
        Virtualizer v = new Virtualizer(null,100,"aaa","test",5432);
    }

    @Test(expected = NullPointerException.class)
    public void testNoSimulationName(){
        FileManager fm = new FileManager("rawInput.csv","virtualizedOutput.csv","computedOutput.csv");
        Virtualizer v = new Virtualizer(fm,100,null,"test",5432);
    }

    @Test(expected = NullPointerException.class)
    public void testNoHost(){
        FileManager fm = new FileManager("rawInput.csv","virtualizedOutput.csv","computedOutput.csv");
        Virtualizer v = new Virtualizer(fm,100,"aaa",null,5432);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoSpeed(){
        FileManager fm = new FileManager("rawInput.csv","virtualizedOutput.csv","computedOutput.csv");
        Virtualizer v = new Virtualizer(fm,0,"aaa","test",5432);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNoPort(){
        FileManager fm = new FileManager("rawInput.csv","virtualizedOutput.csv","computedOutput.csv");
        Virtualizer v = new Virtualizer(fm,100,"aaa","test",0);
    }

    /*Les fichiers ne sont pas versionnés, donc test juste en local
    @Test
    public void testGetters(){
        String workingDir = System.getProperty("user.dir").replace("simulator", "lib");
        FileManager fm = new FileManager(workingDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        Virtualizer v = new Virtualizer(fm,100,"aaa","test",5432);
        try{
            v.start();
            assertEquals(v.getFileManager(), fm);
            assertEquals(v.getSimulationName(),"aaa");
            assertEquals(v.getSpeed(),100);
            assertEquals(v.getHost(),"test");
            assertEquals(v.getPort(),5432);
            assertNotNull(Virtualizer.getLogs());
        }catch(Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    public void testCompare(){
        String workingSourceDir = System.getProperty("user.dir").replace("simulator", "lib");
        String workingErrorDir = System.getProperty("user.dir");
        FileManager sfm = new FileManager(workingSourceDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingSourceDir + "/src/test/java/fr/onema/lib/File/virtualizedOutput.csv", workingSourceDir + "/src/test/java/fr/onema/lib/File/computedOutput.csv");
        FileManager efm = new FileManager(workingSourceDir + "/src/test/java/fr/onema/lib/File/rawInput.csv", workingErrorDir + "/src/test/java/fr/onema/lib/virtualizedErrorOutput.csv", workingErrorDir + "/src/test/java/fr/onema/lib/computedErrorOutput.csv");
        Virtualizer v = new Virtualizer(sfm,100,"aaa","test",5432);
        try{
            Configuration config = Configuration.build(workingSourceDir + "/settingsTest.properties");
            v.start();
            assertNotEquals(v.getDuration(),0);
            assertNotNull(v.compare(sfm, efm, config,1));
        }catch(Exception e){
            fail(e.getMessage());
        }
    }*/
}
