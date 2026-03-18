package dev.surma.parakeeb;

final class LlmResponseParser {
    private static final String OPEN_TAG = "<rewritten_text>";
    private static final String CLOSE_TAG = "</rewritten_text>";

    private LlmResponseParser() {
    }

    static String extractTaggedText(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("Empty response");
        }

        int start = raw.indexOf(OPEN_TAG);
        if (start < 0) {
            throw new IllegalArgumentException("Missing rewritten_text XML tags");
        }

        int contentStart = start + OPEN_TAG.length();
        int end = raw.indexOf(CLOSE_TAG, contentStart);
        if (end < 0) {
            throw new IllegalArgumentException("Missing rewritten_text XML tags");
        }

        String inner = raw.substring(contentStart, end).trim();
        if (inner.isEmpty()) {
            throw new IllegalArgumentException("Tagged rewritten_text content is empty");
        }
        return inner;
    }
}
