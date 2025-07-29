package com.example.morim.adapter;

import android.content.Intent;
import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.ChatActivity;
import com.example.morim.R;
import com.example.morim.databinding.TeacherItemBinding;
import com.example.morim.model.Favorite;
import com.example.morim.model.Meeting;
import com.example.morim.model.Student;
import com.example.morim.model.Teacher;
import com.example.morim.model.User;
import com.example.morim.ui.dialog.RatingListDialog;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.TeacherViewHolder> {


    public void filterFavorites(List<Teacher> teachers, HashSet<Favorite> favorites) {
        this.teachers = (teachers.isEmpty() ? this.teachers : teachers).stream()
                .filter(teacher -> favorites.contains(new Favorite(teacher.getId())))
                .collect(Collectors.toList());
        notifyDataSetChanged();
    }

    public boolean noTeachersYet() {
        return teachers == null;
    }
    public boolean noFavoritesYet() {
        return favorites == null;
    }

    public interface ScheduleClickListener {
        void onRequestScheduleWithTeacher(Teacher teacher);

    }

    public interface TeacherAdapterListener extends ScheduleClickListener {
        void onViewTeacher(Teacher teacher);

        void onSendMessage(Teacher teacher);

        void onAddReview(Teacher teacher);

        void onShowComments(Teacher teacher);

        void onAddToFavorites(Teacher teacher);

        void onRemoveFromFavorites(Teacher teacher);
    }

    private List<Teacher> teachers;
    private Set<Favorite> favorites;
    private final ScheduleClickListener listener;

    public TeacherAdapter(List<Teacher> teachers, ScheduleClickListener listener) {
        this.teachers = teachers;
        this.listener = listener;
    }

    private Student current;
    private List<Meeting> currentMeetings;

    public void setFavorites(Set<Favorite> favorites) {
        this.favorites = favorites;
        notifyDataSetChanged();
    }

    public void updateCurrentUser(Student current) {
        this.current = current;
        notifyDataSetChanged();
    }

    public void updateCurrentMeetings(List<Meeting> currentMeetings) {
        this.currentMeetings = currentMeetings;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setTeacherData(List<Teacher> teachers) {
        this.teachers = teachers.stream() // conversion en stream car on peut pas f sorted
                .sorted((t1, t2) -> Double.compare(t2.getAverageRating(), t1.getAverageRating())) // tri décroissant
                .collect(Collectors.toList()); // retour a une List

        notifyDataSetChanged();
    }

//  @SuppressLint("NotifyDataSetChanged")
//    public void setTeacherData(List<Teacher> teachers) {
//        this.teachers = teachers;
//        notifyDataSetChanged();
//    }

    private boolean isFavoritesAvailable() {
        return favorites != null;
    }

    private boolean isFavorite(Teacher teacher) {
        boolean is = isFavoritesAvailable() && favorites.contains(new Favorite(teacher.getId()));
        return is;
    }

    public void refreshPosition(int position) {
        notifyItemChanged(position);
    }

    @NonNull
    @Override
    public TeacherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TeacherItemBinding binding = TeacherItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new TeacherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull TeacherViewHolder holder, int position) {
        Teacher teacher = teachers.get(position);
        holder.bind(current, currentMeetings, teacher, favorites, (ScheduleClickListener) listener);
    }


    @Override
    public int getItemCount() {
        return teachers.size();
    }


    public static class TeacherViewHolder extends RecyclerView.ViewHolder {
        private final TeacherItemBinding binding;

        public TeacherViewHolder(TeacherItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("DefaultLocale")
        public void bind(
                Student current,
                List<Meeting> currentMeetings,
                Teacher teacher,
                Set<Favorite> favorites,
                ScheduleClickListener listener) {

            boolean isFavorite = favorites != null && favorites.contains(new Favorite(teacher.getId()));

            if (!teacher.getImage().isEmpty())
                Picasso.get()
                        .load(teacher.getImage())
                        .into(binding.ivTeacherItem);
            else
                Picasso.get()
                        .load(User.DEFAULT_IMAGE)
                        .into(binding.ivTeacherItem);

            binding.btnSchedule.setText(String.format("Schedule with %s", teacher.getFullName()));
            binding.tvPrice.setText(String.format("%.1f$ /hour", teacher.getPrice()));
            binding.rbTeacherItem.setRating((float) teacher.getAverageRating());

            binding.tvTeacherItemName.setText(teacher.getFullName());
            binding.tvTeacherItemSubjects.setText(String.join(",", teacher.getTeachingSubjects()));
            binding.tvTeacherItemEducation.setText(teacher.getEducation());

//            binding.getRoot().setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    binding.getRoot().setOnClickListener(v -> {
//                        if (listener instanceof TeacherAdapterListener) {
//                            ((TeacherAdapterListener) listener).onViewTeacher(teacher);
//                        }
//                    });
//                }
//            });
            binding.getRoot().setOnClickListener(v -> {
                if (listener instanceof TeacherAdapterListener) {
                    ((TeacherAdapterListener) listener).onViewTeacher(teacher);
                }
            });

            binding.btnChat.setOnClickListener(view -> {
                ///////////
                String currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().getUid();
                if (teacher.getId().equals(currentUserId)) {
                    Toast.makeText(view.getContext(), "Can't converse with yourself.", Toast.LENGTH_SHORT).show();
                    return;
                }

                ///////////
                if (listener instanceof TeacherAdapterListener) {
                    ((TeacherAdapterListener) listener).onSendMessage(teacher);
                }
            });

            binding.btnFavorite.setVisibility(View.VISIBLE);
            binding.btnFavorite.setImageResource(isFavorite ? R.drawable.baseline_favorite_24 : R.drawable.ic_favorite);
            binding.btnFavorite.setOnClickListener(view -> {
                if (listener instanceof TeacherAdapterListener) {
                    if (isFavorite) {
                        ((TeacherAdapterListener) listener).onRemoveFromFavorites(teacher);
                    } else {
                        ((TeacherAdapterListener) listener).onAddToFavorites(teacher);
                    }
                }
            });


            binding.btnSchedule.setOnClickListener(view -> listener.onRequestScheduleWithTeacher(teacher));


            binding.btnCommentsList.setOnClickListener(view -> {
                if (teacher.getComments().size() > 0) {
                    if (listener instanceof TeacherAdapterListener) {
                        ((TeacherAdapterListener) listener).onShowComments(teacher);
                    }
                } else {
                    Toast.makeText(view.getContext(), "No reviews available", Toast.LENGTH_SHORT).show();
                }
            });
            Log.d("TeacherAdapter", "Current: " + current + " " + currentMeetings);
            if (current != null && currentMeetings != null) {
                Log.d("TeacherAdapter", "Current: " + current.getComments().size() + " " + currentMeetings.size());
                // check if the student has already reviewed the teacher
                // and if he had at-least one meeting with the teacher that is past today and not cancelled
                boolean hasReviewed = current.getComments()
                        .stream()
                        .anyMatch(comment ->
                                comment.getTeacherId().equals(teacher.getId())
                        );
                boolean hasMeeting = currentMeetings
                        .stream()
                        .anyMatch(meeting ->
                                meeting.getTeacherId().equals(teacher.getId())
                                        && meeting.getMeetingDate() < System.currentTimeMillis()
                                        && !meeting.isCanceled()
                        );
                if (!hasReviewed && hasMeeting) {
                    binding.btnAddReview.setVisibility(View.VISIBLE);
                    binding.btnAddReview.setOnClickListener(view -> {
                        if (listener instanceof TeacherAdapterListener) {
                            ((TeacherAdapterListener) listener).onAddReview(teacher);
                        }
                    });
                } else {
                    binding.btnAddReview.setVisibility(View.GONE);
                }
            } else {
                binding.btnAddReview.setVisibility(View.GONE);
            }
        }
    }

}
