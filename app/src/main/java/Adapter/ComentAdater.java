package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project5.R;

import java.util.ArrayList;

import DTO.Coment;

public class ComentAdater extends RecyclerView.Adapter<ComentAdater.ComentViewHolder> {
    private Context context;
    private ArrayList<Coment> arrayList;
    private static final String TAG = "ComentAdater";

    public class ComentViewHolder extends RecyclerView.ViewHolder{
        TextView content;
        TextView name;
        CardView coment_card;
        Button coment_not;

        public ComentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.content = itemView.findViewById(R.id.coment_content);
            this.name = itemView.findViewById(R.id.coment_name);
            this.coment_card = itemView.findViewById(R.id.coment_card);
        }
    }

    public ComentAdater(ArrayList arrayList, Context context){
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ComentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CardView card = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coment, parent, false);
        ComentViewHolder comentHolder = new ComentViewHolder(card);
        return comentHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ComentViewHolder comentHolder, int position) {
        comentHolder.content.setText(arrayList.get(position).getContent_C());
        comentHolder.name.setText(arrayList.get(position).getName() + " :");
    }


    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
