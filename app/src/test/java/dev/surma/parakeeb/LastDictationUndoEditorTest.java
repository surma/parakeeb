package dev.surma.parakeeb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.InputConnection;

import org.junit.Test;

public class LastDictationUndoEditorTest {
    @Test
    public void undo_withMatchingSelection_replacesSelectionWithEmptyText() {
        InputConnection ic = mock(InputConnection.class);
        when(ic.getSelectedText(0)).thenReturn("hello ");

        boolean handled = LastDictationUndoEditor.undo(ic, null, "hello ");

        assertTrue(handled);
        verify(ic).commitText("", 1);
        verify(ic, never()).beginBatchEdit();
    }

    @Test
    public void undo_withMatchingTextImmediatelyBeforeCursor_replacesThatRange() {
        InputConnection ic = mock(InputConnection.class);
        when(ic.getSelectedText(0)).thenReturn("");
        ExtractedText extractedText = new ExtractedText();
        extractedText.startOffset = 0;
        extractedText.text = "prefix hello ";
        extractedText.selectionStart = extractedText.text.length();
        extractedText.selectionEnd = extractedText.text.length();

        boolean handled = LastDictationUndoEditor.undo(ic, extractedText, "hello ");

        assertTrue(handled);
        verify(ic).beginBatchEdit();
        verify(ic).setSelection(7, 13);
        verify(ic).commitText("", 1);
        verify(ic).endBatchEdit();
    }

    @Test
    public void undo_withDifferentSelection_doesNothing() {
        InputConnection ic = mock(InputConnection.class);
        when(ic.getSelectedText(0)).thenReturn("different");

        boolean handled = LastDictationUndoEditor.undo(ic, null, "hello ");

        assertFalse(handled);
        verify(ic, never()).commitText("", 1);
    }

    @Test
    public void undo_whenCursorIsNotImmediatelyAfterLastDictation_doesNothing() {
        InputConnection ic = mock(InputConnection.class);
        when(ic.getSelectedText(0)).thenReturn("");
        ExtractedText extractedText = new ExtractedText();
        extractedText.startOffset = 0;
        extractedText.text = "prefix hello suffix";
        extractedText.selectionStart = extractedText.text.length();
        extractedText.selectionEnd = extractedText.text.length();

        boolean handled = LastDictationUndoEditor.undo(ic, extractedText, "hello ");

        assertFalse(handled);
        verify(ic, never()).commitText("", 1);
        verify(ic, never()).beginBatchEdit();
    }

    @Test
    public void undo_withoutRememberedDictation_doesNothing() {
        InputConnection ic = mock(InputConnection.class);

        boolean handled = LastDictationUndoEditor.undo(ic, null, "");

        assertFalse(handled);
        verify(ic, never()).commitText("", 1);
    }
}
