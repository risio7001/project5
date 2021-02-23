package Adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.project5.R;

import java.util.ArrayList;

public class Gallery_Adapter extends RecyclerView.Adapter<Gallery_Adapter.GalleryViewHolder> {
    private ArrayList<String> mDataset;
    private Activity activity;
    private static final String TAG = "Gallery_Adapter";


    public static class GalleryViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        public GalleryViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

    public Gallery_Adapter(Activity activity, ArrayList<String> myDataset) {
        mDataset = myDataset;
        this.activity = activity;
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView v = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);

        return new GalleryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {

        CardView cardView = holder.cardView;
        // 갤러리 이미지 선택
        cardView.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("imgPath", mDataset.get(holder.getAdapterPosition()));
            activity.setResult(Activity.RESULT_OK, intent);
            activity.finish();
        });
        ImageView img = holder.cardView.findViewById(R.id.gallery_Image);
        Glide.with(activity).load(mDataset.get(position)).centerCrop().override(500).into(img);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}