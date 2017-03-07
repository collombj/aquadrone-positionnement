package fr.onema.lib.database;

import fr.onema.lib.database.entity.DiveEntity;
import fr.onema.lib.database.entity.MeasureEntity;
import fr.onema.lib.database.entity.MeasureInformationEntity;
import fr.onema.lib.geo.GPSCoordinate;
import fr.onema.lib.tools.Configuration;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * Cette classe permet de gérer la connexion à une base de données et de faire les opérations usuelles sur celle-ci
 */
public class DatabaseDriver {
    private static final String ST_SET_SRID_ST_MAKE_POINT = ",ST_SetSRID(ST_MakePoint(?,?,?),?)";
    private static final String INSERT_MEASURE = "INSERT INTO Measure(" +
            "timestamp," +
            "location_corrected," +
            "location_brut," +
            "accelerationx," +
            "accelerationy," +
            "accelerationz," +
            "precision_cm," +
            "measure_value," +
            "roll," +
            "pitch," +
            "yaw," +
            "dive_id," +
            "measureinformation_id" +
            ") VALUES (" +
            "?" +
            ST_SET_SRID_ST_MAKE_POINT +
            ST_SET_SRID_ST_MAKE_POINT +
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
    private static final String INSERT_DIVE = "INSERT INTO Dive(start_time, end_time) VALUES (?,?) RETURNING ID";
    private static final String SELECT_DIVE = "SELECT * FROM Dive ORDER BY id DESC LIMIT 1";
    private static final String SELECT_MEASURES_FROM_DIVE = "SELECT id, timestamp, ST_X(location_brut) AS brutX," +
            "ST_Y(location_brut) AS brutY, ST_Z(location_brut) AS brutZ, ST_X(location_corrected) AS correctX," +
            "ST_Y(location_corrected) AS correctY, ST_Z(location_corrected) AS correctZ, accelerationX," +
            " accelerationY, accelerationZ, roll, pitch, yaw, precision_cm, measure_value" +
            "  FROM Measure WHERE dive_id=? ORDER BY id";
    private static final String UPDATE_MEASURE = "UPDATE Measure SET location_corrected = " +
            "ST_SetSRID(ST_MakePoint(?, ?, ?), ?), precision_cm = ?  WHERE id = ?";
    private static final String UPDATE_DIVE_START = "UPDATE Dive SET start_time = ? WHERE id = ?";
    private static final String INSERT_MEASURE_WITH_INFOS = "INSERT INTO Measure(" +
            "timestamp," +
            "location_corrected," +
            "location_brut," +
            "accelerationx," +
            "accelerationy," +
            "accelerationz," +
            "precision_cm," +
            "measure_value," +
            "roll," +
            "pitch," +
            "yaw," +
            "dive_id," +
            "measureinformation_id" +
            ") VALUES (" +
            "?" +
            ST_SET_SRID_ST_MAKE_POINT +
            ST_SET_SRID_ST_MAKE_POINT +
            ",?" +
            ",?" +
            ",?" +
            ",?" +
            ",?" +
            ",?" +
            ",?" +
            ",?" +
            ",?" +
            "," +
            "(SELECT id FROM measure_information WHERE name=? ORDER BY id DESC LIMIT 1)" +
            ")" +
            "RETURNING ID";
    private static final String UPDATE_DIVE_STOP = "UPDATE Dive SET end_time = ? WHERE id = ?";
    private static final String SELECT_MEASURE_INFOS_FROM_ID = "SELECT * FROM measure_information WHERE id=?";
    private static final String SELECT_MEASURE_INFOS_FROM_NAME = "SELECT * FROM measure_information WHERE name=? LIMIT 1";
    private final String host;
    private final int port;
    private final String base;
    private final String user;
    private final String password;
    private final int srid;
    private Connection connector;

    private DatabaseDriver(String host, int port, String base, String user, String password, int srid) {
        this.host = Objects.requireNonNull(host);
        this.port = port;
        this.base = Objects.requireNonNull(base);
        this.user = Objects.requireNonNull(user);
        this.password = Objects.requireNonNull(password);
        this.srid = srid;
    }

    /**
     * Constructeur de DatabaseDriver, se base sur un fichier de configuration
     *
     * @param config Le fichier de configuration souhaité
     * @return Un DatabaseDriver
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
     * Initialise un accès à la base de données en lecture
     */
    public void initAsReadable() {
        Properties props = new Properties();
        props.setProperty("readOnly", "true");
        initConnection(props);
    }

    /**
     * Initialise un accès à la base de données en écriture
     */
    public void initAsWritable() {
        Properties props = new Properties();
        props.setProperty("readOnly", "false");
        initConnection(props);
    }

