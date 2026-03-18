package dev.surma.parakeeb;

final class LlmPromptRenderer {
    private static final String DEFAULT_INSTRUCTIONS = "(none)";

    private LlmPromptRenderer() {
    }

    static String systemPrompt() {
        return "You rewrite speech-to-text dictation.\n"
                + "Preserve the original meaning and language.\n"
                + "Remove filler words and false starts when appropriate.\n"
                + "Fix punctuation, capitalization, and obvious transcription artifacts.\n"
                + "Do not add new information.\n"
                + "Return the final rewritten text only inside <rewritten_text>...</rewritten_text>.";
    }

    static String rewriteUserPrompt(String extraInstructions, String coreText) {
        String instructions = normalizeExtraInstructions(extraInstructions);
        return "Additional instructions:\n"
                + "<user_instructions>\n"
                + instructions + "\n"
                + "</user_instructions>\n\n"
                + "Text to rewrite:\n"
                + "<input_text>\n"
                + safe(coreText) + "\n"
                + "</input_text>";
    }

    static String testSystemPrompt() {
        return "Return exactly <rewritten_text>ok</rewritten_text>";
    }

    static String testUserPrompt() {
        return "Reply now.";
    }

    private static String normalizeExtraInstructions(String extraInstructions) {
        String trimmed = safe(extraInstructions).trim();
        return trimmed.isEmpty() ? DEFAULT_INSTRUCTIONS : trimmed;
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
