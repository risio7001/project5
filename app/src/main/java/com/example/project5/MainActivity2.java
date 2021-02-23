package com.example.project5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

import Adapter.CustomAdapter;
import DTO.Coment;
import DTO.PostDTO;
import Listener.OnPostListener;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PostDTO> arrayList;
    private FirebaseFirestore db;
    private SwipeRefreshLayout swip;
    private static final String TAG = "MainActivity2";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.btn_addPost).setOnClickListener(OnClickListener);
        swip = findViewById(R.id.swip2);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("members").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if(document!=null){
                            if(document.exists()){
                                Log.d(TAG, document.get("name")+"님 로그인 하셨습니다.");
                            }
                            else{
                                Toast.makeText(MainActivity2.this, "추가 정보 입력창으로 이동합니다.", Toast.LENGTH_SHORT).show();
                                goInfoActivity();
                            }
                        }
                    }
                    else{
                        Log.d(TAG, "Exception : " + task.getException());
                    }
                }
            });
        }

        setView();

        swip.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setView();
                adapter.notifyDataSetChanged();
                swip.setRefreshing(false);
            }
        });
    }

  View.OnClickListener OnClickListener = v -> {    // 게시물 추가 btn
        switch (v.getId()){
            case R.id.btn_addPost:
                goPostActivity();
                break;
        }
    };
    private void setView(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                arrayList = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    arrayList.add(new PostDTO(
                            document.getData().get("title").toString(),
                            document.getData().get("content").toString(),
                            document.getData().get("mediaPath").toString(),
                            document.getData().get("person").toString(),
                            new Date(document.getDate("date").getTime()),
                            document.getData().get("check").toString(),
                            document.getData().get("docPath").toString(),
                            document.getData().get("coPath").toString(),
                            document.getData().get("firstPath").toString()
                    ));
                }
                recyclerView = findViewById(R.id.main_list);
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(MainActivity2.this);
                recyclerView.setLayoutManager(layoutManager);
                adapter = new CustomAdapter(arrayList, MainActivity2.this);
                CustomAdapter myadapter = (CustomAdapter) adapter;
                myadapter.setOnPostClickListener(new OnPostListener() {
                    @Override
                    public void onItemClick(CardView c, PostDTO data) {
                        goDetailActivity(data);
                    }
                });
                recyclerView.setAdapter(adapter); // 리사이클러뷰 <-> CustomAdapter  연결부분
                adapter.notifyDataSetChanged();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return true;
    }
    public void onclick(MenuItem item) {
        switch(item.getItemId()){
            case R.id.btn_info:
                if(FirebaseAuth.getInstance().getCurrentUser() == null){
                    Toast.makeText(this, "로그인 후 이용해 주세요", Toast.LENGTH_SHORT).show();
                    goLoginActivity();
                    return;
                }else{
                    startActivity();
                }
        }
    }
    private void startActivity(){
        Intent intent = new Intent(this, Info_activity.class);
        startActivity(intent);
    }
    private void goInfoActivity(){
        Intent intent = new Intent(this, SignUp2_activity.class);
        startActivity(intent);
    }
    private void goLoginActivity(){
        Intent intent = new Intent(this, Login_activity.class);
        finish();
        startActivity(intent);
    }
    private void goPostActivity(){
        Intent intent = new Intent(this, InsertPost_activity.class);
        startActivity(intent);
    }
    private void goDetailActivity(PostDTO data){
        Intent intent = new Intent(this, Detail_activity.class);
        intent.putExtra("position", data);
        startActivity(intent);
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}