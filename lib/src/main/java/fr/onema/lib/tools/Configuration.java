package fr.onema.lib.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;

/**
 * Created by Jérôme on 06/02/2017.
 */
public class Configuration {
    private static String path;
    private String host;
    private String port;
    private String db;
    private String user;
    private String passwd;
    private String notifyKey;
    private String srid;


    /**
     * Constructeur sans paramètre de Configuration
     */
    public Configuration(){
        path = "settings.properties";
    }

    /**
     * Constructeur avec un paramètre pour choisir le fichier de configuration à utiliser
     * @param settingPath Le chemin du fichier de configuration
     */
    public Configuration(String settingPath){
        if(settingPath == null){
            throw new IllegalArgumentException("No path was specified");
        }else {
            path = settingPath;
        }
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
    public void setCorrection(int x, int y, int z) throws IOException {
        OrderedProperties properties = new OrderedProperties();
        PrintStream output = null;
        try (FileInputStream input = new FileInputStream(path)) {
            properties.load(input);
            input.close();
            host = properties.getProperty("db.host");
            port = properties.getProperty("db.port");
            db = properties.getProperty("db.name");
            user = properties.getProperty("db.user");
            passwd = properties.getProperty("db.passwd");
            notifyKey = properties.getProperty("db.notify-key");
            srid = properties.getProperty("geo.srid");
            if (!properties.getProperty("flow.lat").equals(String.valueOf(x))) {
                properties.replace("flow.lat", String.valueOf(x));
            }
            if (!properties.getProperty("flow.lon").equals(String.valueOf(y))) {
                properties.replace("flow.lon", String.valueOf(y));
            }
            if (!properties.getProperty("flow.alt").equals(String.valueOf(z))) {
                properties.replace("flow.alt", String.valueOf(z));
            }
            output = new PrintStream(path);
            properties.store(output, null);
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }

    /**
     * On récupère le chemin vers le fichier
     *
     * @return path
     */
    public String getPath() {
        return path;
    }

    /**
     * On récupère l'adresse IP de la base
     *
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * On récupère le port sur lequel se connecter à la base
     *
     * @return port
     */
    public String getPort() {
        return port;
    }

    /**
     * On récupère le 'user'  qui permet de se connecter à la base
     *
     * @return user
     */
    public String getUser() {
        return user;
    }

    /**
     * On récupère le 'passwd'  qui permet de se connecter à la base
     *
     * @return passwd
     */
    public String getPasswd() {
        return passwd;
    }

    /**
     * On récupère la clé qui permet de notifier QGIS en cas de mise à jour dans la base
     *
     * @return notifyKey
     */
    public String getNotifyKey() {
        return notifyKey;
    }

    /**
     * On récupère le SRID(système de projection) utilisé dans l'application.
     *
     * @return Le SRID de l'application
     */
    public String getSrid() {
        return srid;
    }

    /**
     * On récupère le nom de la base de données.
     *
     * @return Le nom de la base de données.
     */
    public String getDb() {
        return db;
    }

    /**
     * Classe implémentant Properties, pour conserver l'ordre des propriétés
     * d'un fichier .properties
     */
    public class OrderedProperties extends Properties {
        private final LinkedHashSet<Object> keyOrder = new LinkedHashSet<>();

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(keyOrder);
        }

        @Override
        public synchronized Object put(Object key, Object value) {
            keyOrder.add(key);
            return super.put(key, value);
        }
    }
}
