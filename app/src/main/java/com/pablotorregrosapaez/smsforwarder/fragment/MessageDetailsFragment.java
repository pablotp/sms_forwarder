package com.pablotorregrosapaez.smsforwarder.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pablotorregrosapaez.smsforwarder.R;
import com.pablotorregrosapaez.smsforwarder.SmsSender;
import com.pablotorregrosapaez.smsforwarder.config.AppDatabase;
import com.pablotorregrosapaez.smsforwarder.factory.AppDatabaseFactory;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MessageDetailsFragment extends Fragment {
    private TextView idView;
    private TextView contentView;
    private TextView senderView;
    private TextView simIdView;
    private TextView receivedAtView;
    private TextView forwardedToView;
    private TextView forwardedAtView;
    private Button forwardNowButton;

    public static MessageDetailsFragment newInstance() {
        return new MessageDetailsFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_details_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        idView = this.getActivity().findViewById(R.id.message_id);
        contentView = this.getActivity().findViewById(R.id.message_content);
        senderView = this.getActivity().findViewById(R.id.message_sender);
        simIdView = this.getActivity().findViewById(R.id.message_sim_id);
        receivedAtView = this.getActivity().findViewById(R.id.message_received_at);
        forwardedAtView = this.getActivity().findViewById(R.id.message_forwarded_at);
        forwardedToView = this.getActivity().findViewById(R.id.message_forwarded_to);
        forwardNowButton = this.getActivity().findViewById(R.id.message_forward_now);

        long messageId = this.getActivity().getIntent().getLongExtra("messageId", -1);

        AppDatabase db = AppDatabaseFactory.build(this.getActivity(), AppDatabaseFactory.MESSAGES_DB_NAME);
        db.messageDao().findById(messageId).observe(this, message -> {
            displayMessage(message);
        });
    }

    private void displayMessage(Message message) {
        idView.setText(message.getId().toString());
        contentView.setText(message.getContent());
        senderView.setText(message.getSender());
        simIdView.setText(message.getSimId().toString());
        receivedAtView.setText(formatDate(message.getReceivedAt()));
        forwardedAtView.setText(formatDate(message.getForwardedAt()));
        forwardedToView.setText(message.getForwardedTo());
        forwardNowButton.setOnClickListener(v -> new ForwardAsyncTask(getContext()).execute(message));
    }

    private String formatDate(Long timeInMillis) {
        if (timeInMillis == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(timeInMillis);
        return sdf.format(resultdate);
    }

    private class ForwardAsyncTask extends AsyncTask<Message, Message, Void> {
        private AppDatabase db;
        private Context context;
        private SmsSender smsSender;

        public ForwardAsyncTask(Context context) {
            this.context = context;
            smsSender = new SmsSender(context);
            db = AppDatabaseFactory.build(context, AppDatabaseFactory.MESSAGES_DB_NAME);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            Message message = messages[0];
            smsSender.forwardMessage(message);
            return null;
        }
    }
}
