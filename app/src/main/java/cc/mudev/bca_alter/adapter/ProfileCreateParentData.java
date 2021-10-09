package cc.mudev.bca_alter.adapter;

import android.content.Context;

import androidx.recyclerview.widget.ItemTouchHelper;

import java.util.ArrayList;

public class ProfileCreateParentData implements Comparable<ProfileCreateParentData> {
    public int index;
    public Context context;
    public String childDataName;
    public ArrayList<ProfileCreateChildData> childData;
    public ProfileCreateChildAdapter profileCreateChildAdapter;
    public ItemTouchHelper profileCreateChildItemTouchHelperCallback;

    public ProfileCreateParentData(int index, Context context, String childDataName, ArrayList<ProfileCreateChildData> childData) {
        this.index = index;
        this.context = context;
        this.childDataName = (childDataName != null) ? childDataName : "";
        this.childData = (childData != null) ? childData : new ArrayList<>();
        this.profileCreateChildAdapter = new ProfileCreateChildAdapter(this.childData);
        this.profileCreateChildItemTouchHelperCallback = new ItemTouchHelper(new ProfileCreateChildItemTouchHelperCallback(this.profileCreateChildAdapter));
        this.profileCreateChildAdapter.itemTouchHelper = this.profileCreateChildItemTouchHelperCallback;
    }

    @Override
    public int compareTo(ProfileCreateParentData o) {
        if (o.index < this.index) {
            return 1;
        } else if (o.index > this.index) {
            return -1;
        }
        return 0;
    }
}
