package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Change_activity extends AppCompatActivity {
    private static final String TAG = "Change_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_activity);
        set();

        Button btn_change = findViewById(R.id.btn_apply);
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set2();
            }
        });


    }

    private void set() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("members").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document != null) {
                            if (document.exists()) {
                                String getName = (String) document.get("name");
                                TextView name = findViewById(R.id.ch_name);
                                name.setText(getName);
                                String getPhone = (String) document.get("phone");
                                TextView phone = findViewById(R.id.ch_phone);
                                phone.setText(getPhone);
                            } else {
                                Toast.makeText(Change_activity.this, "데이터가 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.d(TAG, "Exception : " + task.getException());
                    }
                }
            });
        }
    }
    private void set2(){
        TextView name = findViewById(R.id.ch_name);
        String name1 = name.getText().toString();
        TextView phone = findViewById(R.id.ch_phone);
        String phone1 = phone.getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference Ref = db.collection("members").document(user.getUid());
        Ref.update("name", name1);
        Ref.update("phone", phone1)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "\n이름 : " + name1 + "\n연락처 : " + phone1 + "으로 변경하였습니다.");
                        Toast.makeText(Change_activity.this, "수정되었습니다.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                        Toast.makeText(Change_activity.this, "실패하였습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}

