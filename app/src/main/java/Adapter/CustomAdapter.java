package Adapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project5.R;

import java.util.ArrayList;
import java.util.Date;

import DTO.PostDTO;
import Listener.OnPostListener;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {
    private ArrayList<PostDTO> arrayList;
    private Context context;
    private CardView cardView;
    public OnPostListener onPostListener;
    private static final String TAG = "CustomAdapter";

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView view_image;
        TextView view_title;
        TextView view_person;
        TextView view_date;
        CardView cardView;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view_image = itemView.findViewById(R.id.view_image);
            this.view_title = itemView.findViewById(R.id.view_title);
            this.view_person = itemView.findViewById(R.id.view_person);
            this.view_date = itemView.findViewById(R.id.view_date);
            this.cardView = itemView.findViewById(R.id.post_card);
        }
    }


    public CustomAdapter(ArrayList arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView view = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    public void setOnPostClickListener(OnPostListener Listener){
        onPostListener = Listener;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder,int position) {
        Glide.with(holder.itemView).load(arrayList.get(position).getMediaPath()).into(holder.view_image);
        Date su = arrayList.get(position).getDate();
        holder.view_date.setText(su.toString());
        holder.view_person.setText("작성자 : "+arrayList.get(position).getCheck());
        holder.view_title.setText(arrayList.get(position).getTitle());
        holder.itemView.setOnClickListener(v -> {
            if(onPostListener != null){
            onPostListener.onItemClick(holder.cardView, arrayList.get(position));
            }
        });
    }
    @Override
    public int getItemCount() {
        //삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }
}

