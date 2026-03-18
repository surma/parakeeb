package dev.surma.parakeeb;

final class RewriteTextParts {
    final String leadingWhitespace;
    final String coreText;
    final String trailingWhitespace;

    private RewriteTextParts(String leadingWhitespace, String coreText, String trailingWhitespace) {
        this.leadingWhitespace = leadingWhitespace;
        this.coreText = coreText;
        this.trailingWhitespace = trailingWhitespace;
    }

    static RewriteTextParts split(String raw) {
        String value = raw == null ? "" : raw;

        int start = 0;
        while (start < value.length() && Character.isWhitespace(value.charAt(start))) {
            start++;
        }

        int end = value.length();
        while (end > start && Character.isWhitespace(value.charAt(end - 1))) {
            end--;
        }

        return new RewriteTextParts(
                value.substring(0, start),
                value.substring(start, end),
                value.substring(end));
    }

    String reassemble(String newCore) {
        return leadingWhitespace + (newCore == null ? "" : newCore) + trailingWhitespace;
    }
}
