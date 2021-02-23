package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import Adapter.MyPostAdapter;
import DTO.PostDTO;
import Listener.OnMyPostListener;

public class MyPost_activity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PostDTO> arrayList;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private static final String TAG = "MyPost_activity";
    private TextView myPost_name;
    private String check;
    private RelativeLayout loadingLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_post_activity);
        myPost_name = findViewById(R.id.myPost_name);
        setTitleName();
        setView();

    } // end onCreate

    public void setTitleName() {     // 액티비티 상단 이름 출력
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("members").document(user.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        if (document.exists()) {
                            check = String.valueOf(document.get("name"));
                            myPost_name.setText(check);
                        } else {
                            Toast.makeText(MyPost_activity.this, "이름을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Log.d(TAG, "Exception : " + task.getException());
                }
            }
        });
    }

    public void setView() { //  (개선사항) DTO에 담아서 옮기면 처리속도가 빠름.
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        db.collection("members").document(user.getUid()).collection("Post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    arrayList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        arrayList.add(new PostDTO(
                                document.get("title").toString(),
                                document.get("content").toString(),
                                document.get("mediaPath").toString(),
                                document.get("person").toString(),
                                new Date(document.getDate("date").getTime()),
                                document.get("check").toString(),
                                document.get("docPath").toString(),
                                document.get("coPath").toString(),
                                document.get("firstPath").toString()
                        ));
                    }
                    recyclerView = findViewById(R.id.myPost_list);
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(MyPost_activity.this);
                    recyclerView.setLayoutManager(layoutManager);
                    adapter = new MyPostAdapter(arrayList, MyPost_activity.this);
                    MyPostAdapter myPostAdapter = (MyPostAdapter) adapter;
                    myPostAdapter.setOnMyPostListener(new OnMyPostListener() {
                        @Override
                        public void onDeleteClick(PostDTO data) { //  삭제
                            AlertDialog.Builder oDialog = new AlertDialog.Builder(MyPost_activity.this,
                                    android.R.style.Theme_DeviceDefault_Light_Dialog);          // 삭제 팝업창

                            oDialog.setMessage("게시물을 삭제하시겠습니까?")
                                    .setTitle("삭제하기")
                                    .setPositiveButton("아니오", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {                                                       // 삭제 취소하기
                                            Toast.makeText(MyPost_activity.this, "삭제 취소 되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNeutralButton("예", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Delete(data);                               // 삭제 시키기
                                        }
                                    })
                                    .setCancelable(false) // 백버튼으로 팝업창이 닫히지 않도록 한다.
                                    .show();
                        }

                        @Override
                        public void onModifyClick(PostDTO data) {  // 수정
                            goModifyActivity(data);
                        }
                    });
                    recyclerView.setAdapter(adapter);

                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void goModifyActivity(PostDTO data) {         // 수정하는 액티비티로 이동
        Intent intent = new Intent(this, Modified_activity.class);
        intent.putExtra("position", data);
        startActivity(intent);
    }

    public void Delete(PostDTO post) {      // 삭제
        loadingLayout = findViewById(R.id.loading); // 로딩 창 넣기
        loadingLayout.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        underCollectionDelete(post);        // noComment 존재여부 먼저 판단 후 삭제하기
        db.collection("Posts").document(post.getDocPath()).delete(); // 해당 게시물 db삭제
        // 계정에 저장된 게시물 삭제
        db.collection("members").document(user.getUid()).collection("Post").document(post.getDocPath())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "계정 내 게시물 db 삭제 완료");
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "계정 내 게시물 db 삭제 실패");
                    }
                });
        // storage 삭제
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference desertRef = storageRef.child("images/"+post.getDocPath()+"/"+post.getFirstPath());

        desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingLayout = findViewById(R.id.loading); // 로딩 창 넣기
                loadingLayout.setVisibility(View.GONE);
                Toast.makeText(MyPost_activity.this, "storage 내 게시물 삭제 성공!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(MyPost_activity.this, "storage 내 게시물 삭제 실패!", Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });
    }
    public void underCollectionDelete(PostDTO post){
        DocumentReference docRef = db.collection("Posts").document(post.getDocPath())
                .collection("Coment").document("noComment");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {        //  noComment document가 존재 할때
                        db.collection("Posts").document(post.getDocPath())
                                .collection("Coment").document("noComment").delete();
                    } else {                        //  noComment document가 존재하지 않을 때
                        CommentPathSearch(post);
                    }
                } else {
                    Log.d(TAG, " comment * db 찾기 실패 ", task.getException());
                }
            }
        });
    }
    public void CommentPathSearch(PostDTO post){
        db.collection("Posts").document(post.getDocPath()).collection("Coment")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {  // 문서 전체를 가져와서 저장
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        // 반복문으로 하나씩 찾아서 삭제
                        db.collection("Posts").document(post.getDocPath())
                                .collection("Coment").document(document.getId()).delete();
                    }
                } else {
                    Log.d(TAG, "하위 컬렉션 찾아서 삭제하기 실패 : ", task.getException());
                }
            }
        });
    }
}