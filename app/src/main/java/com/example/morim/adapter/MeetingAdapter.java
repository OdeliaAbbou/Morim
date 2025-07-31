package com.example.morim.adapter;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.databinding.MeetingItemBinding;
import com.example.morim.model.Meeting;
import com.example.morim.model.MyMeetingsData;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.util.DateUtils;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;


public class MeetingAdapter extends RecyclerView.Adapter<MeetingAdapter.MeetingsViewHolder> {
    private IMeetingActions actions;
    private MyMeetingsData meetingsData;

    private Set<String> alreadySeen;

    private final HashMap<String, User> users = new HashMap<>();

    private Student current;
    //
    private OnMeetingClickListener listener;
    //

    public interface IMeetingActions {
        void cancel(Meeting meeting, User otherUser);

        void dial(User otherUser);

        void mail(User otherUser);

        void onAddReview(User teacher);

        void onViewTeacher(User other);
    }
    ////
    public interface OnMeetingClickListener {
        void onMeetingClick(Meeting meeting, User otherUser);
    }

    /////

    public MeetingAdapter(MyMeetingsData meetingsData, IMeetingActions actions, OnMeetingClickListener listener) {
        this.meetingsData = meetingsData;
        for (User user : meetingsData.getUsers()) {
            users.put(user.getId(), user);
        }
        this.actions = actions;
        this.alreadySeen = new HashSet<>();
        //
        this.listener = listener;

        //

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setMeetingsData(MyMeetingsData data, Set<String> alreadySeen) {
        users.clear();
        this.alreadySeen = alreadySeen;
        for (User user : data.getUsers()) {
            users.put(user.getId(), user);
        }
        data.setMyMeetings(data.getMyMeetings().stream()
                .filter(meeting -> users.containsKey(meeting.getTeacherId()) && users.containsKey(meeting.getStudentId()))
                .sorted((m1, m2) -> Long.compare(m2.getMeetingDate(), m1.getMeetingDate()))
                .collect(Collectors.toList())
        );
        this.meetingsData = data;
        notifyDataSetChanged();
    }

    public void setCurrent(Student current) {
        this.current = current;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MeetingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MeetingItemBinding binding = MeetingItemBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new MeetingsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MeetingsViewHolder holder, int position) {
        Meeting meeting = meetingsData.getMyMeetings().get(position);
        holder.bind(meeting);
    }

    @Override
    public int getItemCount() {
        return meetingsData.getMyMeetings().size();
    }

    class MeetingsViewHolder extends RecyclerView.ViewHolder {
        private final MeetingItemBinding binding;

        private final String uid;

        public MeetingsViewHolder(MeetingItemBinding binding) {
            super(binding.getRoot());
            uid = FirebaseAuth.getInstance().getUid();
            this.binding = binding;
        }

        public void bind(Meeting meeting) {
            binding.tvMeetingDate.setText(DateUtils.formatEpochMillis(meeting.getMeetingDate()));
            // We are the student viewing the teacher.
            // Get the teacher
            User other;
            Log.d("MeetingsAdapter:bind ", "Meeting: " + meeting.getTeacherId() + ", " + meeting.getStudentId());
            if (meeting.getStudentId().equals(uid)) {
                other = users.get(meeting.getTeacherId());
                if (other == null) {
                    Log.d("MeetingsAdapter:bind ", "Teacher is Null!");
                    return;
                }
            }
            // We are the teacher viewing the student.
            // Get the student
            else if (meeting.getTeacherId().equals(uid)) {
                other = users.get(meeting.getStudentId());
                if (other == null) {
                    Log.d("MeetingsAdapter:bind ", "Student is Null!");
                    return;
                }
            }
            //  We are a malicious intruder
            //  somehow viewing someone else's meeting schedule
            else {
                throw new RuntimeException("MeetingsAdapter:bind Permission to view meeting denied");
            }

            if (alreadySeen.contains(meeting.getId())) {
                binding.newLayout.setVisibility(View.GONE);
            } else {
                binding.newLayout.setVisibility(View.VISIBLE);
            }

            // meeting may be canceled
            if (meeting.isCanceled() ) {
                binding.lessonTag.setTextColor(Color.RED);
                binding.lessonTag.setText(
                        String.format("%s", "CANCELED")
                );
                binding.btnCancelMeeting.setVisibility(View.GONE);
            }
            //ajjoutt
            else if (meeting.getMeetingDate() <= System.currentTimeMillis()) {
                binding.lessonTag.setTextColor(Color.GRAY);
                binding.lessonTag.setText("Completed");
                binding.btnCancelMeeting.setVisibility(View.GONE);
            }  else {
                binding.lessonTag.setTextColor(Color.parseColor("#25A92A"));
                binding.lessonTag.setText(
                        String.format("%s", "")
                );
                binding.btnCancelMeeting.setVisibility(View.VISIBLE);
            }

            binding.tvMeetingEmail.setOnClickListener(v -> actions.mail(other));
            binding.tvMeetingPhone.setOnClickListener(v -> actions.dial(other));

            binding.tvMeetingSubject.setText(meeting.getMeetingSubject());
            binding.tvMeetingEmail.setText(other.getEmail());
            binding.tvMeetingPhone.setText(other.getPhone());
//            binding.tvMeetingParticipant.setText(other.getFullName()); //
//////////////////

            String myUid = FirebaseAuth.getInstance().getUid();
            User me = users.get(myUid);
            if (other == null || me == null) return;
            final User finalOther = other; // ✅ AJOUT

            if (me.isTeacher()) {
                if (meeting.getTeacherId().equals(myUid)) {
                    binding.tvMeetingParticipant.setText(finalOther.getFullName() + " (You teaching)");
                } else if (meeting.getStudentId().equals(myUid)) {
                    binding.tvMeetingParticipant.setText(finalOther.getFullName() + " (You learning)");
                }
            } else {
                binding.tvMeetingParticipant.setText(finalOther.getFullName());
            }


            /////////////////
            binding.btnCancelMeeting.setOnClickListener(view ->
                    actions.cancel(meeting, other)
            );

            binding.btnAddReview.setVisibility(View.GONE);


            //
            binding.getRoot().setOnClickListener(v -> {
                Log.d("ClickTest", "Clicked on meeting with: " + other.getFullName());
                Log.d("MeetingAdapter", "User class: " + other.getClass().getName());

                if (listener != null ) {
                    listener.onMeetingClick(meeting, other);
                }
            });

            //
        }
    }
}
