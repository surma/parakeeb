package dev.surma.parakeeb;

public class TranscriptEntry {
    /** Status value for entries still being transcribed. */
    public static final String STATUS_PENDING = "pending";
    /** Status value for successfully transcribed entries. */
    public static final String STATUS_DONE = "done";
    /** Status value for entries where transcription failed. */
    public static final String STATUS_ERROR = "error";

    public final long id;
    /** May be null for tombstone (pending) entries. */
    public final String text;
    public final long timestamp;
    public final int charCount;
    public final String status;

    public TranscriptEntry(long id, String text, long timestamp, int charCount, String status) {
        this.id = id;
        this.text = text;
        this.timestamp = timestamp;
        this.charCount = charCount;
        this.status = status;
    }

    public boolean isPending() {
        return STATUS_PENDING.equals(status);
    }

    public boolean isError() {
        return STATUS_ERROR.equals(status);
    }
}
