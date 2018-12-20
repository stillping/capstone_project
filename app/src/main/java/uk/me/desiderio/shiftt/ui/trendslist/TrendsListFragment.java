package uk.me.desiderio.shiftt.ui.trendslist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment showing a list of twitter trends in the area
 */
public class TrendsListFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    private TrendsListAdapter adapter;

    public static TrendsListFragment newInstance() {
        return new TrendsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trends_list_fragment, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.trends_recycler_view);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        adapter = new TrendsListAdapter();
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TrendsListViewModel viewModel = ViewModelProviders.
                of(this, viewModelFactory).get(TrendsListViewModel.class);

        viewModel.getTrends().observe(this,
                                      trendsViewDataList -> adapter.swapData(trendsViewDataList));
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
