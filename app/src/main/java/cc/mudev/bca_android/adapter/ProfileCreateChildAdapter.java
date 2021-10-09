package cc.mudev.bca_android.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import cc.mudev.bca_android.R;

public class ProfileCreateChildAdapter extends RecyclerView.Adapter<ProfileCreateChildAdapter.ProfileCreateChildViewHolder> implements ProfileCreateChildItemTouchHelperListener {
    public ArrayList<ProfileCreateChildData> profileCreateChildDataList;
    public ItemTouchHelper itemTouchHelper;

    public ProfileCreateChildAdapter(ArrayList<ProfileCreateChildData> profileCreateChildDataList) {
        this.profileCreateChildDataList = (profileCreateChildDataList != null) ? profileCreateChildDataList : new ArrayList<>();
        if (this.profileCreateChildDataList.isEmpty()) {
            this.profileCreateChildDataList.add(new ProfileCreateChildData(this.profileCreateChildDataList.size(), "", ""));
        }
    }

    public class ProfileCreateChildViewHolder extends RecyclerView.ViewHolder {
        public ProfileCreateChildAdapter profileCreateChildAdapter;
        public ProfileCreateChildData profileCreateChildData;
        public View itemView;
        public EditText key, value;
        public ImageButton moveFieldBtn, deleteFieldBtn;

        public ProfileCreateChildViewHolder(ProfileCreateChildAdapter profileCreateChildAdapter, View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.profileCreateChildAdapter = profileCreateChildAdapter;

            this.key = itemView.findViewById(R.id.li_profileCreateField_editNameField);
            this.value = itemView.findViewById(R.id.li_profileCreateField_editContentField);
            this.moveFieldBtn = itemView.findViewById(R.id.li_profileCreateField_moveBtn);
            this.deleteFieldBtn = itemView.findViewById(R.id.li_profileCreateField_deleteBtn);
        }
    }

    @Override
    public ProfileCreateChildViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.listitem_profilecreate_field, viewGroup, false);
        return new ProfileCreateChildViewHolder(this, view);
    }

    @Override
    public void onBindViewHolder(ProfileCreateChildViewHolder holder, int position) {
        ProfileCreateChildData profileCreateChildData = this.profileCreateChildDataList.get(position);

        holder.profileCreateChildData = profileCreateChildData;
        holder.key.setText(profileCreateChildData.key);
        holder.value.setText(profileCreateChildData.value);
        holder.key.addTextChangedListener(new TextWatcher() {
            String prevText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!prevText.equals(s.toString())) profileCreateChildData.key = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.value.addTextChangedListener(new TextWatcher() {
            String prevText;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                prevText = s.toString();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!prevText.equals(s.toString())) profileCreateChildData.value = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.deleteFieldBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.profileCreateChildAdapter.onItemRemove(position);
            }
        });
        holder.moveFieldBtn.setOnTouchListener(new View.OnTouchListener() {
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
        return this.profileCreateChildDataList.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if(fromPosition < 0 || fromPosition >= profileCreateChildDataList.size() || toPosition < 0 || toPosition >= profileCreateChildDataList.size()){
            return false;
        }

        ProfileCreateChildData fromItem = profileCreateChildDataList.get(fromPosition);
        profileCreateChildDataList.remove(fromPosition);
        profileCreateChildDataList.add(toPosition, fromItem);

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemRemove(int position) {
        try {
            profileCreateChildDataList.remove(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        // I really wanted to use notifyItemRemoved(position); because of smooth animation,
        // but that buttery animation can cause IndexOutOfBoundsException.
        notifyDataSetChanged();
    }
}
