package com.ss.nearby.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.ss.nearby.R;


public class ChatFragment extends Fragment {

    public static final String ARG_NAME = "chat.name";

    private OnSendButtonClick mListener;

    private String mOpponentName;

    public static ChatFragment newInstance(String name) {
        Bundle bundle = new Bundle();
        bundle.putString(ARG_NAME, name);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mOpponentName = bundle.getString(ARG_NAME);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(mOpponentName);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        init(view);
    }


    private void init(View view) {
        final EditText messageEditText = view.findViewById(R.id.chat_message_edit_text);

        Button sendMessageButton = view.findViewById(R.id.chat_message_send_button);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener == null)
                    return;

                String text = messageEditText.getText().toString();
                if (!text.isEmpty()) {
                    mListener.onSend(text);
                }
            }
        });

        RecyclerView recyclerView = view.findViewById(R.id.chat_messages_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

    }

    public void setListener(OnSendButtonClick listener) {
        mListener = listener;
    }

    public interface OnSendButtonClick {
        void onSend(String text);
    }
}
