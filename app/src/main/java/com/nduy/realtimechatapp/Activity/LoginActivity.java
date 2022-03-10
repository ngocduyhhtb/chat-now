package com.nduy.realtimechatapp.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nduy.realtimechatapp.Model.User;
import com.nduy.realtimechatapp.Utils.DBCollectionConstant;
import com.nduy.realtimechatapp.Utils.PreferenceManager;
import com.nduy.realtimechatapp.databinding.ActivityLoginBinding;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

@RequiresApi(api = Build.VERSION_CODES.N)
public class LoginActivity extends AppCompatActivity {
    public ActivityLoginBinding binding;
    private static final int RC_SIGN_IN = 1;
    private ProgressDialog LoadingBar;
    private PreferenceManager preferenceManager;
    GoogleSignInOptions gso;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        preferenceManager = new PreferenceManager(getApplicationContext());
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        View view = binding.getRoot();
        setContentView(view);
        LoadingBar = new ProgressDialog(this);
        binding.gotoRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
        binding.btnLogin.setOnClickListener(v2 -> {
            loginUser();
        });
        binding.googleLogin.setOnClickListener(c -> {
            Intent googleSignInIntent = googleSignInClient.getSignInIntent();
            onGoogleLoginLauncher.launch(googleSignInIntent);
        });
    }

    ActivityResultLauncher<Intent> onGoogleLoginLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    validateGoogleAccount(account);
                } catch (ApiException e) {
                    Log.w("GOOGLE_LOGIN_EXCEPTION", "signInResult:failed code=" + e.getMessage());
                    e.printStackTrace();
                }
            }
    );

    private void validateGoogleAccount(GoogleSignInAccount account) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(DBCollectionConstant.User)
                .whereEqualTo(User.EMAIL_FIELD, account.getEmail())
                .get()
                .addOnCompleteListener(c -> {
                    if (c.isSuccessful()) {
                        if (c.getResult().size() == 0) {
                            addNewUser(account);
                        } else {
                            DocumentSnapshot snapshot = c.getResult().getDocuments().get(0);
                            preferenceManager.putBoolean(User.IS_SIGN_IN, true);
                            preferenceManager.putString(User.DISPLAY_NAME, account.getDisplayName());
                            preferenceManager.putString(User.User_ID, snapshot.getId());
                            preferenceManager.putString(User.EMAIL_FIELD, account.getEmail());
                            preferenceManager.putString(User.IMAGE_FIELD, account.getPhotoUrl().toString());
                            startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    FancyToast.makeText(LoginActivity.this, "Something error, please try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                });
    }

    private void addNewUser(GoogleSignInAccount account) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> newUser = new HashMap<>();
        newUser.put(User.DISPLAY_NAME, account.getDisplayName());
        newUser.put(User.EMAIL_FIELD, account.getEmail());
        newUser.put(User.IMAGE_FIELD, String.valueOf(account.getPhotoUrl()));
        database.collection(DBCollectionConstant.User)
                .add(newUser)
                .addOnSuccessListener(task -> {
                    preferenceManager.putBoolean(User.IS_SIGN_IN, true);
                    preferenceManager.putString(User.DISPLAY_NAME, account.getDisplayName());
                    preferenceManager.putString(User.User_ID, task.getId());
                    preferenceManager.putString(User.EMAIL_FIELD, account.getEmail());
                    preferenceManager.putString(User.IMAGE_FIELD, String.valueOf(account.getPhotoUrl()));
                    startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                })
                .addOnFailureListener(e -> {
                    FancyToast.makeText(LoginActivity.this, "Something error, please try again", FancyToast.LENGTH_SHORT, FancyToast.ERROR, false);
                });
    }

    protected void loginUser() {
        String email = binding.loginInputEmail.getText().toString();
        String password = binding.loginInputPassword.getText().toString();
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.loginInputEmail.setError("Invalid email");
        } else if (TextUtils.isEmpty(password)) {
            binding.loginInputPassword.setError("This field is require");
        } else {
            LoadingBar.setTitle("Logging in");
            LoadingBar.setMessage("Please wait...");
            LoadingBar.setCanceledOnTouchOutside(false);
            LoadingBar.show();
            validateAccount(email, password);
        }
    }

    protected void validateAccount(String email, String password) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(DBCollectionConstant.User)
                .whereEqualTo(User.EMAIL_FIELD, email)
                .whereEqualTo(User.PASSWORD_FIELD, password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Login Result", "Login success");
                        DocumentSnapshot snapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(User.IS_SIGN_IN, true);
                        preferenceManager.putString(User.DISPLAY_NAME, snapshot.getString(User.DISPLAY_NAME));
                        preferenceManager.putString(User.User_ID, snapshot.getId());
                        preferenceManager.putString(User.EMAIL_FIELD, snapshot.getString(User.EMAIL_FIELD));
                        preferenceManager.putString(User.IMAGE_FIELD, snapshot.getString(User.IMAGE_FIELD));
                        LoadingBar.dismiss();
                        startActivity(new Intent(this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finish();
                    } else {
                        Log.d("Login Exception", "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    FancyToast.makeText(LoginActivity.this, e.getMessage(), FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                    LoadingBar.dismiss();
                });
    }
}
