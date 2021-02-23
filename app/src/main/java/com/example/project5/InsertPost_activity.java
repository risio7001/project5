package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.Objects;

import DTO.Coment;
import DTO.PostDTO;

public class InsertPost_activity extends AppCompatActivity {
    private static final String TAG = "InsertPost_activity";
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private String imgPath;
    private FirebaseUser user;
    private String title;
    private String content;
    private Uri downloadUrl;
    private String check;
    private FirebaseFirestore db;
    private RelativeLayout loadingLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_post_activity);

        //버튼 선언

        findViewById(R.id.post_image).setOnClickListener(onClickListener);
        findViewById(R.id.post_video).setOnClickListener(onClickListener);
        findViewById(R.id.post_upload).setOnClickListener(onClickListener);
    }
    //버튼 리스너

    @SuppressLint("NonConstantResourceId")
    View.OnClickListener onClickListener = (v) -> {
        switch (v.getId()) {
            case R.id.post_image:
                if (ContextCompat.checkSelfPermission(InsertPost_activity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InsertPost_activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    if (ActivityCompat.shouldShowRequestPermissionRationale(InsertPost_activity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요", Toast.LENGTH_SHORT);
                    }
                } else {
                    goActivity(Gallery_activity.class, "image");
                }
                break;
            case R.id.post_video:
                if (ContextCompat.checkSelfPermission(InsertPost_activity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InsertPost_activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    if (ActivityCompat.shouldShowRequestPermissionRationale(InsertPost_activity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요", Toast.LENGTH_SHORT);
                    }
                } else {
                    goActivity(Gallery_activity.class, "video");
                }

                break;
            case R.id.post_upload:
                loadingLayout = findViewById(R.id.loading); // 로딩 창 넣기
                loadingLayout.setVisibility(View.VISIBLE);
                title = ((EditText) findViewById(R.id.post_title)).getText().toString();
                content = ((EditText) findViewById(R.id.post_content)).getText().toString();
                if (title.isEmpty()) {
                    Toast.makeText(this, "제목을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else if (content.isEmpty()) {
                    Toast.makeText(this, "내용을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    PostInsert();
                    break;
                }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    return;
                } else {
                    Toast.makeText(this, "권한을 허용해 주세요", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void goActivity(Class c, String media) {
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, 0);
    }

    public void PostInsert() {  // 1.게시물에 넣을 이름가져오기  &&    2. storage에 이미지 넣고 URL뽑아내기
        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference storageRef = storage.getReference();
        db = FirebaseFirestore.getInstance();
        DocumentReference docuPath = db.collection("Posts").document();
        DocumentReference coPath = db.collection("Posts").document(docuPath.getId())
                .collection("Coment").document("noComment");
        final StorageReference mountainImagesRef = storageRef.child("images/" + docuPath.getId() + "/" + imgPath);

        //1. 게시물에 넣을 이름가져오는부분
        DocumentReference docRef = db.collection("members").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document!=null){
                        if(document.exists()){
                            check = String.valueOf(document.get("name"));
                        }
                        else{
                            Toast.makeText(InsertPost_activity.this, "이름을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                        Log.d(TAG, "Exception : " + task.getException());
                }
            }
        });


        // 2. storage에 담기
        InputStream stream = null;
        try {
            stream = new FileInputStream(new File(imgPath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",imgPath).build();
        UploadTask uploadTask = mountainImagesRef.putStream(stream, metadata);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()) {
                throw Objects.requireNonNull(task.getException());
            }
            return mountainImagesRef.getDownloadUrl();
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    downloadUrl = task.getResult();
                    assert downloadUrl != null;
                    Log.d(TAG, "download URL : " + downloadUrl.toString());
                    PostDTO postDTO = new PostDTO(title, content, downloadUrl.toString(), user.getUid(), new Date(), check, docuPath.getId(), coPath.getId(), imgPath);
                    PostLoader(docuPath, postDTO);          //  게시물로 등록하기
                    MyPostLoader(docuPath, postDTO);        //  계정에 게시물 넣기
                    Coment comentDTO = new Coment("","댓글이 없습니다.", new Date(), coPath.getId());
                    PostComent(coPath, comentDTO);          //  첫게시물에 "댓글없음"문구 넣기
                } else {
                    Log.d(TAG, "실패!");
                }
            }
        });
    }
    public void PostLoader(DocumentReference docuPath, PostDTO postDTO) {    //  DB에 insert 하는 로직
        docuPath.set(postDTO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "PostDB업로드 성공!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "PostDB업로드 실패!", e);
                    }
                });
    }

    public void PostComent(DocumentReference coPath, Coment comentDTO){   //첫게시물에 "댓글없음"문구 넣기
        db = FirebaseFirestore.getInstance();
        coPath.set(comentDTO).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                loadingLayout = findViewById(R.id.loading);
                loadingLayout.setVisibility(View.GONE);
                Log.w(TAG, "ComentDB업로드 성공!");
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingLayout = findViewById(R.id.loading);
                loadingLayout.setVisibility(View.GONE);
                Log.w(TAG, "ComentDB업로드 실패!", e);
            }
        });
    }
    public void MyPostLoader(DocumentReference docuPath, PostDTO postDTO){ // 계정에 게시물 정보 넣기
        db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        db.collection("members").document(user.getUid())
                .collection("Post").document(docuPath.getId()).set(postDTO)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, user.getUid() + "에 넣기 성공!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, user.getUid() + "에 넣기 실패!");
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {    // 선택한 이미지 가져오기
        try {
            if (data.getStringExtra("imgPath") == null) {
                Toast.makeText(this, "파일이 선택되지 않았습니다.", Toast.LENGTH_SHORT).show();
            } else {
                imgPath = data.getStringExtra("imgPath");
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == 0) {
                    if (resultCode == Activity.RESULT_OK) {
                        imgPath = data.getStringExtra("imgPath");
                        ImageView insert_image = findViewById(R.id.insert_image);
                        Glide.with(this).load(imgPath).override(500).into(insert_image);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

