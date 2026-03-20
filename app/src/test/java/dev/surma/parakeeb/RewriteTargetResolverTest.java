package dev.surma.parakeeb;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.view.inputmethod.ExtractedText;

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
    public void fromSelectedText_returnsCurrentSelectionTarget() {
        RewriteTarget target = RewriteTargetResolver.fromSelectedText("  hello there  ");

        assertEquals(RewriteTarget.Kind.CURRENT_SELECTION, target.kind);
        assertEquals("  hello there  ", target.rawText);
    }

    @Test
    public void fromExtractedText_returnsCurrentFieldTarget() {
        ExtractedText extractedText = new ExtractedText();
        extractedText.startOffset = 3;
        extractedText.text = "  hello there  ";

        RewriteTarget target = RewriteTargetResolver.fromExtractedText(extractedText);

        assertEquals(RewriteTarget.Kind.CURRENT_FIELD, target.kind);
        assertEquals("  hello there  ", target.rawText);
    }

    @Test
    public void fullFieldRange_coversEntireExtractedBuffer() {
        ExtractedText extractedText = new ExtractedText();
        extractedText.startOffset = 7;
        extractedText.text = "hello there";

        assertArrayEquals(new int[]{7, 18}, RewriteTargetResolver.fullFieldRange(extractedText));
    }

    @Test
    public void emptyOrMissingExtractedText_returnsNoTargetOrRange() {
        assertNull(RewriteTargetResolver.fromExtractedText(null));
        assertNull(RewriteTargetResolver.fullFieldRange(null));

        ExtractedText empty = new ExtractedText();
        empty.startOffset = 4;
        empty.text = "";

        assertNull(RewriteTargetResolver.fromExtractedText(empty));
        assertNull(RewriteTargetResolver.fullFieldRange(empty));
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