    /**
     * Initialise une connexion à la base de données
     *
     * @param props Le fichier de configuration à utiliser pour la connexion
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
     * Ferme une connexion à la base de données
     *
     * @throws SQLException Dans le cas ou une erreur de connexion est détectée
     */
    public void closeConnection() throws SQLException {
        connector.close();
    }

    /**
     * Permet de récupérer les mesures associées à une plongée
     *
     * @param dive La plongée recherchée
     * @return Un liste comportant toutes les mesures associés à la plongée
     */
    public List<MeasureEntity> getMeasureFrom(DiveEntity dive) throws SQLException {
        List<MeasureEntity> mesures = new LinkedList<>();
        try (PreparedStatement ps = connector.prepareStatement(SELECT_MEASURES_FROM_DIVE)) {
            ps.setInt(1, dive.getId());
            ResultSet results = ps.executeQuery();
            while (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                long timestamp = results.getTimestamp("timestamp").getTime();
                long brutX = (long) (Double.parseDouble(results.getString("brutX")) * 10_000_000.);
                long brutY = (long) (Double.parseDouble(results.getString("brutY")) * 10_000_000.);
                long brutZ = (long) (Double.parseDouble(results.getString("brutZ")) * 1000.);
                long correctX = (long) (Double.parseDouble(results.getString("correctX")) * 10_000_000.);
                long correctY = (long) (Double.parseDouble(results.getString("correctY")) * 10_000_000.);
                long correctZ = (long) (Double.parseDouble(results.getString("correctZ")) * 1000.);
                int accelerationX = Integer.parseInt(results.getString("accelerationX"));
                int accelerationY = Integer.parseInt(results.getString("accelerationY"));
                int accelerationZ = Integer.parseInt(results.getString("accelerationZ"));
                double roll = Double.parseDouble(results.getString("roll"));
                double pitch = Double.parseDouble(results.getString("pitch"));
                double yaw = Double.parseDouble(results.getString("yaw"));
                int precisionCm = Integer.parseInt(results.getString("precision_cm"));
                String measureValue = results.getString("measure_value");
                mesures.add(new MeasureEntity(id, timestamp, new GPSCoordinate(brutY, brutX, brutZ),
                        new GPSCoordinate(correctY, correctX, correctZ), accelerationX, accelerationY, accelerationZ,
                        roll, pitch, yaw, precisionCm, measureValue));
            }
        }
        return mesures;
    }

