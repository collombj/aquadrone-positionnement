package fr.onema.lib.file;


import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;

// TODO : complete -> replace into factory
public class Parser {
    // TODO : complete
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
                Short.parseShort(s[5]));
    }

    // TODO : complete
    public static VirtualizerEntry parseVirtualizer(String line) {
        String[] s = line.split(",");
        if(s.length != 15) {
            throw new IllegalArgumentException();
        }
        return new VirtualizerEntry(Long.parseLong(s[0]),
                Integer.parseInt(s[2]),
                Integer.parseInt(s[1]),
                Integer.parseInt(s[3]),
                Short.parseShort(s[4]),
                Short.parseShort(s[5]),
                Short.parseShort(s[6]),
                Double.parseDouble(s[7]),
                Double.parseDouble(s[8]),
                Double.parseDouble(s[9]),
                Short.parseShort(s[10]),
                Short.parseShort(s[11]),
                Short.parseShort(s[12]),
                Float.parseFloat(s[13]),
                Short.parseShort(s[14]));
    }
}
