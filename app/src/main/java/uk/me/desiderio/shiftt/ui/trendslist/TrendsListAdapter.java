package uk.me.desiderio.shiftt.ui.trendslist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;

public class TrendsListAdapter extends RecyclerView.Adapter<TrendsListAdapter.TrendsViewHolder> {

    private List<TrendEnt> data;
    private final Context context;

    public TrendsListAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public TrendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trend_list_item_layout, parent, false);
        return new TrendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendsViewHolder holder, int position) {
        TrendEnt trend = data.get(position);

        holder.primaryTextView.setText(trend.name);

        holder.metadataTextView.setText(getVolumeLabel(trend.tweetVolume));

        DrawableCompat.setTint(holder.iconImageView.getDrawable(), getTintColor(trend.tweetVolume));
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    public void swapData(List<TrendEnt> data) {
        this.data = (data != null) ? data : Collections.emptyList();
        notifyDataSetChanged();
    }

    @ColorInt
    private int getTintColor(long volume) {
        if (volume > 50000f) {
            return context.getColor(R.color.colorLightOrange);
        } else if (volume > 10000f) {
            return context.getColor(R.color.colorPrimary);
        } else if (volume > 1000f) {
            return context.getColor(R.color.colorAccent);
        } else {
            return context.getColor(R.color.colorAccent_a70);
        }
    }

    private String getVolumeLabel(long volume) {
        if (volume > 0) {
            return String.valueOf(volume);
        } else {
            return context.getString(R.string.trend_list_no_volume);
        }
    }

    class TrendsViewHolder extends RecyclerView.ViewHolder {

        final TextView primaryTextView;
        final TextView metadataTextView;
        final ImageView iconImageView;

        TrendsViewHolder(@NonNull View itemView) {
            super(itemView);
            primaryTextView = itemView.findViewById(R.id.trend_list_item_primary_text_view);
            metadataTextView = itemView.findViewById(R.id.trend_list_item_metadata_text_view);
            iconImageView = itemView.findViewById(R.id.trend_list_item_image_view);
        }
    }
}
