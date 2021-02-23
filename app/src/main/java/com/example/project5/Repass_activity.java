package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Repass_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repass_activity);
    }

    public void onclick(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String email = ((EditText) findViewById(R.id.re_email)).getText().toString();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            TextView tf = findViewById(R.id.send_mail);
                            tf.setVisibility(View.VISIBLE);
                            Toast.makeText(Repass_activity.this, email + "로 비밀번호 재설정 메일이 발송되었습니다. \n자동 로그아웃 되었습니다.", Toast.LENGTH_LONG).show();
                            FirebaseAuth.getInstance().signOut();
                            goActivity();
                        }else{
                            Toast.makeText(Repass_activity.this, "이메일을 확인해 주세요.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
    public void goActivity(){
        Intent intent  = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}
