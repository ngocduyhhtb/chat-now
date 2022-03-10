package com.nduy.realtimechatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nduy.realtimechatapp.Model.UserAction;
import com.nduy.realtimechatapp.databinding.ContainerProfileDetailItemBinding;

import java.util.List;

public class UserActionAdapter extends RecyclerView.Adapter<UserActionAdapter.UserActionViewHolder> {
    private final List<UserAction> listUserAction;
    private final Context context;

    public UserActionAdapter(List<UserAction> listUserAction, Context context) {
        this.listUserAction = listUserAction;
        this.context = context;
    }

    @NonNull
    @Override
    public UserActionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ContainerProfileDetailItemBinding binding = ContainerProfileDetailItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new UserActionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserActionViewHolder holder, int position) {
        holder.setUserActionData(listUserAction.get(position));
    }

    @Override
    public int getItemCount() {
        return listUserAction.size();
    }

    public static class UserActionViewHolder extends RecyclerView.ViewHolder {
        ContainerProfileDetailItemBinding binding;

        public UserActionViewHolder(ContainerProfileDetailItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void setUserActionData(UserAction userAction) {
            binding.profileDetailItem.setText(userAction.getActionName());
            binding.profileDetailItemImage.setImageResource(userAction.getIconID());
        }
    }
}
