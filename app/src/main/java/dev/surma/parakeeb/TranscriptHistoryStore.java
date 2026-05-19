package dev.surma.parakeeb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TranscriptHistoryStore extends SQLiteOpenHelper {
    private static final String DB_NAME = "transcript_history.db";
    private static final int DB_VERSION = 2;
    private static final String TABLE = "transcripts";
    private static final int MAX_ENTRIES = 100;

    public TranscriptHistoryStore(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "text TEXT, "
                + "timestamp INTEGER NOT NULL, "
                + "char_count INTEGER NOT NULL, "
                + "status TEXT NOT NULL DEFAULT 'done')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add status column; existing entries are all completed.
            db.execSQL("ALTER TABLE " + TABLE + " ADD COLUMN status TEXT NOT NULL DEFAULT 'done'");
        }
    }

    /** Insert a completed transcript entry. */
    public void insert(String text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("text", text);
        values.put("timestamp", System.currentTimeMillis());
        values.put("char_count", text.length());
        values.put("status", TranscriptEntry.STATUS_DONE);
        db.insert(TABLE, null, values);
        prune(db);
    }

    /** Insert a tombstone (pending) entry. Returns the row ID for later update. */
    public long insertTombstone() {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull("text");
        values.put("timestamp", System.currentTimeMillis());
        values.put("char_count", 0);
        values.put("status", TranscriptEntry.STATUS_PENDING);
        long id = db.insert(TABLE, null, values);
        prune(db);
        return id;
    }

    /** Update a tombstone with the transcribed text. */
    public void updateWithText(long id, String text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("text", text);
        values.put("char_count", text.length());
        values.put("status", TranscriptEntry.STATUS_DONE);
        db.update(TABLE, values, "id = ?", new String[]{String.valueOf(id)});
    }

    /** Mark a tombstone as failed. */
    public void updateStatus(long id, String status) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);
        db.update(TABLE, values, "id = ?", new String[]{String.valueOf(id)});
    }

    public List<TranscriptEntry> getRecent() {
        List<TranscriptEntry> entries = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE, null, null, null, null, null,
                "timestamp DESC", String.valueOf(MAX_ENTRIES));
        try {
            while (cursor.moveToNext()) {
                String text = null;
                int textIdx = cursor.getColumnIndexOrThrow("text");
                if (!cursor.isNull(textIdx)) {
                    text = cursor.getString(textIdx);
                }
                String status = cursor.getString(cursor.getColumnIndexOrThrow("status"));
                entries.add(new TranscriptEntry(
                        cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                        text,
                        cursor.getLong(cursor.getColumnIndexOrThrow("timestamp")),
                        cursor.getInt(cursor.getColumnIndexOrThrow("char_count")),
                        status
                ));
            }
        } finally {
            cursor.close();
        }
        return entries;
    }

    private void prune(SQLiteDatabase db) {
        db.execSQL("DELETE FROM " + TABLE + " WHERE id NOT IN ("
                + "SELECT id FROM " + TABLE + " ORDER BY timestamp DESC LIMIT " + MAX_ENTRIES + ")");
    }
}
