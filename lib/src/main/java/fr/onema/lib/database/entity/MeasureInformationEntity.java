package fr.onema.lib.database.entity;

import java.util.Objects;

/**
 * Cette classe représente les informations mesurées, dans la base de données
 * Created by Francois Vanderperre on 06/02/2017.
 */
public class MeasureInformationEntity {
    private int id;
    private String name;
    private String unit;
    private String type;
    private String display;

    /**
     * Le constructeur
     * @param id
     *  L'identifiant de la mesure en base
     * @param name
     *  Le nom de la mesure en base
     * @param unit
     *  L'unité de la mesure
     * @param type
     *  Le type de mesure
     * @param display
     *  L affichage de la mesure
     */
    public MeasureInformationEntity(int id, String name, String unit, String type, String display) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(unit);
        Objects.requireNonNull(type);
        Objects.requireNonNull(display);
        if (id<=0)
            throw new IllegalArgumentException("parameter id must be positive but has value " + id);
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.type = type;
        this.display = display;
    }

    /***
     *
     * @return L'identifiant de la mesure en base
     */
    public int getId() {
        return id;
    }

    /**
     * @return Le nom de la mesure en base
     */
    public String getName() {
        return name;
    }

    /**
     * @return L'unité de la mesure
     */
    public String getUnit() {
        return unit;
    }

    /**
     * @return Le type de mesure
     */
    public String getType() {
        return type;
    }


    /**
     * @return L affichage de la mesure
     */
    public String getDisplay() {
        return display;
    }
}
