package com.nduy.realtimechatapp.Activity;

import androidx.annotation.RequiresApi;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nduy.realtimechatapp.Adapter.UserListAdapter;
import com.nduy.realtimechatapp.Listener.UserListener;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;
import com.nduy.realtimechatapp.Utils.PreferenceManager;
import com.nduy.realtimechatapp.databinding.ActivityUserListBinding;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UserListActivity extends BaseActivity implements UserListener {
    private ActivityUserListBinding binding;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        getUser();
        setListener();
    }

    private void setListener() {
        binding.imageBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void getUser() {
        loading(true);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(DBCollectionConstant.User)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentUserID = preferenceManager.getString(User.User_ID);
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<User> listUser = new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                            if (currentUserID.equals(queryDocumentSnapshot.getId())) {
                                continue;
                            }
                            User user = new User();
                            user.setDisplayName(queryDocumentSnapshot.getString(User.DISPLAY_NAME));
                            user.setEmail(queryDocumentSnapshot.getString(User.EMAIL_FIELD));
                            user.setImageEncode(queryDocumentSnapshot.getString(User.IMAGE_FIELD));
                            user.setToken(queryDocumentSnapshot.getString(User.USER_FCM_TOKEN));
                            user.setUserID(queryDocumentSnapshot.getId());
                            user.setIsOnline(queryDocumentSnapshot.getLong(DBCollectionConstant.UserAvailability));
                            listUser.add(user);
                        }
                        if (listUser.size() > 0) {
                            UserListAdapter userListAdapter = new UserListAdapter(listUser, this, getApplicationContext());
                            binding.userList.setAdapter(userListAdapter);
                            binding.userList.setVisibility(View.VISIBLE);
                        } else {
                            showErrorMessage();
                        }
                    } else {
                        showErrorMessage();
                    }
                });
    }

    private void showErrorMessage() {
        binding.textErrorMessage.setText(String.format("%s", "No user available"));
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUserClick(User user) {
        Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
        intent.putExtra("user", user);
        System.out.println("SENT ACT USER=================" + user.toString());
        startActivity(intent);
        finish();
    }
}