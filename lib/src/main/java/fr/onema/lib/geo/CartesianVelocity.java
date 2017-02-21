package fr.onema.lib.geo;

/***
 * Classe représentant les valeurs d'accélération sur les axes x, y, z de coordonnées d'un plan cartésien
 */
public class CartesianVelocity {
    public final double vx;
    public final double vy;
    public final double vz;

    /***
     * Constructeur
     * @param vx Accélération sur l'axe x
     * @param vy Accélération sur l'axe y
     * @param vz Accélération sur l'axe z
     */
    public CartesianVelocity(double vx, double vy, double vz) {
        this.vx = vx;
        this.vy = vy;
        this.vz = vz;
    }
}
