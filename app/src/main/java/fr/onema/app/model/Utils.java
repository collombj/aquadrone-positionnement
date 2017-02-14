package fr.onema.app.model;

import fr.onema.lib.database.DatabaseDriver;
import fr.onema.lib.tools.Configuration;

import java.util.Objects;

/**
 * Created by you on 14/02/2017.
 */
public class Utils {
  public static boolean checkPostgresAvailability(Configuration c) {
      Objects.requireNonNull(c);
        try {
            DatabaseDriver dd = DatabaseDriver.build(c);
            dd.initAsReadable();
            dd.closeConnection();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static boolean checkMavlinkAvailability() {
        // TODO : implement availability with MessageWorker
        return true;
    }
}
