package fr.onema.lib;

import fr.onema.lib.file.Parser;
import fr.onema.lib.virtualizer.entry.ReferenceEntry;
import fr.onema.lib.virtualizer.entry.VirtualizerEntry;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ParserTest {
    Parser parser = new Parser();

    @Test
    public void ReferenceEntryNotNull() {
        ReferenceEntry ref = parser.parseReference("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19");
        assertNotNull(ref);
    }

    @Test
    public void VirtualizerEntryNotNull() {
        VirtualizerEntry ref = parser.parseVirtualizer("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument1() {
        ReferenceEntry ref = parser.parseReference("1");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument2() {
        ReferenceEntry ref = parser.parseReference("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument3() {
        VirtualizerEntry ref = parser.parseVirtualizer("1");
        assertNotNull(ref);
    }

    @Test(expected=IllegalArgumentException.class)
    public void sizeArgument4() {
        VirtualizerEntry ref = parser.parseVirtualizer("1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20");
        assertNotNull(ref);
    }
}
