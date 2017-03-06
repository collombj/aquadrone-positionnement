package fr.onema.lib.database.entity;

import java.util.Objects;

/**
 * Cette classe représente les informations mesurées, dans la base de données
 */
public class MeasureInformationEntity {
    private int id;
    private String name;
    private String unit;
    private String type;
    private String display;

    /**
     * Constructeur par défaut
     * @param id      L'identifiant de la mesure en base
     * @param name    Le nom de la mesure en base
     * @param unit    L'unité de la mesure
     * @param type    Le type de mesure
     * @param display L affichage de la mesure
     */
    public MeasureInformationEntity(int id, String name, String unit, String type, String display) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(unit);
        Objects.requireNonNull(type);
        Objects.requireNonNull(display);
        if (id <= 0)
            throw new IllegalArgumentException("L'identifiant est censé être positif : " + id);
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.type = type;
        this.display = display;
    }

    /***
     * Getter de l'identifiant de la mesure
     * @return L'identifiant de la mesure en base
     */
    public int getId() {
        return id;
    }

    /**
     * Getter du nom de la mesure
     * @return Le nom de la mesure en base
     */
    public String getName() {
        return name;
    }

    /**
     * Getter de l'unité de la mesure
     * @return L'unité de la mesure
     */
    String getUnit() {
        return unit;
    }

    /**
     * Getter du type de mesure
     * @return Le type de mesure
     */
    public String getType() {
        return type;
    }


    /**
     * Getter de l'affichage de la mesure
     * @return L'affichage de la mesure
     */
    String getDisplay() {
        return display;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        MeasureInformationEntity that = (MeasureInformationEntity) o;

        if (id != that.id)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null)
            return false;
        if (unit != null ? !unit.equals(that.unit) : that.unit != null)
            return false;
        if (type != null ? !type.equals(that.type) : that.type != null)
            return false;
        return display != null ? display.equals(that.display) : that.display == null;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (display != null ? display.hashCode() : 0);
        return result;
    }
}
