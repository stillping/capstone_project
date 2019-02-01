package uk.me.desiderio.shiftt.ui.trendslist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import dagger.android.support.AndroidSupportInjection;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.data.database.model.TrendEnt;
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.util.ConnectivityLiveData;
import uk.me.desiderio.shiftt.util.SnackbarDelegate;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment showing a list of twitter trends in the area
 */
public class TrendsListFragment extends Fragment {

    @Inject
    ViewModelFactory viewModelFactory;

    @Inject
    ConnectivityLiveData connectivityLiveData;

    private TrendsListAdapter adapter;
    private ProgressBar progressBar;
    private View emptyView;

    private TrendsListViewModel viewModel;

    private SnackbarDelegate snackbarDelegate;

    private Observer<Boolean> onConnectedObserver;

    public static TrendsListFragment newInstance() {
        return new TrendsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trends_list_fragment, container, false);

        progressBar = rootView.findViewById(R.id.trends_progress_bar);

        emptyView = rootView.findViewById(R.id.trends_empty_view);
        showEmptyView(false);

        RecyclerView recyclerView = rootView.findViewById(R.id.trends_recycler_view);

        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new TrendsListAdapter(getContext());
        recyclerView.setAdapter(adapter);


        snackbarDelegate = new SnackbarDelegate(R.string
                                                        .snackbar_connected_message_trends_suffix,
                                                recyclerView);

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
        toggleProgressBar(resource.status);
        swapViewData(resource.data);
        toggleSnackbar(resource.status);
    }


    private void toggleProgressBar(@Resource.ResourceStatus int status) {
        if (status == Resource.LOADING) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void swapViewData(List<TrendEnt> trends) {
        adapter.swapData(trends);
        showEmptyView(trends == null || trends.isEmpty());
    }

    private void toggleSnackbar(@Resource.ResourceStatus int status) {
        if (status == Resource.ERROR) {
            snackbarDelegate.showSnackbar(SnackbarDelegate.ERROR, v -> viewModel.retry());
        } else if (status == Resource.NO_CONNECTION) {
            snackbarDelegate.showSnackbar(SnackbarDelegate.NO_CONNECTED, null);
            registerConnectedUpdates(v -> viewModel.retry());
        } else {
            // branch for Resource.LOADING || Resource.SUCCESS
            snackbarDelegate.hideSnackbar();
        }
    }

    private void showEmptyView(boolean shouldShow) {
        if (shouldShow) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void registerConnectedUpdates(View.OnClickListener listener) {
        if (onConnectedObserver == null) {
            onConnectedObserver = isConnected -> {
                if (isConnected) {
                    snackbarDelegate.showSnackbar(SnackbarDelegate.CONNECTED, listener);
                    connectivityLiveData.removeObserver(onConnectedObserver);
                    onConnectedObserver = null;
                }
            };
        }
        connectivityLiveData.observe(this, onConnectedObserver);
    }
}
