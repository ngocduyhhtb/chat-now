package com.nduy.realtimechatapp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.nduy.realtimechatapp.Adapter.RecentConversationAdapter;
import com.nduy.realtimechatapp.Listener.ConversionsListener;
import com.nduy.realtimechatapp.Model.ChatMessage;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.R;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;
import com.nduy.realtimechatapp.Utils.PreferenceManager;
import com.nduy.realtimechatapp.databinding.ActivityMainBinding;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.apache.commons.validator.routines.UrlValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends BaseActivity implements ConversionsListener {
    private PreferenceManager preferenceManager;
    private ActivityMainBinding binding;
    private List<ChatMessage> conversations;
    private RecentConversationAdapter conversationAdapter;
    private FirebaseFirestore database;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        setContentView(binding.getRoot());
        init();
        loadUserDetail();
        getToken();
        setListener();
        listenConversations();
    }

    private void init() {
        conversations = new ArrayList<>();
        conversationAdapter = new RecentConversationAdapter(conversations, this, getApplicationContext());
        binding.conversationRecyclerView.setAdapter(conversationAdapter);
        database = FirebaseFirestore.getInstance();
    }

    private void setListener() {
        binding.imageSignOut.setOnClickListener(view -> {
            signOut();
        });
        binding.fabNewChat.setOnClickListener(c -> {
            startActivity(new Intent(getApplicationContext(), UserListActivity.class));
        });
        binding.imageProfile.setOnClickListener(c -> {
            startActivity(new Intent(MainActivity.this, UserActionActivity.class));
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "LongLogTag"})
    private void loadUserDetail() {
        binding.txtUsername.setText(preferenceManager.getString(User.DISPLAY_NAME));
        Glide.with(this).load(preferenceManager.getString(User.IMAGE_FIELD)).error(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_user)).into(binding.imageProfile);
    }

    private void getToken() {
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateUserToken);
    }

    private void updateUserToken(String token) {
        preferenceManager.putString(User.USER_FCM_TOKEN, token);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userID = preferenceManager.getString(User.User_ID);
        if (userID != null) {
            DocumentReference documentReference = db.collection(DBCollectionConstant.User).document(
                    userID
            );
            documentReference.update(User.USER_FCM_TOKEN, token)
                    .addOnFailureListener(e -> {
                        FancyToast.makeText(MainActivity.this, "Unable to update user token", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                    });
        }
    }

    private void signOut() {
        FancyToast.makeText(MainActivity.this, "Signing out...", FancyToast.LENGTH_SHORT, FancyToast.INFO, false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        String userID = preferenceManager.getString(User.User_ID);
        if (userID != null) {
            DocumentReference documentReference =
                    database.collection(DBCollectionConstant.User).document(
                            userID
                    );
            HashMap<String, Object> updateUser = new HashMap<>();
            updateUser.put(User.USER_FCM_TOKEN, FieldValue.delete());
            documentReference.update(updateUser)
                    .addOnSuccessListener(unused -> {
                        preferenceManager.clear();
                        startActivity(new Intent(MainActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        FancyToast.makeText(MainActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.INFO, false);
                    });
        }
    }

    private void listenConversations() {
        database.collection(DBCollectionConstant.Conversation)
                .whereEqualTo(ChatMessage.SENDER_ID, preferenceManager.getString(User.User_ID))
                .addSnapshotListener(eventListener);
        database.collection(DBCollectionConstant.Conversation)
                .whereEqualTo(ChatMessage.RECEIVED_ID, preferenceManager.getString(User.User_ID))
                .addSnapshotListener(eventListener);
    }

    @SuppressLint({"NotifyDataSetChanged", "SimpleDateFormat"})
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            Log.d("CONVERSATION", "UPDATE");
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    String senderID = documentChange.getDocument().getString(ChatMessage.SENDER_ID);
                    String receiverID = documentChange.getDocument().getString(ChatMessage.RECEIVED_ID);
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderID(senderID);
                    chatMessage.setReceiverID(receiverID);
                    if (preferenceManager.getString(User.User_ID).equals(senderID)) {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(ChatMessage.RECEIVER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(ChatMessage.RECEIVER_NAME));
                        chatMessage.setConversionID(documentChange.getDocument().getString(ChatMessage.RECEIVED_ID));
                    } else {
                        chatMessage.setConversionImage(documentChange.getDocument().getString(ChatMessage.SENDER_IMAGE));
                        chatMessage.setConversionName(documentChange.getDocument().getString(ChatMessage.SENDER_NAME));
                        chatMessage.setConversionID(documentChange.getDocument().getString(ChatMessage.SENDER_ID));
                    }
                    chatMessage.setMessage(documentChange.getDocument().getString(ChatMessage.LAST_MESSAGE));
                    chatMessage.setDateObj(new Date(documentChange.getDocument().getString(ChatMessage.TIME_STAMP)));
                    conversations.add(chatMessage);
                } else if (documentChange.getType() == DocumentChange.Type.MODIFIED) {
                    for (int i = 0; i < conversations.size(); i++) {
                        String senderID = documentChange.getDocument().getString(ChatMessage.SENDER_ID);
                        String receiverID = documentChange.getDocument().getString(ChatMessage.RECEIVED_ID);
                        if (conversations.get(i).getSenderID().equals(senderID) && conversations.get(i).getReceiverID().equals(receiverID)) {
                            conversations.get(i).setMessage(documentChange.getDocument().getString(ChatMessage.LAST_MESSAGE));
                            conversations.get(i).setDateObj(new Date(documentChange.getDocument().getString(ChatMessage.TIME_STAMP)));
                            break;
                        }
                    }
                }
            }
            Collections.sort(conversations, Comparator.comparing(ChatMessage::getDateObj));
            conversationAdapter.notifyDataSetChanged();
            binding.conversationRecyclerView.smoothScrollToPosition(0);
            binding.conversationRecyclerView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    };

    @Override
    public void onConversionClickListener(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra(DBCollectionConstant.User, user);
        startActivity(intent);
    }
}