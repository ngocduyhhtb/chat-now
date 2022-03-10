package com.nduy.realtimechatapp.Activity;

import androidx.annotation.RequiresApi;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nduy.realtimechatapp.Adapter.ChatAdapter;
import com.nduy.realtimechatapp.Model.ChatMessage;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;
import com.nduy.realtimechatapp.Utils.PreferenceManager;
import com.nduy.realtimechatapp.databinding.ActivityChatBinding;
import com.shashank.sony.fancytoastlib.FancyToast;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.N)
public class ChatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private User receiverUser;
    private List<ChatMessage> chatMessages;
    private ChatAdapter chatAdapter;
    private PreferenceManager preferenceManager;
    private FirebaseFirestore database;
    private String conversionID = null;
    private int isReceiverOnline = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setListener();
        loadReceiverUserDetail();
        init();
        listenerMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        listenReceiverOnline();
    }

    private void loadReceiverUserDetail() {
        if (getIntent().getExtras() != null) {
            receiverUser = (User) getIntent().getSerializableExtra("user");
        }
        binding.textName.setText(receiverUser.getDisplayName());
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.layoutSend.setAlpha(0.5F);
        binding.layoutSend.setEnabled(false);
        binding.inputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (binding.inputMessage.getText().toString().trim().length() > 0) {
                    binding.layoutSend.setEnabled(true);
                    binding.layoutSend.setAlpha(1F);
                    binding.layoutSend.setOnClickListener(v -> sendMessage());
                } else {
                    binding.layoutSend.setAlpha(0.5F);
                    binding.layoutSend.setEnabled(false);
                    binding.layoutSend.setOnClickListener(null);
                }
            }
        });
    }

    private void init() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatAdapter = new ChatAdapter(receiverUser.getImageEncode(),
                chatMessages,
                preferenceManager.getString(User.User_ID),
                getApplicationContext()
        );
        binding.chatRecycleView.setAdapter(chatAdapter);
        database = FirebaseFirestore.getInstance();
    }

    @SuppressLint("LongLogTag")
    private void sendMessage() {
        HashMap<String, Object> newMessage = new HashMap<>();
        newMessage.put(ChatMessage.SENDER_ID, preferenceManager.getString(User.User_ID));
        newMessage.put(ChatMessage.RECEIVED_ID, receiverUser.getUserID());
        newMessage.put(ChatMessage.MESSAGE, binding.inputMessage.getText().toString().trim());
        newMessage.put(ChatMessage.TIME_STAMP, new Date().toString());
        database.collection(DBCollectionConstant.Message).add(newMessage)
                .addOnFailureListener(error -> {
                    Log.e("SEND_MESSAGE_EXCEPTION", error.getMessage());
                });
        if (conversionID != null) {
            updateConversion(binding.inputMessage.getText().toString());
        } else {
            HashMap<String, Object> conversion = new HashMap<>();
            conversion.put(ChatMessage.SENDER_ID, preferenceManager.getString(User.User_ID));
            conversion.put(ChatMessage.SENDER_NAME, preferenceManager.getString(User.DISPLAY_NAME));
            conversion.put(ChatMessage.SENDER_IMAGE, preferenceManager.getString(User.IMAGE_FIELD));
            conversion.put(ChatMessage.RECEIVED_ID, receiverUser.getUserID());
            conversion.put(ChatMessage.RECEIVER_NAME, receiverUser.getDisplayName());
            conversion.put(ChatMessage.RECEIVER_IMAGE, receiverUser.getImageEncode());
            conversion.put(ChatMessage.TIME_STAMP, new Date().toString());
            addConversion(conversion);
        }
        if (isReceiverOnline == 0) {
            try {
                JSONObject notification = new JSONObject();
                JSONObject body = new JSONObject();
                notification.put("body", binding.inputMessage.getText().toString());
                notification.put("title", preferenceManager.getString(User.DISPLAY_NAME));
                body.put("notification", notification);
                body.put("to", receiverUser.getToken());
                sendNotification(body);
            } catch (Exception e) {
                Log.e("CHECK_USER_ONLINE_EXCEPTION", e.getMessage());
                FancyToast.makeText(ChatActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
            }
        }
        binding.inputMessage.setText(null);
    }

    private void listenerMessage() {
        database.collection(DBCollectionConstant.Message)
                .whereEqualTo(ChatMessage.SENDER_ID, preferenceManager.getString(User.User_ID))
                .whereEqualTo(ChatMessage.RECEIVED_ID, receiverUser.getUserID())
                .addSnapshotListener(eventListener);
        database.collection(DBCollectionConstant.Message)
                .whereEqualTo(ChatMessage.SENDER_ID, receiverUser.getUserID())
                .whereEqualTo(ChatMessage.RECEIVED_ID, preferenceManager.getString(User.User_ID))
                .addSnapshotListener(eventListener);
    }

    @SuppressLint("NotifyDataSetChanged")
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error != null) {
            return;
        }
        if (value != null) {
            System.out.println("Notify event change");
            int count = chatMessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()) {
                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setSenderID(documentChange.getDocument().getString(ChatMessage.SENDER_ID));
                    chatMessage.setReceiverID(documentChange.getDocument().getString(ChatMessage.RECEIVED_ID));
                    chatMessage.setMessage(documentChange.getDocument().getString(ChatMessage.MESSAGE));
                    chatMessage.setDateTime(getReadableDateTime(documentChange.getDocument().getString(ChatMessage.TIME_STAMP)));
                    chatMessage.setDateObj(new Date(documentChange.getDocument().getString(ChatMessage.TIME_STAMP)));
                    chatMessages.add(chatMessage);
                }
            }
            Collections.sort(chatMessages, Comparator.comparing(ChatMessage::getDateObj));
            if (count == 0) {
                chatAdapter.notifyDataSetChanged();
            } else {
                chatAdapter.notifyItemRangeInserted(chatMessages.size(), chatMessages.size());
                binding.chatRecycleView.smoothScrollToPosition(chatMessages.size() - 1);
            }
            binding.chatRecycleView.setVisibility(View.VISIBLE);
        }
        binding.progressBar.setVisibility(View.GONE);
        if (conversionID == null) {
            checkForConversion();
        }
    };

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private String getReadableDateTime(String date) {
        return new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", new Locale("vi", "VN")).format(new Date(date));
    }

    private void addConversion(HashMap<String, Object> conversion) {
        database.collection(DBCollectionConstant.Conversation)
                .add(conversion)
                .addOnSuccessListener(documentRef -> conversionID = documentRef.getId());
    }

    private void updateConversion(String message) {
        DocumentReference documentReference = database.collection(DBCollectionConstant.Conversation).document(conversionID);
        documentReference.update(ChatMessage.LAST_MESSAGE, message, ChatMessage.TIME_STAMP, new Date().toString());
    }

    private void checkForConversion() {
        if (chatMessages.size() != 0) {
            checkForConversionRemotely(
                    preferenceManager.getString(User.User_ID),
                    receiverUser.getUserID()
            );
            checkForConversionRemotely(
                    receiverUser.getUserID(),
                    preferenceManager.getString(User.User_ID)
            );
        }
    }

    private void checkForConversionRemotely(String senderID, String receiverID) {
        database.collection(DBCollectionConstant.Conversation)
                .whereEqualTo(ChatMessage.SENDER_ID, senderID)
                .whereEqualTo(ChatMessage.RECEIVED_ID, receiverID)
                .get()
                .addOnCompleteListener(conversionOnCompleteListener);
    }

    private final OnCompleteListener<QuerySnapshot> conversionOnCompleteListener = task -> {
        if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
            conversionID = documentSnapshot.getId();
        }
    };

    private void listenReceiverOnline() {
        database.collection(DBCollectionConstant.User).document(
                receiverUser.getUserID()
        ).addSnapshotListener(ChatActivity.this, ((value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null) {
                if (value.getLong(DBCollectionConstant.UserAvailability) != null) {
                    Long availability = value.getLong(DBCollectionConstant.UserAvailability);
                    isReceiverOnline = Math.toIntExact(availability);
                }
                receiverUser.setToken(value.getString(User.USER_FCM_TOKEN));
                if (receiverUser.getImageEncode() == null) {
//                    chatAdapter.setReceivedProfileImage(getBitmapFromEncodedString(receiverUser.getImageEncode()));
                    chatAdapter.notifyItemRangeInserted(0, chatMessages.size());
                }
            }
            if (Boolean.parseBoolean(String.valueOf(isReceiverOnline))) {
                binding.userOnline.setVisibility(View.VISIBLE);
            } else {
                binding.userOnline.setVisibility(View.GONE);
            }
            assert value != null;
            receiverUser.setToken(value.getString(User.USER_FCM_TOKEN));
        }));
    }

    private void sendNotification(JSONObject messageBody) {
        Log.e("TAG", "sendNotification");
        RequestQueue queue = Volley.newRequestQueue(this);
        @SuppressLint("LongLogTag") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                DBCollectionConstant.FCM_API,
                messageBody,
                response -> {
                    Log.d("MESSAGE_SEND_NOTIFICATION_RES", response.toString());
                },
                error -> {
                    Log.d("MESSAGE_SEND_NOTIFICATION_RES", error.toString());
                }
        ) {
            @Override
            public Map<String, String> getHeaders() {
                return DBCollectionConstant.getRemoteMessageHeaders(getApplicationContext());
            }
        };
        queue.add(jsonObjectRequest);
    }
}