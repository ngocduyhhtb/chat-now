package com.nduy.realtimechatapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nduy.realtimechatapp.Listener.UserListener;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.R;
import com.nduy.realtimechatapp.databinding.ContainerUserItemBinding;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;


public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {
    private final List<User> userList;
    private final UserListener userListener;
    private final Context context;

    public UserListAdapter(List<User> userList, UserListener userListener, Context context) {
        this.userList = userList;
        this.userListener = userListener;
        this.context = context;
    }

    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContainerUserItemBinding itemContainerBinding = ContainerUserItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new UserListViewHolder(itemContainerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.setUserData(userList.get(position));
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder {
        ContainerUserItemBinding binding;

        public UserListViewHolder(ContainerUserItemBinding itemContainerBinding) {
            super(itemContainerBinding.getRoot());
            this.binding = itemContainerBinding;
        }

        void setUserData(User user) {
            binding.textName.setText(user.getDisplayName());
            binding.textEmail.setText(user.getEmail());
            Glide.with(context).load(user.getImageEncode()).error(ContextCompat.getDrawable(context, R.drawable.ic_empty_user)).into(binding.imageUserProfile);
            binding.getRoot().setOnClickListener(v -> userListener.onUserClick(user));
        }
    }

    private Bitmap getUserImages(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
