package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Francois Vanderperre on 07/02/2017.
 * <p>
 * Cette classe permet de gérer la connexion à une base de données et de faire les opérations usuelles sur celle ci
 */
public class DatabaseDriver {
    private String host;
    private String port;
    private String base;
    private String user;
    private String password;
    private String srid;
    private Connection connector;

    private DatabaseDriver(String host, String port, String base, String user, String password, String srid) {
        this.host = host;
        this.port = port;
        this.base = base;
        this.user = user;
        this.password = password;
        this.srid = srid;
    }

    public void initAsReadable() {
        Properties props = new Properties();
        props.setProperty("readOnly", "true");
        initConnection(props);
    }

    public void initAsWritable() {
        Properties props = new Properties();
        props.setProperty("readOnly", "false");
        initConnection(props);
    }

    private void initConnection(Properties props) {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + base;
            props.setProperty("user", user);
            props.setProperty("password", password);
            connector = DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException e) {
            System.out.println("Postgresql driver not found");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Failed to connect to database");
            e.printStackTrace();
        }
    }

    public void closeConnection() {
        try {
            connector.close();
        } catch (SQLException e) {
            System.out.println("Failed to close connection to database");
            e.printStackTrace();
        }
    }

    public List<MeasureEntity> getMeasureFrom(DiveEntity dive) { //TODO surcharger
        List<MeasureEntity> mesures = new LinkedList<>();
        try (PreparedStatement ps = connector.prepareStatement("SELECT id, timestamp, ST_X(location_brut) as brutX," +
                "ST_Y(location_brut) as brutY, ST_Z(location_brut) as brutZ, ST_X(location_corrected) as correctX," +
                "ST_Y(location_corrected) as correctY, ST_Z(location_corrected) as correctZ, accelerationX," +
                " accelerationY, accelerationZ, rotationX, rotationY, rotationZ, precision_cm, measure_value" +
                "  FROM Measure WHERE #dive_id=? ORDER BY id")) {
            ps.setInt(1, dive.getId());
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                long timestamp = Long.parseLong(results.getString("timestamp"));
                long brutX = Long.parseLong(results.getString("brutX"));
                long brutY = Long.parseLong(results.getString("brutY"));
                long brutZ = Long.parseLong(results.getString("brutZ"));
                long correctX = Long.parseLong(results.getString("correctX"));
                long correctY = Long.parseLong(results.getString("correctY"));
                long correctZ = Long.parseLong(results.getString("correctZ"));

                int accelerationX = Integer.parseInt(results.getString("accelerationX"));
                int accelerationY = Integer.parseInt(results.getString("accelerationY"));
                int accelerationZ = Integer.parseInt(results.getString("accelerationZ"));
                int rotationX = Integer.parseInt(results.getString("rotationX"));
                int rotationY = Integer.parseInt(results.getString("rotationY"));
                int rotationZ = Integer.parseInt(results.getString("rotationZ"));

                int precisionCm = Integer.parseInt(results.getString("precision_cm"));
                String measureValue = results.getString("measure_value");
                mesures.add(new MeasureEntity(id, timestamp, new GPSCoordinate(brutX, brutY, brutZ),
                        new GPSCoordinate(correctX, correctY, correctZ), accelerationX, accelerationY, accelerationZ,
                        rotationX, rotationY, rotationZ, precisionCm, measureValue));

            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve the last dive");
            e.printStackTrace();
        }
        return mesures;
    }

    public DiveEntity getLastDive() {
        try (PreparedStatement ps = connector.prepareStatement("SELECT * FROM Dive ORDER BY id DESC LIMIT 1")) {
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                long start = Long.parseLong(results.getString("start_time"));
                long end = Long.parseLong(results.getString("end_time"));
                return new DiveEntity(id, start, end);
            }
        } catch (SQLException e) {
            System.out.println("Failed to retrieve the last dive");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insert une nouvelle Dive dans la base de données.
     *
     * @param diveEntity L'objet représentant une plongée dans le programme.
     * @return L'ID de la nouvelle plongée et -1 en cas d'erreur lors de la récupération de l'ID.
     * @throws SQLException Cette exception est levée si un problème de connexion à la base de données est trouvé.
     */
    public int insertDive(DiveEntity diveEntity) throws SQLException {
        PreparedStatement insertStatement = null;
        String insertString = "INSERT INTO Dive VALUES (?,?)";

        try {
            insertStatement = connector.prepareStatement(insertString);

            insertStatement.setLong(1, diveEntity.getStartTime());
            insertStatement.setLong(2, diveEntity.getEndTime());

            insertStatement.execute();

            // Get the generated key from the previous insert.
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } finally {
            if (insertStatement != null) {
                insertStatement.close();
            }
        }
        return -1;
    }

    /**
     * Méthode permettant d'insérer des mesures dans la base de données.
     *
     * @param measureEntity L'objet entité de représentant une mesure réalisée.
     * @param diveID        L'identifiant de la plongée associée.
     * @param measureInfoID L'identifiant du type de la mesure réalisée.
     * @return L'ID de la nouvelle mesure et -1 en cas d'erreur pour lors de la récupération de l'ID.
     * @throws SQLException Cette exception est levée si un problème de connexion à la base de données est trouvé.
     */
    public int insertMeasure(MeasureEntity measureEntity, int diveID, int measureInfoID) throws SQLException {
        PreparedStatement insertStatement = null;
        String insertString = "INSERT INTO Measure VALUES (" +
                "?" +
                ",ST_SetSRID(ST_MakePoint(?,?,?),?)" +
                ",ST_SetSRID(ST_MakePoint(?,?,?),?)" +
                ",?" +
                ",?" +
                ",?" +
                ",?" +
                ",?" +
                ",?" +
                ",?" +
                ",?" +
                ",?" +
                ",?)";

        try {
            insertStatement = connector.prepareStatement(insertString);

            // Timestamp
            insertStatement.setLong(1, measureEntity.getTimestamp());

            // location_corrected
            insertStatement.setLong(2, measureEntity.getLocationCorrected().lon);
            insertStatement.setLong(3, measureEntity.getLocationCorrected().lat);
            insertStatement.setLong(4, measureEntity.getLocationCorrected().alt);
            insertStatement.setString(5, srid);

            // location_brut
            insertStatement.setLong(6, measureEntity.getLocationBrut().lon);
            insertStatement.setLong(7, measureEntity.getLocationBrut().lat);
            insertStatement.setLong(8, measureEntity.getLocationBrut().alt);
            insertStatement.setString(9, srid);

            //acceleration XYZ
            insertStatement.setInt(10, measureEntity.getAccelerationX());
            insertStatement.setInt(11, measureEntity.getAccelerationY());
            insertStatement.setInt(12, measureEntity.getAccelerationZ());

            //precision_cm
            insertStatement.setInt(13, measureEntity.getPrecisionCm());

            // measure_value
            insertStatement.setString(14, measureEntity.getMeasureValue());

            // rotationXYZ
            insertStatement.setInt(15, measureEntity.getRotationX());
            insertStatement.setInt(16, measureEntity.getRotationY());
            insertStatement.setInt(17, measureEntity.getRotationZ());

            // dive_id
            insertStatement.setInt(18, diveID);

            // measure_information_id
            insertStatement.setInt(19, measureInfoID);

            insertStatement.execute();

            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }

        } finally {
            if (insertStatement != null) {
                insertStatement.close();
            }
        }
        return -1;
    }

    public void updatePosition(int measureId, long lat, long lon, long alt, int precision) {
        try (PreparedStatement ps = connector.prepareStatement("UPDATE Measure SET location_corrected = " +
                "ST_SetSRID(ST_MakePoint(?, ?, ?), ?), precision_cm = ?  WHERE id = ?")) {
            ps.setLong(1, lon);
            ps.setLong(2, lat);
            ps.setLong(3, alt);
            ps.setString(4, srid);
            ps.setInt(5, precision);
            ps.setInt(6, measureId);
            ps.execute();

        } catch (SQLException e) {
            System.out.println("Failed correcting mesure n°" + measureId);
            e.printStackTrace();
        }
    }

    public void startRecording(long timestamp, int diveId) {
        try (PreparedStatement ps = connector.prepareStatement("UPDATE Dive SET start_time = ? WHERE id = ?")) {
            ps.setLong(1, timestamp);
            ps.setLong(2, diveId);
            ps.execute();
        } catch (SQLException e) {
            System.out.println("Failed updating dive n°" + diveId);
            e.printStackTrace();
        }
    }

    public void stopRecording(long timestamp, int diveId) {
        try (PreparedStatement ps = connector.prepareStatement("UPDATE Dive SET end_time = ? WHERE id = ?")) {
            ps.setLong(1, timestamp);
            ps.setLong(2, diveId);
            ps.execute();
        } catch (SQLException e) {
            System.out.println("Failed updating dive n°" + diveId);
            e.printStackTrace();
        }
    }

    public static class DatabaseDriverFactory {

        public static DatabaseDriver getDatabaseDriver(Configuration config) {
            return new DatabaseDriver(
                    config.getHost(),
                    config.getPort(),
                    config.getDb(),
                    config.getUser(),
                    config.getPasswd(),
                    config.getSrid());
        }
    }



}
