package com.nduy.realtimechatapp.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;
import com.nduy.realtimechatapp.Utils.PreferenceManager;
@RequiresApi(api = Build.VERSION_CODES.N)
public class BaseActivity extends AppCompatActivity {
    private DocumentReference documentReference;
    PreferenceManager preferenceManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferenceManager = new PreferenceManager(getApplicationContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if (preferenceManager.getBoolean(User.IS_SIGN_IN)) {
            documentReference = database.collection(DBCollectionConstant.User).document(preferenceManager.getString(User.User_ID));
        } else {
            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (preferenceManager.getBoolean(User.IS_SIGN_IN)) {
            documentReference.update(DBCollectionConstant.UserAvailability, 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (preferenceManager.getBoolean(User.IS_SIGN_IN)) {
            documentReference.update(DBCollectionConstant.UserAvailability, 1);
        }
    }
}
