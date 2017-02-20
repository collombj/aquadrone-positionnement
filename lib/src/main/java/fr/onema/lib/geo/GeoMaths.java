package fr.onema.lib.geo;


import fr.onema.lib.drone.Position;
import fr.onema.lib.sensor.position.IMU.Accelerometer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Classe Helper pour toutes les opérations géographiques/mathématiques
 * <p>
 * <p>
 * Created by julien on 06/02/2017.
 */
public class GeoMaths {
    private static final double DEG_2_RAD = Math.PI / 180;
    private static final double RAD_2_DEG = 180 / Math.PI;
    private static final int R = 6_378_137; //Rayon terrestre à l'équateur en mètres
    private static final double F = 1.0 / 298.257223563;
    private static final double MS2_TO_G = 0.101972;
    private static final double G_TO_MS2 = 9.80665;

    private GeoMaths() {
    }

    /**
     * Cette méthode calcule la distance cartésienne entre deux points
     *
     * @param pos1 un point {@link CartesianCoordinate}
     * @param pos2 un point {@link CartesianCoordinate}
     * @return la distance entre les deux points
     */
    public static double cartesianDistance(CartesianCoordinate pos1, CartesianCoordinate pos2) {
        Objects.requireNonNull(pos1);
        Objects.requireNonNull(pos2);
        return Math.sqrt(Math.pow(pos2.x - pos1.x, 2) + Math.pow(pos2.y - pos1.y, 2) + Math.pow(pos2.z - pos1.z, 2));
    }

    /**
     * Calcul la distance de deux coordonnées gps
     *
     * @param a premiere coordonnée GPS {@link GPSCoordinate}
     * @param b seconde coordonnée GPS {@link GPSCoordinate}
     * @return la distance double en m
     */
    public static double gpsDistance(GPSCoordinate a, GPSCoordinate b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);
        CartesianCoordinate a2 = computeXYZfromLatLonAlt(deg2rad(a.lat / 10_000_000.), deg2rad(a.lon / 10_000_000.), deg2rad(a.alt / 1_000.));
        CartesianCoordinate b2 = computeXYZfromLatLonAlt(deg2rad(b.lat / 10_000_000.), deg2rad(b.lon / 10_000_000.), deg2rad(b.alt / 1_000.));

