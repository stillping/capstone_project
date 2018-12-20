package uk.me.desiderio.shiftt.ui.trendslist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.ui.model.TrendsViewData;

public class TrendsListAdapter extends RecyclerView.Adapter<TrendsListAdapter.TrendsViewHolder> {

    List<TrendsViewData> data;

    private static final String TAG = TrendsListAdapter.class.getSimpleName();

    @NonNull
    @Override
    public TrendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new TrendsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TrendsViewHolder holder, int position) {
        Log.d(TAG, " chandelier position: " + position);
        String trendName = data.get(position).trendName;
        holder.textView.setText(trendName);
    }

    @Override
    public int getItemCount() {
        return (data != null) ? data.size() : 0;
    }

    public void swapData(List<TrendsViewData> data) {
        Log.d(TAG, " chandelier swapData: " + data.size() + "================");
        this.data = data;
        notifyDataSetChanged();
    }


    class TrendsViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;
        public TrendsViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }
}
