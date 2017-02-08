--
-- PostgreSQL database dump
--
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = ON;
SET check_function_bodies = FALSE;
SET client_min_messages = WARNING;
SET search_path = public, pg_catalog;
SET default_tablespace = '';
SET default_with_oids = FALSE;
--
-- TOC entry 196 (class 1259 OID 20742)
-- Name: dive; Type: TABLE; Schema: public; Owner: -; Tablespace:
--
CREATE TABLE dive (
  id         INTEGER NOT NULL,
  start_time TIMESTAMP WITH TIME ZONE,
  end_time   TIMESTAMP WITH TIME ZONE
);

--
-- TOC entry 200 (class 1259 OID 20765)
-- Name: measure; Type: TABLE; Schema: public; Owner: -; Tablespace:
--
CREATE TABLE measure (
  id                    INTEGER                  NOT NULL,
  "timestamp"           TIMESTAMP WITH TIME ZONE NOT NULL,
  location_corrected    GEOMETRY,
  location_brut         GEOMETRY                 NOT NULL,
  accelerationX         INTEGER                  NOT NULL,
  accelerationY         INTEGER                  NOT NULL,
  accelerationZ         INTEGER                  NOT NULL,
  precision_cm          INTEGER,
  measure_value         CHARACTER VARYING(255)   NOT NULL,
  rotationX             INTEGER                  NOT NULL,
  rotationY             INTEGER                  NOT NULL,
  rotationZ             INTEGER                  NOT NULL,
  dive_id               INTEGER                  NOT NULL,
  measureinformation_id INTEGER
);
--
-- TOC entry 198 (class 1259 OID 20752)
-- Name: measure_information; Type: TABLE; Schema: public; Owner: -; Tablespace:
--
CREATE TABLE measure_information (
  id      INTEGER                NOT NULL,
  type    CHARACTER VARYING(255) NOT NULL,
  display CHARACTER VARYING(255) NOT NULL,
  unit    CHARACTER VARYING(255) NOT NULL,
  name    CHARACTER VARYING(255) NOT NULL
);

CREATE SEQUENCE dive_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;
ALTER SEQUENCE dive_id_seq OWNED BY dive.id;

CREATE SEQUENCE measure_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;
ALTER SEQUENCE measure_id_seq OWNED BY measure.id;

CREATE SEQUENCE measure_information_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;
ALTER SEQUENCE measure_information_id_seq OWNED BY measure_information.id;

ALTER TABLE ONLY dive
  ALTER COLUMN id SET DEFAULT nextval('dive_id_seq' :: REGCLASS);
ALTER TABLE ONLY measure
  ALTER COLUMN id SET DEFAULT nextval('measure_id_seq' :: REGCLASS);
ALTER TABLE ONLY measure_information
  ALTER COLUMN id SET DEFAULT nextval('measure_information_id_seq' :: REGCLASS);
ALTER TABLE ONLY dive
  ADD CONSTRAINT "dive_PK_ID" PRIMARY KEY (id);
ALTER TABLE ONLY dive
  ADD CONSTRAINT "dive_UNIQUE_START_END" UNIQUE (start_time, end_time);
ALTER TABLE ONLY measure_information
  ADD CONSTRAINT "measureINFORMATION_PK_ID" PRIMARY KEY (id);
ALTER TABLE ONLY measure_information
  ADD CONSTRAINT "measureINFORMATION_UNIQUE_NAME" UNIQUE (name);
ALTER TABLE ONLY measure
  ADD CONSTRAINT "measure_PK_ID" PRIMARY KEY (id);
ALTER TABLE ONLY measure
  ADD CONSTRAINT "measure_UNIQUE_POSITION_MEASURE_VALUE" UNIQUE (location_brut, location_corrected, measureinformation_id);
ALTER TABLE ONLY measure
  ADD CONSTRAINT "measure_FK_DIVE_ID_dive_ID" FOREIGN KEY (dive_id) REFERENCES dive (id) ON DELETE CASCADE;
ALTER TABLE ONLY measure
  ADD CONSTRAINT "measure_FK_MEASUREINFORMATION_ID_measureINFORMATION_ID" FOREIGN KEY (measureinformation_id) REFERENCES measure_information (id) ON DELETE CASCADE;
--
-- View: public.measure_formated
--
CREATE OR REPLACE VIEW public.measure_formated AS
  SELECT
    measure.id                                                                                          AS measure_id,
    dive.id                                                                                             AS dive_id,
    dive.start_time,
    dive.end_time,
    measure."timestamp",
    measure.location_corrected,
    measure.location_brut,
    measure.measure_value,
    regexp_replace(measure_information.display :: TEXT, '\{0\}' :: TEXT, measure.measure_value :: TEXT) AS display,
    measure_information.type,
    measure_information.unit,
    measure_information.name
  FROM dive,
    measure,
    measure_information
  WHERE measure.dive_id = dive.id AND measure_information.id = measure.measureinformation_id;
SELECT Populate_Geometry_Columns(('public.measure') :: REGCLASS);
SELECT Populate_Geometry_Columns(('public.measure_formated') :: REGCLASS);

--- INSERTS
--INSERT INTO public.dive (start_time, end_time) VALUES (?, ?);
--INSERT INTO public.measure_information (type, display, unit, name) VALUES (?, ?, ?, ?);
--INSERT INTO public.measure ("timestamp", location_brut, acceleration, measure_value, dive_id, measureinformation_id)
--VALUES (time, ST_SetSRID(ST_MakePoint(X, Y, Z), 4326), ST_MakePoint(aX, aY, aZ), temp, dive_id, measure_id);
