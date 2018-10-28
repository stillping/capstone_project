package uk.me.desiderio.shiftt.ui.trendslist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.AndroidSupportInjection;
import uk.me.desiderio.shiftt.R;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * View controller showing a list of twitter trends in the area
 */

public class TrendsListFragment extends Fragment {

    ViewModelFactory viewModelFactory;

    private TrendsListViewModel viewModel;
    private TextView textView;

    public static TrendsListFragment newInstance() {
        return new TrendsListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.trends_list_fragment, container, false);

        textView = rootView.findViewById(R.id.trends_message);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TrendsListViewModel.class);
        // TODO: Use the ViewModel
        viewModel.getMessage().observe(this, message -> {
            textView.setText(message);
        });

        viewModel.getMessage().setValue("Hello World, Winter is coming [TRENDS]");
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }
}
