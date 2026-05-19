package dev.surma.parakeeb;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<TranscriptEntry> entries = new ArrayList<>();

    public HistoryAdapter(Context context) {
        this.inflater = LayoutInflater.from(context);
    }

    public void setEntries(List<TranscriptEntry> newEntries) {
        entries.clear();
        entries.addAll(newEntries);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public TranscriptEntry getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return entries.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.history_list_item, parent, false);
            holder = new ViewHolder();
            holder.text = convertView.findViewById(R.id.history_text);
            holder.timestamp = convertView.findViewById(R.id.history_timestamp);
            holder.charCount = convertView.findViewById(R.id.history_char_count);
            holder.progress = convertView.findViewById(R.id.history_progress);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TranscriptEntry entry = getItem(position);

        if (entry.isPending()) {
            holder.text.setText(R.string.history_transcribing);
            holder.text.setAlpha(0.6f);
            holder.progress.setVisibility(View.VISIBLE);
            holder.charCount.setVisibility(View.GONE);
        } else if (entry.isError()) {
            holder.text.setText(R.string.history_transcription_failed);
            holder.text.setAlpha(0.6f);
            holder.progress.setVisibility(View.GONE);
            holder.charCount.setVisibility(View.GONE);
        } else {
            holder.text.setText(entry.text);
            holder.text.setAlpha(1.0f);
            holder.progress.setVisibility(View.GONE);
            holder.charCount.setVisibility(View.VISIBLE);
            holder.charCount.setText(entry.charCount + " chars");
        }

        holder.timestamp.setText(DateUtils.getRelativeTimeSpanString(
                entry.timestamp, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS));

        return convertView;
    }

    private static class ViewHolder {
        TextView text;
        TextView timestamp;
        TextView charCount;
        ProgressBar progress;
    }
}
