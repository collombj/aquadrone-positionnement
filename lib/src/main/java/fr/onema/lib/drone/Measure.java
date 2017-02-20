package fr.onema.lib.drone;

import fr.onema.lib.file.CSV;

/**
 * Interface utilisé pour récupérer les données mesurées par les capteurs pour exploitation
 *
 * @author loics
 * @since 06-02-2017
 */
public interface Measure extends CSV {
    /**
     * Méthode permettant d'obtenir le nom d'une mesure.
     *
     * @return Une chaine de caractère représentant le nom de la mesure associée.
     */
    String getName();

    /**
     * Méthode permettant d'obtenir l'unité d'une mesure.
     *
     * @return Une chaine de caractère représentant l'unité de la mesure associée.
     */
    String getUnit();

    /**
     * Méthode permettant d'obtenir le type de la mesure.
     *
     * @return Une chaine de caractère représentant le type de la mesure associée.
     */
    String getType();

    /**
     * Méthode permettant d'obtenir le valeur de la mesure.
     *
     * @return Une chaine de caractère représentant la ou les valeurs de la mesure associée.
     */
    String getValue();

    /**
     * Méthode permettant d'obtenir l'affichage associé à une mesure.
     * L'affichage sera du type: "{value} Unit".
     *
     * @return Une chaine de caractère représentant l'affichage de la mesure associée.
     */
    String getDisplay();

    /**
     * Retourne l'heure de la mesure
     * @return Le timestamp de la mesure
     */
    long getTimestamp();
}
