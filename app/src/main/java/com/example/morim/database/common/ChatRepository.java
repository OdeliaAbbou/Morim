package com.example.morim.database.common;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.local.ChatDao;
import com.example.morim.database.local.MeetingDao;
import com.example.morim.database.remote.FirebaseChatManager;
import com.example.morim.database.remote.FirebaseMeetingsManager;
import com.example.morim.model.Chat;
import com.example.morim.model.Meeting;
import com.example.morim.model.Message;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ChatRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final ScheduledThreadPoolExecutor executor;
    private final FirebaseChatManager remoteDb;
    private final ChatDao chatDao;

    public ChatRepository(
            ScheduledThreadPoolExecutor executor,
            ChatDao chatDao,
            FirebaseChatManager remoteDb
    ) {
        this.executor = executor;
        this.chatDao = chatDao;
        this.remoteDb = remoteDb;
    }

    public Task<Void> sendMessage(Message m, Chat c) {
        return remoteDb.sendMessage(m, c);
    }

    public Task<Chat> createChat(Chat c) {
        return remoteDb.createChat(c).addOnSuccessListener((v) -> {
            executor.execute(() -> {
                chatDao.insert(c);
            });
        }).continueWith(task -> c);
    }


    public FirebaseListener<Chat> listenChat(String chatId) {
        return new FirebaseListener<>(
                chatDao.listenChat(chatId),
                remoteDb.listenChat(chatId, new OnDataCallback<Chat>() {
                    @Override
                    public void onData(Chat value) {
                        executor.execute(() -> {
                            chatDao.insert(value);
                        });
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                }));
    }

    public ListenerRegistration listenMyChats(OnDataCallback<List<Chat>> chatsCallback) {
        return remoteDb.listenChats(new OnDataCallback<List<Chat>>() {
            @Override
            public void onData(List<Chat> value) {
                chatsCallback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                chatsCallback.onException(e);
            }
        });
    }

    public void getMyChats(OnDataCallback<List<Chat>> chats) {
        remoteDb.getMyChats(chats);
    }
    public Task<Void> updateChat(Chat chat) {
        return remoteDb.updateChat(chat);
    }


}
