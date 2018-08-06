package com.ss.nearby.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ss.nearby.R;
import com.ss.nearby.model.Endpoint;

import java.util.ArrayList;
import java.util.List;

public class EndpointListAdapter extends RecyclerView.Adapter<EndpointListAdapter.EndpointViewHolder> {

    private OnItemClickListener mItemClickListener;

    private List<Endpoint> mList = new ArrayList<>();

    @NonNull
    @Override
    public EndpointViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.endpoint_item_view, parent, false);
        return new EndpointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EndpointViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void addEndpoint(Endpoint endpoint) {
        mList.add(endpoint);
        notifyItemInserted(mList.size() - 1);
    }

    public void removeEndpointByPosition(int position) {
        if (position == -1)
            return;

        mList.remove(position);
        notifyItemRemoved(position);
    }

    public int getEndpointPositionById(String id) {
        int position = -1;
        for (int i = 0; i < mList.size(); i++) {
            if (mList.get(i).getId().equals(id)) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        mItemClickListener = itemClickListener;
    }

    public interface OnItemClickListener {
        void onClick(Endpoint endpoint);
    }

    class EndpointViewHolder extends RecyclerView.ViewHolder {

        private TextView mEndpointNameTextView;
        private TextView mEndpointServiceIdTextView;

        EndpointViewHolder(View itemView) {
            super(itemView);

            mEndpointNameTextView = itemView.findViewById(R.id.endpoint_name_text_view);
            mEndpointServiceIdTextView = itemView.findViewById(R.id.endpoint_service_id_text_view);
        }

        void bind(final Endpoint endpoint) {
            mEndpointNameTextView.setText(endpoint.getName());
            mEndpointServiceIdTextView.setText(endpoint.getId());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mItemClickListener != null) {
                        mItemClickListener.onClick(endpoint);
                    }
                }
            });
        }
    }
}
