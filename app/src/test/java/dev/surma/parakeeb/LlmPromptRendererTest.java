package dev.surma.parakeeb;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class LlmPromptRendererTest {
    @Test
    public void rewriteUserPrompt_includesExtraInstructionsWhenProvided() {
        String prompt = LlmPromptRenderer.rewriteUserPrompt("Remove filler words.", "hello there");

        assertTrue(prompt.contains("Remove filler words."));
        assertTrue(prompt.contains("<input_text>\nhello there\n</input_text>"));
    }

    @Test
    public void rewriteUserPrompt_usesNoneWhenInstructionsBlank() {
        String prompt = LlmPromptRenderer.rewriteUserPrompt("   ", "hello there");

        assertTrue(prompt.contains("<user_instructions>\n(none)\n</user_instructions>"));
    }
}
