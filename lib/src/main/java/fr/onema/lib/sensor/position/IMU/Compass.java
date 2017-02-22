package fr.onema.lib.sensor.position.IMU;

/***
 * Classe représentant le compas magnétique sur les axes x, y, z
 */
public class Compass {
    private final int xMagnetic;
    private final int yMagnetic;
    private final int zMagnetic;

    /**
     * @param xMagnetic coordonnée magnétique x
     * @param yMagnetic coordonnée magnétique y
     * @param zMagnetic coordonnée magnétique z
     */
    public Compass(int xMagnetic, int yMagnetic, int zMagnetic) {
        this.xMagnetic = xMagnetic;
        this.yMagnetic = yMagnetic;
        this.zMagnetic = zMagnetic;
    }

    /***
     * Getter de la valeur magnétique de l'axe x
     * @return La valeur magnétique de l'axe x
     */
    public int getxMagnetic() {
        return xMagnetic;
    }

    /***
     * Getter de la valeur magnétique de l'axe y
     * @return La valeur magnétique de l'axe y
     */
    public int getyMagnetic() {
        return yMagnetic;
    }

    /***
     * Getter de la valeur magnétique de l'axe z
     * @return La valeur magnétique de l'axe z
     */
    public int getzMagnetic() {
        return zMagnetic;
    }
}
