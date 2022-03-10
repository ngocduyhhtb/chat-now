package com.nduy.realtimechatapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nduy.realtimechatapp.Model.ChatMessage;
import com.nduy.realtimechatapp.R;
import com.nduy.realtimechatapp.databinding.ContainerReceivedMessageBinding;
import com.nduy.realtimechatapp.databinding.ContainerSentMessageBinding;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private String receivedProfileImage;
    private final List<ChatMessage> chatMessageList;
    private final String senderID;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private final Context appContext;

    public ChatAdapter(String receivedProfileImage, List<ChatMessage> chatMessageList, String senderID, Context appContext) {
        this.receivedProfileImage = receivedProfileImage;
        this.chatMessageList = chatMessageList;
        this.senderID = senderID;
        this.appContext = appContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).setData(chatMessageList.get(position));
        } else {
            ((ReceivedMessageViewHolder) holder).setData(chatMessageList.get(position), receivedProfileImage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatMessageList.get(position).getSenderID().equals(senderID)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        private final ContainerSentMessageBinding binding;

        SentMessageViewHolder(ContainerSentMessageBinding sentMessageBinding) {
            super(sentMessageBinding.getRoot());
            this.binding = sentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        private final ContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ContainerReceivedMessageBinding receivedMessageBinding) {
            super(receivedMessageBinding.getRoot());
            this.binding = receivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, String receivedProfileImage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getDateTime());
            Glide.with(appContext).load(receivedProfileImage).error(ContextCompat.getDrawable(appContext, R.drawable.ic_empty_user)).into(binding.imageProfile);
        }
    }

//    public void setReceivedProfileImage(Bitmap bitmap) {
//        this.receivedProfileImage = bitmap;
//    }
}