    /**
     * Récupère la dernière plongée en base
     *
     * @return La dernière plongée en base
     * @throws SQLException Dans le cas ou une erreur de connexion est détectée
     */
    public DiveEntity getLastDive() throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement(SELECT_DIVE)) {
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
     * Insert une nouvelle Dive dans la base de données
     *
     * @param diveEntity L'objet représentant une plongée dans le programme
     * @return L'ID de la nouvelle plongée et -1 en cas d'erreur lors de la récupération de l'ID
     * @throws SQLException Cette exception est levée si un problème de connexion à la base de données est trouvé
     */
    public int insertDive(DiveEntity diveEntity) throws SQLException {
        PreparedStatement insertStatement = null;
        try {
            insertStatement = connector.prepareStatement(INSERT_DIVE);
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
     * Méthode permettant d'insérer des mesures dans la base de données
     *
     * @param measureEntity L'objet entité de représentant une mesure réalisée
     * @param diveID        L'identifiant de la plongée associée
     * @param measureInfoID L'objet MeasureInformationEntity représenant une information de mesure
     * @return L'ID de la nouvelle mesure et -1 en cas d'erreur pour lors de la récupération de l'ID
     * @throws SQLException Cette exception est levée si un problème de connexion à la base de données est trouvé
     */
    public int insertMeasure(MeasureEntity measureEntity, int diveID, int measureInfoID) throws SQLException {
        PreparedStatement insertStatement = null;
        try {
            insertStatement = connector.prepareStatement(INSERT_MEASURE);
            prepareQuery(measureEntity, diveID, insertStatement);
            insertStatement.setInt(19, measureInfoID);
            ResultSet generatedKeys = insertStatement.executeQuery();

            if (generatedKeys.next()) {
                measureEntity.setId(generatedKeys.getInt(1));
                return generatedKeys.getInt(1);
            }
        } finally {
            if (insertStatement != null) {
                insertStatement.close();
            }
        }
        return -1;
    }

    private void prepareQuery(MeasureEntity measureEntity, int diveID, PreparedStatement insertStatement) throws SQLException {
        insertStatement.setTimestamp(1, new Timestamp(measureEntity.getTimestamp()));

        if (measureEntity.getLocationCorrected() == null) {
            insertStatement.setNull(2, Types.BIGINT);
            insertStatement.setNull(3, Types.BIGINT);
            insertStatement.setNull(4, Types.BIGINT);
        } else {
            insertStatement.setDouble(2, (double) measureEntity.getLocationCorrected().lon / 10_000_000.);
            insertStatement.setDouble(3, (double) measureEntity.getLocationCorrected().lat / 10_000_000.);
            insertStatement.setDouble(4, (double) measureEntity.getLocationCorrected().alt / 1000.);
        }
        insertStatement.setInt(5, srid);

        if (measureEntity.getLocationBrute() == null) {
            insertStatement.setNull(6, Types.BIGINT);
            insertStatement.setNull(7, Types.BIGINT);
            insertStatement.setNull(8, Types.BIGINT);
        } else {
            insertStatement.setDouble(6, (double) measureEntity.getLocationBrute().lon / 10_000_000.);
            insertStatement.setDouble(7, (double) measureEntity.getLocationBrute().lat / 10_000_000.);
            insertStatement.setDouble(8, (double) measureEntity.getLocationBrute().alt / 1000.);
        }
        insertStatement.setInt(9, srid);
        insertStatement.setInt(10, measureEntity.getAccelerationX());
        insertStatement.setInt(11, measureEntity.getAccelerationY());
        insertStatement.setInt(12, measureEntity.getAccelerationZ());
        insertStatement.setInt(13, measureEntity.getPrecisionCm());
        insertStatement.setString(14, measureEntity.getMeasureValue());
        insertStatement.setDouble(15, measureEntity.getRoll());
        insertStatement.setDouble(16, measureEntity.getPitch());
        insertStatement.setDouble(17, measureEntity.getYaw());
        insertStatement.setInt(18, diveID);
    }

    public int insertMeasure(MeasureEntity measureEntity, int diveID, String measureInfoName) throws SQLException {
        PreparedStatement insertStatement = null;

        try {
            insertStatement = connector.prepareStatement(INSERT_MEASURE_WITH_INFOS);
            prepareQuery(measureEntity, diveID, insertStatement);
            insertStatement.setString(19, measureInfoName
            );
            ResultSet generatedKeys = insertStatement.executeQuery();

            if (generatedKeys.next()) {
                measureEntity.setId(generatedKeys.getInt(1));
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
        try (PreparedStatement ps = connector.prepareStatement(UPDATE_MEASURE)) {
            ps.setDouble(1, (double) lon / 10_000_000.);
            ps.setDouble(2, (double) lat / 10_000_000.);
            ps.setDouble(3, (double) alt / 1000.);
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
        try (PreparedStatement ps = connector.prepareStatement(UPDATE_DIVE_START)) {
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
        try (Statement ps = connector.createStatement()) {
            ps.execute("NOTIFY " + message);
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
        try (PreparedStatement ps = connector.prepareStatement(UPDATE_DIVE_STOP)) {
            ps.setTimestamp(1, new Timestamp(timestamp));
            ps.setInt(2, diveId);
            ps.execute();
        }
    }


    /**
     * Retourne les informations relatives à un type de mesures dans la base de données
     *
     * @param measureInfoId l'identifiant de la mesure en base de données
     * @return La MeasureInformationEntity representant l'entité en base
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public MeasureInformationEntity getMeasureInfo(int measureInfoId) throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement(
                SELECT_MEASURE_INFOS_FROM_ID)) {
            ps.setInt(1, measureInfoId);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                String type = results.getString("type");
                String display = results.getString("display");
                String unit = results.getString("unit");
                String name = results.getString("name");
                return new MeasureInformationEntity(id, name, unit, type, display);
            }
            return null;
        }
    }

    /**
     * Retourne les informations relatives à un type de mesures dans la base de données
     *
     * @param name le nom de l'entité en base
     * @return La MeasureInformationEntity representant l'entité en base
     * @throws SQLException Dans le cas ou une erreur de connexion est détéctée.
     */
    public MeasureInformationEntity getMeasureInfoFromName(String name) throws SQLException {
        try (PreparedStatement ps = connector.prepareStatement(
                SELECT_MEASURE_INFOS_FROM_NAME)) {
            ps.setString(1, name);
            ResultSet results = ps.executeQuery();
            if (results.next()) {
                int id = Integer.parseInt(results.getString("id"));
                String type = results.getString("type");
                String display = results.getString("display");
                String unit = results.getString("unit");
                return new MeasureInformationEntity(id, name, unit, type, display);
            }
            return null;
        }
    }
}
