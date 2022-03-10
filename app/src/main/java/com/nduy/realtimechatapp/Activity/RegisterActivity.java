package com.nduy.realtimechatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.databinding.ActivityRegisterBinding;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;

@RequiresApi(api = Build.VERSION_CODES.N)
public class RegisterActivity extends AppCompatActivity {
    public ActivityRegisterBinding binding;
    private ProgressDialog LoadingBar;
    FirebaseFirestore database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        LoadingBar = new ProgressDialog(this);
        binding.btnRegister.setOnClickListener(v -> {
            createAccount();
        });
        binding.gotoLogin.setOnClickListener(v2 -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
    }

    private void createAccount() {
        String displayName = binding.registerInputName.getText().toString().trim();
        String email = binding.registerInputEmail.getText().toString().trim();
        String password = binding.registerInputPassword.getText().toString();
        String repeatPassword = binding.registerInputRepeatPassword.getText().toString();
        if (TextUtils.isEmpty(displayName)) {
            binding.registerInputName.setError("Please enter your display name");
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.registerInputEmail.setError("Invalid email");
        } else if (TextUtils.isEmpty(password) || password.length() < 8) {
            binding.registerInputPassword.setError("You must have 8 characters in your password");
        } else if (!repeatPassword.equals(password)) {
            binding.registerInputRepeatPassword.setError("Repeat password not match");
        } else {
            LoadingBar.setTitle("Create Account");
            LoadingBar.setMessage("Please wait...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            validateAccount(displayName, email, password);
        }
    }

    private void validateAccount(String displayName, String email, String password) {
        database = FirebaseFirestore.getInstance();
        HashMap<String, Object> newUser = new HashMap<>();
        newUser.put(User.DISPLAY_NAME, displayName);
        newUser.put(User.EMAIL_FIELD, email);
        newUser.put(User.PASSWORD_FIELD, password);
        newUser.put(User.IMAGE_FIELD, "");
        database.collection(DBCollectionConstant.User)
                .add(newUser)
                .addOnSuccessListener(documentReference -> {
                    FancyToast.makeText(RegisterActivity.this, "Register complete", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                    LoadingBar.dismiss();
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .addOnFailureListener(e -> {
                    FancyToast.makeText(RegisterActivity.this, "Something error, please try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                    LoadingBar.dismiss();
                });
    }
}
