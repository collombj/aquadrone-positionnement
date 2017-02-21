package fr.onema.lib.tools;

import fr.onema.lib.drone.Dive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Properties;

/**
 * Class permettant de maipuler la configuration de l'application.
 * <p>
 * Utilisation :
 * Configuration exemple = Configuration.build(path/production.properties);
 * exemple.getDatabaseInformation.getHostname() // Permet de récupérer les informations de connexion de la BDD
 */
public class Configuration {
    private static Configuration INSTANCE;
    private static final String DB_HOST = "database.host";
    private static final String DB_PORT = "database.port";
    private static final String DB_BASE = "database.base";
    private static final String DB_USER = "database.user";
    private static final String DB_TOKEN = "database.password";
    private static final String DB_NOTIFY_KEY = "database.notify-key";
    private static final String GEO_SRID = "geo.srid";
    private static final String FLOW_LAT = "flow.lat";
    private static final String FLOW_LON = "flow.lon";
    private static final String FLOW_ALT = "flow.alt";
    private static final String DIVEDATA_PRECISION = "divedata.precision";
    private static final String DIVEDATA_DUREE_MAX = "divedata.dureemax";
    private static final String DIVEDATA_MOUVEMENTS_MAX = "divedata.mouvementsmax";
    private static final String DIVEDATA_DELAI_CAPTEUR_HS = "divedata.delaicapteurhs";
    private static final String DIVEDATA_FREQUENCE_TEST_FLUX_MAVLINK = "divedata.frequencetestmavlink";
    private static final String DIVEDATA_FREQUENCE_TEST_FLUX_DATABASE = "divedata.frequencetestdatabase";

    private String path;
    private Database database;
    private Geo geo;
    private Flow flow;
    private DiveData diveData;

    private Configuration(String path, Properties properties) throws FileNotFoundException {
        this.path = path;
        this.database = new Database(
                properties.getProperty(DB_HOST),
                Integer.parseInt(properties.getProperty(DB_PORT)),
                properties.getProperty(DB_BASE),
                properties.getProperty(DB_USER),
                properties.getProperty(DB_TOKEN),
                properties.getProperty(DB_NOTIFY_KEY)
        );
        this.geo = new Geo(Integer.parseInt(properties.getProperty(GEO_SRID)));
        this.flow = new Flow(
                Double.parseDouble(properties.getProperty(FLOW_LAT)),
                Double.parseDouble(properties.getProperty(FLOW_LON)),
                Double.parseDouble(properties.getProperty(FLOW_ALT))
        );

        this.diveData = new DiveData(Double.parseDouble(properties.getProperty(DIVEDATA_PRECISION)),
                Integer.parseInt(properties.getProperty(DIVEDATA_DUREE_MAX)),
                Integer.parseInt(properties.getProperty(DIVEDATA_MOUVEMENTS_MAX)),
                Integer.parseInt(properties.getProperty(DIVEDATA_DELAI_CAPTEUR_HS)),
                Integer.parseInt(properties.getProperty(DIVEDATA_FREQUENCE_TEST_FLUX_MAVLINK)),
                Integer.parseInt(properties.getProperty(DIVEDATA_FREQUENCE_TEST_FLUX_DATABASE)));
    }

