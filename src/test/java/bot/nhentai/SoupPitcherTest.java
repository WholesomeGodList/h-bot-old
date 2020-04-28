package bot.nhentai;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SoupPitcherTest {
    @Test
    void testMetadata() throws IOException {
        SoupPitcher checker = new SoupPitcher("https://nhentai.net/g/187539/");
        assertEquals("hisasi", checker.getArtists().get(0));
        assertEquals(2, checker.getChars().size());
        assertEquals("English", checker.getLanguage());
        assertEquals(7, checker.getTags().size());
        assertTrue(checker.getTags().contains("ahegao"));
        assertTrue(checker.getFaves() > 6000);
        assertEquals(2, checker.getChars().size());
        assertTrue(checker.getChars().contains("erina nakiri"));
        assertEquals("Erina-sama's Love Laboratory. 2", checker.getTitle());
        assertEquals("neko wa manma ga utsukushii", checker.getGroups().get(0));
        assertEquals("shokugeki no soma", checker.getParodies().get(0));
    }
}