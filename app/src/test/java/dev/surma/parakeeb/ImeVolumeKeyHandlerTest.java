package dev.surma.parakeeb;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.view.KeyEvent;

import org.junit.Test;

public class ImeVolumeKeyHandlerTest {
    @Test
    public void volumeDownFirstPressWhileImeShown_startsTrackingAndIsConsumed() {
        assertTrue(ImeVolumeKeyHandler.shouldConsumeKeyDown(true, KeyEvent.KEYCODE_VOLUME_DOWN));
        assertTrue(ImeVolumeKeyHandler.shouldTrackVolumeDownOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_DOWN, 0));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeUpOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_DOWN, 0));
    }

    @Test
    public void repeatedVolumeDownPressWhileImeShown_isConsumedWithoutRestartingTracking() {
        assertTrue(ImeVolumeKeyHandler.shouldConsumeKeyDown(true, KeyEvent.KEYCODE_VOLUME_DOWN));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeDownOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_DOWN, 2));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeUpOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_DOWN, 2));
    }

    @Test
    public void volumeDownLongPressWhileImeShown_triggersSend() {
        assertTrue(ImeVolumeKeyHandler.shouldTriggerSendOnLongPress(true, KeyEvent.KEYCODE_VOLUME_DOWN));
    }

    @Test
    public void volumeDownKeyUpWhileImeShown_togglesRecordingUnlessLongPressHandled() {
        assertTrue(ImeVolumeKeyHandler.shouldConsumeKeyUp(true, KeyEvent.KEYCODE_VOLUME_DOWN));
        assertTrue(ImeVolumeKeyHandler.shouldToggleRecordingOnKeyUp(true, KeyEvent.KEYCODE_VOLUME_DOWN, false));
        assertFalse(ImeVolumeKeyHandler.shouldToggleRecordingOnKeyUp(true, KeyEvent.KEYCODE_VOLUME_DOWN, true));
    }

    @Test
    public void volumeUpFirstPressWhileImeShown_startsTrackingAndIsConsumed() {
        assertTrue(ImeVolumeKeyHandler.shouldConsumeKeyDown(true, KeyEvent.KEYCODE_VOLUME_UP));
        assertTrue(ImeVolumeKeyHandler.shouldTrackVolumeUpOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_UP, 0));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeDownOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_UP, 0));
    }

    @Test
    public void repeatedVolumeUpPressWhileImeShown_isConsumedWithoutRestartingTracking() {
        assertTrue(ImeVolumeKeyHandler.shouldConsumeKeyDown(true, KeyEvent.KEYCODE_VOLUME_UP));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeUpOnKeyDown(true, KeyEvent.KEYCODE_VOLUME_UP, 3));
    }

    @Test
    public void volumeUpLongPressWhileImeShown_triggersUndo() {
        assertTrue(ImeVolumeKeyHandler.shouldTriggerUndoOnLongPress(true, KeyEvent.KEYCODE_VOLUME_UP));
    }

    @Test
    public void volumeUpKeyUpWhileImeShown_triggersRewriteUnlessLongPressHandled() {
        assertTrue(ImeVolumeKeyHandler.shouldConsumeKeyUp(true, KeyEvent.KEYCODE_VOLUME_UP));
        assertTrue(ImeVolumeKeyHandler.shouldTriggerRewriteOnKeyUp(true, KeyEvent.KEYCODE_VOLUME_UP, false));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerRewriteOnKeyUp(true, KeyEvent.KEYCODE_VOLUME_UP, true));
    }

    @Test
    public void volumeKeysWhileImeHidden_areIgnored() {
        assertFalse(ImeVolumeKeyHandler.shouldConsumeKeyDown(false, KeyEvent.KEYCODE_VOLUME_DOWN));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeDownOnKeyDown(false, KeyEvent.KEYCODE_VOLUME_DOWN, 0));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerSendOnLongPress(false, KeyEvent.KEYCODE_VOLUME_DOWN));
        assertFalse(ImeVolumeKeyHandler.shouldToggleRecordingOnKeyUp(false, KeyEvent.KEYCODE_VOLUME_DOWN, false));
        assertFalse(ImeVolumeKeyHandler.shouldConsumeKeyUp(false, KeyEvent.KEYCODE_VOLUME_DOWN));

        assertFalse(ImeVolumeKeyHandler.shouldConsumeKeyDown(false, KeyEvent.KEYCODE_VOLUME_UP));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeUpOnKeyDown(false, KeyEvent.KEYCODE_VOLUME_UP, 0));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerUndoOnLongPress(false, KeyEvent.KEYCODE_VOLUME_UP));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerRewriteOnKeyUp(false, KeyEvent.KEYCODE_VOLUME_UP, false));
        assertFalse(ImeVolumeKeyHandler.shouldConsumeKeyUp(false, KeyEvent.KEYCODE_VOLUME_UP));
    }

    @Test
    public void nonVolumeKeys_areIgnored() {
        assertFalse(ImeVolumeKeyHandler.shouldConsumeKeyDown(true, KeyEvent.KEYCODE_A));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeDownOnKeyDown(true, KeyEvent.KEYCODE_A, 0));
        assertFalse(ImeVolumeKeyHandler.shouldTrackVolumeUpOnKeyDown(true, KeyEvent.KEYCODE_A, 0));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerSendOnLongPress(true, KeyEvent.KEYCODE_A));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerUndoOnLongPress(true, KeyEvent.KEYCODE_A));
        assertFalse(ImeVolumeKeyHandler.shouldToggleRecordingOnKeyUp(true, KeyEvent.KEYCODE_A, false));
        assertFalse(ImeVolumeKeyHandler.shouldTriggerRewriteOnKeyUp(true, KeyEvent.KEYCODE_A, false));
        assertFalse(ImeVolumeKeyHandler.shouldConsumeKeyUp(true, KeyEvent.KEYCODE_A));
    }
}
