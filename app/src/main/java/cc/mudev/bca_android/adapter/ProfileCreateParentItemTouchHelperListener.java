package cc.mudev.bca_android.adapter;

public interface ProfileCreateParentItemTouchHelperListener {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemRemove(int position);
}
