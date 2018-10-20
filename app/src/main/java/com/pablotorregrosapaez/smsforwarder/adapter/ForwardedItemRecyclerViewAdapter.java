package com.pablotorregrosapaez.smsforwarder.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.pablotorregrosapaez.smsforwarder.R;
import com.pablotorregrosapaez.smsforwarder.fragment.ForwardedItemFragment.OnListFragmentInteractionListener;
import com.pablotorregrosapaez.smsforwarder.model.Message;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class ForwardedItemRecyclerViewAdapter extends RecyclerView.Adapter<ForwardedItemRecyclerViewAdapter.ViewHolder> {

    private final List<Message> messages;
    private final OnListFragmentInteractionListener mListener;

    public ForwardedItemRecyclerViewAdapter(OnListFragmentInteractionListener listener) {
        messages = new ArrayList<>();
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_forwardeditem, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = messages.get(position);
        holder.mSenderNumberView.setText(String.valueOf(messages.get(position).getSender()));
        holder.mContentView.setText(shortenContent(messages.get(position).getContent()));
        holder.mForwardedCheck.setChecked(messages.get(position).getForwardedTo() != null);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    private String shortenContent(String content) {
        String shrinkedContent = content.replaceAll("\n", "");
        if (shrinkedContent.length() > 30) {
            shrinkedContent = shrinkedContent.substring(0, 30) + "...";
        }
        return shrinkedContent;
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setData(List<Message> newMessages) {
        PostDiffCallback postDiffCallback = new PostDiffCallback(messages, newMessages);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(postDiffCallback);

        messages.clear();
        messages.addAll(newMessages);
        messages.sort((o1, o2) -> o1.getReceivedAt() < o2.getReceivedAt() ? 1 : -1);
        diffResult.dispatchUpdatesTo(this);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mSenderNumberView;
        public final TextView mContentView;
        public final CheckBox mForwardedCheck;
        public Message mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mSenderNumberView = (TextView) view.findViewById(R.id.sender_number);
            mContentView = (TextView) view.findViewById(R.id.content);
            mForwardedCheck = (CheckBox) view.findViewById(R.id.forwarded_check);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * Calculates the diff between the existing messages and the new ones, in order to only
     * refresh the new ones in the UI.
     */
    class PostDiffCallback extends DiffUtil.Callback {
        private final List<Message> oldMessages, newMessages;

        public PostDiffCallback(List<Message> oldMessages, List<Message> newMessages) {
            this.oldMessages = oldMessages;
            this.newMessages = newMessages;
        }

        @Override
        public int getOldListSize() {
            return oldMessages.size();
        }

        @Override
        public int getNewListSize() {
            return newMessages.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMessages.get(oldItemPosition).getId() == newMessages.get(newItemPosition).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldMessages.get(oldItemPosition).equals(newMessages.get(newItemPosition));
        }
    }
}
