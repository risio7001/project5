package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login_activity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        mAuth = FirebaseAuth.getInstance();
    }
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:   // 로그인 버튼 클릭시 tf_email, tf_passward를 db와 비교 후 맞으면 성공 틀리면 재확인 메시지 출력
                login();
                break;
            case R.id.btn_Osignup:
                Intent intent = new Intent(this, SignUp_activity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
    private void login(){
        RelativeLayout loadingLayout = findViewById(R.id.loading);
        loadingLayout.setVisibility(View.VISIBLE);
        EditText tf_email = findViewById(R.id.tf_email);
        EditText tf_passward = findViewById(R.id.tf_passward);

        String email = tf_email.getText().toString();
        String pw = tf_passward.getText().toString();

        if(email.isEmpty()){ // 이메일 공백일때
            Toast.makeText(Login_activity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else if(pw.isEmpty()){// 비밀번호 공백일때
            Toast.makeText(Login_activity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
        }
        else{
            mAuth.signInWithEmailAndPassword(email, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(Login_activity.this, "로그인 성공!", Toast.LENGTH_SHORT).show();
                            finish();
                            startActivity();
                        } else {
                            loadingLayout.setVisibility(View.GONE);
                            Toast.makeText(Login_activity.this, "로그인 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        }
    }
    private void startActivity(){
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}

