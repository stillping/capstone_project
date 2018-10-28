package uk.me.desiderio.shiftt.ui.neighbourhood;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.ui.trendslist.TrendsListViewModel;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment to show map with drawn areas where some twitter activity had taken place
 */

public class NeighbourhoodFragment extends Fragment {

    ViewModelFactory viewModelFactory;

    private NeighbourhoodViewModel viewModel;
    private TextView textView;

    public static NeighbourhoodFragment newInstance() {
        return new NeighbourhoodFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.neighbourhood_fragment, container, false);

        textView = rootView.findViewById(R.id.neighbourhood_message);
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(NeighbourhoodViewModel.class);
        // TODO: Use the ViewModel
        viewModel.getMessage().observe(this, message -> {
            textView.setText(message);
        });

        viewModel.getMessage().setValue("Hello World, Autumn is coming [Neighbours]");
    }

}
