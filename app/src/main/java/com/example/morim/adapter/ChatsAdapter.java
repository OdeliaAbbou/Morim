package com.example.morim.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.databinding.ChatItemBinding;
import com.example.morim.model.Chat;
import com.example.morim.model.MyChatsData;
import com.example.morim.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.Comparator;
import java.util.Objects;


public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ChatViewHolder> {


    public interface ChatItemAction {
        void openChat(Chat c, User student, User teacher);
    }

    private MyChatsData chatData;

    private ChatItemAction actions;

    public ChatsAdapter(MyChatsData chatData, ChatItemAction actions) {
        this.chatData = chatData;
        this.actions = actions;
    }


    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChatViewHolder(
                ChatItemBinding.inflate(LayoutInflater.from(parent.getContext()),
                        parent,
                        false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Chat c = chatData.getMyChats().get(position);
        User teacher = chatData.idToUser().get(c.getTeacherId());
        User student = chatData.idToUser().get(c.getStudentId());
        try {
            assert teacher != null;
            assert student != null;
            holder.bind(c, teacher, student, actions);
        } catch (AssertionError e) {
            Log.d("ChatsAdapter", "Chat users fetch exception, user got null value");
        }
    }

    @Override
    public int getItemCount() {
        return chatData.getMyChats().size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private ChatItemBinding binding;

        public ChatViewHolder(ChatItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void bind(Chat c, User teacher, User student, ChatItemAction actions) {

            // are we the teacher?
            boolean current_is_teacher = Objects.equals(FirebaseAuth.getInstance().getUid(), teacher.getId());

            if (current_is_teacher) {
                // the other user is student
                binding.chatPersonNameTv.setText("Chat with " + student.getFullName());
                Picasso.get().load(
                        student.getImage()
                ).into(binding.chatPersonIv);
            } else {
                // the other user is teacher
                binding.chatPersonNameTv.setText("Chat with " + teacher.getFullName());
                Picasso.get().load(
                        teacher.getImage()
                ).into(binding.chatPersonIv);
            }
            binding.getRoot().setOnClickListener(v -> {
                actions.openChat(c, student, teacher);
            });
        }
    }
}
