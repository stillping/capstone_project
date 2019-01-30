package uk.me.desiderio.shiftt.ui.trendslist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;

public class TrendsListAdapter extends RecyclerView.Adapter<TrendsListAdapter.TrendsViewHolder> {

    private List<TrendEnt> data;

    @NonNull
    @Override
    public TrendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new TrendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendsViewHolder holder, int position) {
        String trendName = data.get(position).name;
        holder.textView.setText(trendName);
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    public void swapData(List<TrendEnt> data) {
        this.data = (data != null) ? data : Collections.emptyList();
        notifyDataSetChanged();
    }

    class TrendsViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        TrendsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
