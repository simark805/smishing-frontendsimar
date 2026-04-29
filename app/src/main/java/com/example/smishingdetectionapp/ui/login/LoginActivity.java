package com.example.smishingdetectionapp.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.smishingdetectionapp.BuildConfig;
import com.example.smishingdetectionapp.DataBase.DBresult;
import com.example.smishingdetectionapp.DataBase.Retrofitinterface;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.databinding.ActivityLoginBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.ui.Register.RegisterMain;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private Retrofit retrofit;
    private Retrofitinterface retrofitinterface;
    private DatabaseAccess databaseAccess;

    private String BASE_URL = BuildConfig.SERVERIP;

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    private boolean isPinLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitinterface = retrofit.create(Retrofitinterface.class);

        databaseAccess = DatabaseAccess.getInstance(this);
        databaseAccess.open();

        final EditText usernameEditText = binding.email;
        final EditText passwordEditText = binding.password;
        final Button loginButton = binding.loginButton;
        final ProgressBar loadingProgressBar = binding.progressbar;
        final SignInButton googleBtn = binding.googleBtn;
        final Button registerButton = binding.registerButton;
        final ImageButton togglePasswordVisibility = binding.togglePasswordVisibility;
        final Button togglePinLogin = binding.togglePinLogin;

        togglePinLogin.setOnClickListener(v -> {
            if (isPinLogin) {
                passwordEditText.setHint("Password");
                passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                loginButton.setText("Login");
                togglePinLogin.setText("Login with PIN");
                isPinLogin = false;
            } else {
                passwordEditText.setHint("Enter 6-digit PIN");
                passwordEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                loginButton.setText("Login with PIN");
                togglePinLogin.setText("Login with Password");
                isPinLogin = true;
            }
        });

        loginButton.setOnClickListener(v -> {
            String input = passwordEditText.getText().toString();

            if (isPinLogin) {
                if (input.length() != 6) {
                    passwordEditText.setError("PIN must be 6 digits");
                    return;
                }
                loginWithPin(input);
            } else {
                String email = usernameEditText.getText().toString();

                if (email.isEmpty() || input.isEmpty()) {
                    Toast.makeText(this, "Email and Password must not be empty", Toast.LENGTH_SHORT).show();
                    return;
                }

                loginWithPassword(email, input);
            }
        });

        registerButton.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterMain.class));
            finish();
        });

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);

        googleBtn.setOnClickListener(v -> signInGoogle());
    }

    // ---------------- PIN LOGIN ----------------
    private void loginWithPin(String pin) {
        if (databaseAccess.validatePin(pin)) {
            navigateToMainActivity();
        } else {
            Toast.makeText(this, "Invalid PIN", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------------- PASSWORD LOGIN ----------------
    private void loginWithPassword(String email, String password) {

        if (canUseDebugBypassWithPassword(email, password)) {
            Toast.makeText(this, "Debug login successful", Toast.LENGTH_SHORT).show();
            navigateToMainActivity();
            return;
        }

        handleLoginDialog(email, password);
    }

    // ---------------- RETROFIT LOGIN ----------------
    private void handleLoginDialog(String email, String password) {

        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);

        Call<DBresult> call = retrofitinterface.executeLogin(map);

        call.enqueue(new Callback<DBresult>() {
            @Override
            public void onResponse(Call<DBresult> call, Response<DBresult> response) {
                if (response.code() == 200) {
                    navigateToMainActivity();
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong Credentials", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DBresult> call, Throwable t) {
                Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------------- GOOGLE LOGIN ----------------
    private void signInGoogle() {
        Intent signInIntent = gsc.getSignInIntent();
        startActivityForResult(signInIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                navigateToMainActivity();
            } catch (ApiException e) {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ---------------- NAVIGATION ----------------
    private void navigateToMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    // ---------------- PLACEHOLDERS ----------------
    private boolean canUseDebugBypassWithPassword(String email, String password) {
        return false;
    }

    private boolean isUserLoggedIn() {
        return false;
    }

    private void updateUiWithUser(Object model) {}

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}