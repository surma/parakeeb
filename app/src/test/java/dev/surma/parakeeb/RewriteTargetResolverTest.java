package dev.surma.parakeeb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class RewriteTargetResolverTest {
    @Test
    public void split_preservesLeadingAndTrailingWhitespace() {
        RewriteTextParts parts = RewriteTextParts.split("  hello there  ");

        assertEquals("  ", parts.leadingWhitespace);
        assertEquals("hello there", parts.coreText);
        assertEquals("  ", parts.trailingWhitespace);
        assertEquals("  fixed text  ", parts.reassemble("fixed text"));
    }

    @Test
    public void findNearestBackwardMatch_returnsMinusOneWhenMissing() {
        assertEquals(-1, RewriteTargetResolver.findNearestBackwardMatch("hello there", 11, "missing"));
    }

    @Test
    public void findNearestBackwardMatch_prefersNearestMatchBeforeCursor() {
        String text = "alpha beta alpha beta";

        assertEquals(11, RewriteTargetResolver.findNearestBackwardMatch(text, text.length(), "alpha"));
    }

    @Test
    public void findNearestBackwardMatch_doesNotMatchPastCursor() {
        String text = "alpha beta alpha beta";

        assertEquals(0, RewriteTargetResolver.findNearestBackwardMatch(text, 10, "alpha"));
    }
}
