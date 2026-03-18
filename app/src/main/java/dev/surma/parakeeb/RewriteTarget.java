package dev.surma.parakeeb;

final class RewriteTarget {
    enum Kind {
        CURRENT_SELECTION,
        LAST_DICTATION
    }

    final Kind kind;
    final String rawText;
    final RewriteTextParts parts;

    RewriteTarget(Kind kind, String rawText) {
        this.kind = kind;
        this.rawText = rawText == null ? "" : rawText;
        this.parts = RewriteTextParts.split(this.rawText);
    }
}
