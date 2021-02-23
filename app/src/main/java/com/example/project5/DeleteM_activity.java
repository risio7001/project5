package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class DeleteM_activity extends AppCompatActivity {
    private static final String TAG = "DeleteM_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_m_activity);
        findViewById(R.id.delete_btn).setOnClickListener(onclicklistener);
    }

    View.OnClickListener onclicklistener = v -> {
        switch (v.getId()) {
            case R.id.delete_btn:
                delete();
        }
    };

    public void delete(){
        String delete_email = ((EditText)findViewById(R.id.delete_email)).getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String email = user.getEmail();
        if(delete_email.equals(email)){
            postExist();
        }
        else{
            Toast.makeText(DeleteM_activity.this, "이메일을 확인해주세요.", Toast.LENGTH_SHORT).show();
        }
    }

    public void postExist() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("members").document(user.getUid()).collection("Post")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if (task.isSuccessful()) {                                                              // 게시물 읽어오기

                            if (task.getResult().size() == 0) {                                                   // 게시물이 없을때

                                AlertDialog.Builder di = new AlertDialog.Builder(DeleteM_activity.this, android.R.style.Theme_DeviceDefault_Light_Dialog);
                                di.setMessage("탈퇴 하시겠습니까?")
                                        .setTitle("탈퇴하기")
                                        .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Toast.makeText(DeleteM_activity.this, "탈퇴 취소되었습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNeutralButton("탈퇴", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteEnd();
                                            }
                                        })
                                        .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                                        .show();
                            } else {                                                                                      //게시물이 있을때
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    AlertDialog.Builder di = new AlertDialog.Builder(DeleteM_activity.this,
                                            android.R.style.Theme_DeviceDefault_Light_Dialog);
                                    di.setMessage("계정에 게시물이 남아있습니다\n삭제 후 탈퇴가 가능합니다")
                                            .setTitle("탈퇴하기")
                                            .setNeutralButton("게시물 삭제", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    goMyPostActivity();
                                                }
                                            })
                                            .setPositiveButton("취소", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Toast.makeText(DeleteM_activity.this, "탈퇴 취소되었습니다.", Toast.LENGTH_SHORT).show();
                                                }
                                            })
                                            .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                                            .show();
                                }
                            }
                        } else {

                        }
                    }
                });
    }

    public void deleteEnd(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("members").document(user.getUid()).delete();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User account deleted.");
                            goMainActivity();
                        }
                    }
                });
    }

    public void goMyPostActivity(){
        Intent intent = new Intent(this, MyPost_activity.class);
        startActivity(intent);
    }
    public void goMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}