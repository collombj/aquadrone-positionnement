package fr.onema.lib.geo;

import java.util.Objects;

/**
 * Created by julien on 13/02/2017.
 */
public class Matrix {

    private final double[][] tab;

    private Matrix(int n, int m) {
        this.tab = new double[n][m];
    }

    public static Matrix getInstance(int n, int m) {
        if (n < 1 || m < 1) {
            throw new IllegalArgumentException("Numbers must be positive");
        }

        return new Matrix(n, m);
    }

    public int lines() {
        return tab.length;
    }

    public int columns() {
        return tab[0].length;
    }

    private void checkBounds(int n, int m) {
        if ((n >= tab.length || n < 0) && (m >= tab[0].length || m < 0)) {
            throw new IllegalArgumentException("Indexes out of bounds");
        }
    }

    /**
     * Insere une valeur dans la matrice
     *
     * @param n     la ligne désirée
     * @param m     la colonne désirée
     * @param value la valeur à insérer
     */
    public void set(int n, int m, double value) {
        checkBounds(n, m);
        tab[n][m] = value;
    }

    /**
     * Retourne une valeur contenue dans la matrice
     *
     * @param n la ligne de la valeur désirée
     * @param m la colonne de la valeur désirée
     * @return la valeur désirée
     */
    public double get(int n, int m) {
        checkBounds(n, m);
        return tab[n][m];
    }


    /**
     * Effectue une multiplication matricielle et retourne le resultat dans une nouvelle matrice
     *
     * @param b la matrice par laquelle la matrice courante va être multipliée
     * @return une matrice contenant le resultat
     */
    public Matrix mult(Matrix b) {
        Objects.requireNonNull(b);

        if (this.columns() != b.lines()) {
            throw new IllegalArgumentException("The number of columns in matrix A must be the number of lines in matrix B");
        }

        Matrix result = Matrix.getInstance(this.lines(), b.columns());

        for (int i = 0; i < this.lines(); i++) {
            for (int j = 0; j < b.columns(); j++) {
                for (int k = 0; k < this.columns(); k++) {
                    result.set(i, j, result.get(i, j) + (this.tab[i][k] * b.tab[k][j]));
                }
            }
        }

        return result;
    }
}
