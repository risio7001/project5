package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import Adapter.ComentAdater;
import DTO.Coment;
import DTO.PostDTO;

public class Detail_activity extends AppCompatActivity {
    private static final String TAG = "Detail_activity";
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Coment> arrayList;
    private String path;
    private PostDTO post;
    private ArrayList<Coment> addcomment;
    private Button coment_not;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_activity);
        // 해당 view 데이터 받아오기
        Bundle data = getIntent().getExtras();
        post = data.getParcelable("position");

        findViewById(R.id.btn_comentWrite).setOnClickListener(onClickListener);
        findViewById(R.id.coment_login).setOnClickListener(onClickListener);


        // 화면 선언부
        TextView detail_title = findViewById(R.id.detail_title);
        TextView detail_person = findViewById(R.id.detail_person);
        ImageView detail_img = findViewById(R.id.detail_img);
        Button coment_login = findViewById(R.id.coment_login);
        Button btn_comentWrite = findViewById(R.id.btn_comentWrite);
        EditText coment_write = findViewById(R.id.coment_write);

        VideoView detail_video = findViewById(R.id.detail_video);
//        Glide.with(Detail_activity.this).load(post.getMediaPath()).into(detail_video);
        Glide.with(Detail_activity.this).load(post.getMediaPath()).into(detail_img);

        // 화면 구성
//        detail_video.setVisibility(View.VISIBLE);
        detail_img.setVisibility(View.VISIBLE);
        detail_title.setText(post.getTitle());
        detail_person.setText(post.getCheck());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (user == null){
            coment_login.setVisibility(View.VISIBLE);
            btn_comentWrite.setVisibility(View.GONE);
            coment_write.setVisibility(View.GONE);
        }else{
            coment_login.setVisibility(View.GONE);
            coment_write.setVisibility(View.VISIBLE);
            btn_comentWrite.setVisibility(View.VISIBLE);
        }


        db.collection("Posts").document(post.getDocPath()).collection("Coment").orderBy("date", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                arrayList = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    arrayList.add(new Coment(
                            document.getData().get("name").toString(),
                            document.getData().get("content_C").toString(),
                            new Date(document.getDate("date").getTime()),
                            document.getData().get("c_path").toString()
                    ));
                }
                recyclerView = findViewById(R.id.detail_coment);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(Detail_activity.this);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new ComentAdater(arrayList, Detail_activity.this);
                recyclerView.setAdapter(adapter); // 리사이클러뷰 <-> CustomAdapter  연결부분
            }

        });
//        setComent(post.getDocPath(), post.getCoPath());
    }

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onClickListener = v -> {    // 게시물 추가 btn
        switch (v.getId()) {
            case R.id.btn_comentWrite:
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // user의 닉네임 가져오는 부분
                db = FirebaseFirestore.getInstance();
                String coment_write = ((EditText) findViewById(R.id.coment_write)).getText().toString();
                path = post.getDocPath();
                addComent(coment_write);
                break;
            case R.id.coment_login:
                goLoinActivity();
                break;
        }
    };

    public void addComent(String coment_write) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // user의 닉네임 가져오는 부분
        db = FirebaseFirestore.getInstance();
        Log.d("user Uid", ""+user.getUid());
        db.collection("members").document(user.getUid())
        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document!=null){
                        if(document.exists()){
                            String name = document.get("name").toString();
                            addDB(name, coment_write);
                        }
                        else{
                            Toast.makeText(Detail_activity.this, "재로그인 해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Log.d(TAG, "Exception : " + task.getException());
                }
            }
        });
    }
    public void addDB(String name, String coment_write){ // 댓글 전부 가져와서 뿌려주기
         db = FirebaseFirestore.getInstance();
        DocumentReference path2 = db.collection("Posts").document(path).collection("Coment").document();
        Coment commentAdd = new Coment(name,coment_write,new Date() ,path2.getId());
        path2.set(commentAdd).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Detail_activity.this, "댓글작성 완료!!", Toast.LENGTH_SHORT).show();
                // 최초댓글이 생겼을때 댓글 없음 db를 삭제 시켜야함
                noComentDelete();                   // 댓글없음 삭제하기
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Detail_activity.this, "댓글작성 실패....", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void goLoinActivity(){
        Intent intent = new Intent(this, Login_activity.class);
        intent.addFlags(intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
    }
    public void noComentDelete() {       // 댓글없음 삭제
        db.collection("Posts").document(path).collection("Coment")
                .document("noComment").delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
}