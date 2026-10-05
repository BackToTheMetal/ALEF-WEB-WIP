package com.webawt.portable;
import java.awt.AWTKeyStroke;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ImplicitKeyStrokeTest {
    @Test public void implicitPressedMatchesExplicitGrammar() {
        for (String key : new String[] {"F2", "A", "ENTER", "PAGE_UP", "F24"}) {
            for (String modifier : new String[] {"", "shift ", "ctrl ", "alt ", "meta ", "ctrl shift "}) {
                assertEquals(AWTKeyStroke.getAWTKeyStroke(modifier + "pressed " + key), AWTKeyStroke.getAWTKeyStroke(modifier + key));
            }
        }
        assertTrue(AWTKeyStroke.getAWTKeyStroke("shift released F2").isOnKeyRelease());
        assertEquals('a', AWTKeyStroke.getAWTKeyStroke("typed a").getKeyChar());
    }
    @Test public void malformedSpecificationsStillFail() {
        for (String text : new String[] {null, "", "shift", "ctrl pressed", "ctrl+K", "typed aa", "F2 extra"}) {
            try { AWTKeyStroke.getAWTKeyStroke(text); fail("Accepted: " + text); }
            catch (IllegalArgumentException expected) { }
        }
    }
}
