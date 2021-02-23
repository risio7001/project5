package com.example.project5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;

import Adapter.CustomAdapter;
import DTO.PostDTO;
import Listener.OnPostListener;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth mAuth;
    private RecyclerView recyclerView2;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<PostDTO> arrayList2;
    private SwipeRefreshLayout swip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swip = findViewById(R.id.swip);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            FirebaseAuth.getInstance().signOut();
        }
        mAuth = FirebaseAuth.getInstance();
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

    private void setView(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Posts").orderBy("date", Query.Direction.DESCENDING).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                arrayList2 = new ArrayList<>();
                for(QueryDocumentSnapshot document : task.getResult()){
                    arrayList2.add(new PostDTO(
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
                recyclerView2 = findViewById(R.id.main_list2);
                recyclerView2.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(MainActivity.this);
                recyclerView2.setLayoutManager(layoutManager);
                adapter = new CustomAdapter(arrayList2, MainActivity.this);
                CustomAdapter myadapter = (CustomAdapter) adapter;
                myadapter.setOnPostClickListener(new OnPostListener() {
                    @Override
                    public void onItemClick(CardView c, PostDTO data) {
                        goDetailActivity(data);
                    }
                });
                recyclerView2.setAdapter(adapter); // 리사이클러뷰 <-> CustomAdapter  연결부분
                adapter.notifyDataSetChanged();
            }
        });

    }


    // 상단 액션 바
    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    public void updateUI(FirebaseUser n) {
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    public void onclick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.btn_Ologin: // 로그인 버튼
                Intent intent = new Intent(this, Login_activity.class);
                startActivity(intent);
                break;
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
    private void goDetailActivity(PostDTO data){
        Intent intent = new Intent(this, Detail_activity.class);
        intent.putExtra("position", (Parcelable) data);
        startActivity(intent);
    }
}