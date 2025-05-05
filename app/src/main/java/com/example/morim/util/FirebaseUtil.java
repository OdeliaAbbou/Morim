package com.example.morim.util;

import static com.example.morim.database.remote.FirebaseUserManager.CURRENT_USER_TYPE_KEY;

import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.morim.database.OnDataCallback;
import com.example.morim.model.BaseDocument;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {

    private final SharedPreferences sp;

    public FirebaseUtil(SharedPreferences sharedPreferences) {
        this.sp = sharedPreferences;
    }

    public <T extends BaseDocument> ListenerRegistration listenDocs(
            String refPath,
            String editKey,
            Class<T> type,
            OnDataCallback<List<T>> callback
    ) {
        return listenDocs(refPath, editKey, type, null, callback);
    }

    public <T extends BaseDocument> Task<Void> insertDoc(String collection, T something) {
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection(collection)
                .document();
        something.setId(ref.getId());
        return ref.set(something);
    }

    public <T extends BaseDocument> Task<Void> insertDoc(String collection, String id, T something) {
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection(collection)
                .document(id);
        Log.d("insertDoc", collection + "/" + id);
        something.setId(ref.getId());
        return ref.set(something);
    }

    public <T extends BaseDocument> Task<Void> insertDoc(String collection, String subCollection, String userId, T something) {
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection(collection)
                .document(userId)
                .collection(subCollection)
                .document();
        something.setId(ref.getId());
        return ref.set(something);
    }
    public <T extends BaseDocument> Task<Void> insertDocDesignatedId(String collection, String subCollection, String userId, String docId, T something) {
        DocumentReference ref = FirebaseFirestore.getInstance()
                .collection(collection)
                .document(userId)
                .collection(subCollection)
                .document(docId);
        something.setId(docId);
        return ref.set(something);
    }

    public <T extends BaseDocument> Task<Void> updateDoc(String collection, String docId, T something) {
        return FirebaseFirestore.getInstance()
                .collection(collection)
                .document(docId)
                .set(something);
    }

    public void uploadImage(String pathRef, Uri uri, OnDataCallback<String> callback) {
        StorageReference ref = FirebaseStorage.getInstance().getReference(pathRef);
        ref.putFile(uri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl()
                        .addOnSuccessListener(resultUri -> callback.onData(resultUri.toString()))
                        .addOnFailureListener(callback::onException))
                .addOnFailureListener(callback::onException);
    }

    public Task<Void> deleteDoc(String collection, String docId) {
        return FirebaseFirestore.getInstance()
                .collection(collection)
                .document(docId)
                .delete();
    }

    public Task<Void> deleteDoc(String collection, String subCollection, String userId, String docId) {
        return FirebaseFirestore.getInstance()
                .collection(collection)
                .document(userId)
                .collection(subCollection)
                .document(docId)
                .delete();
    }

    public <T extends BaseDocument> void getDoc(String collection, String id, OnDataCallback<T> callback, Class<T> type) {
        FirebaseFirestore.getInstance()
                .collection(collection)
                .document(id)
                .get()
                .addOnSuccessListener(dataSnapshot -> callback.onData(dataSnapshot.toObject(type)))
                .addOnFailureListener(callback::onException);
    }

    public <T extends BaseDocument> void getNestedDoc(String collection, String id, String subCollection, String id2, OnDataCallback<T> callback, Class<T> type) {
        FirebaseFirestore.getInstance()
                .collection(collection)
                .document(id)
                .collection(subCollection)
                .document(id2)
                .get()
                .addOnSuccessListener(dataSnapshot -> callback.onData(dataSnapshot.toObject(type)))
                .addOnFailureListener(callback::onException);
    }

    public <T extends BaseDocument> void getDocs(String pathRef,
                                                 OnDataCallback<List<T>> callback,
                                                 Class<T> type) {
        getDocs(pathRef, callback, null, type);
    }

    public <T extends BaseDocument> void getDocs(String collection, OnDataCallback<List<T>> callback, @Nullable Query baseQuery, Class<T> type) {

        Query q = baseQuery == null ? FirebaseFirestore.getInstance().collection(collection) : baseQuery;
        q.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.d("FirebaseQuery", "Query completed.");
                        Log.d("FirebaseQuery", "Task is successful.");
                        List<T> data = new ArrayList<>();
                        for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                            if (snap.get("id") == null) continue;
                            data.add(snap.toObject(type));
                        }
                        callback.onData(data);
                    }
                })
                .addOnFailureListener(callback::onException);
    }

    public <T extends BaseDocument> void getDocs(String collection, String subCollection, String userId, OnDataCallback<List<T>> callback, Class<T> type) {
        FirebaseFirestore.getInstance()
                .collection(collection)
                .document(userId)
                .collection(subCollection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<T> data = new ArrayList<>();
                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                        if (snap.get("id") == null) continue;
                        data.add(snap.toObject(type));
                    }
                    callback.onData(data);
                })
                .addOnFailureListener(callback::onException);
    }

    public <T extends BaseDocument> ListenerRegistration listenDocs(
            String collection,
            String editKey,
            Class<T> type,
            @Nullable Query initialQuery,
            OnDataCallback<List<T>> callback
    ) {

        final long lastUpdateTime = sp.getLong(editKey, System.currentTimeMillis() - (10 * 60 * 1000));
        CollectionReference ref = FirebaseFirestore.getInstance().collection(collection);
        Query query;
        if (initialQuery != null) {
            query = initialQuery;
        } else {
            query = ref;
        }

        return query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onException(error);
                    return;
                }
                if (value == null) {

                    callback.onException(new Exception("Invalid document collection"));
                    return;
                }
                List<T> data = new ArrayList<>();
                long maxUpdateTime = lastUpdateTime; // Keep track of the maximum update time in this fetch

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    try {
                        if (snapshot.get("id") == null) continue;
                        T teacher = snapshot.toObject(type);


                        if (teacher == null) continue;
                        data.add(teacher);
                        Long updatedAt = snapshot.getLong("updatedAt");
                        if (updatedAt != null && updatedAt > maxUpdateTime) {
                            maxUpdateTime = updatedAt;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sp.edit()
                        .putLong(editKey, maxUpdateTime)
                        .apply();
                callback.onData(data);
            }
        });
    }

    public ListenerRegistration listenUsers(
            OnDataCallback<List<User>> callback
    ) {
        String collection = "users";
        final long lastUpdateTime = sp.getLong(collection, System.currentTimeMillis() - (10 * 60 * 1000));
        CollectionReference ref = FirebaseFirestore.getInstance().collection(collection);
        return ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onException(error);
                    return;
                }
                if (value == null) {

                    callback.onException(new Exception("Invalid document collection"));
                    return;
                }
                List<User> data = new ArrayList<>();
                long maxUpdateTime = lastUpdateTime; // Keep track of the maximum update time in this fetch

                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    try {
                        User user;
                        if (snapshot.get("id") == null) continue;
                        if (snapshot.get("teacher") == null) continue;
                        if (snapshot.get("teacher").equals(true)) {
                            user = snapshot.toObject(Teacher.class);
                        } else {
                            user = snapshot.toObject(Student.class);
                        }
                        data.add(user);
                        Long updatedAt = snapshot.getLong("updatedAt");
                        if (updatedAt != null && updatedAt > maxUpdateTime) {
                            maxUpdateTime = updatedAt;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                sp.edit()
                        .putLong(collection, maxUpdateTime)
                        .apply();
                callback.onData(data);
            }
        });
    }

    public void getUsers(OnDataCallback<List<User>> callback) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<User> data = new ArrayList<>();
                    for (DocumentSnapshot snap : queryDocumentSnapshots.getDocuments()) {
                        if (snap.get("id") == null) continue;
                        if (snap.get("teacher") == null) continue;
                        if (snap.getBoolean("teacher")) {
                            data.add(snap.toObject(Teacher.class));
                        } else {
                            data.add(snap.toObject(Student.class));
                        }
                    }
                    callback.onData(data);
                })
                .addOnFailureListener(callback::onException);
    }

    public <T extends BaseDocument> ListenerRegistration listenDocs(
            String collection,
            String subCollection,
            String userId,
            OnDataCallback<List<T>> callback,
            Class<T> type
    ) {
        final long lastUpdateTime = sp.getLong(collection + "/" + userId, System.currentTimeMillis() - (10 * 60 * 1000));
        CollectionReference ref = FirebaseFirestore.getInstance()
                .collection(collection)
                .document(userId)
                .collection(subCollection);
        return ref.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onException(error);
                    return;
                }
                if (value == null) {
                    callback.onException(new Exception("Invalid document collection"));
                    return;
                }
                List<T> data = new ArrayList<>();
                long maxUpdateTime = lastUpdateTime;
                for (DocumentSnapshot snapshot : value.getDocuments()) {
                    if (snapshot.get("id") == null) continue;
                    T teacher = snapshot.toObject(type);
                    if (teacher == null) continue;
                    data.add(teacher);
                    Long updatedAt = snapshot.getLong("updatedAt");
                    if (updatedAt != null && updatedAt > maxUpdateTime) {
                        maxUpdateTime = updatedAt;
                    }
                }
                sp.edit()
                        .putLong(collection + "/" + userId, maxUpdateTime)
                        .apply();
                callback.onData(data);
            }
        });
    }


    public <T extends BaseDocument> ListenerRegistration listenDoc(
            String collection,
            String id,
            Class<T> type,
            OnDataCallback<T> callback
    ) {
        String key = collection + "/" + id;
        final long lastUpdateTime = sp.getLong(key, System.currentTimeMillis() - (10 * 60 * 1000));
        DocumentReference ref = FirebaseFirestore.getInstance().collection(collection).document(id);
        return ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onException(error);
                    return;
                }
                if (value == null) {
                    callback.onException(new Exception("Invalid document collection"));
                    return;
                }
                if (value.get("id") == null) {
                    return;
                }
                T some = value.toObject(type);
                Long updatedAt = value.getLong("updatedAt");
                long maxUpdateTime = lastUpdateTime;
                if (updatedAt != null && updatedAt > maxUpdateTime) {
                    maxUpdateTime = updatedAt;
                }
                if (some != null) {
                    sp.edit()
                            .putLong(key, maxUpdateTime)
                            .apply();
                }
                callback.onData(some);
            }
        });
    }

    public ListenerRegistration listenUser(
            String collection,
            String id,
            OnDataCallback<User> callback
    ) {
        String key = collection + "/" + id;
        final long lastUpdateTime = sp.getLong(key, System.currentTimeMillis() - (10 * 60 * 1000));
        DocumentReference ref = FirebaseFirestore.getInstance().collection(collection).document(id);
        return ref.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onException(error);
                    return;
                }
                if (value == null) {
                    callback.onException(new Exception("Invalid document collection"));
                    return;
                }

                Class<? extends User> type;
                if (value.exists() && Boolean.TRUE.equals(value.getBoolean("teacher"))) {
                    type = Teacher.class;
                } else {
                    type = Student.class;
                }
                User some = value.toObject(type);
                Long updatedAt = value.getLong("updatedAt");
                long maxUpdateTime = lastUpdateTime;
                if (updatedAt != null && updatedAt > maxUpdateTime) {
                    maxUpdateTime = updatedAt;
                }

                if (some != null) {
                    if (some.isTeacher()) {
                        sp.edit()
                                .putString(CURRENT_USER_TYPE_KEY, "teachers")
                                .apply();
                    } else {
                        sp.edit()
                                .putString(CURRENT_USER_TYPE_KEY, "students")
                                .apply();
                    }
                    sp.edit()
                            .putLong(key, maxUpdateTime)
                            .apply();
                }
                callback.onData(some);
            }
        });
    }

    public void getUser(String usersCollection, String uid, OnDataCallback<User> callback) {
        FirebaseFirestore.getInstance()
                .collection(usersCollection)
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        if (documentSnapshot.getBoolean("teacher")) {
                            callback.onData(documentSnapshot.toObject(Teacher.class));
                        } else {
                            callback.onData(documentSnapshot.toObject(Student.class));
                        }
                    } else {
                        callback.onData(null);
                    }
                })
                .addOnFailureListener(callback::onException);
    }
}
