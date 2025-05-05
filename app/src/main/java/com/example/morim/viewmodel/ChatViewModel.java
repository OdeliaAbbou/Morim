package com.example.morim.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.common.ChatRepository;
import com.example.morim.database.common.UserRepository;
import com.example.morim.model.Chat;
import com.example.morim.model.Message;
import com.example.morim.model.MyChatsData;
import com.example.morim.model.SingleChatData;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;


// @TODO: Listen to specific chat messages upon entering ChatActivity
@HiltViewModel
public class ChatViewModel extends ViewModel {
    private final ChatRepository repository;
    private final UserRepository userRepository;
    private final FirebaseListener<List<User>> users;

    private FirebaseListener<List<Chat>> myChats;

    private final MediatorLiveData<MyChatsData> myChatsDataMediatorLiveData = new MediatorLiveData<>(new MyChatsData());


    private FirebaseListener<Chat> activeChat;
    private FirebaseListener<Teacher> activeChatOtherUser;
    private MediatorLiveData<SingleChatData> activeChatMediator = new MediatorLiveData<>(new SingleChatData());

    private ListenerRegistration chatsListener;

    @Inject
    public ChatViewModel(ChatRepository chatRepository, UserRepository userRepository) {
        this.repository = chatRepository;
        this.userRepository = userRepository;
        users = userRepository.listenUsers();
        chatsListener = chatRepository.listenMyChats(new OnDataCallback<List<Chat>>() {
            @Override
            public void onData(List<Chat> value) {
                MyChatsData current = myChatsDataMediatorLiveData.getValue();
                current.setMyMeetings(value);
                Log.d("ChatViewModel", "onDataValue: " + value.size());
                myChatsDataMediatorLiveData.postValue(current);
            }

            @Override
            public void onException(Exception e) {

            }
        });
        myChatsDataMediatorLiveData.addSource(users.get(), new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                MyChatsData combinedData = myChatsDataMediatorLiveData.getValue();
                combinedData.setUsers(users);
                myChatsDataMediatorLiveData.postValue(combinedData);
            }
        });
    }

    public LiveData<List<Chat>> getMyChats() {
        return myChats.get();
    }

    public LiveData<SingleChatData> getActiveChat() {
        return activeChatMediator;
    }


    public void getUsers(OnDataCallback<List<User>> callback) {
        userRepository.getUsers(callback);
    }
    public void listenChat(String chatId, String otherUserId, LiveData<User> currentUserLiveData) {
        activeChat = repository.listenChat(chatId);
        activeChatOtherUser = userRepository.listenTeacher(otherUserId);
        activeChatMediator.postValue(new SingleChatData());
        activeChatMediator.addSource(activeChat.get(), new Observer<Chat>() {
            @Override
            public void onChanged(Chat chat) {
                SingleChatData current = activeChatMediator.getValue();
                current.setChat(chat);
                activeChatMediator.postValue(current);
            }
        });
        activeChatMediator.addSource(activeChatOtherUser.get(), new Observer<Teacher>() {
            @Override
            public void onChanged(Teacher otherUser) {
                SingleChatData current = activeChatMediator.getValue();
                current.setUser(otherUser);
                activeChatMediator.postValue(current);
            }
        });
        activeChatMediator.addSource(currentUserLiveData, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                SingleChatData current = activeChatMediator.getValue();
                current.setCurrentUser(user);
                activeChatMediator.postValue(current);
            }
        });
    }


    // @TODO: Make this not recreate the chat
    public void createOrGetChat(String studentId, String teacherId, OnDataCallback<Chat> callback) {

        repository.getMyChats(new OnDataCallback<List<Chat>>() {
            @Override
            public void onData(List<Chat> allChats) {

                if (allChats == null || allChats.isEmpty()) {
                    Log.d("createOrGetChat", "Creating new chat");
                    createChat(studentId, teacherId)
                            .addOnSuccessListener(callback::onData);
                    return;
                }
                if (teacherId.equals(FirebaseAuth.getInstance().getUid())) {
                    Log.d("createOrGetChat", "Teacher is the current user");
                    for (Chat c : allChats) {
                        if (c.getStudentId().equals(studentId)) {
                            callback.onData(c);
                            Log.d("createOrGetChat", "Chat was found 2 (:");
                            return;
                        }
                    }

                }


                Chat foundChat = null;
                for (Chat c : allChats) {
                    if (c.getId().equals(studentId + "_" + teacherId)
                            || c.getId().equals(teacherId + "_" + studentId)) {
                        foundChat = c;
                        Log.d("createOrGetChat", "Chat was found (:");
                        break;
                    }
                }
                if (foundChat == null) {
                    createChat(studentId, teacherId)
                            .addOnSuccessListener(callback::onData);
                    return;
                }
                callback.onData(foundChat);
            }

            @Override
            public void onException(Exception e) {

            }
        });


    }

    public LiveData<MyChatsData> getMyChatsData() {
        return myChatsDataMediatorLiveData;
    }

    public Task<Chat> createChat(String studentId, String teacherId) {
        return repository.createChat(new Chat(studentId, teacherId, new ArrayList<>(), System.currentTimeMillis()));
    }

    public Task<Void> sendMessage(Chat c, String senderId, String content) {
        Message m = new Message(senderId, content, System.currentTimeMillis());
        return repository.sendMessage(m, c);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (myChats != null)
            myChats.stopListening();
        if (users != null)
            users.stopListening();
        if (activeChat != null)
            activeChat.stopListening();
        if (activeChatOtherUser != null)
            activeChatOtherUser.stopListening();
        if (chatsListener != null)
            chatsListener.remove();
    }

    public void getChat(String studentId, String teacherId, OnDataCallback<Chat> onDataCallback) {
        repository.getMyChats(new OnDataCallback<List<Chat>>() {
            @Override
            public void onData(List<Chat> value) {
                for (Chat c : value) {
                    if (c.getStudentId().equals(studentId) && c.getTeacherId().equals(teacherId)) {
                        onDataCallback.onData(c);
                        return;
                    }
                }
            }

            @Override
            public void onException(Exception e) {
                onDataCallback.onException(e);
            }
        });
    }

}
