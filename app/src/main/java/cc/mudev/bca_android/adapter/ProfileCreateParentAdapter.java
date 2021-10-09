package cc.mudev.bca_android.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import cc.mudev.bca_android.R;

public class ProfileCreateParentAdapter extends RecyclerView.Adapter<ProfileCreateParentAdapter.ProfileCreateParentViewHolder> implements ProfileCreateParentItemTouchHelperListener {
    public ArrayList<ProfileCreateParentData> profileCreateParentDataList;
    public ItemTouchHelper itemTouchHelper;
    public Context context;

    public class ProfileCreateParentViewHolder extends RecyclerView.ViewHolder {
        public ProfileCreateParentAdapter profileCreateParentAdapter;
        public ProfileCreateParentData profileCreateParentData;
        public View itemView;
        public TextView groupNameTextView;
        public ImageButton dragGroupBtn, addFieldBtn, deleteGroupBtn;
        public RecyclerView fieldRecyclerView;

        public ProfileCreateParentViewHolder(ProfileCreateParentAdapter profileCreateParentAdapter, View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.profileCreateParentAdapter = profileCreateParentAdapter;

            this.groupNameTextView = itemView.findViewById(R.id.li_profileCreateFieldGroup_groupNameText);
            this.dragGroupBtn = itemView.findViewById(R.id.li_profileCreateFieldGroup_dragMoveBtn);
            this.addFieldBtn = itemView.findViewById(R.id.li_profileCreateFieldGroup_addFieldBtn);
            this.deleteGroupBtn = itemView.findViewById(R.id.li_profileCreateFieldGroup_deleteGroupBtn);
            this.fieldRecyclerView = itemView.findViewById(R.id.li_profileCreateFieldGroup_fieldRecyclerView);
            this.fieldRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        }
    }

    public ProfileCreateParentAdapter(Context context) {
        this.context = context;
        this.profileCreateParentDataList = new ArrayList<>();
    }
    public ProfileCreateParentAdapter(Context context, ArrayList<ProfileCreateParentData> profileCreateParentDataList) {
        this.context = context;
        this.profileCreateParentDataList = (profileCreateParentDataList == null) ? profileCreateParentDataList : new ArrayList<>();
    }
    public ProfileCreateParentAdapter(Context context, JSONObject profileCreateParentDataJson) {
        this.context = context;
        this.profileCreateParentDataList = new ArrayList<>();

        int parentJsonDataIndex = 0;
        for(Iterator<String> groupJsonIter = profileCreateParentDataJson.keys(); groupJsonIter.hasNext();) {
            String groupKey = groupJsonIter.next();
            ProfileCreateParentData groupData = new ProfileCreateParentData(parentJsonDataIndex, context, groupKey, null);
            parentJsonDataIndex++;

            try {
                JSONObject childJson = profileCreateParentDataJson.getJSONObject(groupKey);

                int childJsonDataIndex = 0;
                for(Iterator<String> fieldJsonIter = childJson.keys(); fieldJsonIter.hasNext();) {
                    String fieldKey = fieldJsonIter.next();
                    groupData.childData.add(new ProfileCreateChildData(childJsonDataIndex, fieldKey, childJson.getString(fieldKey)));
                    childJsonDataIndex++;
                }
            } catch (JSONException e) { e.printStackTrace(); }

            // JSON object spec does not have any sort-related things,
            // so we need to order it manually with additional field "index".
            Collections.sort(groupData.childData);
            this.profileCreateParentDataList.add(groupData);
        }

        // As I said earlier, JSON object spec does not have any sort-related things.
        // We need to order it manually with "index" field.
        Collections.sort(this.profileCreateParentDataList);
    }

    @Override
    public ProfileCreateParentAdapter.ProfileCreateParentViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_profilecreate_field_group, viewGroup, false);
        return new ProfileCreateParentAdapter.ProfileCreateParentViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(ProfileCreateParentAdapter.ProfileCreateParentViewHolder holder, int position) {
        ProfileCreateParentData profileCreateParentData = profileCreateParentDataList.get(position);
        holder.profileCreateParentData = profileCreateParentData;
        holder.groupNameTextView.setText(profileCreateParentData.childDataName);
        holder.fieldRecyclerView.setAdapter(profileCreateParentData.profileCreateChildAdapter);
        profileCreateParentData.profileCreateChildItemTouchHelperCallback.attachToRecyclerView(holder.fieldRecyclerView);
        holder.addFieldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                profileCreateParentData.childData.add(new ProfileCreateChildData(profileCreateParentData.childData.size(), "", ""));
                profileCreateParentData.profileCreateChildAdapter.notifyItemInserted(profileCreateParentData.childData.size() - 1);
            }
        });
        holder.deleteGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog signoutDialog = (new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("프로필 항목 삭제")
                    .setMessage(String.format("\"%s\" 항목 전체를 삭제하시겠습니까?\n이 동작은 되돌릴 수 없습니다.", profileCreateParentData.childDataName))
                    .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            holder.profileCreateParentAdapter.onItemRemove(position);
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {}
                    })).create();
                signoutDialog.show();
            }
        });
        holder.dragGroupBtn.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    itemTouchHelper.startDrag(holder);
                }
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.profileCreateParentDataList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < 0 || fromPosition >= profileCreateParentDataList.size() || toPosition < 0 || toPosition >= profileCreateParentDataList.size()){
            return false;
        }

        ProfileCreateParentData fromItem = profileCreateParentDataList.get(fromPosition);
        profileCreateParentDataList.remove(fromPosition);
        profileCreateParentDataList.add(toPosition, fromItem);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemRemove(int position) {
        try {
            profileCreateParentDataList.remove(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        // I really wanted to use notifyItemRemoved(position); because of smooth animation,
        // but that buttery animation can cause IndexOutOfBoundsException.
        notifyDataSetChanged();
    }

    public JSONObject toJSONObject() {
        JSONObject resultJSONObject = new JSONObject();

        int parentDataForEachIndex = 0;
        for (ProfileCreateParentData parentData: this.profileCreateParentDataList) {
            if (!resultJSONObject.has(parentData.childDataName)) {
                try {
                    JSONObject childJSONObject = new JSONObject();

                    int childDataForEachIndex = 0;
                    for (ProfileCreateChildData childData: parentData.childData) {
                        if (!childJSONObject.has(childData.key) && childData.key != null && childData.key.length() > 0 && childData.value.length() > 0) {
                            JSONObject childDataJSONObject = new JSONObject();
                            childDataJSONObject.put("index", childDataForEachIndex);
                            childDataJSONObject.put("value", childData.value);
                            childJSONObject.put(childData.key, childDataJSONObject);

                            childDataForEachIndex++;
                        }
                    }

                    if (childJSONObject.length() > 0) {
                        JSONObject parentDataJSONObject = new JSONObject();
                        parentDataJSONObject.put("index", parentDataForEachIndex);
                        parentDataJSONObject.put("value", childJSONObject);
                        String childDataName = null;
                        switch (parentData.childDataName) {
                            case "전화번호":
                                childDataName = "phone";
                                break;
                            case "이메일":
                                childDataName = "email";
                                break;
                            case "주소":
                                childDataName = "address";
                                break;
                            case "SNS":
                                childDataName = "sns";
                                break;
                            default:
                                childDataName = parentData.childDataName;
                                break;
                        }
                        resultJSONObject.put(childDataName, parentDataJSONObject);

                        parentDataForEachIndex++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultJSONObject;
    }
}
