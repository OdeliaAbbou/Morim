package com.example.morim.database.remote;


import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.morim.database.OnDataCallback;
import com.example.morim.dto.StudentRegisterForm;
import com.example.morim.dto.TeacherRegisterForm;
import com.example.morim.dto.UserLoginForm;
import com.example.morim.dto.UserRegisterForm;
import com.example.morim.model.Comment;
import com.example.morim.model.Meeting;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.util.FirebaseListener;
import com.example.morim.util.FirebaseUtil;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FirebaseUserManager {

    private final SharedPreferences sp;
    public final static String CURRENT_USER_TYPE_KEY = "currentUserType";

    public final String USERS_COLLECTION = "users";
    private final FirebaseUtil firebaseUtil;

    public static final String DEFAULT_IMAGE = "https://upload.wikimedia.org/wikipedia/commons/thumb/6/65/No-Image-Placeholder.svg/660px-No-Image-Placeholder.svg.png?20200912122019";


    public FirebaseUserManager(FirebaseUtil firebaseUtil, SharedPreferences sharedPreferences) {
        this.firebaseUtil = firebaseUtil;
        this.sp = sharedPreferences;
    }

    public void getAllUsers(OnDataCallback<List<User>> callback) {
        firebaseUtil.getDocs(USERS_COLLECTION,
                callback,
                FirebaseFirestore.getInstance()
                        .collection(USERS_COLLECTION),
                User.class);
    }

    public ListenerRegistration listenTeachers(OnDataCallback<List<Teacher>> callback) {
        return firebaseUtil.listenDocs(USERS_COLLECTION, "users/teachers",
                Teacher.class,
                FirebaseFirestore.getInstance()
                        .collection(USERS_COLLECTION),
                new OnDataCallback<List<Teacher>>() {
                    @Override
                    public void onData(List<Teacher> value) {
                        callback.onData(value.stream()
                                .filter(User::isTeacher)
                                .collect(Collectors.toList()));
                    }

                    @Override
                    public void onException(Exception e) {
                        callback.onException(e);
                    }
                });
    }


    public void addComment(String comment,
                           float rating,
                           User t,
                           User s,
                           OnDataCallback<Void> callback
    ) {
        Comment c = new Comment(s.getId(), t.getId(), comment, s.getFullName(), rating);
        t.addComment(c);
        s.addComment(c);
        Tasks.whenAllComplete(
                        firebaseUtil.updateDoc(USERS_COLLECTION, t.getId(), t),
                        firebaseUtil.updateDoc(USERS_COLLECTION, s.getId(), s)
                ).addOnSuccessListener(tasks -> callback.onData(null))
                .addOnFailureListener(callback::onException);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ListenerRegistration listenCurrentUser(OnDataCallback callback) {
        return firebaseUtil.listenUser(
                USERS_COLLECTION,
                FirebaseAuth.getInstance().getUid(),
                callback
        );
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public ListenerRegistration listenTeacher(OnDataCallback callback, String teacherId) {
        return firebaseUtil.listenUser(
                USERS_COLLECTION,
                teacherId,
                callback
        );
    }

    public void getTeacher(String teacherId, OnDataCallback<Teacher> callback) {
        firebaseUtil.getDoc(USERS_COLLECTION, teacherId, callback, Teacher.class);
    }

    public void signIn(UserLoginForm form, OnDataCallback<AuthResult> callback) {
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(form.getEmail(), form.getPassword())
                .addOnSuccessListener(callback::onData)
                .addOnFailureListener(callback::onException);
    }

    public void createNewUser(UserRegisterForm form, OnDataCallback<User> callback) {

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(form.getEmail(), form.getPassword())
                .addOnSuccessListener(authResult -> {
                    if (authResult.getUser() == null) {
                        callback.onException(new FirebaseException("Account registration failed, with an unknown error. please try again later"));
                        return;
                    }

                    String id = authResult.getUser().getUid();

                    User user = null;
                    if (form instanceof TeacherRegisterForm) {
                        TeacherRegisterForm teacherForm = (TeacherRegisterForm) form;
                        user = new Teacher(
                                id, teacherForm.getEmail(),
                                teacherForm.getFullName(),
                                form.getPhone(),teacherForm.getAddress(),
                                "",
                                teacherForm.getTeachingSubjects(),
                                teacherForm.getTeachingArea(),
                                teacherForm.getEducation(),
                                new ArrayList<>(),
                                teacherForm.getTeachingLocation(),
                                0,
                                teacherForm.getPrice()
                        );
                        firebaseUtil.updateDoc(FirebaseMeetingsManager.MEETINGS_COLLECTION, user.getId(), new Meeting())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d("OnSuccess insert", "Successfull!!");
                                    }
                                });
                    } else if (form instanceof StudentRegisterForm) {
                        StudentRegisterForm studentForm = (StudentRegisterForm) form;
                        user = new Student(id, studentForm.getEmail(), studentForm.getFullName(), studentForm.getAddress(), form.getPhone(), "");
                    }
                    final User finalUser = user;
                    saveUser(id, finalUser, form.getImage(), callback);
                })
                .addOnFailureListener(callback::onException);
    }

    public void saveUser(String id, User user, Uri uri, OnDataCallback<User> callback) {
        String imagePath = "userImages/" + id;
        if (uri != null) {

            firebaseUtil.uploadImage(imagePath, uri, new OnDataCallback<String>() {
                @Override
                public void onData(String value) {
                    user.setImage(value);
                    if (user.isTeacher()) {
                        Teacher teacher = (Teacher) user;
                        firebaseUtil.updateDoc(USERS_COLLECTION, id, teacher)
                                .addOnSuccessListener(unused -> callback.onData(teacher))
                                .addOnFailureListener(callback::onException);
                    } else {


                        Student student = (Student) user;
                        firebaseUtil.updateDoc(USERS_COLLECTION, id, student)
                                .addOnSuccessListener(unused -> callback.onData(student))
                                .addOnFailureListener(callback::onException);
                    }
                }

                @Override
                public void onException(Exception e) {
                    callback.onException(e);
                }
            });
        } else {

            if (user.isTeacher()) {
                Teacher teacher = (Teacher) user;
                firebaseUtil.updateDoc(USERS_COLLECTION, id, teacher)
                        .addOnSuccessListener(unused -> callback.onData(teacher))
                        .addOnFailureListener(callback::onException);
            } else {
                Student student = (Student) user;
                firebaseUtil.updateDoc(USERS_COLLECTION, id, student)
                        .addOnSuccessListener(unused -> callback.onData(student))
                        .addOnFailureListener(callback::onException);
            }
        }
    }

    public ListenerRegistration listenAllUsers(OnDataCallback<List<User>> callback) {
        return firebaseUtil.listenUsers(new OnDataCallback<List<User>>() {
            @Override
            public void onData(List<User> value) {
                callback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                callback.onException(e);
            }
        });
    }

    public void getUsers(OnDataCallback<List<User>> callback) {
        firebaseUtil.getUsers(callback);
    }

    public void getCurrentUser(OnDataCallback<User> callback) {
        firebaseUtil.getUser(USERS_COLLECTION, FirebaseAuth.getInstance().getUid(), callback);
    }
}
