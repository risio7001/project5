package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUp_activity extends AppCompatActivity {
    private static final String TAG = "SignUp_activity";
    private FirebaseAuth mAuth;
    EditText tf_email, tf_passward, tf_passward2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);
        mAuth = FirebaseAuth.getInstance();
    }

    public void onclick(View view) {
        tf_email = findViewById(R.id.signUp2_name);
        tf_passward = findViewById(R.id.signUp_pw);
        tf_passward2 = findViewById(R.id.signUp_pw2);

        String email = tf_email.getText().toString();
        String pw = tf_passward.getText().toString();
        String pw2 = tf_passward2.getText().toString();

        switch (view.getId()) {
            case R.id.btn_signUp2:
                if (email.isEmpty()) {
                    Toast.makeText(this, "Email을 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (pw.isEmpty()) {
                    Toast.makeText(this, "비밀번호를 입력해 주세요", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!pw.equals(pw2)) {
                    Toast.makeText(this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email, pw)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(SignUp_activity.this, "회원가입에 성공 하였습니다.\n환영합니다", Toast.LENGTH_SHORT).show();
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    startActivity();
//                                    updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(SignUp_activity.this, "회원가입에 실패하였습니다.\n관리자에게 문의 바랍니다.",
                                            Toast.LENGTH_SHORT).show();
//                                    updateUI(null);
                                }
                                // ...
                            }
                        });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void startActivity() { // 회원가입 성공시
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}
