package fr.onema.lib.tools;

import fr.onema.lib.drone.Dive;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Class permettant de maipuler la configuration de l'application.
 * Utilisation :
 * Configuration exemple = Configuration.build(path/production.properties);
 * exemple.getDatabaseInformation.getHostname() // Permet de récupérer les informations de connexion de la BDD
 */
public class Configuration {
    private static final String DB_HOST = "database.host";
    private static final String DB_PORT = "database.port";
    private static final String DB_BASE = "database.base";
    private static final String DB_USER = "database.user";
    private static final String DB_TOKEN = "database.password";
    private static final String DB_NOTIFY_KEY = "database.notify-key";
    private static final String GEO_SRID = "geo.srid";
    private static final String GEO_MAGNETIC_NORTH_LATITUDE = "geo.magneticnorthlatitude";
    private static final String OFFSET_ACC_X = "offset.acc.x";
    private static final String OFFSET_ACC_Y = "offset.acc.y";
    private static final String OFFSET_ACC_Z = "offset.acc.z";
    private static final String DIVEDATA_PRECISION = "divedata.precision";
    private static final String DIVEDATA_DUREE_MAX = "divedata.dureemax";
    private static final String DIVEDATA_MOUVEMENTS_MAX = "divedata.mouvementsmax";
    private static final String DIVEDATA_DELAI_CAPTEUR_HS = "divedata.delaicapteurhs";
    private static final String DIVEDATA_FREQUENCE_TEST_FLUX_MAVLINK = "divedata.frequencetestmavlink";
    private static final String DIVEDATA_FREQUENCE_TEST_FLUX_DATABASE = "divedata.frequencetestdatabase";
    private static final String DIVEDATA_COEFFICIENT_RANGE_IMU = "divedata.coefficientrangeimu";
    private static Configuration instance;
    private String path;
    private Database database;
    private Geo geo;
    private AccelerationOffset offset;
    private DiveData diveData;

    private Configuration(String path, Properties properties) {
        this.path = path;
        this.database = new Database(
                properties.getProperty(DB_HOST),
                Integer.parseInt(properties.getProperty(DB_PORT)),
                properties.getProperty(DB_BASE),
                properties.getProperty(DB_USER),
                properties.getProperty(DB_TOKEN),
                properties.getProperty(DB_NOTIFY_KEY)
        );
        this.geo = new Geo(Integer.parseInt(properties.getProperty(GEO_SRID)), Double.parseDouble(properties.getProperty(GEO_MAGNETIC_NORTH_LATITUDE)));
        this.offset = new AccelerationOffset(Double.parseDouble(properties.getProperty(OFFSET_ACC_X)),
                Double.parseDouble(properties.getProperty(OFFSET_ACC_Y)),
                Double.parseDouble(properties.getProperty(OFFSET_ACC_Z)));

        this.diveData = new DiveData(Double.parseDouble(properties.getProperty(DIVEDATA_PRECISION)),
                Integer.parseInt(properties.getProperty(DIVEDATA_DUREE_MAX)),
                Integer.parseInt(properties.getProperty(DIVEDATA_MOUVEMENTS_MAX)),
                Integer.parseInt(properties.getProperty(DIVEDATA_DELAI_CAPTEUR_HS)),
                Integer.parseInt(properties.getProperty(DIVEDATA_FREQUENCE_TEST_FLUX_MAVLINK)),
                Integer.parseInt(properties.getProperty(DIVEDATA_FREQUENCE_TEST_FLUX_DATABASE)),
                Double.parseDouble(properties.getProperty(DIVEDATA_COEFFICIENT_RANGE_IMU)));
    }

