package com.nduy.realtimechatapp.Activity;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.nduy.realtimechatapp.Adapter.UserActionAdapter;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.Model.UserAction;
import com.nduy.realtimechatapp.R;
import com.nduy.realtimechatapp.Utils.PreferenceManager;
import com.nduy.realtimechatapp.databinding.ActivityUserActionBinding;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.N)
public class UserActionActivity extends BaseActivity {
    ActivityUserActionBinding binding;
    List<UserAction> userActionList;
    UserActionAdapter userActionAdapter;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserActionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        setListenerInit();
    }

    private void setListenerInit() {
        binding.imageBack.setOnClickListener(v -> onBackPressed());
    }

    private void init() {
        userActionList = new ArrayList<>();
        preferenceManager = new PreferenceManager(getApplicationContext());
        userActionList.add(new UserAction("Logout", R.drawable.ic_logout));
        userActionList.add(new UserAction("Profile photo", R.drawable.ic_person));
        userActionAdapter = new UserActionAdapter(userActionList, getApplicationContext());
        binding.userActionList.setAdapter(userActionAdapter);
        binding.txtUsername.setText(preferenceManager.getString(User.DISPLAY_NAME));
        Glide.with(getApplicationContext()).load(preferenceManager.getString(User.IMAGE_FIELD)).error(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_empty_user)).into(binding.imageProfile);
    }
}