    /**
     * Builder permettant de créer une représentation des paramètres
     *
     * @param path Chemin d'accès au fichier de configuration
     * @return La représentation du fichier de configuration
     * @throws FileNotFoundException En cas d'absence de fichier de configuration
     */
    private static void build(String path) throws FileNotFoundException {
        Objects.requireNonNull(path, "A non null path is required for the settings");

        try (FileInputStream input = new FileInputStream(path)) {
            Properties properties = new Properties();
            properties.load(input);

            INSTANCE = new Configuration(path, properties);
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }


    public static Configuration getInstance() {//TODO faire crasher l appli si erreur de config
        if (INSTANCE == null) {
            try {
                build("settings.properties");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return INSTANCE;

    }

    /**
     * On entre une latitude, une longitude et une altitude. Ces valeurs sont comparées
     * à celles présentes dans notre fichier de configuration. En cas de différence on remplace, à l'intérieur
     * du fichier, l'ancienne valeur par la nouvelle valeur
     *
     * @param x Latitude
     * @param y Longitude
     * @param z Altitude
     * @throws IOException -Si le fichier n'est pas trouvé
     *                     -Si l'écriture dans ce fichier plante
     *                     -Si on ne peut pas fermer le fichier de sortie(probablement parce qu'il l'est déjà)
     */
    public void setCorrection(double x, double y, double z) throws IOException {
        if (flow.update(x, y, z)) {
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
        properties.put(FLOW_LAT, Double.toString(flow.getLat()));
        properties.put(FLOW_LON, Double.toString(flow.getLon()));
        properties.put(FLOW_ALT, Double.toString(flow.getAlt()));
        properties.put(DIVEDATA_PRECISION, Double.toString(diveData.getPrecision()));
        properties.put(DIVEDATA_DUREE_MAX, Integer.toString(diveData.getDureemax()));
        properties.put(DIVEDATA_MOUVEMENTS_MAX, Integer.toString(diveData.getMouvementsmax()));
        properties.put(DIVEDATA_DELAI_CAPTEUR_HS, Integer.toString(diveData.getDelaicapteurhs()));
        properties.put(DIVEDATA_FREQUENCE_TEST_FLUX_MAVLINK, Integer.toString(diveData.getFrequencetestmavlink()));
        properties.put(DIVEDATA_FREQUENCE_TEST_FLUX_DATABASE, Integer.toString(diveData.getFrequencetestdatabase()));
        PrintStream output = new PrintStream(path);
        properties.store(output, null);
    }

    /**
     * Méthode permettant de récupérer la configuration de la base de données.
     * Pour plus de détails se référer à {@link Database}.
     *
     * @return La configuration de la base de données
     */
    public Database getDatabaseInformation() {
        return database;
    }

    /**
     * Méthode permettant de récupérer les informations relatives à la géographie de l'application.
     * Pour plus de détails se référer à {@link Geo}.
     *
     * @return La configuration géographique de l'application
     */
    public Geo getGeo() {
        return geo;
    }

    /**
     * Méthode permettant de récupérer la configuration des courrants (pour la correction de position.
     * Pour plus de détails se référer à {@link Flow} et {@link Dive}.
     *
     * @return La configuration géographique de l'application
     */
    public Flow getFlow() {
        return flow;
    }

    /**
     * Méthode permettant de récupérer la configuration des données de plongée
     *
     * @return la configuration d'une plongée
     */
    public DiveData getDiveData() {
        return diveData;
    }

    /**
     * Class représentant le courant d'eau
     */
    public static final class Flow {
        private double lat;
        private double lon;
        private double alt;

        Flow(double lat, double lon, double alt) {
            this.lat = lat;
            this.lon = lon;
            this.alt = alt;
        }

        /**
         * Méthode permettant d'obtenir le courant présent sur l'axe latitudinale
         *
         * @return Le courant en latitude
         */
        public double getLat() {
            return lat;
        }

        /**
         * Méthode permettant d'obtenir le courant présent sur l'axe longitudinal
         *
         * @return Le courant en longitude
         */
        public double getLon() {
            return lon;
        }

        /**
         * Méthode permettant d'obtenir le courant présent sur l'axe de la profondeur
         *
         * @return Le courant en profondeur
         */
        public double getAlt() {
            return alt;
        }

        boolean update(double lat, double lon, double alt) {
            boolean edited = false;

            if (lat != this.lat) {
                this.lat = lat;
                edited = true;
            }

            if (lon != this.lon) {
                this.lon = lon;
                edited = true;
            }

            if (alt != this.alt) {
                this.alt = alt;
                edited = true;
            }

            return edited;
        }
    }

    /**
     * Class représentant la configuration de la base de données
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
         *
         * @return Nom d'hôte de la BDD
         */
        public String getHostname() {
            return hostname;
        }

        /**
         * Méthode permettant d'obtenir le port de la BDD
         *
         * @return Port de la BDD
         */
        public int getPort() {
            return port;
        }

        /**
         * Méthode permettant d'obtenir le nom de la base relatif à l'application
         *
         * @return Le nom de la base
         */
        public String getBase() {
            return base;
        }

        /**
         * Méthode permettant d'obtenir le nom d'utilisateur pour la connexion à la BDD
         *
         * @return Le nom d'utilisateur
         */
        public String getUsername() {
            return username;
        }

        /**
         * Méthode permettant d'obtenir le mot de passe pour la connexion à la BDD
         *
         * @return Le mot de passe
         */
        public String getPassword() {
            return password;
        }

        /**
         * Méthode permettant d'obtenir la clé de notification pour la BDD
         *
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

        /**
         * Le constructeur de la classe
         *
         * @param precision             la précision en mètres
         * @param dureemax              la durée en secondes de la plognée
         * @param mouvementsmax         le nombre max de mouvements avant de perdre trop de précision
         * @param delaicapteurhs        la durée pour considerer un capteur HS en secondes
         * @param frequencetestmavlink  la fréquence de test du flux mavlink
         * @param frequencetestdatabase la fréquence de test du flux mavlink
         */
        public DiveData(double precision, int dureemax, int mouvementsmax, int delaicapteurhs, int frequencetestmavlink, int frequencetestdatabase) {
            this.precision = precision;
            this.dureemax = dureemax;
            this.mouvementsmax = mouvementsmax;
            this.delaicapteurhs = delaicapteurhs;
            this.frequencetestmavlink = frequencetestmavlink;
            this.frequencetestdatabase = frequencetestdatabase;
        }

        /**
         * Retourne la précision
         *
         * @return la précision en mètres
         */
        public double getPrecision() {
            return precision;
        }

        /**
         * Retourne la durée max conseillée d'une plongée
         *
         * @return la durée max conseillée d'une plongée
         */
        public int getDureemax() {
            return dureemax;
        }

        /**
         * Retourne le nombre de mouvements max conseillés avant de perdre trop de précision
         *
         * @return le nombre de mouvements max conseillé
         */
        public int getMouvementsmax() {
            return mouvementsmax;
        }

        /**
         * Retourne le délai avant de considerer un capteur HS
         *
         * @return le délai avant de considérer un capteur HS en secondes
         */
        public int getDelaicapteurhs() {
            return delaicapteurhs;
        }

        /**
         * Retourne la fréquence de test du flux mavlink
         *
         * @return la fréquence de test du flux mavlink
         */
        public int getFrequencetestmavlink() {
            return frequencetestmavlink;
        }

        /**
         * Retourne la fréquence de test du flux database
         *
         * @return la fréquence de test du flux database
         */
        public int getFrequencetestdatabase() {
            return frequencetestdatabase;
        }
    }

    /**
     * Class représentant la configuration des données géographiques
     */
    public final class Geo {
        private final int srid;

        Geo(int srid) {
            this.srid = srid;
        }

        /**
         * Méthode permettant d'obtenir le SRID des données à stocker en base
         *
         * @return Le SRID souhaité
         */
        public int getSrid() {
            return srid;
        }
    }
}
