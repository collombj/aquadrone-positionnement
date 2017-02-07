
package fr.onema.lib.database.entity;


/**
 * Created by Francois Vanderperre on 06/02/2017.
 *
 * Cette classe représente une plongée telle qu'elle est modélisée dans la base de données
 */
public class DiveEntity {
    private int id;
    private long startTime;
    private long endTime;

    /**
     * Constructeur
     * @param id
     *      ce paramètre correspond a l'identifiant de la plongée pour la base de données
     * @param startTime
     *      la timestamp à laquelle a commencé la plongée, en millisecondes
     * @param endTime
     *      la timestamp à laquelle s'est terminée la plongée, en millisecondes
     */
    public DiveEntity(int id, long startTime, long endTime) {
        if (id <= 0 || startTime <= 0 || endTime <= 0)
            throw new IllegalArgumentException("parameters must be positive");
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Cette méthode renvoie l'id de la plongée dans la base
     * @return
     *      l'identifiant de la plongée
     */
    public int getId() {
        return id;
    }

    /**
     * Cette méthode renvoie la timestamp à laquelle a commencé la plongée, en millisecondes
     * @return
     *       la timestamp à laquelle a commencé la plongée, en millisecondes
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Cette méthode renvoie la timestamp à laquelle s'est terminée la plongée, en millisecondes
     * @return
     *      la timestamp à laquelle s'est terminée la plongée, en millisecondes
     */
    public long getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiveEntity that = (DiveEntity) o;

        if (id != that.id) return false;
        if (startTime != that.startTime) return false;
        return endTime == that.endTime;

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }
}
