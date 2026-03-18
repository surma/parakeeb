package dev.surma.parakeeb;

import android.view.inputmethod.ExtractedText;

final class RewriteTargetResolver {
    private RewriteTargetResolver() {
    }

    static RewriteTarget fromSelectedText(CharSequence selectedText) {
        if (selectedText == null || selectedText.length() == 0) {
            return null;
        }
        return new RewriteTarget(RewriteTarget.Kind.CURRENT_SELECTION, selectedText.toString());
    }

    static int findNearestBackwardMatch(String fullText, int cursor, String needle) {
        if (fullText == null || needle == null || needle.isEmpty()) {
            return -1;
        }
        int safeCursor = Math.max(0, Math.min(cursor, fullText.length()));
        return fullText.substring(0, safeCursor).lastIndexOf(needle);
    }

    static int[] findReplacementRange(ExtractedText extractedText, String needle) {
        if (extractedText == null || extractedText.text == null) {
            return null;
        }

        int cursorInExtracted = extractedText.selectionEnd - extractedText.startOffset;
        int matchStart = findNearestBackwardMatch(
                extractedText.text.toString(),
                cursorInExtracted,
                needle);
        if (matchStart < 0) {
            return null;
        }

        int absoluteStart = extractedText.startOffset + matchStart;
        return new int[]{absoluteStart, absoluteStart + needle.length()};
    }
}
