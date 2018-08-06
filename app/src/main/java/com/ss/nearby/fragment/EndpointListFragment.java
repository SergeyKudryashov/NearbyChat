package com.ss.nearby.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.nearby.Nearby;
import com.ss.nearby.R;
import com.ss.nearby.adapter.EndpointListAdapter;
import com.ss.nearby.model.Endpoint;

public class EndpointListFragment extends Fragment {

    private static final String TAG = "NearbyChat";


    private EndpointListAdapter mEndpointListAdapter;

    private EndpointListAdapter.OnItemClickListener mItemClickListener;

    public EndpointListFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_endpoint_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }

    private void init(View view) {
        mEndpointListAdapter = new EndpointListAdapter();
        mEndpointListAdapter.setItemClickListener(new EndpointListAdapter.OnItemClickListener() {
            @Override
            public void onClick(Endpoint endpoint) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(endpoint);
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mEndpointListAdapter);
    }

    public void setItemClickListener(EndpointListAdapter.OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public void addEndpoint(Endpoint endpoint) {
        mEndpointListAdapter.addEndpoint(endpoint);
    }

    public void removeEndpointById(String id) {
        int position = mEndpointListAdapter.getEndpointPositionById(id);
        mEndpointListAdapter.removeEndpointByPosition(position);
    }
}
