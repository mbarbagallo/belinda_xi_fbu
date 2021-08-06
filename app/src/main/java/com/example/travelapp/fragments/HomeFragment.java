package com.example.travelapp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.travelapp.Itinerary;
import com.example.travelapp.ItineraryAdapter;
import com.example.travelapp.MainActivity;
import com.example.travelapp.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements ItineraryAdapter.DetailsLongClickListener {

    private RecyclerView rvItineraries;
    protected List<Itinerary> allItineraries;
    protected ItineraryAdapter adapter;
    private PullToRefreshView pullToRefreshView;
    public static final String TAG = "HomeFragment";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ((MainActivity) getActivity())
                .getSupportActionBar().setTitle("Travel Feed");
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rvItineraries = view.findViewById(R.id.rvItineraries);

        allItineraries = new ArrayList<>();
        adapter = new ItineraryAdapter(getContext(), allItineraries, this);
        rvItineraries.setAdapter(adapter);
        rvItineraries.setLayoutManager(new LinearLayoutManager(getContext()));
        queryItineraries();
        pullToRefreshView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefreshView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fetchTimelineAsync();
                        pullToRefreshView.setRefreshing(false);
                    }
                }, 1000);
            }
        });
    }

    private void fetchTimelineAsync() {
        // clear out old itineraries
        adapter.clear();
        // query new itineraries
        queryItineraries();
    }

    private void queryItineraries() {
        ParseQuery<Itinerary> query = ParseQuery.getQuery(Itinerary.class);
        query.include(Itinerary.KEY_USER);
        query.setLimit(20);
        query.addDescendingOrder("createdAt");
        query.findInBackground(new FindCallback<Itinerary>() {
            @Override
            public void done(List<Itinerary> itineraries, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                allItineraries.addAll(itineraries);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void OnLongClick(int position) {
        if (position != RecyclerView.NO_POSITION) {
            Fragment fragment = MainActivity.getCurrentFragment();
            if (fragment instanceof HomeFragment) {
                adapter.queryDetail(position);
            } else {
                adapter.queryMyDetail(position);
            }
        }
    }

    static class SwipeToDelete extends ItemTouchHelper.SimpleCallback {
        private ItineraryAdapter adapter;

        public SwipeToDelete(ItineraryAdapter adapter) {
            // first parameter is 0 since we don't care about swiping up/down
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            this.adapter = adapter;
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            // do nothing on move, but required to override method
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            adapter.deleteItem(position);
        }
    }
}