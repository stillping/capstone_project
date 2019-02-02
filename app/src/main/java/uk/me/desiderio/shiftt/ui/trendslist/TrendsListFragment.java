package uk.me.desiderio.shiftt.ui.trendslist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import uk.me.desiderio.shiftt.NetworkStateResourceActivity;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment showing a list of twitter trends in the area
 */
public class TrendsListFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    ConnectivityLiveData connectivityLiveData;

    private TrendsListViewModel viewModel;

    private View emptyView;
    private TrendsListAdapter adapter;

    public static TrendsListFragment newInstance() {
        return new TrendsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trends_list_fragment, container, false);

        emptyView = rootView.findViewById(R.id.trends_empty_view);
        shouldShowEmptyView(false);

        RecyclerView recyclerView = rootView.findViewById(R.id.trends_recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TrendsListAdapter(getContext());
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        viewModel = ViewModelProviders.
                of(this, viewModelFactory).get(TrendsListViewModel.class);

        viewModel.getTrendsResource().observe(this, this::processResource);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    private void processResource(@NonNull Resource<List<TrendEnt>> resource) {
        swapViewData(resource.data);
        showEmptyView(resource.data, resource.status);
        updateGlobalViewStateOnResource(resource);
    }

    private void swapViewData(List<TrendEnt> trends) {
        adapter.swapData(trends);
    }

    private void shouldShowEmptyView(boolean shouldShow) {
        if (shouldShow) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView(List data, @Resource.ResourceStatus int status) {
        boolean shouldShow = status != Resource.LOADING && (data == null || data.isEmpty());
        shouldShowEmptyView(shouldShow);
    }

    private void updateGlobalViewStateOnResource(@NonNull Resource<List<TrendEnt>> resource) {
        ((NetworkStateResourceActivity) getActivity()).updateViewStateOnResource(resource,
                                                                                 v -> viewModel.retry());
    }
}
