package fr.onema.lib.geo;

/**
 * Created by julien on 06/02/2017.
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
}
