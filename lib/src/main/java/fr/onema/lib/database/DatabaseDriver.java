package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Cette classe permet de gérer la connexion à une base de données et de faire les opérations usuelles sur celle ci
 *
 * @author francois & loic
 * @since 08-02-2017
 */
public class DatabaseDriver {
    private final String host;
    private final int port;
    private final String base;
    private final String user;
    private final String password;
    private final int srid;
    private Connection connector;

    private DatabaseDriver(String host, int port, String base, String user, String password, int srid) {
        Objects.requireNonNull(host);
        Objects.requireNonNull(port);
        Objects.requireNonNull(base);
        Objects.requireNonNull(user);
        Objects.requireNonNull(password);
        Objects.requireNonNull(srid);

        this.host = host;
        this.port = port;
        this.base = base;
        this.user = user;
        this.password = password;
        this.srid = srid;
    }

    /**
     * Constructeur de DatabaseDriver, se base sur un fichier de configuration.
     *
     * @param config Le fichier de configuration souhaité.
     * @return Un DatabaseDriver.
     */
    public static DatabaseDriver build(Configuration config) {
        Objects.requireNonNull(config);
        return new DatabaseDriver(
                config.getDatabaseInformation().getHostname(),
                config.getDatabaseInformation().getPort(),
                config.getDatabaseInformation().getBase(),
                config.getDatabaseInformation().getUsername(),
                config.getDatabaseInformation().getPassword(),
                config.getGeo().getSrid());
    }

    /**
     * Initialise un accès à la base de données en lecture.
     */
    public void initAsReadable() {
        Properties props = new Properties();
        props.setProperty("readOnly", "true");
        initConnection(props);
    }

    /**
     * Initialise un accès à la base de données en écriture.
     */
    public void initAsWritable() {
        Properties props = new Properties();
        props.setProperty("readOnly", "false");
        initConnection(props);
    }

