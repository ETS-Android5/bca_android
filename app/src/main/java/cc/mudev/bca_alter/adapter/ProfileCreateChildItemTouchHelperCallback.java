package cc.mudev.bca_alter.adapter;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileCreateChildItemTouchHelperCallback extends ItemTouchHelper.Callback {
    ProfileCreateChildItemTouchHelperListener listener;

    public ProfileCreateChildItemTouchHelperCallback(ProfileCreateChildItemTouchHelperListener listener){
        this.listener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        return makeMovementFlags(dragFlags, 0);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView,
                          RecyclerView.ViewHolder source, RecyclerView.ViewHolder target) {
        return listener.onItemMove(source.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {}
}