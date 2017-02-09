package fr.onema.lib.geo;

import fr.onema.lib.sensor.position.IMU.Accelerometer;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by julien on 06/02/2017.
 */
// TODO : complete
public class GeoMaths {
    private static final double DEG_2_RAD = Math.PI / 180;
    private static final double RAD_2_DEG = 180 / Math.PI;
    private static final int R = 6_378_137; //Rayon terrestre à l'équateur en mètres
    private static final double F = 1.0 / 298.257223563;
    private static final double MS2_TO_G = 0.101972;

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
        CartesianCoordinate a2 = computeXYZfromLatLonAlt(a.lat, a.lon, a.alt);
        CartesianCoordinate b2 = computeXYZfromLatLonAlt(b.lat, b.lon, b.alt);


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
     *
     * @param lat la latitude en radians
     * @param lon la longitude en radians
     * @param alt l'altitude
     * @return la valeur de XYZ associée à ces coordonnées
     */
    private static CartesianCoordinate computeXYZfromLatLonAlt(double lat, double lon, double alt) {
        double cosLat = Math.cos(lat);
        double sinLat = Math.sin(lat);
        double cosLon = Math.cos(lon);
        double sinLon = Math.sin(lon);
        double c = 1.0 / Math.sqrt(cosLat * cosLat + (1 - F) * (1 - F) * sinLat * sinLat);
        double s = (1.0 - F) * (1.0 - F) * c;
        double x = (R * c + alt) * cosLat * cosLon;
        double y = (R * c + alt) * cosLat * sinLon;
        double z = (R * s + alt) * sinLat;

        return new CartesianCoordinate(x, y, z);
    }

    /**
     * Calcule la position cartésienne d'un point GPS dans le référentiel GPS demandé
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
    public static CartesianVelocity computeVelocityFromCartesianCoordinate(CartesianCoordinate coordinate, long timestamp) {
        Objects.requireNonNull(coordinate);

        double frac = 1_000. / timestamp;
        double vx = coordinate.x * frac;
        double vy = coordinate.y * frac;
        double vz = coordinate.z * frac;

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
            throw new IllegalArgumentException("Cannot compute the acceleration with a timestamp equals to 0");
        }

        double accelerationX = (((velocityCurrent.vx - velocityRef.vx) / (timestamp / 1000.)) * MS2_TO_G) * 1_000;
        double accelerationY = (((velocityCurrent.vy - velocityRef.vy) / (timestamp / 1000.)) * MS2_TO_G) * 1_000;
        double accelerationZ = (((velocityCurrent.vz - velocityRef.vz) / (timestamp / 1000.)) * MS2_TO_G) * 1_000;

        return new Accelerometer((int)Math.round(accelerationX), (int)Math.round(accelerationY), (int)Math.round(accelerationZ));
    }

    /**
     * Calcule les coordonnées GPS d'un point cartésien (qui utilise le point GPS de référence comme origine)
     *
     * @param refPoint le point GPS de référence
     * @param point le point cartésien
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
        double b = R*(1-F);
        double theta = Math.atan((pointECEF.z*R)/(p*b));
        BigDecimal bigR = new BigDecimal(R);
        BigDecimal bigB = BigDecimal.valueOf(b);
        BigDecimal b2 = bigB.multiply(bigB);
        BigDecimal r2 = bigR.multiply(bigR);
        BigDecimal r2Minusb2 = r2.subtract(b2);
        BigDecimal finalOperation = r2Minusb2.divide(r2, BigDecimal.ROUND_UP);
        double e = Math.sqrt(finalOperation.doubleValue());
        BigDecimal finalOperation2 = r2Minusb2.divide(b2, BigDecimal.ROUND_UP);
        double ePrime = Math.sqrt(finalOperation2.doubleValue());



        double lon = Math.atan(pointECEF.y/pointECEF.x);
        double lat = Math.atan((pointECEF.z + (ePrime*ePrime*b*Math.sin(theta)*Math.sin(theta)*Math.sin(theta)))/
                (p-(e*e*R*Math.cos(theta)*Math.cos(theta)*Math.cos(theta))));

        double n = R / Math.sqrt(1 - (e*e*Math.sin(lat)*Math.sin(lat)));

        double alt = (p/Math.cos(lat)) - n;

        return new GPSCoordinate(Math.round(rad2deg(lat)*10_000_000), Math.round(rad2deg(lon)*10_000_000), Math.round(alt*1_000));
    }


}
