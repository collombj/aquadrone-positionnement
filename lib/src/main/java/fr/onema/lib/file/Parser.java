package fr.onema.lib.file;


import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

/***
 * Classe utilitaire permettant de transformer une ligne CSV en entités
 */
public class Parser {

    private Parser() {
        // Avoid instantiation
    }

    /***
     * Parse la ligne et la transforme en entrée de référence
     * @param line La ligne CSV à parser
     * @return L'objet {@link ReferenceEntry} associé
     */
    public static ReferenceEntry parseReference(String line) {
        String[] s = line.split(",");
        if (s.length != 6) {
            throw new IllegalArgumentException();
        }
        return new ReferenceEntry(Long.parseLong(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                Integer.parseInt(s[3]),
                Float.parseFloat(s[4]),
                Integer.parseInt(s[5]));
    }

    /***
     * Parse la ligne et la transforme en entrée virtualisée
     * @param line La ligne CSV à parser
     * @return L'objet {@link VirtualizerEntry} associé
     */
    public static VirtualizerEntry parseVirtualizer(String line) {
        String[] s = line.split(",");
        if (s.length != 15) {
            throw new IllegalArgumentException();
        }
        return new VirtualizerEntry(Long.parseLong(s[0]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[2]),
                Integer.parseInt(s[3]),
                Integer.parseInt(s[4]),
                Integer.parseInt(s[5]),
                Integer.parseInt(s[6]),
                Double.parseDouble(s[7]),
                Double.parseDouble(s[8]),
                Double.parseDouble(s[9]),
                Integer.parseInt(s[10]),
                Integer.parseInt(s[11]),
                Integer.parseInt(s[12]),
                Float.parseFloat(s[13]),
                Integer.parseInt(s[14]));
    }
}
