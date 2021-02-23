package Listener;

import androidx.cardview.widget.CardView;

import DTO.PostDTO;

public interface OnPostListener {
    void onItemClick(CardView c, PostDTO data);
}
