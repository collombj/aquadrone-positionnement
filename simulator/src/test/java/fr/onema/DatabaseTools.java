package fr.onema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by Jeremie on 08/02/2017.
 */
public final class DatabaseTools {
    public static void createStructure(String host, int port, String base, String user, String password) throws ClassNotFoundException, SQLException {
        Connection connection = getConnection(host, port, base, user, password);

        connection.prepareStatement("--\n" +
                "-- PostgreSQL database dump\n" +
                "--\n" +
                "SET lock_timeout = 0;\n" +
                "SET client_encoding = 'UTF8';\n" +
                "SET standard_conforming_strings = ON;\n" +
                "SET check_function_bodies = FALSE;\n" +
                "SET client_min_messages = WARNING;\n" +
                "SET search_path = public, pg_catalog;\n" +
                "SET default_tablespace = '';\n" +
                "SET default_with_oids = FALSE;\n" +
                "--\n" +
                "-- TOC entry 196 (class 1259 OID 20742)\n" +
                "-- Name: dive; Type: TABLE; Schema: public; Owner: -; Tablespace:\n" +
                "--\n" +
                "CREATE TABLE dive (\n" +
                "  id         INTEGER NOT NULL,\n" +
                "  start_time TIMESTAMP WITH TIME ZONE,\n" +
                "  end_time   TIMESTAMP WITH TIME ZONE\n" +
                ");\n" +
                "\n" +
                "\n" +
                "--\n" +
                "-- TOC entry 200 (class 1259 OID 20765)\n" +
                "-- Name: measure; Type: TABLE; Schema: public; Owner: -; Tablespace:\n" +
                "--\n" +
                "CREATE TABLE measure (\n" +
                "  id                    INTEGER                  NOT NULL,\n" +
                "  \"timestamp\"           TIMESTAMP WITH TIME ZONE NOT NULL,\n" +
                "  location_corrected    GEOMETRY,\n" +
                "  location_brut         GEOMETRY                 NOT NULL,\n" +
                "  accelerationX  INTEGER                 NOT NULL,\n" +
                "  accelerationY  INTEGER   NOT NULL,\n" +
                "  accelerationZ  INTEGER   NOT NULL,\n" +
                "  precision_cm          INTEGER,\n" +
                "  measure_value         CHARACTER VARYING(255)   NOT NULL,\n" +
                "  roll  DECIMAL   NOT NULL,\n" +
                "  pitch  DECIMAL   NOT NULL,\n" +
                "  yaw  DECIMAL   NOT NULL,\n" +
                "  dive_id               INTEGER                  NOT NULL,\n" +
                "  measureinformation_id INTEGER\n" +
                ");\n" +
                "--\n" +
                "-- TOC entry 198 (class 1259 OID 20752)\n" +
                "-- Name: measure_information; Type: TABLE; Schema: public; Owner: -; Tablespace:\n" +
                "--\n" +
                "CREATE TABLE measure_information (\n" +
                "  id      INTEGER                NOT NULL,\n" +
                "  type    CHARACTER VARYING(255) NOT NULL,\n" +
                "  display CHARACTER VARYING(255) NOT NULL,\n" +
                "  unit    CHARACTER VARYING(255) NOT NULL,\n" +
                "  name    CHARACTER VARYING(255) NOT NULL\n" +
                ");\n" +
                "\n" +
                "CREATE SEQUENCE dive_id_seq\n" +
                "START WITH 1\n" +
                "INCREMENT BY 1\n" +
                "NO MINVALUE\n" +
                "NO MAXVALUE\n" +
                "CACHE 1;\n" +
                "ALTER SEQUENCE dive_id_seq OWNED BY dive.id;\n" +
                "\n" +
                "CREATE SEQUENCE measure_id_seq\n" +
                "START WITH 1\n" +
                "INCREMENT BY 1\n" +
                "NO MINVALUE\n" +
                "NO MAXVALUE\n" +
                "CACHE 1;\n" +
                "ALTER SEQUENCE measure_id_seq OWNED BY measure.id;\n" +
                "\n" +
                "CREATE SEQUENCE measure_information_id_seq\n" +
                "START WITH 1\n" +
                "INCREMENT BY 1\n" +
                "NO MINVALUE\n" +
                "NO MAXVALUE\n" +
                "CACHE 1;\n" +
                "ALTER SEQUENCE measure_information_id_seq OWNED BY measure_information.id;\n" +
                "\n" +
                "ALTER TABLE ONLY dive\n" +
                "  ALTER COLUMN id SET DEFAULT nextval('dive_id_seq' :: REGCLASS);\n" +
                "ALTER TABLE ONLY measure\n" +
                "  ALTER COLUMN id SET DEFAULT nextval('measure_id_seq' :: REGCLASS);\n" +
                "ALTER TABLE ONLY measure_information\n" +
                "  ALTER COLUMN id SET DEFAULT nextval('measure_information_id_seq' :: REGCLASS);\n" +
                "ALTER TABLE ONLY dive\n" +
                "  ADD CONSTRAINT \"dive_PK_ID\" PRIMARY KEY (id);\n" +
                "ALTER TABLE ONLY dive\n" +
                "  ADD CONSTRAINT \"dive_UNIQUE_START_END\" UNIQUE (start_time, end_time);\n" +
                "ALTER TABLE ONLY measure_information\n" +
                "  ADD CONSTRAINT \"measureINFORMATION_PK_ID\" PRIMARY KEY (id);\n" +
                "ALTER TABLE ONLY measure_information\n" +
                "  ADD CONSTRAINT \"measureINFORMATION_UNIQUE_NAME\" UNIQUE (name);\n" +
                "ALTER TABLE ONLY measure\n" +
                "  ADD CONSTRAINT \"measure_PK_ID\" PRIMARY KEY (id);\n" +
                "ALTER TABLE ONLY measure\n" +
                "  ADD CONSTRAINT \"measure_UNIQUE_POSITION_MEASURE_VALUE\" UNIQUE (location_brut, location_corrected, measureinformation_id);\n" +
                "ALTER TABLE ONLY measure\n" +
                "  ADD CONSTRAINT \"measure_FK_DIVE_ID_dive_ID\" FOREIGN KEY (dive_id) REFERENCES dive (id) ON DELETE CASCADE;\n" +
                "ALTER TABLE ONLY measure\n" +
                "  ADD CONSTRAINT \"measure_FK_MEASUREINFORMATION_ID_measureINFORMATION_ID\" FOREIGN KEY (measureinformation_id) REFERENCES measure_information (id) ON DELETE CASCADE;\n" +
                "--\n" +
                "-- View: public.measure_formated\n" +
                "--\n" +
                "CREATE OR REPLACE VIEW public.measure_formated AS\n" +
                "  SELECT\n" +
                "    measure.id                                                                                           AS measure_id,\n" +
                "    dive.id                                                                                              AS dive_id,\n" +
                "    dive.start_time,\n" +
                "    dive.end_time,\n" +
                "    measure.\"timestamp\",\n" +
                "    measure.location_corrected,\n" +
                "    measure.location_brut,\n" +
                "    measure.measure_value,\n" +
                "    regexp_replace(measure_information.display :: TEXT, '\\{0\\}' :: TEXT, measure.measure_value :: TEXT) AS display,\n" +
                "    measure_information.type,\n" +
                "    measure_information.unit,\n" +
                "    measure_information.name\n" +
                "  FROM dive,\n" +
                "    measure,\n" +
                "    measure_information\n" +
                "  WHERE measure.dive_id = dive.id AND measure_information.id = measure.measureinformation_id;\n" +
                "SELECT Populate_Geometry_Columns(('public.measure') :: REGCLASS);\n" +
                "SELECT Populate_Geometry_Columns(('public.measure_formated') :: REGCLASS);\n" +
                "\n" +
                "\n" +
                "--- INSERTS\n" +
                "--INSERT INTO public.dive (start_time, end_time) VALUES (?, ?);\n" +
                "--INSERT INTO public.measure_information (type, display, unit, name) VALUES (?, ?, ?, ?);\n" +
                "--INSERT INTO public.measure (\"timestamp\", location_brut, acceleration, measure_value, dive_id, measureinformation_id)\n" +
                "--VALUES (time, ST_SetSRID(ST_MakePoint(X, Y, Z), 4326), ST_MakePoint(aX, aY, aZ), temp, dive_id, measure_id);\n").execute();
        connection.close();
    }

    public static void clean(String host, int port, String base, String user, String password) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection(host, port, base, user, password);
        connection.prepareStatement("TRUNCATE dive CASCADE; TRUNCATE measure CASCADE; TRUNCATE measure_information CASCADE").execute();
        connection.close();
    }

    public static void dropStructure(String host, int port, String base, String user, String password) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection(host, port, base, user, password);
        connection.prepareStatement("DROP TABLE IF EXISTS dive CASCADE; DROP TABLE IF EXISTS measure CASCADE; DROP TABLE IF EXISTS measure_information CASCADE").execute();
        connection.close();
    }

    public static void insertFakeMeasureInformation(String host, int port, String base, String user, String password) throws SQLException, ClassNotFoundException {
        Connection connection = getConnection(host, port, base, user, password);
        connection.prepareStatement("INSERT INTO measure_information (type, display, unit, name) VALUES " +
                "('Integer', '{0} °C', 'Celsius', 'temperature');").execute();
        connection.close();
    }

    private static Connection getConnection(String host, int port, String base, String user, String password) throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + base;

        return DriverManager.getConnection(url, user, password);
    }
}
