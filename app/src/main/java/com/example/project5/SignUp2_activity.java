package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import DTO.MemberDTO;

public class SignUp2_activity extends AppCompatActivity {
    private static final String TAG = "SignUp2_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up2_activity);
    }


    public void onclick(View view) {

        switch (view.getId()) {

            case R.id.btn_signUp2:

                String name = ((EditText) findViewById(R.id.signUp2_name)).getText().toString();
                String phone = ((EditText) findViewById(R.id.signUp2_phone)).getText().toString();

                if (name.isEmpty()) {// 이름이 공백일때
                    Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if (user != null) { // user가 없을때

                                    MemberDTO memberDTO = new MemberDTO(name, phone);
                        db.collection("members").document(user.getUid()).set(memberDTO)
                                .addOnSuccessListener(new OnSuccessListener<Void>() { // 추가정보 입력 성공시
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(SignUp2_activity.this, "추가 정보입력에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                            goActivity();
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {  //추가정보 입력 실패시
                                        @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUp2_activity.this, "회원 정보입력에 실패하였습니다!", Toast.LENGTH_SHORT).show();
                                        Log.w(TAG, "Error writing document", e);
                                        return;
                                    }
                                });
                    }
                }
//                case
        }
    }
    public void goActivity() { // 추가 정보 기입 activity 이동
        Intent intent = new Intent(this, MainActivity2.class);
        startActivity(intent);
    }
}