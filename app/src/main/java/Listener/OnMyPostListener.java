package Listener;

import DTO.PostDTO;

public interface OnMyPostListener {
    void onDeleteClick(PostDTO data);
    void onModifyClick(PostDTO data);
}
