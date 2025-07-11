package com.example.morim;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.R;
import com.example.morim.adapter.MessageAdapter;
import com.example.morim.database.OnDataCallback;
import com.example.morim.model.Chat;
import com.example.morim.model.Message;
import com.example.morim.model.SingleChatData;
import com.example.morim.model.User;
import com.example.morim.viewmodel.AuthViewModel;
import com.example.morim.viewmodel.ChatViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatActivity extends BaseActivity {
    private MessageAdapter adapter; // Adaptateur pour les messages
    private RecyclerView recyclerView; // RecyclerView pour afficher les messages
    private ChatViewModel chatViewModel;
    private AuthViewModel authViewModel;
    private Chat currentChat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatViewModel = viewModel(ChatViewModel.class);
        authViewModel = viewModel(AuthViewModel.class);
        String teacherId = getIntent().getStringExtra("TEACHER_ID");
        String studentId = getIntent().getStringExtra("STUDENT_ID");
        Log.d("ChatActivity", "onCreate: " + teacherId + ", " + studentId);
        if (teacherId.equals(FirebaseAuth.getInstance().getUid())) {
            chatViewModel.getChat(studentId, FirebaseAuth.getInstance().getUid(), new OnDataCallback<Chat>() {
                @Override
                public void onData(Chat chat) {
                    Log.d("ChatActivity", "onData: " + chat.getId() + ", " + chat.getMessages().size());
                    chatViewModel.listenChat(chat.getId(), teacherId, authViewModel.getCurrentUser());
                }

                @Override
                public void onException(Exception e) {

                }
            });
        } else {
            chatViewModel.createOrGetChat(FirebaseAuth.getInstance().getUid(), teacherId, new OnDataCallback<Chat>() {
                @Override
                public void onData(Chat chat) {
                    chatViewModel.listenChat(chat.getId(), teacherId, authViewModel.getCurrentUser());
                }

                @Override
                public void onException(Exception e) {

                }
            });
        }

//        chatViewModel.getActiveChat().observe(this, new Observer<SingleChatData>() {
//            @Override
//            public void onChanged(SingleChatData singleChatData) {
//                ChatActivity.this.currentChat = singleChatData.getMyChat();
//                if (singleChatData.allResourcesAvailable()) {
//                    System.out.println(singleChatData.getMyChat().getStudentId() + ", " + singleChatData.getMyChat().getTeacherId() + ", " + singleChatData.getMyChat().getMessages().size());
//                    Log.d("ChatActivity", "onChanged: " + singleChatData.getMyChat().getMessages().size());
//
//
//
//                    adapter.update(singleChatData.getMyChat().getMessages());
//                    recyclerView.scrollToPosition(singleChatData.getMyChat().getMessages().size() - 1);
//                }
//            }
//        });

        chatViewModel.getActiveChat().observe(this, new Observer<SingleChatData>() {
            @Override
            public void onChanged(SingleChatData singleChatData) {
                Chat currentChat = singleChatData.getMyChat();

                // 🔒 Sécurité : éviter le crash si données incomplètes
                if (!singleChatData.allResourcesAvailable() || currentChat == null || currentChat.getMessages() == null)
                    return;

                ChatActivity.this.currentChat = currentChat;

                // ✅ Marquer les messages reçus comme lus
                String currentUserId = FirebaseAuth.getInstance().getUid();
                boolean updated = false;

                for (Message m : currentChat.getMessages()) {
                    if (!m.isRead() && !m.getSenderId().equals(currentUserId)) {
                        m.setRead(true);
                        updated = true;
                    }
                }

                if (updated) {
                    chatViewModel.updateChat(currentChat);
                }

                // ✅ Affichage des messages
                Log.d("ChatActivity", "onChanged: " + currentChat.getMessages().size());
                System.out.println(currentChat.getStudentId() + ", " + currentChat.getTeacherId() + ", " + currentChat.getMessages().size());

                adapter.update(currentChat.getMessages());
                recyclerView.scrollToPosition(currentChat.getMessages().size() - 1);
            }
        });

        chatViewModel.getUsers(new OnDataCallback<List<User>>() {
            @Override
            public void onData(List<User> value) {
                Map<String, User> users = new HashMap<>();
                for (User user : value) {
                    users.put(user.getId(), user);
                }
                adapter.setUsers(users);
            }

            @Override
            public void onException(Exception e) {

            }
        });

        // Initialiser RecyclerView et l'adaptateur
        recyclerView = findViewById(R.id.rvMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MessageAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Initialiser les champs pour l'entrée et l'envoi de messages
        EditText messageInput = findViewById(R.id.etMessage);
        ImageButton sendButton = findViewById(R.id.btnSend);
        sendButton.setOnClickListener(v -> {
            String content = messageInput.getText().toString();
            if (!content.trim().isEmpty() && currentChat != null) {
                chatViewModel.sendMessage(currentChat, FirebaseAuth.getInstance().getUid(), content);
            }
            messageInput.setText("");
        });

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> {
            finish(); // ferme l'activité et retourne au fragment précédent
        });

    }


}
