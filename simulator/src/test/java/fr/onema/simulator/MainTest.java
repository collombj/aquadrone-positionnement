package fr.onema.simulator;

import org.apache.commons.cli.*;
import org.junit.Test;

import static fr.onema.simulator.Main.main;

public class MainTest {
    @Test
    public void testMain() throws Exception {
        main(new String[]{});
        main(new String[]{"--help"});
        CommandLineParser parser = new DefaultParser();
        Options options = Main.initOptions();
        CommandLine command = parser.parse(options, new String[]{"gener"});
        Main.action(command, options);
        command = parser.parse(options, new String[]{"compare"});
        Main.action(command, options);
        command = parser.parse(options, new String[]{"run"});
        Main.action(command, options);
    }
}