    /**
     * Initialise une connexion à la base de données.
     *
     * @param props Le fichier de configuration à utiliser pour la connexion.
     */
    private void initConnection(Properties props) {
        try {
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + base;
            props.setProperty("user", user);
            props.setProperty("password", password);
            connector = DriverManager.getConnection(url, props);
        } catch (ClassNotFoundException | SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Ferme une connexion à la base de données.
     *
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public void closeConnection() throws SQLException {
        connector.close();
    }

    /**
     * Permet de récupérer les mesures associés à une plongée.
     *
     * @param dive La plongée recherché.
     * @return Un liste comportant toutes les mesures associés à la plongée.
     */
    public List<MeasureEntity> getMeasureFrom(DiveEntity dive) throws SQLException {
        List<MeasureEntity> mesures = new LinkedList<>();
        try (PreparedStatement ps = connector.prepareStatement("SELECT id, timestamp, ST_X(location_brut) AS brutX," +
                "ST_Y(location_brut) AS brutY, ST_Z(location_brut) AS brutZ, ST_X(location_corrected) AS correctX," +
                "ST_Y(location_corrected) AS correctY, ST_Z(location_corrected) AS correctZ, accelerationX," +
                " accelerationY, accelerationZ, rotationX, rotationY, rotationZ, precision_cm, measure_value" +
                "  FROM Measure WHERE dive_id=? ORDER BY id")) {
            ps.setInt(1, dive.getId());
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                long timestamp = results.getTimestamp("timestamp").getTime();
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
        }
        return mesures;
    }

    /**
     * Récupère la dernière plongée en base.
     *
     * @return La dernière plongée en base.
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public DiveEntity getLastDive() throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement("SELECT * FROM Dive ORDER BY id DESC LIMIT 1")) {
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                long start = results.getTimestamp("start_time").getTime();
                long end = results.getTimestamp("end_time").getTime();
                return new DiveEntity(id, start, end);
            }
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
        String insertString = "INSERT INTO Dive(start_time, end_time) VALUES (?,?) RETURNING ID";

        try {
            insertStatement = connector.prepareStatement(insertString);

            insertStatement.setTimestamp(1, new Timestamp(diveEntity.getStartTime()));
            insertStatement.setTimestamp(2, new Timestamp(diveEntity.getEndTime()));

            ResultSet generatedKeys = insertStatement.executeQuery();

            if (generatedKeys.next()) {
                diveEntity.setId(generatedKeys.getInt(1));
                return generatedKeys.getInt(1);
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
     * @param measureInfoID L'objet MeasureInformationEntity représenant une information de mesure.
     * @return L'ID de la nouvelle mesure et -1 en cas d'erreur pour lors de la récupération de l'ID.
     * @throws SQLException Cette exception est levée si un problème de connexion à la base de données est trouvé.
     */
    public int insertMeasure(MeasureEntity measureEntity, int diveID, int measureInfoID) throws SQLException {
        PreparedStatement insertStatement = null;
        String insertString = "INSERT INTO Measure(" +
                "timestamp," +
                "location_corrected," +
                "location_brut," +
                "accelerationx," +
                "accelerationy," +
                "accelerationz," +
                "precision_cm," +
                "measure_value," +
                "rotationx," +
                "rotationy," +
                "rotationz," +
                "dive_id," +
                "measureinformation_id" +
                ") VALUES (" +
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
                ",?)" +
                "RETURNING ID";

        try {
            insertStatement = connector.prepareStatement(insertString);

            // Timestamp
            insertStatement.setTimestamp(1, new Timestamp(measureEntity.getTimestamp()));

            // location_corrected
            insertStatement.setLong(2, measureEntity.getLocationCorrected().lon);
            insertStatement.setLong(3, measureEntity.getLocationCorrected().lat);
            insertStatement.setLong(4, measureEntity.getLocationCorrected().alt);
            insertStatement.setInt(5, srid);

            // location_brut
            insertStatement.setLong(6, measureEntity.getLocationBrut().lon);
            insertStatement.setLong(7, measureEntity.getLocationBrut().lat);
            insertStatement.setLong(8, measureEntity.getLocationBrut().alt);
            insertStatement.setInt(9, srid);

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

            ResultSet generatedKeys = insertStatement.executeQuery();


            if (generatedKeys.next()) {
                measureEntity.setId(generatedKeys.getInt(1));
                return generatedKeys.getInt(1);
            }

        } finally

        {
            if (insertStatement != null) {
                insertStatement.close();
            }
        }
        return -1;
    }

    /**
     * Mets à jour la position d'une mesure.
     *
     * @param measureId L'ID de la mesure.
     * @param lat       La nouvelle latitude de la mesure.
     * @param lon       La nouvelle longitude de la mesure.
     * @param alt       La nouvelle altitude de la mesure.
     * @param precision La nouvelle valeur de précision de la mesure.
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public void updatePosition(int measureId, long lat, long lon, long alt, int precision) throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement("UPDATE Measure SET location_corrected = " +
                "ST_SetSRID(ST_MakePoint(?, ?, ?), ?), precision_cm = ?  WHERE id = ?")) {
            ps.setLong(1, lon);
            ps.setLong(2, lat);
            ps.setLong(3, alt);
            ps.setInt(4, srid);
            ps.setInt(5, precision);
            ps.setInt(6, measureId);
            ps.execute();

        }
    }

    /**
     * Démarre un enregistrement en remplissant la valeur de début de plongée en base.
     *
     * @param timestamp Le timestamp correspondant au début de la plongée.
     * @param diveId    L'ID de la plongée.
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public void startRecording(long timestamp, int diveId) throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement("UPDATE Dive SET start_time = ? WHERE id = ?")) {
            ps.setTimestamp(1, new Timestamp(timestamp));
            ps.setInt(2, diveId);
            ps.execute();
        }
    }

    /**
     * Envoie une notification.
     *
     * @param message Le message à envoyer via la notification.
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public void sendNotification(String message) throws SQLException {
        Objects.requireNonNull(message);
        try (PreparedStatement ps = connector.prepareStatement("NOTIFY ?;")) {
            ps.setString(1, message);
            ps.execute();
        }
    }

    /**
     * Arrête un enregistrement en remplissant la valeur de fin de plongée en base.
     *
     * @param timestamp Le timestamp correspondant à la fin de la plongée.
     * @param diveId    L'ID de la plongée.
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public void stopRecording(long timestamp, int diveId) throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement("UPDATE Dive SET end_time = ? WHERE id = ?")) {
            ps.setTimestamp(1, new Timestamp(timestamp));
            ps.setInt(2, diveId);
            ps.execute();
        }
    }
}
