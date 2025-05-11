package com.example.edubridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public class SignUp extends AppCompatActivity {
    // Views
    private TextInputEditText emailSignup, passwordSignup, otpInput;
    private TextInputLayout emailSignupLayout, passwordSignupLayout, otpLayout;
    private MaterialButton actionButton, backToLoginBtn;
    private TextView otpTimerText, resendOtpBtn;
    private ProgressBar progressBar;
    private View otpTimerLayout;

    // Firebase
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // Variables
    private String userEmail, userPassword, storedOtp;
    private CountDownTimer countDownTimer;
    private OtpApiService otpApiService;

    // Retrofit Interface
    interface OtpApiService {
        @POST("api/send-otp")
        Call<OtpResponse> sendOtp(@Body OtpRequest request);
    }

    // Data Classes
    static class OtpRequest {
        String email;
        OtpRequest(String email) { this.email = email; }
    }

    static class OtpResponse {
        boolean success;
        String otp;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://otp-verifier-kmas.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        otpApiService = retrofit.create(OtpApiService.class);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        emailSignup = findViewById(R.id.emailSignup);
        passwordSignup = findViewById(R.id.passwordSignup);
        otpInput = findViewById(R.id.otpInput);
        emailSignupLayout = findViewById(R.id.emailSignupLayout);
        passwordSignupLayout = findViewById(R.id.passwordSignupLayout);
        otpLayout = findViewById(R.id.otpLayout);
        actionButton = findViewById(R.id.actionButton);
        backToLoginBtn = findViewById(R.id.backToLoginBtn);
        otpTimerText = findViewById(R.id.otpTimerText);
        resendOtpBtn = findViewById(R.id.resendOtpBtn);
        progressBar = findViewById(R.id.progressBar);
        otpTimerLayout = findViewById(R.id.otpTimerLayout);
    }

    private void setupClickListeners() {
        actionButton.setOnClickListener(v -> {
            if (actionButton.getText().toString().equals(getString(R.string.send_otp))) {
                validateAndSendOtp();
            } else {
                verifyOtp();
            }
        });

        backToLoginBtn.setOnClickListener(v -> finish());
        resendOtpBtn.setOnClickListener(v -> resendOtp());
    }

    private void validateAndSendOtp() {
        userEmail = emailSignup.getText().toString().trim();
        userPassword = passwordSignup.getText().toString().trim();

        if (!validateInputs()) return;

        showLoading(true);
        checkEmailExists();
    }

    private boolean validateInputs() {
        if (userEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(userEmail).matches()) {
            emailSignup.setError("Enter valid email");
            return false;
        }

        if (userPassword.isEmpty() || userPassword.length() < 6) {
            passwordSignup.setError("Password must be ≥6 characters");
            return false;
        }
        return true;
    }

    private void checkEmailExists() {
        auth.fetchSignInMethodsForEmail(userEmail).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().getSignInMethods().isEmpty()) {
                    showLoading(false);
                    Toast.makeText(this, "Email already registered", Toast.LENGTH_SHORT).show();
                } else {
                    sendOtpViaApi();
                }
            } else {
                showLoading(false);
                Toast.makeText(this, "Error checking email", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOtpViaApi() {
        otpApiService.sendOtp(new OtpRequest(userEmail)).enqueue(new Callback<OtpResponse>() {
            @Override
            public void onResponse(Call<OtpResponse> call, Response<OtpResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    storedOtp = response.body().otp;
                    showOtpUi();
                    startOtpCountdown();
                    Toast.makeText(SignUp.this, "OTP sent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUp.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
                showLoading(false);
            }

            @Override
            public void onFailure(Call<OtpResponse> call, Throwable t) {
                showLoading(false);
                Toast.makeText(SignUp.this, "Network error", Toast.LENGTH_SHORT).show();
                Log.e("OTP_API", "Error: ", t);
            }
        });
    }

    private void verifyOtp() {
        String enteredOtp = otpInput.getText().toString().trim();

        if (enteredOtp.isEmpty() || !enteredOtp.equals(storedOtp)) {
            otpInput.setError("Invalid OTP");
            return;
        }

        showLoading(true);
        createFirebaseAccount();
    }

    private void createFirebaseAccount() {
        auth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        saveBasicUserData(user);
                    } else {
                        Toast.makeText(this, "Signup failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                    showLoading(false);
                });
    }

    private void saveBasicUserData(FirebaseUser user) {
        if (user != null) {
            Map<String, Object> userData = new HashMap<>();
            userData.put("email", user.getEmail());
            userData.put("createdAt", FieldValue.serverTimestamp());
            userData.put("profileComplete", false); // Mark profile as incomplete

            db.collection("users").document(user.getUid())
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show();
                        navigateToDescribes();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                        Log.e("FIRESTORE", "Error saving user", e);
                    });
        }
    }

    private void navigateToDescribes() {
        startActivity(new Intent(this, Describes.class));
        finish();
    }

    private void showOtpUi() {
        passwordSignupLayout.setVisibility(View.GONE);
        otpLayout.setVisibility(View.VISIBLE);
        otpTimerLayout.setVisibility(View.VISIBLE);
        actionButton.setText(R.string.verify_otp);
    }

    private void startOtpCountdown() {
        countDownTimer = new CountDownTimer(60000, 1000) {
            public void onTick(long millisLeft) {
                otpTimerText.setText(String.format("Expires in %ds", millisLeft / 1000));
            }
            public void onFinish() {
                otpTimerText.setText("OTP expired");
                resendOtpBtn.setEnabled(true);
            }
        }.start();
    }

    private void resendOtp() {
        resendOtpBtn.setEnabled(false);
        sendOtpViaApi();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        actionButton.setEnabled(!isLoading);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}