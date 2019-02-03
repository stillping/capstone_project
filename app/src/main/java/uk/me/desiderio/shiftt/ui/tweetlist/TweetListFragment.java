package uk.me.desiderio.shiftt.ui.tweetlist;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.FixedTweetTimeline;
import com.twitter.sdk.android.tweetui.Timeline;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;

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
import uk.me.desiderio.shiftt.data.repository.Resource;
import uk.me.desiderio.shiftt.viewmodel.ViewModelFactory;

/**
 * Fragment to show list of tweets.
 */

public class TweetListFragment extends Fragment {

    public static final String ARGS_PLACE_FULL_NAME_KEY = "tweet_list_fragment_place_name_key";

    @Inject
    ViewModelFactory viewModelFactory;

    private RecyclerView recyclerView;
    private View emptyView;

    public static TweetListFragment newInstance(Bundle args) {
        TweetListFragment frag = new TweetListFragment();
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        View rootView = inflater
                .inflate(R.layout.tweet_list_fragment, container, false);

        emptyView = rootView.findViewById(R.id.tweets_empty_view);
        shouldShowEmptyView(false);

        recyclerView = rootView.findViewById(R.id.tweets_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TweetListViewModel viewModel = ViewModelProviders.
                of(this, viewModelFactory).get(TweetListViewModel.class);
        viewModel.getTweetOnPlace(getPlaceFullNameArgsKey()).observe(this, this::processResource);
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    private void processResource(@NonNull Resource<List<Tweet>> resource) {
        swapViewData(resource.data);
        showEmptyView(resource.data, resource.status);
        updateGlobalViewStateOnResource(resource);
    }

    private void swapViewData(List<Tweet> tweetList) {
        if (tweetList != null) {
            FixedTweetTimeline timeline = buildTimeline(tweetList);
            swapAdapter(timeline);
        }
    }

    private FixedTweetTimeline buildTimeline(List<Tweet> tweetList) {
        return new FixedTweetTimeline.Builder()
                .setTweets(tweetList)
                .build();
    }


    private void swapAdapter(Timeline<Tweet> timeline) {
        TweetTimelineRecyclerViewAdapter adapter =
                new TweetTimelineRecyclerViewAdapter.Builder(getContext())
                        .setTimeline(timeline)
                        .setViewStyle(R.style.tw__TweetLightStyle)
                        .build();

        recyclerView.setAdapter(adapter);
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

    @Nullable
    private String getPlaceFullNameArgsKey() {
        if (getArguments() != null) {
            return getArguments().getString(ARGS_PLACE_FULL_NAME_KEY);
        } else {
            return null;
        }
    }

    private void updateGlobalViewStateOnResource(@NonNull Resource<List<Tweet>> resource) {
        ((NetworkStateResourceActivity) getActivity()).updateViewStateOnResource(resource, null);
    }

}

