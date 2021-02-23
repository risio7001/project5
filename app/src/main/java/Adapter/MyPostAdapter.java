package Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project5.Modified_activity;
import com.example.project5.R;

import java.util.ArrayList;

import DTO.Coment;
import DTO.PostDTO;
import Listener.OnMyPostListener;
import Listener.OnPostListener;

public class MyPostAdapter extends RecyclerView.Adapter<MyPostAdapter.MyPostViewHolder> {
    private Context context;
    private ArrayList<PostDTO> arrayList;
    private static final String TAG = "MyPostAdapter";
    private Activity activity;
    public OnMyPostListener onMyPostListener;

    public class MyPostViewHolder extends RecyclerView.ViewHolder{
        TextView myPost_title;
        TextView myPost_content;
        ImageView myPost_img;
        CardView myPost_card;
        Button myPost_delete;
        Button myPost_modify;

        public MyPostViewHolder(@NonNull View itemView) {
            super(itemView);
            this.myPost_content = itemView.findViewById(R.id.myPost_content);
            this.myPost_title = itemView.findViewById(R.id.myPost_title);
            this.myPost_card = itemView.findViewById(R.id.myPost_card);
            this.myPost_delete = itemView.findViewById(R.id.myPost_delete);
            this.myPost_modify = itemView.findViewById(R.id.myPost_modify);
            this.myPost_img = itemView.findViewById(R.id.myPost_img);

        }
    }

    public MyPostAdapter(ArrayList arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyPostAdapter.MyPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mypost, parent, false);
        MyPostAdapter.MyPostViewHolder MyPostHolder = new MyPostAdapter.MyPostViewHolder(card);
        return MyPostHolder;
    }

    public void setOnMyPostListener(OnMyPostListener Listener){
        onMyPostListener = Listener;
    }

    @Override
    public void onBindViewHolder(@NonNull MyPostViewHolder myPostHolder, int position) {
        Glide.with(myPostHolder.itemView).load(arrayList.get(position).getMediaPath()).into(myPostHolder.myPost_img);
        myPostHolder.myPost_content.setText(arrayList.get(position).getContent());
        myPostHolder.myPost_title.setText(arrayList.get(position).getTitle());
        myPostHolder.myPost_modify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyPostListener.onModifyClick(arrayList.get(position));
            }
        });
        myPostHolder.myPost_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onMyPostListener.onDeleteClick(arrayList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
