package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import DTO.PostDTO;

public class Modified_activity extends AppCompatActivity {
    private PostDTO post;
    private EditText modify_title;
    private EditText modify_content;
    private ImageView modify_img;
    private String imgPath;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private static final String TAG = "Modified_activity";
    private Uri downloadUrl;
    private RelativeLayout loadingLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modified_activity);
        // 값 받아오기
        Bundle data = getIntent().getExtras();
        post = data.getParcelable("position");

        // xml 선언
        modify_title = findViewById(R.id.modify_title);
        modify_content = findViewById(R.id.modify_content);
        modify_img = findViewById(R.id.modify_img);
        setView(post);
        findViewById(R.id.modify_upload).setOnClickListener(onClickListener);
        findViewById(R.id.modify_image).setOnClickListener(onClickListener);
        findViewById(R.id.modify_video).setOnClickListener(onClickListener);
    }


    //  화면 초기 값 세팅
    public void setView(PostDTO post){
        modify_title.setText(post.getTitle());
        modify_content.setText(post.getContent());
        imgPath = post.getMediaPath();
        Glide.with(this).load(imgPath).override(500).into(modify_img);
    }

    //  버튼 Listener
    View.OnClickListener onClickListener = v -> {
        switch (v.getId()){
            case R.id.modify_image:             // 이미지 btn
                if (ContextCompat.checkSelfPermission(Modified_activity.this,   // 권한 체크하기
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Modified_activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Modified_activity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요", Toast.LENGTH_SHORT);
                    }
                } else {
                    goActivity(Gallery_activity.class, "image");
                }
                break;
            case R.id.modify_video:             //  동영상 btn
                if (ContextCompat.checkSelfPermission(Modified_activity.this,     // 권한 체크하기
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Modified_activity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                    if (ActivityCompat.shouldShowRequestPermissionRationale(Modified_activity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    } else {
                        Toast.makeText(this, "권한을 허용해 주세요", Toast.LENGTH_SHORT);
                    }
                } else {
                    goActivity(Gallery_activity.class, "video");
                }
                break;
            case R.id.modify_upload:        //  수정하기 btn
                loadingLayout = findViewById(R.id.loading); // 로딩 창 넣기
                loadingLayout.setVisibility(View.VISIBLE);
                post_modify(post);
                break;

        }
    };

    public void post_modify(PostDTO post){
        //      선언부
        String title = modify_title.getText().toString();       // 입력된 Title text 가져오기
        String content = modify_content.getText().toString();   // 입력된 Content text 가져오기
        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        DocumentReference modify_db2 = db.collection("members").document(user.getUid())
                .collection("Post").document(post.getDocPath());                          // members Post update
        DocumentReference modify_db = db.collection("Posts").document(post.getDocPath()); // Posts update
        //         스토리지에 넣기 전 imgPath가 바뀌었는지 확인하는 작업
        if(imgPath.equals(post.getMediaPath())){        //imgPath 가 바뀌지 않았다면
            // storage에 저장없이 업데이트
            modify_db.update("title", title);
            modify_db.update("content", content);
            modify_db.update("mediaPath", post.getMediaPath());
            // collection (members) - document(user.getUid)
            // - collection(Post) - document(docuPath)  data Update
            modify_db2.update("title", title);
            modify_db2.update("content", content);
            modify_db2.update("mediaPath", post.getMediaPath());
            loadingLayout = findViewById(R.id.loading);
            loadingLayout.setVisibility(View.GONE);
            finish();
        }else{
            //imgPath 가 바뀌었다면
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference strRef = storageRef.child("images/" + post.getDocPath()+"/"+imgPath);

            // storage에 저장 후 url 추출 하고 업데이트
            InputStream stream = null;
            try {
                stream = new FileInputStream(new File(imgPath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("index",imgPath).build();
            UploadTask uploadTask = strRef.putStream(stream, metadata);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return strRef.getDownloadUrl();
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {              // storage에 업로드 성공시
                        // url 추출 가능
                        downloadUrl = task.getResult();
                        assert downloadUrl != null;
                        Log.d(TAG, "download URL : " + downloadUrl.toString());
                        // collection(Posts) - document(docuPath) data Update
                        modify_db.update("title", title);
                        modify_db.update("content", content);
                        modify_db.update("mediaPath", downloadUrl.toString());
                        modify_db.update("firstPath", imgPath);
                        // collection (members) - document(user.getUid)
                        // - collection(Post) - document(docuPath)  data Update
                        modify_db2.update("title", title);
                        modify_db2.update("content", content);
                        modify_db2.update("mediaPath", downloadUrl.toString());
                        modify_db2.update("firstPath", imgPath);
                        loadingLayout = findViewById(R.id.loading);
                        loadingLayout.setVisibility(View.GONE);
                        finish();
                    } else {                                // storage에 업로드 실패시
                        loadingLayout = findViewById(R.id.loading);
                        loadingLayout.setVisibility(View.GONE);
                        Log.d(TAG, "실패!");
                    }
                }
            });

        }
    }
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

    public void goActivity(Class c, String media){
        Intent intent = new Intent(this, c);
        intent.putExtra("media", media);
        startActivityForResult(intent, 0);
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
                        ImageView modify_img = findViewById(R.id.modify_img);
                        Glide.with(this).load(imgPath).override(500).into(modify_img);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}