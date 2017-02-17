package fr.onema.lib.File;

import fr.onema.lib.file.Parser;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ParserTest {

    @Test
    public void testReferenceEntry() {
        ReferenceEntry ref = Parser.parseReference("1,2,3,4,5,6");
        ReferenceEntry entry = new ReferenceEntry(1, 2, 3, 4,5f, 6);
        assertEquals(entry, ref);
    }

    @Test
    public void testVirtualizer() {
        VirtualizerEntry ref = Parser.parseVirtualizer("1,3,2,4,5,6,7,8,9,10,11,12,13,14,15");
        VirtualizerEntry entry = new VirtualizerEntry(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15);
        assertEquals(entry, ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument1() {
        ReferenceEntry ref = Parser.parseReference("1");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument2() {
        ReferenceEntry ref = Parser.parseReference("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument3() {
        VirtualizerEntry ref = Parser.parseVirtualizer("1");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument4() {
        VirtualizerEntry ref = Parser.parseVirtualizer("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
        assertNotNull(ref);
    }
}
