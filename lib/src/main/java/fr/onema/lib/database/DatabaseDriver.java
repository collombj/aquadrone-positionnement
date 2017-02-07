package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.drone.Measure;

import java.sql.Connection;
import java.util.Date;
import java.util.stream.Stream;

/**
 * Created by Francois Vanderperre on 07/02/2017.
 * <p>
 * Cette classe permet de gérer la connexion à une base de données et de faire les opérations usuelles sur celle ci
 */
public class DatabaseDriver {
    private String host;
    private int port;
    private String base;
    private String user;
    private String password;
    private Connection connector;

    private DatabaseDriver(String host, int port, String base, String user, String password) {
        this.host = host;
        this.port = port;
        this.base = base;
        this.user = user;
        this.password = password;
    }

    public class DatabaseDriverFactory {
        public DatabaseDriver getDatabaseDriver() { //TODO doit recevoir une configuration en argument
            return null;
        }
    }

    public void initAsReadable() {//TODO

    }

    public void initAsWritable() {//TODO

    }

    public Stream<Measure> getMeasureFrom(DiveEntity dive) {//TODO
        return null;
    }

    public DiveEntity getLastDive() {//TODO
        return null;
    }

    public int insertDive(Date date) {//TODO
        return 0;
    }

    public int insertMeasure() {//TODO
        return 0;
    }

    public void updatePosition() {//TODO changer les arguments

    }

    public void startRecording(long timestamp, int diveId) {

    }

    public void stopRecording(long timestamp, int diveId) {
        
    }

}