        return cartesianDistance(a2, b2);

    }

    /**
     * Convertis des degrés décimaux en radians
     *
     * @param deg l'angle en degrés décimaux
     * @return l'angle équivalent exprimé en radians
     */
    public static double deg2rad(double deg) {
        return deg * DEG_2_RAD;
    }

    /**
     * Convertis des radians en degrés decimaux
     *
     * @param rad l'angle en radians
     * @return l'agnel équivalent exprimé en degrés décimaux
     */
    public static double rad2deg(double rad) {
        return rad * RAD_2_DEG;
    }


    /**
     * Calcule les XYZ à partir d'une lat/lon/alt (Earth centered reference)
     * <p>
     * Cette méthode a très peu d'usage HORS méthodes de GeoMath, soyez sur que c'est bien ce dont vous avez besoin
     *
     * @param lat la latitude en radians
     * @param lon la longitude en radians
     * @param alt l'altitude
     * @return la valeur de XYZ associée à ces coordonnées
     */
    public static CartesianCoordinate computeXYZfromLatLonAlt(double lat, double lon, double alt) {
        double cosLat = cos(lat);
        double sinLat = sin(lat);
        double cosLon = cos(lon);
        double sinLon = sin(lon);
        double c = 1.0 / Math.sqrt(cosLat * cosLat + (1 - F) * (1 - F) * sinLat * sinLat);
        double s = (1.0 - F) * (1.0 - F) * c;
        double x = (R * c + alt) * cosLat * cosLon;
        double y = (R * c + alt) * cosLat * sinLon;
        double z = (R * s + alt) * sinLat;

        return new CartesianCoordinate(x, y, z);
    }

    /**
     * Calcule la position cartésienne d'un point GPS_SENSOR dans le référentiel GPS_SENSOR demandé
     *
     * @param refPoint (exprimé en deg*1e7)
     * @param point    (exprimé en deg*1e7)
     * @return la position cartésienne courante selon le point de référence demandé {@link CartesianCoordinate}
     */
    public static CartesianCoordinate computeCartesianPosition(GPSCoordinate refPoint, GPSCoordinate point) {
        Objects.requireNonNull(refPoint);
        Objects.requireNonNull(point);

        double latRefRad = deg2rad(refPoint.lat / 10_000_000.);
        double lonRefRad = deg2rad(refPoint.lon / 10_000_000.);
        double altRefM = refPoint.alt / 1_000.;

        CartesianCoordinate refPointCartesian = computeXYZfromLatLonAlt(latRefRad, lonRefRad, altRefM);

        double latRad = deg2rad(point.lat / 10_000_000.);
        double lonRad = deg2rad(point.lon / 10_000_000.);
        double altM = point.alt / 1_000.;

        CartesianCoordinate pointCartesian = computeXYZfromLatLonAlt(latRad, lonRad, altM);

        return new CartesianCoordinate(pointCartesian.x - refPointCartesian.x, pointCartesian.y - refPointCartesian.y,
                pointCartesian.z - refPointCartesian.z);
    }


    /**
     * Retourne la vitesse en m/s d'une coordonnée (par rapport à sa référence)
     * La coordonnée équivaut au vecteur vitesse dans le cas recherché
     *
     * @param coordinate la coordonnée qui est le vecteur vitesse
     * @param timestamp  temps écoulé en ms depuis la derniere mesure (timestampCourrant - timestampPrecedent)
     * @return La vitesse en m/s sur chaque axe {@link CartesianVelocity}
     */
    public static CartesianVelocity computeVelocityFromCartesianCoordinate(CartesianCoordinate prevCoordinate, CartesianCoordinate coordinate, long timestamp) {
        Objects.requireNonNull(coordinate);
        double frac = 1_000. / ((timestamp == 0) ? 1_000. : timestamp);
        double vx = (coordinate.x - prevCoordinate.x) * frac;
        double vy = (coordinate.y - prevCoordinate.y) * frac;
        double vz = (coordinate.z - prevCoordinate.z) * frac;

        return new CartesianVelocity(vx, vy, vz);
    }


    /**
     * Calcule l'acceleration courante a partir de la vitesse courante et de la vitesse précédente
     *
     * @param velocityRef     vitesse précédente
     * @param velocityCurrent vitesse courante
     * @param timestamp       temps entre les deux valeurs de vitesse (en ms)
     * @return Les données d'acceleration {@link Accelerometer}
     */
    public static Accelerometer computeAccelerometerData(CartesianVelocity velocityRef, CartesianVelocity velocityCurrent, long timestamp) {
        Objects.requireNonNull(velocityRef);
        Objects.requireNonNull(velocityCurrent);

        if (timestamp == 0) {
            return new Accelerometer(0, 0, 0);
        }

        double accelerationX = (((velocityCurrent.vx - velocityRef.vx) / (timestamp / 1000.)) * MS2_TO_G) * 1_000;
        double accelerationY = (((velocityCurrent.vy - velocityRef.vy) / (timestamp / 1000.)) * MS2_TO_G) * 1_000;
        double accelerationZ = (((velocityCurrent.vz - velocityRef.vz) / (timestamp / 1000.)) * MS2_TO_G) * 1_000;

        return new Accelerometer((int) Math.round(accelerationX), (int) Math.round(accelerationY), (int) Math.round(accelerationZ));
    }

    /**
     * Calcule les coordonnées GPS_SENSOR d'un point cartésien (qui utilise le point GPS_SENSOR de référence comme origine)
     *
     * @param refPoint le point GPS de référence
     * @param point    le point cartésien
     * @return les coordonnées GPS du point cartésien
     */
    public static GPSCoordinate computeGPSCoordinateFromCartesian(GPSCoordinate refPoint, CartesianCoordinate point) {
        Objects.requireNonNull(refPoint);
        Objects.requireNonNull(point);
        double latRefRad = deg2rad(refPoint.lat / 10_000_000.);
        double lonRefRad = deg2rad(refPoint.lon / 10_000_000.);
        double altRefM = refPoint.alt / 1_000.;
        CartesianCoordinate refPointCartesian = computeXYZfromLatLonAlt(latRefRad, lonRefRad, altRefM);
        CartesianCoordinate pointECEF = new CartesianCoordinate(refPointCartesian.x + point.x, refPointCartesian.y + point.y,
                refPointCartesian.z + point.z);
        double p = Math.sqrt((pointECEF.x * pointECEF.x) + (pointECEF.y * pointECEF.y));
        double b = R * (1 - F);
        double theta = Math.atan((pointECEF.z * R) / (p * b));
        BigDecimal bigR = new BigDecimal(R);
        BigDecimal bigB = BigDecimal.valueOf(b);
        BigDecimal b2 = bigB.multiply(bigB);
        BigDecimal r2 = bigR.multiply(bigR);
        BigDecimal r2Minusb2 = r2.subtract(b2);
        BigDecimal finalOperation = r2Minusb2.divide(r2, BigDecimal.ROUND_UP);
        double e = Math.sqrt(finalOperation.doubleValue());
        BigDecimal finalOperation2 = r2Minusb2.divide(b2, BigDecimal.ROUND_UP);
        double ePrime = Math.sqrt(finalOperation2.doubleValue());


        double lon = Math.atan(pointECEF.y / pointECEF.x);
        double lat = Math.atan((pointECEF.z + (ePrime * ePrime * b * sin(theta) * sin(theta) * sin(theta))) /
                (p - (e * e * R * cos(theta) * cos(theta) * cos(theta))));

        double n = R / Math.sqrt(1 - (e * e * sin(lat) * sin(lat)));

        double alt = (p / cos(lat)) - n;

        return new GPSCoordinate(Math.round(rad2deg(lat) * 10_000_000), Math.round(rad2deg(lon) * 10_000_000), Math.round(alt * 1_000));

    }


    /**
     * Calcul les nouvelles cordonnées cartésienne après une rotaion
     *
     * @param base {@link CartesianCoordinate} coordonnées cartésiennes
     * @param a    angle en radian correspondant au yaw
     * @param b    angle en radian correspondant au pitch
     * @param g    angle en radian correspondant au roll
     * @return une coordonnée cartésiennes {@link CartesianCoordinate}
     */
    private static CartesianCoordinate doRotation(CartesianCoordinate base, double a, double b, double g) {
        Objects.requireNonNull(base);

        Matrix rotation = Matrix.getInstance(3, 3);
        rotation.set(0, 0, cos(a) * cos(b));
        rotation.set(0, 1, (cos(a) * sin(b) * sin(g)) - (sin(a) * cos(g)));
        rotation.set(0, 2, (cos(a) * sin(b) * cos(g)) + (sin(a) * sin(g)));

        rotation.set(1, 0, sin(a) * cos(b));
        rotation.set(1, 1, (sin(a) * sin(b) * sin(g)) - (cos(a) * cos(g)));
        rotation.set(1, 2, (sin(a) * sin(b) * cos(g)) - (cos(a) * sin(g)));

        rotation.set(2, 0, -(sin(b)));
        rotation.set(2, 1, cos(b) * sin(g));
        rotation.set(2, 2, cos(b) * cos(g));


        Matrix previousPoint = Matrix.getInstance(3, 1);
        previousPoint.set(0, 0, base.x);
        previousPoint.set(1, 0, base.y);
        previousPoint.set(2, 0, base.z);

        Matrix result = rotation.mult(previousPoint);

        return new CartesianCoordinate(result.get(0, 0), result.get(1, 0), result.get(2, 0));


    }

    /**
     * Calcule la position cartésienne selon les données des capteurs IMU
     *
     * @param last             coordonnée cartésienne precedente
     * @param yaw              le yaw courant
     * @param pitch            le pitch courant
     * @param roll             le roll courant
     * @param previousVelocity la vitesse précédente (en m/s)
     * @param time             le temps entre les deux coordonnées(en ms)
     * @param accelerometer    les données d'accelerometre
     * @return la nouvelle position estimée du drone
     */
    public static MovementWrapper computeNewPosition(CartesianCoordinate last, double yaw, double pitch, double roll, CartesianVelocity previousVelocity, long time, Accelerometer accelerometer) {
        Objects.requireNonNull(last);
        Objects.requireNonNull(previousVelocity);
        Objects.requireNonNull(accelerometer);

        CartesianVelocity velocity = new CartesianVelocity(
                (((accelerometer.getxAcceleration() / 1000) * G_TO_MS2) * (time / 1000.)) + previousVelocity.vx,
                (((accelerometer.getyAcceleration() / 1000) * G_TO_MS2) * (time / 1000.)) + previousVelocity.vy,
                (((accelerometer.getzAcceleration() / 1000) * G_TO_MS2) * (time / 1000.)) + previousVelocity.vz);


        CartesianCoordinate velocityVector = new CartesianCoordinate(velocity.vx, velocity.vy, velocity.vz);

        CartesianCoordinate velocityRotated = doRotation(velocityVector, -yaw, -pitch, -roll);
        CartesianCoordinate computedCoords = new CartesianCoordinate(
                last.x + (velocityRotated.x * (time / 1000.)),
                last.y + (velocityRotated.y * (time / 1000.)),
                last.z + (velocityRotated.z * (time / 1000.)));
        return new MovementWrapper(computedCoords, velocity);
    }


    public static List<Position> recalculatePosition(List<Position> rawPositions, GPSCoordinate ref, GPSCoordinate resurface) {
        Objects.requireNonNull(rawPositions);
        Objects.requireNonNull(ref);
        Objects.requireNonNull(resurface);


        CartesianCoordinate cartesianResurface = computeCartesianPosition(ref, resurface);
        CartesianCoordinate cartesianResurfaceBrut = rawPositions.get(rawPositions.size() - 1).getCartesianBrute();


        double deltax = cartesianResurface.x - cartesianResurfaceBrut.x;
        double deltay = cartesianResurface.y - cartesianResurfaceBrut.y;
        double deltaz = cartesianResurface.z - cartesianResurfaceBrut.z;
        double ecart = (double) 1 / (rawPositions.size() - 1);

        for (int i = 0; i < rawPositions.size(); i++) {

            rawPositions.get(i).setPositionRecalculated(computeGPSCoordinateFromCartesian(ref, new CartesianCoordinate(
                    rawPositions.get(i).getCartesianBrute().x + (deltax * ecart * i),
                    rawPositions.get(i).getCartesianBrute().y + (deltay * ecart * i),
                    rawPositions.get(i).getCartesianBrute().z + (deltaz * ecart * i))));
        }

        return rawPositions;
    }


    /**
     * Cette classe sert à retourner à la fois une vitesse et une coordonnées dans un meme object
     */
    public static class MovementWrapper {
        private CartesianCoordinate coordinate;
        private CartesianVelocity velocity;

        /**
         * Le constructeur
         *
         * @param coordinate les coordonnees calculees
         * @param velocity   la vitesse calculée
         */
        public MovementWrapper(CartesianCoordinate coordinate, CartesianVelocity velocity) {
            this.coordinate = coordinate;
            this.velocity = velocity;
        }

        /**
         * Permet d obtenir les coordonnées
         *
         * @return des coordonnées cartésiennes
         */
        public CartesianCoordinate getCoordinate() {
            return coordinate;
        }

        /**
         * Permet d'obtenir la velocité
         *
         * @return des coordonnées gps
         */
        public CartesianVelocity getVelocity() {
            return velocity;
        }
    }
}