    /**
     * Builder permettant de créer une représentation des paramètres
     * @param path Chemin d'accès au fichier de configuration
     * @return La représentation du fichier de configuration
     */
    public static Configuration build(String path) {
        Objects.requireNonNull(path, "A non null path is required for the settings");

        try (FileInputStream input = new FileInputStream(path)) {
            Properties properties = new Properties();
            properties.load(input);

            instance = new Configuration(path, properties);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (NullPointerException e) {
            throw new IllegalStateException("All fields are not fields", e);
        }
        return instance;
    }

    /***
     * Getter de la configuration actuelle
     * @return La configuration associée
     */
    public static Configuration getInstance() {
        if (instance == null) {
            build("settings.properties");
        }
        return instance;
    }

    /**
     * Setter de la valeur de correction des axes x, y, z
     * On entre une latitude, une longitude et une altitude. Ces valeurs sont comparées
     * à celles présentes dans notre fichier de configuration. En cas de différence on remplace, à l'intérieur
     * du fichier, l'ancienne valeur par la nouvelle valeur
     * @param accelerationOffsetX Offset de l'accélération sur l'axe x
     * @param accelerationOffsetY Offset de l'accélération sur l'axe y
     * @param accelerationOffsetZ Offset de l'accélération sur l'axe z
     * @throws IOException -Si le fichier n'est pas trouvé
     *                     -Si l'écriture dans ce fichier plante
     *                     -Si on ne peut pas fermer le fichier de sortie(probablement parce qu'il l'est déjà)
     */
    public void setCorrection(double accelerationOffsetX, double accelerationOffsetY, double accelerationOffsetZ) throws IOException {
        if (offset.update(accelerationOffsetX, accelerationOffsetY, accelerationOffsetZ)) {
            update();
        }
    }

    private void update() throws IOException {
        Properties properties = new Properties();
        properties.put(DB_HOST, database.getHostname());
        properties.put(DB_PORT, Integer.toString(database.getPort()));
        properties.put(DB_BASE, database.getBase());
        properties.put(DB_USER, database.getUsername());
        properties.put(DB_TOKEN, database.getPassword());
        properties.put(DB_NOTIFY_KEY, database.getNotifyKey());
        properties.put(GEO_SRID, Integer.toString(geo.getSrid()));
        properties.put(GEO_MAGNETIC_NORTH_LATITUDE, Double.toString(geo.getMagneticNorthLatitude()));
        properties.put(OFFSET_ACC_X, Double.toString(offset.getAccelerationOffsetX()));
        properties.put(OFFSET_ACC_Y, Double.toString(offset.getAccelerationOffsetY()));
        properties.put(OFFSET_ACC_Z, Double.toString(offset.getAccelerationOffsetZ()));
        properties.put(DIVEDATA_PRECISION, Double.toString(diveData.getPrecision()));
        properties.put(DIVEDATA_DUREE_MAX, Integer.toString(diveData.getDureemax()));
        properties.put(DIVEDATA_MOUVEMENTS_MAX, Integer.toString(diveData.getMouvementsmax()));
        properties.put(DIVEDATA_DELAI_CAPTEUR_HS, Integer.toString(diveData.getDelaicapteurhs()));
        properties.put(DIVEDATA_FREQUENCE_TEST_FLUX_MAVLINK, Integer.toString(diveData.getFrequencetestmavlink()));
        properties.put(DIVEDATA_FREQUENCE_TEST_FLUX_DATABASE, Integer.toString(diveData.getFrequencetestdatabase()));
        properties.put(DIVEDATA_COEFFICIENT_RANGE_IMU, Double.toString(diveData.getCoefficientRangeIMU()));
        PrintStream output = new PrintStream(path);
        properties.store(output, null);
    }

    /**
     * Méthode permettant de récupérer la configuration de la base de données.
     * Pour plus de détails se référer à {@link Database}.
     * @return La configuration de la base de données
     */
    public Database getDatabaseInformation() {
        return database;
    }

    /**
     * Méthode permettant de récupérer les informations relatives à la géographie de l'application.
     * Pour plus de détails se référer à {@link Geo}.
     * @return La configuration géographique de l'application
     */
    public Geo getGeo() {
        return geo;
    }

    /**
     * Méthode permettant de récupérer la configuration de l'accélération (pour la correction de position.
     * Pour plus de détails se référer à {@link AccelerationOffset} et {@link Dive}.
     * @return La configuration géographique de l'application
     */
    public AccelerationOffset getOffset() {
        return offset;
    }

    /**
     * Méthode permettant de récupérer la configuration des données de plongée
     * @return la configuration d'une plongée
     */
    public DiveData getDiveData() {
        return diveData;
    }

    /**
     * Classe représentant le décalage de l'accélération sur les axes x, y, z
     */
    public static final class AccelerationOffset {
        private double accelerationOffsetX;
        private double accelerationOffsetY;
        private double accelerationOffsetZ;

        /***
         * Constructeur par défaut
         * @param accelerationOffsetX Offset de la valeur d'accélération en X
         * @param accelerationOffsetY Offset de la valeur d'accélération en Y
         * @param accelerationOffsetZ Offset de la valeur d'accélération en Z
         */
        public AccelerationOffset(double accelerationOffsetX, double accelerationOffsetY, double accelerationOffsetZ) {
            this.accelerationOffsetX = accelerationOffsetX;
            this.accelerationOffsetY = accelerationOffsetY;
            this.accelerationOffsetZ = accelerationOffsetZ;
        }

        boolean update(double accelerationOffsetX, double accelerationOffsetY, double accelerationOffsetZ) {
            boolean edited = false;

            if (Double.compare(accelerationOffsetX, this.accelerationOffsetX) != 0) {
                this.accelerationOffsetX = accelerationOffsetX;
                edited = true;
            }

            if (Double.compare(accelerationOffsetY, this.accelerationOffsetY) != 0) {
                this.accelerationOffsetY = accelerationOffsetY;
                edited = true;
            }

            if (Double.compare(accelerationOffsetZ, this.accelerationOffsetZ) != 0) {
                this.accelerationOffsetZ = accelerationOffsetZ;
                edited = true;
            }

            return edited;
        }

        /***
         * Getter de la valeur d'accélération en X
         * @return La valeur de l'accélération en X
         */
        public double getAccelerationOffsetX() {
            return accelerationOffsetX;
        }

        /***
         * Getter de la valeur d'accélération en Y
         * @return La valeur de l'accélération en Y
         */
        public double getAccelerationOffsetY() {
            return accelerationOffsetY;
        }

        /***
         * Getter de la valeur d'accélération en Z
         * @return La valeur de l'accélération en Z
         */
        public double getAccelerationOffsetZ() {
            return accelerationOffsetZ;
        }
    }

    /**
     * Classe représentant la configuration de la base de données
     */
    public static final class Database {
        private final String hostname;
        private final int port;
        private final String base;
        private final String username;
        private final String password;
        private final String notifyKey;

        Database(String hostname, int port, String base, String username, String password, String notifyKey) {
            this.hostname = hostname;
            this.port = port;
            this.base = base;
            this.username = username;
            this.password = password;
            this.notifyKey = notifyKey;
        }

        /**
         * Méthode permettant d'obtenir le nom d'hôte de la BDD
         * @return Nom d'hôte de la BDD
         */
        public String getHostname() {
            return hostname;
        }

        /**
         * Méthode permettant d'obtenir le port de la BDD
         * @return Le numéro de port de la BDD
         */
        public int getPort() {
            return port;
        }

        /**
         * Méthode permettant d'obtenir le nom de la base relatif à l'application
         * @return Le nom de la base de données
         */
        public String getBase() {
            return base;
        }

        /**
         * Méthode permettant d'obtenir le nom d'utilisateur pour la connexion à la BDD
         * @return Le nom de l'utilisateur
         */
        public String getUsername() {
            return username;
        }

        /**
         * Méthode permettant d'obtenir le mot de passe pour la connexion à la BDD
         * @return Le mot de passe
         */
        public String getPassword() {
            return password;
        }

        /**
         * Méthode permettant d'obtenir la clé de notification pour la BDD
         * @return La clé de notification
         */
        public String getNotifyKey() {
            return notifyKey;
        }
    }

    /**
     * Classe représentant la configuration des données de plongée
     */
    public static final class DiveData {
        private final double precision;
        private final int dureemax;
        private final int mouvementsmax;
        private final int delaicapteurhs;
        private final int frequencetestmavlink;
        private final int frequencetestdatabase;
        private final double coefficientRangeIMU;

        /**
         * Constructeur par défaut
         * @param precision             la précision en mètres
         * @param dureemax              la durée en secondes de la plognée
         * @param mouvementsmax         le nombre max de mouvements avant de perdre trop de précision
         * @param delaicapteurhs        la durée pour considerer un capteur HS en secondes
         * @param frequencetestmavlink  la fréquence de test du flux mavlink
         * @param frequencetestdatabase la fréquence de test du flux mavlink
         */
        public DiveData(double precision, int dureemax, int mouvementsmax, int delaicapteurhs, int frequencetestmavlink, int frequencetestdatabase, double coefficientRangeIMU) {
            this.precision = precision;
            this.dureemax = dureemax;
            this.mouvementsmax = mouvementsmax;
            this.delaicapteurhs = delaicapteurhs;
            this.frequencetestmavlink = frequencetestmavlink;
            this.frequencetestdatabase = frequencetestdatabase;
            this.coefficientRangeIMU = coefficientRangeIMU;
        }

        /**
         * Retourne la précision
         * @return La valeur de la précision en mètres
         */
        public double getPrecision() {
            return precision;
        }

        /**
         * Retourne la durée max conseillée d'une plongée
         * @return La durée max conseillée d'une plongée
         */
        public int getDureemax() {
            return dureemax;
        }

        /**
         * Retourne le nombre de mouvements max conseillés avant de perdre trop de précision
         * @return Le nombre de mouvements max conseillé
         */
        public int getMouvementsmax() {
            return mouvementsmax;
        }

        /**
         * Retourne le délai avant de considerer un capteur HS
         * @return Le délai avant de considérer un capteur HS en secondes
         */
        public int getDelaicapteurhs() {
            return delaicapteurhs;
        }

        /**
         * Retourne la fréquence de test du flux mavlink
         * @return La fréquence de test du flux mavlink
         */
        public int getFrequencetestmavlink() {
            return frequencetestmavlink;
        }

        /**
         * Retourne la fréquence de test du flux database
         * @return La fréquence de test du flux database
         */
        public int getFrequencetestdatabase() {
            return frequencetestdatabase;
        }

        public double getCoefficientRangeIMU() {
            return coefficientRangeIMU;
        }
    }

    /**
     * Classe représentant la configuration des données géographiques
     */
    public final class Geo {
        private final int srid;
        private final double magneticNorthLatitude;

        Geo(int srid, double magneticNorthLatitude) {
            this.srid = srid;
            this.magneticNorthLatitude = magneticNorthLatitude;
        }

        /**
         * Méthode permettant d'obtenir le SRID des données à stocker en base
         * @return Le SRID souhaité
         */
        public int getSrid() {
            return srid;
        }

        /**
         * Permet d'obtenir la latitude du nord magnetique
         * @return La latitude du nord magnetique
         */
        public double getMagneticNorthLatitude() {
            return magneticNorthLatitude;
        }
    }
}
