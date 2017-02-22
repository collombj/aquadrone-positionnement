package fr.onema.lib.geo;

/***
 * Classe représentant des coordonnées d'un plan cartésien sur les axes x, y, z
 */
public class CartesianCoordinate {
    public final double x;
    public final double y;
    public final double z;

    /**
     * Construit une coordonnée cartésienne
     *
     * @param x la valeur sur x
     * @param y la valeur sur y
     * @param z la valeur sur z
     */
    public CartesianCoordinate(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "[x: " + x + ", y: " + y + ", v: " + z +"]";
    }
}
