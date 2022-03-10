package com.nduy.realtimechatapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nduy.realtimechatapp.Activity.BaseActivity;
import com.nduy.realtimechatapp.Listener.ConversionsListener;
import com.nduy.realtimechatapp.Model.ChatMessage;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.R;
import com.nduy.realtimechatapp.databinding.ContainerRecentItemConversionBinding;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.List;

public class RecentConversationAdapter extends RecyclerView.Adapter<RecentConversationAdapter.ConversionViewHolder> {
    private final List<ChatMessage> chatMessages;
    private final ConversionsListener conversionsListener;
    private Context appContext;

    public RecentConversationAdapter(List<ChatMessage> chatMessages, ConversionsListener conversionsListener, Context appContext) {
        this.chatMessages = chatMessages;
        this.conversionsListener = conversionsListener;
        this.appContext = appContext;
    }

    @NonNull
    @Override
    public ConversionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ConversionViewHolder(
                ContainerRecentItemConversionBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ConversionViewHolder holder, int position) {
        holder.setData(chatMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ConversionViewHolder extends RecyclerView.ViewHolder {
        private final ContainerRecentItemConversionBinding binding;

        public ConversionViewHolder(ContainerRecentItemConversionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void setData(ChatMessage chatMessage) {
            Glide.with(appContext).load(chatMessage.getConversionImage()).error(ContextCompat.getDrawable(appContext, R.drawable.ic_empty_user)).into(binding.imageUserProfile);
            binding.textName.setText(chatMessage.getConversionName());
            binding.textRecentMessage.setText(chatMessage.getMessage());
            binding.getRoot().setOnClickListener(v -> {
                User user = new User();
                user.setUserID(chatMessage.getConversionID());
                user.setDisplayName(chatMessage.getConversionName());
                user.setImageEncode(chatMessage.getConversionImage());
                conversionsListener.onConversionClickListener(user);
            });
        }
    }

    private Bitmap getConversionImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
