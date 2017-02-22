package fr.onema.simulator;

/***
 * Classe représentant des exceptions de comparaison
 */
class ComparisonException extends Exception {

    /***
     * Méthode de création de l'exception
     * @param cause La cause de l'exception
     */
    ComparisonException(Throwable cause) {
        super(cause);
    }
}
