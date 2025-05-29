package com.example.morim.database.remote;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.morim.database.OnDataCallback;
import com.example.morim.model.BaseDocument;
import com.example.morim.model.Chat;
import com.example.morim.model.Meeting;
import com.example.morim.model.Message;
import com.example.morim.util.FirebaseUtil;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FirebaseChatManager {

    private final SharedPreferences sp;
    public final static String CURRENT_USER_TYPE_KEY = "currentUserType";

    public final String CHATS_COLLECTION = "chats";
    private final FirebaseUtil firebaseUtil;

    public FirebaseChatManager(FirebaseUtil firebaseUtil, SharedPreferences sharedPreferences) {
        this.firebaseUtil = firebaseUtil;
        this.sp = sharedPreferences;
    }


    public Task<Void> sendMessage(Message m, Chat c) {
        c.getMessages().add(m);
        c.setUpdatedAt(System.currentTimeMillis());
        return firebaseUtil.updateDoc(
                CHATS_COLLECTION,
                c.getId(),
                c);
    }

    public Task<Void> createChat(Chat c) {
        Log.d("createChat", c.toString());
        return firebaseUtil.insertDoc(
                CHATS_COLLECTION,
                c.getId(),
                c);
    }


    public ListenerRegistration listenChat(String chatId, OnDataCallback<Chat> callback) {
        return firebaseUtil.listenDoc(
                CHATS_COLLECTION,
                chatId,
                Chat.class,
                new OnDataCallback<Chat>() {
                    @Override
                    public void onData(Chat value) {
                        callback.onData(value);
                    }

                    @Override
                    public void onException(Exception e) {
                        callback.onException(e);
                    }
                });
    }


    public void getMyChats(OnDataCallback<List<Chat>> callback) {
        firebaseUtil.getDocs("chats", callback, Chat.class);
    }

    public ListenerRegistration listenChats(OnDataCallback<List<Chat>> callback) {
        boolean isTeacher = "teacher".equals(sp.getString(CURRENT_USER_TYPE_KEY, "teacher"));
        String uid = FirebaseAuth.getInstance().getUid();

        return firebaseUtil.listenDocs(
                CHATS_COLLECTION,
                CHATS_COLLECTION,
                Chat.class,
                new OnDataCallback<List<Chat>>() {
                    @Override
                    public void onData(List<Chat> value) {
                        for(Chat c : value) {
                            Log.d("listenChats", c.toString());
                        }
                        callback.onData(new ArrayList<>(value.stream().filter(Chat::isCurrentUsers).collect(Collectors.toSet())));
                    }

                    @Override
                    public void onException(Exception e) {
                        callback.onException(e);
                    }
                });
    }


    public Task<Void> updateChat(Chat c) {
        return firebaseUtil.updateDoc(CHATS_COLLECTION, c.getId(), c);
    }

}
