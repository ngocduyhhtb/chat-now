package com.nduy.realtimechatapp.Services;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.nduy.realtimechatapp.Activity.ChatActivity;
import com.nduy.realtimechatapp.Activity.MainActivity;
import com.nduy.realtimechatapp.Model.ChatMessage;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.R;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;

import java.util.Random;

public class MessageService extends FirebaseMessagingService {
    public static final String KEY_TEXT_REPLY = "key_text_reply";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("FCM", "TOKEN: " + s);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("FCM", "Message: " + remoteMessage.getNotification().getBody());
        String channelID = "chat_message";
        int notificationID = new Random().nextInt();

        User user = new User();
        user.setUserID(remoteMessage.getData().get(User.User_ID));
        user.setDisplayName(remoteMessage.getData().get(User.DISPLAY_NAME));
        user.setToken(remoteMessage.getData().get(User.USER_FCM_TOKEN));

        Intent intent = new Intent(this, ChatActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(DBCollectionConstant.User, user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY).setLabel("Type to reply...").build();
            @SuppressLint("UnspecifiedImmutableFlag") PendingIntent resultPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationID, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationCompat.Action action = new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "REPLY", resultPendingIntent)
                    .addRemoteInput(remoteInput)
                    .setAllowGeneratedReplies(true)
                    .build();
//            Notification newMessageNotification = new NotificationCompat.Builder(getApplicationContext(), channelID)
//                    .setSmallIcon(R.drawable.ic_round_notifications)
//                    .setContentTitle(user.getDisplayName())
//                    .setContentText(remoteMessage.getData().get(ChatMessage.MESSAGE))
//                    .setPriority(Notification.PRIORITY_DEFAULT)
//                    .setAutoCancel(true)
//                    .addAction(action)
//                    .build();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                    .setSmallIcon(R.drawable.ic_round_notifications)
                    .setContentTitle(user.getDisplayName())
                    .setContentText(remoteMessage.getData().get(ChatMessage.MESSAGE))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .addAction(action);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(notificationID, builder.build());
        }
    }
}
