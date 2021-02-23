package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Info_activity extends AppCompatActivity {
    private static final String TAG = "Info_activity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_activity);
        readDB();

        Button btn_myPost = findViewById(R.id.btn_myPost);
        btn_myPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goMyPostActivity();
            }
        });

        Button pass_re = findViewById(R.id.btn_re);
        pass_re.setOnClickListener(new View.OnClickListener() { // 비밀번호 재설정
            @Override
            public void onClick(View v) {
                goActivity();
            }
        });

        Button Logout = findViewById(R.id.btn_logout);
        Logout.setOnClickListener(new View.OnClickListener() { // 로그아웃
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                goActivity2();
            }
        });

        Button change = findViewById(R.id.btn_change);
        change.setOnClickListener(new View.OnClickListener() { // 정보 변경
            @Override
            public void onClick(View v) {
                goActivity3();
            }
        });

        Button btn_btn_deleteM = findViewById(R.id.btn_deleteM);
        btn_btn_deleteM.setOnClickListener(new View.OnClickListener() {     //회원 탈퇴
            @Override
            public void onClick(View v) {
                goDeleteM();
            }
        });

    }

    private void goDeleteM(){       // 회원탈퇴
        Intent intent = new Intent(this, DeleteM_activity.class);
        startActivity(intent);
    }
    private void goMyPostActivity(){    // 내 게시물 목록
        Intent intent = new Intent(this, MyPost_activity.class);
        startActivity(intent);
    }

    private void goActivity(){      // 비밀 번호 재설정
        Intent intent = new Intent(this, Repass_activity.class);
        startActivity(intent);
    }
    private void goActivity2(){     // 로그아웃
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
    private void goActivity3(){     // 정보 수정
        Intent intent = new Intent(this, Change_activity.class);
        startActivity(intent);
    }

    private void readDB(){          // setView
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){ // 로그인 정보 가져오기
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("members").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document!=null){
                            if(document.exists()){
                                TextView name = findViewById(R.id.info_name);
                                name.setText("이름 : " + document.get("name") + "님");
                                Log.d(TAG, document.get("name")+"님 로그인 하셨습니다.");
                            }
                            else{
                                Toast.makeText(Info_activity.this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else{
                        Log.d(TAG, "Exception : " + task.getException());
                    }
                }
            });
        }
    }
}