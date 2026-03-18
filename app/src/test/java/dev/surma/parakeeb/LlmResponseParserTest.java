package dev.surma.parakeeb;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LlmResponseParserTest {
    @Test
    public void extractTaggedText_returnsTaggedContentEvenWithExtraText() {
        String raw = "Sure, here you go\n<rewritten_text>Hello, world!</rewritten_text>\nThanks";

        assertEquals("Hello, world!", LlmResponseParser.extractTaggedText(raw));
    }

    @Test(expected = IllegalArgumentException.class)
    public void extractTaggedText_failsWhenTagsAreMissing() {
        LlmResponseParser.extractTaggedText("Hello, world!");
    }
}
