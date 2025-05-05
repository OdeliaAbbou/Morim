package com.example.morim.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.morim.databinding.RatingItemBinding;
import com.example.morim.model.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        Comment.sortComments(comments);
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CommentViewHolder(RatingItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }


    static class CommentViewHolder extends RecyclerView.ViewHolder {
        private final RatingItemBinding binding;

        public CommentViewHolder(RatingItemBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        public void bind(Comment comment) {
            binding.reviewerName.setText(comment.getStudentName());
            binding.reviewText.setText(comment.getComment());
            binding.ratingBar.setRating(comment.getRating());
        }
    }
}
