package fr.onema.lib.file;

/**
 * Interface qui permet de générer des données au format CSV.
 */
public interface CSV {
    /**
     * Méthode utilisé pour affiché une mesure dans le format CSV.
     *
     * @return Une chaine de caractère au format CSV.
     */
    String toCSV();

    /**
     * Méthode permettant d'obtenir le header CSV de la mesure.
     *
     * @return Le header CSV associé.
     */
    String getCSVHeader();
}
