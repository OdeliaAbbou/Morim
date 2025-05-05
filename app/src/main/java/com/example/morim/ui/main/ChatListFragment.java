package com.example.morim.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.example.morim.ChatActivity;
import com.example.morim.adapter.ChatsAdapter;
import com.example.morim.databinding.FragmentChatListBinding;
import com.example.morim.model.Chat;
import com.example.morim.model.MyChatsData;
import com.example.morim.model.User;
import com.example.morim.ui.BaseFragment;
import com.example.morim.viewmodel.ChatViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatListFragment extends BaseFragment {
    private FragmentChatListBinding binding;
    private ChatViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatListBinding.inflate(
                inflater,
                container,
                false
        );
        return binding.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = activityScopedViewModel(ChatViewModel.class);

        viewModel.getMyChatsData().observe(getViewLifecycleOwner(), new Observer<MyChatsData>() {
            @Override
            public void onChanged(MyChatsData myChatsData) {
                if (myChatsData.allResourcesAvailable()) {
                    binding.rvChats.setAdapter(new ChatsAdapter(myChatsData, new ChatsAdapter.ChatItemAction() {
                        @Override
                        public void openChat(Chat c, User student, User teacher) {
                            Intent toChatActivity = new Intent(getActivity(), ChatActivity.class);
                            toChatActivity.putExtra("TEACHER_ID", teacher.getId());
                            toChatActivity.putExtra("STUDENT_ID", student.getId());
                            startActivity(toChatActivity);
                        }
                    }));
                    viewModel.getMyChatsData().removeObserver(this);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
