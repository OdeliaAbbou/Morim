package com.example.morim.adapter;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.R;
import com.example.morim.model.Message;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message);
    }

    public void update(List<Message> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private Map<String, User> users = new HashMap<>();

    public void setUsers(Map<String, User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        private final TextView messageText;
        private final TextView messageTimestamp;
        private final TextView nameTv;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            nameTv = itemView.findViewById(R.id.messageSender);
            messageTimestamp = itemView.findViewById(R.id.messageTimestamp);
        }

        public void bind(Message message) {
            User sender = users.get(message.getSenderId());

            if (sender != null) {
                if (sender.isTeacher()) {
                    messageText.setBackgroundResource(R.drawable.bg_message_sent_teacher);
                } else {
                    messageText.setBackgroundResource(R.drawable.bg_message_sent);
                }
                // change the whole layout direction if its not current user sending
                if (!sender.getId().equals(FirebaseAuth.getInstance().getUid())) {
                    itemView.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                } else
                    itemView.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

                nameTv.setText(sender.getFullName());
            }

            messageText.setText(message.getContent());

            // Formatage du timestamp
            String time = DateFormat.format("hh:mm a", message.getTimestamp()).toString();
            messageTimestamp.setText(time);
        }
    }
}
