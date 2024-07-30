package com.example.edubridge;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GetStarted extends AppCompatActivity {
AppCompatButton signIn,signUp;
ImageButton contGoogle;
EditText passLog,emailLog;
public FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_get_started);
        signIn=findViewById(R.id.loginBtn);
        signUp=findViewById(R.id.signUpBtn);
        contGoogle=findViewById(R.id.contWithGoogle);
        passLog=findViewById(R.id.passwLog);
        emailLog=findViewById(R.id.emailLog);
        FirebaseApp.initializeApp(this);
        mAuth = FirebaseAuth.getInstance();
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emailLog.getText().toString().equals("")) {


                    Toast.makeText(GetStarted.this, "Email cant be empty", Toast.LENGTH_SHORT).show();


                } else if (passLog.getText().toString().contentEquals("")) {

                    Toast.makeText(GetStarted.this, "Password cant be empty", Toast.LENGTH_SHORT).show();

                } else {

                    mAuth.signInWithEmailAndPassword(emailLog.getText().toString(), passLog.getText().toString())
                            .addOnCompleteListener(GetStarted.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signInWithEmail:success");

                                        FirebaseUser user = mAuth.getCurrentUser();


                                      // Intent intent = new Intent(GetStarted.this, home.class);
                                        // startActivity(intent);


                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                                        Toast.makeText(GetStarted.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();

                                        Log.e(emailLog.getText().toString(),"k");
                                    }

                                }
                            });


                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpActivity=new Intent(GetStarted.this,Describes.class);
                startActivity(signUpActivity);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}