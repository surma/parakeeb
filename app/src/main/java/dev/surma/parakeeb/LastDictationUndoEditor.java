package dev.surma.parakeeb;

import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;

final class LastDictationUndoEditor {
    private LastDictationUndoEditor() {
    }

    static boolean undo(InputConnection ic, ExtractedText extractedText, String lastDictationText) {
        if (ic == null || lastDictationText == null || lastDictationText.isEmpty()) {
            return false;
        }

        CharSequence selectedText = ic.getSelectedText(0);
        if (selectedText != null && selectedText.length() > 0) {
            if (!lastDictationText.contentEquals(selectedText)) {
                return false;
            }
            ic.commitText("", 1);
            return true;
        }

        if (extractedText == null || extractedText.text == null) {
            return false;
        }

        int selectionStart = extractedText.selectionStart;
        int selectionEnd = extractedText.selectionEnd;
        if (selectionStart != selectionEnd) {
            return false;
        }

        int cursorInExtracted = selectionEnd - extractedText.startOffset;
        if (cursorInExtracted < lastDictationText.length()) {
            return false;
        }

        int startInExtracted = cursorInExtracted - lastDictationText.length();
        CharSequence textBeforeCursor = extractedText.text.subSequence(startInExtracted, cursorInExtracted);
        if (!lastDictationText.contentEquals(textBeforeCursor)) {
            return false;
        }

        int absoluteStart = selectionEnd - lastDictationText.length();
        ic.beginBatchEdit();
        try {
            ic.setSelection(absoluteStart, selectionEnd);
            ic.commitText("", 1);
        } finally {
            ic.endBatchEdit();
        }
        return true;
    }
}
