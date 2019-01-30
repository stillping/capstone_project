package uk.me.desiderio.shiftt.ui.tweetlist;

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
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment to show list of tweets.
 */

public class TweetListFragment extends Fragment {

    ViewModelFactory viewModelFactory;

    private TweetListViewModel viewModel;

    private TextView textView;

    public static TweetListFragment newInstance() {
        return new TweetListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.tweet_list_fragment, container, false);

        textView = rootView.findViewById(R.id.tweet_message);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TweetListViewModel.class);

        viewModel.getMessage().observe(this, message -> {
            textView.setText(message);
        });

        viewModel.getMessage().setValue("Hello World, Winter is coming [TWEET]");
    }

}
