package com.example.morim.database.common;

import android.content.SharedPreferences;
import android.net.Uri;

import com.example.morim.database.OnDataCallback;
import com.example.morim.database.local.CurrentUserDao;
import com.example.morim.database.local.StudentDao;
import com.example.morim.database.local.TeacherDao;
import com.example.morim.database.local.UserDao;
import com.example.morim.database.remote.FirebaseUserManager;
import com.example.morim.dto.UserLoginForm;
import com.example.morim.dto.UserRegisterForm;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.util.FirebaseListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class UserRepository {

    private SharedPreferences sp;
    private ScheduledThreadPoolExecutor executor;
    private final StudentDao studentDao;

    private final UserDao userDao;

    private final TeacherDao teacherDao;
    private final CurrentUserDao currentUserDao;

    private final FirebaseUserManager remoteDb;

    public UserRepository(
            SharedPreferences sp,
            ScheduledThreadPoolExecutor executor,
            StudentDao studentDao,
            TeacherDao teacherDao,
            CurrentUserDao currentUserDao,
            UserDao userDao,
            FirebaseUserManager remoteDb
    ) {
        this.sp = sp;
        this.executor = executor;
        this.studentDao = studentDao;
        this.teacherDao = teacherDao;
        this.currentUserDao = currentUserDao;
        this.userDao = userDao;
        this.remoteDb = remoteDb;
    }


    public FirebaseListener<List<User>> listenUsers() {
        return new FirebaseListener<>(
                userDao.listenAllUsers(),
                remoteDb.listenAllUsers(new OnDataCallback<List<User>>() {
                    @Override
                    public void onData(List<User> value) {
                        if (value != null) {
                            executor.execute(() -> {
                                userDao.insert(value);
                            });
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
        );
    }



    public FirebaseListener<List<Teacher>> listenTeachers() {
        return new FirebaseListener<>(
                teacherDao.listenAllTeachers(),
                remoteDb.listenTeachers(new OnDataCallback<List<Teacher>>() {
                    @Override
                    public void onData(List<Teacher> value) {
                        if (value != null) {
                            executor.execute(() -> {
                                teacherDao.insert(value);
                            });
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
        );
    }


    public void signIn(UserLoginForm form, OnDataCallback<AuthResult> callback) {
        remoteDb.signIn(form, callback);
    }

    public void createNewUser(UserRegisterForm form, OnDataCallback<User> callback) {
        remoteDb.createNewUser(form, callback);
    }

    public void updateUser(User user, Uri uri, OnDataCallback<User> callback) {
        remoteDb.saveUser(user.getId(), user, uri, callback);
    }

    public void getCurrentUser(OnDataCallback<User> callback) {
        remoteDb.getCurrentUser(callback);
    }

    public FirebaseListener<User> listenCurrentUser(
            OnSuccessListener<Void> logoutListener
    ) {
        return new FirebaseListener<>(
                currentUserDao.listenCurrentUser(FirebaseAuth.getInstance().getUid()),
                remoteDb.listenCurrentUser(new OnDataCallback<User>() {
                    @Override
                    public void onData(User value) {
                        if (value != null) {
                            executor.execute(() -> {
                                currentUserDao.insert(value);
                            });
                        } else {
                            logoutListener.onSuccess(null);
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                })
        );
    }

    public FirebaseListener<Teacher> listenTeacher(
            String userId
    ) {
        return new FirebaseListener<>(
                teacherDao.listenTeacher(userId),
                remoteDb.listenTeacher(new OnDataCallback<Teacher>() {
                    @Override
                    public void onData(Teacher value) {
                        if (value != null) {
                            executor.execute(() -> {
                                userDao.insert(value);
                            });
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        e.printStackTrace();
                    }
                }, userId)
        );
    }

    public void addComment(String comment,
                           float rating,
                           User teacher,
                           User student,
                           OnDataCallback<Void> callback

    ) {
        remoteDb.addComment(comment, rating, teacher, student, new OnDataCallback<Void>() {
            @Override
            public void onData(Void value) {
                executor.execute(() -> {
                    Gson g = new Gson();
                    teacherDao.updateComments(teacher.getId(), g.toJson(teacher.getComments()));
                    studentDao.updateComments(student.getId(), g.toJson(student.getComments()));
                });
                callback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                callback.onException(e);
            }
        });

    }

    public void saveUser(User u, OnDataCallback<User> callback) {
        remoteDb.saveUser(u.getId(), u, null, new OnDataCallback<User>() {
            @Override
            public void onData(User value) {
                executor.execute(() -> {
                    userDao.insert(value);
                    if (value.isTeacher()) {
                        teacherDao.insert((Teacher) value);
                    } else {
                        studentDao.insert((Student) value);
                    }
                });
                callback.onData(value);
            }

            @Override
            public void onException(Exception e) {
                e.printStackTrace();
                callback.onException(e);
            }
        });
    }

    public void getUsers(OnDataCallback<List<User>> callback) {
        remoteDb.getUsers(callback);
    }
}
