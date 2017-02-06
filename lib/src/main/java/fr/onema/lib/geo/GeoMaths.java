package fr.onema.lib.geo;

import java.util.Objects;

/**
 * Created by julien on 06/02/2017.
 */
public class GeoMaths {

    public static double cartesianDistance(CartesianCoordinate pos1, CartesianCoordinate pos2) {
        Objects.requireNonNull(pos1);
        Objects.requireNonNull(pos2);

        return Math.sqrt(Math.pow((pos2.x - pos1.x),2) + Math.pow((pos2.y - pos1.y),2) + Math.pow((pos2.z - pos1.z),2));
    }
}
