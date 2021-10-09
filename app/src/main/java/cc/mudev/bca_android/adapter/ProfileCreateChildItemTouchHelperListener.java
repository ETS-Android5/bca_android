package cc.mudev.bca_android.adapter;

public interface ProfileCreateChildItemTouchHelperListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemRemove(int position);
}