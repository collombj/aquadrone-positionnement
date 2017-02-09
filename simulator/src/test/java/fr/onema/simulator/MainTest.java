package fr.onema.simulator;

import org.junit.Test;

import static fr.onema.simulator.Main.main;

public class MainTest {
    @Test
    public void testMain() throws Exception {
        main(new String[]{});
        main(new String[]{"--help"});
    }

}